package chorus.chorus.com.chorus;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import chorus.chorus.com.chorus.AndroidMultiPartEntity.ProgressListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.database.Cursor;
import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import static chorus.chorus.com.chorus.R.id.progressBar;

/**
 * Created by pc on 20/07/2016.
 */
public class DiscussActivity extends AppCompatActivity {
    private String idautreMembre;
    private String message;
    private Toolbar toolbar;
    private DiscussAdapter adapter;
    private ListView lv;
    private EditText editText1;
    private MyReceiver receiver;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private ProgressBar progressBar;
    long totalSize = 0;

    private boolean mIsScrollingUp;
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int CAMERA_CAPTURE_GALLERY_REQUEST_CODE =300;
    private static final int CAMERA_CAPTURE_AUDIO_REQUEST_CODE =400;
    private static final int CAMERA_CAPTURE_FILE_REQUEST_CODE =500;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int MEDIA_TYPE_AUDIO = 4;
    private static final int MEDIA_TYPE_GALERY = 3;
    private static final int MEDIA_TYPE_FILE = 3;
    private Uri fileUri; // file url to store image/video
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setContentView(R.layout.detail_message);
        // seekBar=(RangeSeekBar)findViewById(R.id.rangeSeekbar) ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
            // getWindow().setStatusBarColor(ContextCompat.getColor(this,(AppSession.currentUser.sexe.equals("M"))? R.color.colorvertclair : R.color.colorrose));
        }
        setSupportActionBar(toolbar);
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;

        Intent intent = getIntent();
        idautreMembre= intent.getStringExtra("idautreMembre");
        //receiver = new MyReceiver();
        // on lance le service
        IntentFilter filter = new IntentFilter(MyReceiver.ACTION_NEW_MESSAGE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);


        final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        final View rootView = findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        final ImageView cameraButton = (ImageView) findViewById(R.id.camera_btn);
        final ImageView videoButton = (ImageView) findViewById(R.id.video_btn);
        final ImageView submitButton = (ImageView) findViewById(R.id.submit_btn);
        final ImageView galleryButton = (ImageView) findViewById(R.id.galerie_btn);
        final ImageView audioButton = (ImageView) findViewById(R.id.audio_btn);
        final ImageView fileButton = (ImageView) findViewById(R.id.file_btn);

      //  final TextView txtEmojis = (TextView) findViewById(R.id.txtEmojs);

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {

                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });
        /**
         * Capture image button click event
         */
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        /**
         * Record video button click event
         */
        videoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                chooseGalleryimage();
            }
        });
        fileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showFileChooser();
            }
        });
        audioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openGalleryAudio();
            }
        });
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
        //On submit, add the edittext text to listview and clear the edittext
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                                   message = encrypt(getApplicationContext(),emojiconEditText.getText().toString());

                    // txtEmojis.setText(newText);
                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                    int minute = c.get(Calendar.MINUTE);
                    //12 hour format
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    String date =String.valueOf(hour) + ":" + String.valueOf(minute);
                    // message = editText1.getText().toString();
                    if(! message.equals("")) {
                        Message msg=new Message();
                        msg.idMemAutreMembre=idautreMembre;
                        msg.mes_mem_id_dest=idautreMembre;
                        msg.mes_mem_id_exp=AppSession.currentUser.id;
                        msg.mes_subject=message;
                        msg.mes_type="1";
                        msg.image_profil=AppSession.currentUser.Logo.thumb_file_url;
                        msg.DateMessage=date;
                        AppSession.usermessages.add(msg);
                        adapter.notifyDataSetChanged();
                        lv.setSelection(AppSession.usermessages.size() - 1);
                        // adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));

                        emojiconEditText.getText().clear();

                        new AddMessageToTask(getBaseContext()).execute(message,"1","");

                }


                //Toast.makeText(DiscussActivity.this, newText + "", Toast.LENGTH_SHORT).show();



            }
        });
        //setToolbarTitle();

        lv = (ListView) findViewById(R.id.listView1);

        adapter = new DiscussAdapter(this,AppSession.usermessages);

        lv.setAdapter(adapter);
        lv.setItemsCanFocus(true);
        lv.setOnScrollListener(new LoadMoreMessagesListener());
        lv.setSelection(AppSession.usermessages.size() - 1);
      /*  editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setVisibility(View.GONE);*/
       /* if(AppSession.currentUser.getAboDateFin()!=true)
        {
     editText1.setVisibility(View.GONE);
      /*    editText1.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                        int minute = c.get(Calendar.MINUTE);
                        //12 hour format
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        String date = "à" + String.valueOf(hour) + "h" + String.valueOf(minute);
                        message = editText1.getText().toString();
                        if(!message.equals("")) {
                            Message msg=new Message();
                            msg.idMemAutreMembre=idautreMembre;
                            msg.mes_mem_id_dest=idautreMembre;
                            msg.mes_mem_id_exp=AppSession.currentUser.id;
                            msg.mes_subject=message;
                            msg.image_profil=AppSession.currentUser.Logo.thumb_file_url;
                            msg.DateMessage=date;
                            AppSession.usermessages.add(msg);
                            Log.e("msg",message);
                            adapter.notifyDataSetChanged();
                            lv.setSelection(AppSession.usermessages.size() - 1);
                           // adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));

                            editText1.setText("");

                            new AddMessageToTask(getBaseContext()).execute();
                            return true;
                        }
                        else
                            return false;
                    }
                    return false;
                }
            });*/
       /* }
        else {*/
          /*  editText1.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                        int minute = c.get(Calendar.MINUTE);
                        //12 hour format
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        String date = "à" + String.valueOf(hour) + "h" + String.valueOf(minute);
                        message =  editText1.getText().toString();
                        if(! message.equals("")) {
                            Message msg=new Message();
                            msg.idMemAutreMembre=idautreMembre;
                            msg.mes_mem_id_dest=idautreMembre;
                            msg.mes_mem_id_exp=AppSession.currentUser.id;
                            msg.mes_subject=message;
                            msg.image_profil=AppSession.currentUser.Logo.thumb_file_url;
                            msg.DateMessage=date;
                            AppSession.usermessages.add(msg);
                            adapter.notifyDataSetChanged();
                            lv.setSelection(AppSession.usermessages.size() - 1);
                            // adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));

                            editText1.setText("");

                            new AddMessageToTask(getBaseContext()).execute();
                            return true;
                        }
                        else
                            return false;

                    }
                    return false;
                }
            });*/
       // }

    //    addItems();
    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }
    private static String encrypt(Context cont, String value) {
        try {
            return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        }
        catch (IllegalArgumentException e) {
            // TODO: handle exception
            return "";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void setToolbarTitle() {

        getSupportActionBar().setTitle("Paramètres");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

            // on déclare notre Broadcast Receiver
            Gson gson = new Gson();
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String json = mPrefs.getString("user", "");
            User obj = gson.fromJson(json, User.class);
            if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;

        // on déclare notre Broadcast Receiver
        Intent msgIntent = new Intent(this, RefreshTchattService.class);
        msgIntent.putExtra("idautreMembre",idautreMembre);
        startService(msgIntent);
        IntentFilter filter = new IntentFilter(MyReceiver.ACTION_NEW_MESSAGE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause ();
        // on désenregistre notre broadcast
        unregisterReceiver(receiver);
        Intent msgIntent1 = new Intent(this, RefreshTchattService.class);
        stopService(msgIntent1);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy ();
//        unregisterReceiver(receiver);

        Intent msgIntent1 = new Intent(this, RefreshTchattService.class);
        stopService(msgIntent1);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (  Integer.valueOf(android.os.Build.VERSION.SDK) < 7 //Instead use android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            // Take care of calling this method on earlier versions of
            // the platform where it doesn't exist.
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.

        Intent msgIntent1 = new Intent(this, RefreshTchattService.class);
        stopService(msgIntent1);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        this.finish();
        return;
    }


    private void addItems() {
        //   adapter.add(new OneMessage(true, "Hello bubbles!"));
        for(int i=0;i<AppSession.usermessages.size();i++)
        {
            boolean left = AppSession.usermessages.get(i).mes_mem_id_dest.equals(AppSession.currentUser.id) ? true : false;
           //adapter.add(new OneMessage(left, AppSession.usermessages.get(i).mes_subject,AppSession.usermessages.get(i).image_profil,AppSession.usermessages.get(i).DateMessage));
        }
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

       intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private void chooseGalleryimage() {
        showGalleryChooser();


    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true,"2");


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false,"3");

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled image recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }


        else if (requestCode == CAMERA_CAPTURE_GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
               // Bundle extras1 = data.getExtras();
                fileUri = data.getData();
               // filePath = getPath2(fileUri);
                Log.e("filehhhhhhh",String.valueOf(filePath));
                //filePath = getPath2(fileUri);
                // photo2=BitmapFactory.decodeFile(mImageCaptureUri);

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false,"2");

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled check image", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == CAMERA_CAPTURE_FILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Bundle extras1 = data.getExtras();
                fileUri = data.getData();
                Log.e("file",String.valueOf(fileUri));
                //filePath = getPath(fileUri);
                // photo2=BitmapFactory.decodeFile(mImageCaptureUri);

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false,"5");

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled check image", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 public String getPath2(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public void openGalleryAudio(){

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Audio "), CAMERA_CAPTURE_AUDIO_REQUEST_CODE);
    }
    private void showGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_GALERY);
       //< intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Terminer l'action avec "), CAMERA_CAPTURE_GALLERY_REQUEST_CODE);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_FILE);
      // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.setType("application/msword,application/pdf,application/audio,application/video,application/vnd.ms-powerpoint");
        //intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),CAMERA_CAPTURE_FILE_REQUEST_CODE);
    }

    private void launchUploadActivity(boolean isImage,String type){
        // txtEmojis.setText(newText);
      //  String type1=type;
       if((type.equals("2"))&&isImage==false)
        filePath=getPath2(fileUri);
       else  if((type.equals("5"))&&isImage==false)
           filePath = FilePath.getPath(this, fileUri);
        else
            filePath=fileUri.getPath();

      /*  if(type.equals("5")){
            if(filePath.toString().contains(".jpg") || filePath.toString().contains(".jpeg") || filePath.toString().contains(".png")) {
                // JPG file
                type="2";
            }
            else if (filePath.toString().contains(".3gp") || filePath.toString().contains(".mpg") || filePath.toString().contains(".mpeg") || filePath.toString().contains(".mpe") || filePath.toString().contains(".mp4") || filePath.toString().contains(".avi")) {
                // Word document
                type="3";
            }
            else if(filePath.toString().contains(".wav") || filePath.toString().contains(".mp3")) {
                // WAV audio file
               type="4";
            }
            else  type="5";
        }*/



///Log.e("file urlllllll",filePath.toString());

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        int minute = c.get(Calendar.MINUTE);
        //12 hour format
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String date =String.valueOf(hour) + ":" + String.valueOf(minute);
        // message = editText1.getText().toString();
            Message msg=new Message();
            msg.idMemAutreMembre=idautreMembre;
            msg.mes_mem_id_dest=idautreMembre;
            msg.mes_mem_id_exp=AppSession.currentUser.id;
            msg.mes_file_url=filePath;
            msg.mes_subject="";

            msg.mes_type=type;
            msg.image_profil=AppSession.currentUser.Logo.thumb_file_url;
            msg.DateMessage=date;
            AppSession.usermessages.add(msg);
          adapter.notifyDataSetChanged();
         //   lv.setSelection(AppSession.usermessages.size() - 1);
            // adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));
     View wantedView = adapter.getView(AppSession.usermessages.size()-1, null, lv);


        progressBar=(ProgressBar)wantedView.findViewById(R.id.progressBar);
       txtPercentage=(TextView)wantedView.findViewById(R.id.txtPercentage);
        imgPreview = (ImageView) wantedView.findViewById(R.id.imgPreview);
        vidPreview = (VideoView) wantedView.findViewById(R.id.videoPreview);
       // String type1=isImage?"2":"3";
            if (filePath != null) {
                // Displaying the image or video on the screen
                Log.e("hhhh",filePath);
                previewMedia(isImage);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
            }
        new UploadFileToServer().execute(type);
           // new AddMessageToTask(getBaseContext()).execute();




        /*Intent i = new Intent(DiscussActivity.this, UploadFileActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);*/
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {


        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }
       else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "AUD_" + timeStamp + ".mp3");
        }
      else if (type == MEDIA_TYPE_GALERY) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
      /*else   if (type == MEDIA_TYPE_FILE) {
            mediaFile = new File(fileUri.getPath());
        }*/
        else {
            return null;
        }

        return mediaFile;
    }
    class LoadMoreMessagesListener implements AbsListView.OnScrollListener {

        private int mLastFirstVisibleItem;
        private boolean mIsScrollingUp;
        public Boolean fetching = false;

        public LoadMoreMessagesListener() {
            super();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
           /* if(mLastFirstVisibleItem<firstVisibleItem)
            {
                Log.e("SCROLLING DOWN","TRUE");
            }
            if(mLastFirstVisibleItem>firstVisibleItem)
            {
                if (AppSession.usermessages.size() < AppSession.currentUserMessagesNumber) {
                    new LoadMoreMessagesTask(view.getContext(),view, this).execute();
                }
                Log.e("SCROLLING UP","TRUE");
            }
            mLastFirstVisibleItem=firstVisibleItem;*/
        }

        @Override
        public void onScrollStateChanged(AbsListView listView, int scrollState) {

                final int currentFirstVisibleItem = listView.getFirstVisiblePosition();
            int first = listView.getFirstVisiblePosition();
            int count = listView.getChildCount();
            if (!fetching) {
                if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                    mIsScrollingUp = false;
                } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                    mIsScrollingUp = true;
                }
                if ((scrollState == SCROLL_STATE_IDLE && mIsScrollingUp == true)) {
                    fetching = true;
                    if (AppSession.usermessages.size() < AppSession.currentUserMessagesNumber) {
                        new LoadMoreMessagesTask(listView.getContext(), listView, this).execute();
                    }
                }
                mLastFirstVisibleItem = currentFirstVisibleItem;
                Log.e("scrolling up", String.valueOf(mIsScrollingUp));
            }
          /*  if (!fetching) {
               // int first = listView.getFirstVisiblePosition();
               //int count = listView.getChildCount();
                int last=listView.getLastVisiblePosition();

                if ((scrollState == SCROLL_STATE_IDLE && listView.getFirstVisiblePosition()>= listView.getCount() - 1 )
                        || first+count >= AppSession.usermessages.size()) {
                    fetching = true;
                    if (AppSession.usermessages.size() < AppSession.currentUserMessagesNumber) {
                     //   new LoadMoreMessagesTask(listView.getContext(), listView, this).execute();
                    }
                }
            }*/
        }

    }

    class LoadMoreMessagesTask extends ApiTask<Void, Void, MessagesResult> {
        private AbsListView listView;
        private Boolean isAlert;
        private LoadMoreMessagesListener LoadMoreMessagesListener;

        LoadMoreMessagesTask(Context context, AbsListView _listView, LoadMoreMessagesListener _LoadMoreMessagesListener) {
            super(context, R.string.dialog_loading_more_messages);
            listView = _listView;
            LoadMoreMessagesListener = _LoadMoreMessagesListener;
        }

        protected MessagesResult doInBackground(Void... voids) {
            try {
                AppSession.currentPageMessage++;

                return AppSession.getApi(context).UserMessagesList(AppSession.currentPageMessage,"",idautreMembre);

            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(MessagesResult searchMessagesResult) {
            if (searchMessagesResult != null) {
                for(int i=0;i<searchMessagesResult.messages.size();i++)
                {
                    AppSession.usermessages.add(0,searchMessagesResult.messages.get(i));
                }
                //AppSession.usermessages.addAll(searchMessagesResult.messages);

                //((DiscussAdapter) listView.getAdapter()).notifyDataSetChanged();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    listView.setSelection(0);
                   /* adapter = new DiscussAdapter(context, searchMessagesResult.messages);
                    // listview = (ListView) findViewById(R.id.listingsList);
                    lv.setAdapter(adapter);*/
                    LoadMoreMessagesListener.fetching = false;


                }

               /* for(int i=0;i<searchMessagesResult.messages.size();i++)
                {
                 //   boolean left = searchMessagesResult.messages.get(i).mes_mem_id_dest.equals(AppSession.currentUser.id) ? true : false;
                  //  adapter.add(new OneMessage(left, searchMessagesResult.messages.get(i).mes_subject,searchMessagesResult.messages.get(i).image_profil,searchMessagesResult.messages.get(i).DateMessage));
                }*/
            } else {
                showError();
            }
            super.onPostExecute(searchMessagesResult);
        }
    }

    //liste des messages avec un autre membre
    class AddMessageToTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String mesg;
        String type;
        String file_url;
        AddMessageToTask(Context context) {
            super(context);

        }

        protected Boolean doInBackground(String... criteria) {
            this.mesg=criteria[0];
            this.type=criteria[1];
            this.file_url=criteria[2];
            try {


                String base64String = encrypt(context, this.mesg);
                Boolean messageResult = AppSession.getApi(context).addMessagesTo(this.mesg,idautreMembre,this.type, this.file_url);
             /*   if (visitesResult.visites.size() == 0|| visitesResult.visites==null) {
                    throw new SJBException("NOTHING_FOUND");
                }*/
                return messageResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;

        }}
        protected void onPostExecute(Boolean messageResult) {
            if (messageResult) {
                Log.e("sucess","Message is sent with sucess");
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                //showError();
            super.onPostExecute(messageResult);
            Toast.makeText(getApplicationContext(),"Votre message ne peut pas etre envoyé",Toast.LENGTH_SHORT);
            Log.e("error","Message is not sent with sucess");
        }
        }
    }


    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            Log.e("hhhh","here");
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            Log.e("hhhh","here2");
            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
           // adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... params) {

            return uploadFile(params[0]);
        }


        private String uploadFile(String type) {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
          //  Log.e("url",AppSession.getConfig(getApplicationContext()).FILE_UPLOAD_URL);
            try {
              /*  AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });*/

                File sourceFile = new File(filePath);

                MultipartEntity entity = new MultipartEntity(
                        HttpMultipartMode.BROWSER_COMPATIBLE);
                Log.e("filepath22",filePath);
                entity.addPart("function", new StringBody("uploadMessage_file"));
                //  reqEntity.addPart("base64", new StringBody(data.get(1)));
                entity.addPart("user", new StringBody((AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
                entity.addPart("type", new StringBody(type));
               // entity.addPart("photo", new StringBody("img_"+System.currentTimeMillis() +"_"+AppSession.currentUser.id+ ".png"));


                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                /*entity.addPart("website",
                        new StringBody("www.androidhive.info"));
                entity.addPart("email", new StringBody("abc@gmail.com"));*/

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
               // new AddMessageToTask(getBaseContext()).execute("",type,filePath);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    new AddMessageToTask(getBaseContext()).execute("",type,filePath);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
       // adapter.notifyDataSetChanged();
    }

    public class MyReceiver extends BroadcastReceiver {
    public static final String ACTION_NEW_MESSAGE ="chorus.chorus.com.chorus.action.NEW_MESSAGE";

        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
                //Toast.makeText(DiscussActivity.this, "new message", Toast.LENGTH_SHORT).show();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    lv.setSelection(AppSession.usermessages.size() - 1);

            }

        }
    }
}
