package chorus.chorus.com.chorus;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by hp on 25/11/2016.
 */

public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    LayoutInflater layoutInflater;
    private AlertDialog dialog;
    private int slectedpos;
    public int imageTotal;
    private Membre member;
    ArrayList<ContactObjet> objects;
    /*  public static String[] mThumbIds = {
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
              "http://192.168.1.34/chorue/"+ "/" + AppSession.currentUser.Logo.thumb_file_url,
      };
  */
    public ContactAdapter(Context c,ArrayList<ContactObjet> contacts) {
        mContext = c;
        objects=contacts;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }


    public long getItemId(int i) {
        return i;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
      slectedpos=position;
        Log.e("position is",String.valueOf(position));
        if (view == null) {

            view = layoutInflater.inflate(R.layout.contact_item, parent, false);
        }
          /*  int j=position%3;
            if(j==1)
            view = layoutInflater.inflate(R.layout.contact_item_down, parent, false);
            else*/
        //   view = layoutInflater.inflate(R.layout.contact_item, parent, false);

      member = getMember(position);
        String type=getType(position);
        Log.e("color",type);
//        ContactAdapter.DownloadImageTask t=      new ContactAdapter.DownloadImageTask((ImageView) convertView.findViewById(R.id.image));
        //  t.execute(mContext.getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url);
        CircularImageView imagev=(CircularImageView) view.findViewById(R.id.image);

        new DownloadImageTask(imagev)
                .execute(mContext.getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url);
        TextView fisrtname=(TextView) view.findViewById(R.id.firstname);
        // TextView lastname=(TextView) view.findViewById(R.id.lastname);
        fisrtname.setText(member.username);
        ImageView image_logged=(ImageView) view.findViewById(R.id.image_logged);
        if(member.isConnected())
            image_logged.setImageResource(R.drawable.icon_logged);
        else
            image_logged.setImageResource(R.drawable.icon_notlogged);
        // lastname.setText(member.lastname);
       imagev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // AppSession.currentMember= (Membre) getItem(position);
                  Membre membre=(Membre)getMember(position);
                        Double distance= distance(Double.parseDouble(AppSession.currentUser.latitude),Double.parseDouble(AppSession.currentUser.longitude),Double.parseDouble(membre.latitude),Double.parseDouble(membre.longitude));
                        membre.distance= String.valueOf(distance);
                        AppSession.currentMember=membre;
                        Intent intent = new Intent(mContext, MemberProfilContactActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                        //mContext.overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        //final String[] items = new String[]{"Supprimer", "Gallery"};
        final String[] items = new String[]{"Supprimer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

      //  builder.setTitle("Importer une photo depuis");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {

                    new deleteContactTask(mContext).execute(AppSession.currentMember.getId());

                }
            }
        });

       dialog = builder.create();
        imagev.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                AppSession.currentMember = getMember(position);
                dialog.show();

                return true;    // <- set to true
            }
        });
        // imageView.setImageResource(R.drawable.agenda);
        return view;
    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private Membre getMember(int position) {
        ContactObjet contact=(ContactObjet)getItem(position);
        return ((Membre) contact.getMembre());

    }
    private String getType(int position) {
        ContactObjet contact=(ContactObjet)getItem(position);
        return contact.type ;

    }
    public double distance(Double latitude, Double longitude, double e, double f) {
        double d2r = Math.PI / 180;

        double dlong = (longitude - f) * d2r;
        double dlat = (latitude - e) * d2r;
        double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(e * d2r)
                * Math.cos(latitude * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367 * c;
        return d/1000;

    }
    class deleteContactTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idfav;
        deleteContactTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idfav = credentials[0];
                Boolean result = AppSession.getApi(context).deleteFromContact(this.idfav);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {


                //     favAdapter.currentmemb.isContacts="0";
                AppSession.currentMember.isContacts="0";
                AppSession.contacts.remove(slectedpos);
                PageFragment.contactAdapter.notifyDataSetChanged();
                //contactAdapter.notifyDataSetChanged();


            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Membre retiré de vos contacts", Toast.LENGTH_SHORT).show();

            super.onPostExecute(isadded);
        }

    }
}