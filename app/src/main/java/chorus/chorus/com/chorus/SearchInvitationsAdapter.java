package chorus.chorus.com.chorus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StringLoader;
import com.loopj.android.image.SmartImageView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by hp on 21/11/2016.
 */

public class SearchInvitationsAdapter extends BaseAdapter {
    private static final String LOG_TAG ="SearchInvitationsAdapter";
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<InvitObject> objects;
    Boolean deletable;
    Boolean editMode;

    SearchInvitationsAdapter(Context _context, ArrayList<InvitObject> invitations, Boolean _deletable) {
        context = _context;
        objects = invitations;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        deletable = _deletable;
        editMode = false;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("LongLogTag")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.searchinvitation_item, parent, false);
        }


        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Membre membre=getMember(position);
               Double distance= distance(Double.parseDouble(AppSession.currentUser.latitude),Double.parseDouble(AppSession.currentUser.longitude),Double.parseDouble(membre.latitude),Double.parseDouble(membre.longitude));
                membre.distance= String.valueOf(distance);
                AppSession.currentMember = membre;

/*
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(AppSession.currentMember);
                prefsEditor.putString("membre", json);
                prefsEditor.commit();
*/

                Activity activity = (Activity) context;

                Intent intent = new Intent(context, InvitationProfilActivity.class);
                InvitObject in=AppSession.invitations.get(position);
               String id_invit=String.valueOf(in.id);
                intent.putExtra("type",in.type);
                intent.putExtra("idInvit",id_invit);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                activity.finish();

            }

        });

        Membre member = getMember(position);
        String username = "";
        if (member.username != null) {
            if (member.username.length() < 10)
                username = member.username;
            else
                username = member.username.substring(0, 10) + "...";
        }
        ImageView img=(ImageView)view.findViewById(R.id.Butt);
        InvitObject in=(InvitObject)getItem(position);
        if (in.type.equals("1"))
            img.setImageResource(R.drawable.heart_rose);

        else
        img.setImageResource(R.drawable.plus_vert);



       // CircularImageView imageLogo = (CircularImageView) view.findViewById(R.id.logo);

        if (member.Logo.thumb_file_url != null) {
            //Log.e("logo url",context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
            // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
            // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/"+member.Logo.thumb_file_url);
          /*  Glide.with(this.context).load(context.getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url)
                    .bitmapTransform(new CircleTransform(context))
                    .into(imageLogo);*/
            CircularImageView imagem=  (CircularImageView) view.findViewById(R.id.logo);
            if(in.type.equals("1")){
                imagem.setBorderColor(Color.parseColor("#FF4081"));}
            else{
                imagem.setBorderColor(Color.parseColor("#26dfac"));
            }

            DownloadImageTask t=         new DownloadImageTask(imagem);
            t.execute(context.getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url);
           // imageLogo.setVisibility(View.VISIBLE);
        } else {
            //imageLogo.setVisibility(View.GONE);
        }
        final SearchInvitationsAdapter adapter = this;
        return view;
    }

    private Membre getMember(int position) {
        InvitObject invit=(InvitObject)getItem(position);
        return ((Membre) invit.getMembre());
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
}