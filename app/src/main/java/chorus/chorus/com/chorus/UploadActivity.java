package chorus.chorus.com.chorus;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.util.ArrayList;
        import java.util.List;
        import android.Manifest;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.ComponentName;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.pm.ResolveInfo;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Base64;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.Toast;

public class UploadActivity extends AppCompatActivity {
    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;
    private Button btn_select_image;
    private ImageView imageView;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private ArrayList<String>datasend;
    String ba1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        btn_select_image = (Button) findViewById(R.id.btn_select_image);
        imageView = (ImageView) findViewById(R.id.img_photo);
        btn_select_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageOption();
            }
        });
    }

    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  //  File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                    //  Log.e("image name", mImageCaptureUri.toString());
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                   //mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        datasend = new ArrayList<String>();
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();
            System.out.println("Gallery Image URI : " + mImageCaptureUri);
            CropingIMG();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            System.out.println("Camera Image URI : " + mImageCaptureUri);
            CropingIMG();
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    Bitmap photo = decodeFile(outPutFile);
                    //String name = mImageCaptureUri.getPath().substring(mImageCaptureUri.getPath().lastIndexOf("/") + 1);
                    datasend.add("hhhhhhhhh");
                    upload(photo);
                    imageView.setImageBitmap(photo);

                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void upload(Bitmap photo) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG,75, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba,Base64.NO_WRAP);
        datasend.add(ba1);
        new uploadToServer(this).execute(datasend,outPutFile);

    }
    private void CropingIMG() {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY",512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);
            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res =list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
          //  int inSampleSize = 1;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    class uploadToServer extends ApiTaskNoDialog<Object, Void, ImageProfil> {
        uploadToServer(Context context) {
            super(context);
        }

        protected ImageProfil doInBackground(Object... params) {
            //filePath.substring(filePath.lastIndexOf("."));

            try {
                ArrayList<String>data= (ArrayList<String>) params[0];
                File file = (File) params[1];
                // ImageResult isuploaded= AppSession.getApi(context).savepicture1(params[0]);
                ImageProfil isuploaded = AppSession.getApi(context).savepicture2(data,file);
                Log.e("result upload", String.valueOf(isuploaded));
                return isuploaded;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }


        }

        protected void onPostExecute(ImageProfil result) {
            if (result != null) {
                AppSession.currentUser.Logo = result;
                //Log.e("image file",result.thumb_file_url);
                //loadHomeFragment();
//                finish();
               // startActivity(getIntent());
                Toast.makeText(getApplicationContext(), "Votre image est ajouté avec succés", Toast.LENGTH_LONG).show();
                /*AppSession.currentUser.image_list = result.image_list;
                AppSession.currentUser.Logo=result.logo;
                SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.image_list = result.image_list;
                obj.Logo=result.logo;
            }    String json2 = gson.toJson(obj);
    prefsEditor.putString("user", json2);
    prefsEditor.commit();
    Intent intent = new Intent(MesphotosActivity2.this, MyAccountActivity.class);
    MesphotosActivity2.this.startActivity(intent);
    MesphotosActivity2.this.finish();*/
            } else {

                // showError();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }
}
