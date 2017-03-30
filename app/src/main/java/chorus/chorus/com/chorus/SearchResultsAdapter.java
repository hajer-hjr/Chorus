package chorus.chorus.com.chorus;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import com.loop.android.image.SmartImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.loopj.android.image.SmartImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchResultsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Membre> objects;
    Boolean deletable;
    Boolean editMode;
    View view;

    SearchResultsAdapter(Context _context, ArrayList<Membre> members, Boolean _deletable) {
        context = _context;
        objects = members;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
         view = convertView;
        String t=String.valueOf(getCount());
        Log.d("tetttnnnnnnnnnnnnnnnn",t);

        Membre member = getMember(position);
        if (view == null) {
            if(member.sexe.equals("F") )

                view = layoutInflater.inflate(R.layout.search_result_item_femme, parent, false);
            else
                view = layoutInflater.inflate(R.layout.search_result_item, parent, false);
            //  view = layoutInflater.inflate(R.layout.search_result_item, parent, false);
        }
     /*   if (getCount() == 1) {
            view.setBackgroundResource(R.drawable.rounded_listview_single);
        } else if (position == 0) {
            view.setBackgroundResource(R.drawable.rounded_listview_first);
        } else if (position == getCount() - 1) {
            view.setBackgroundResource(R.drawable.rounded_listview_last);
        } else {
            view.setBackgroundResource(R.drawable.rounded_listview_middle);
        }*/

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppSession.currentMember= (Membre) getItem(position);

/*
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(AppSession.currentMember);
                prefsEditor.putString("membre", json);
                prefsEditor.commit();
*/

                Activity activity = (Activity) context;

                if(AppSession.currentMember.isContacts.equals("1")) {
                    // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                    Intent intent = new Intent(context, MemberProfilContactActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                else
                {

                    Intent intent = new Intent(context, MemberProfilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);}
            }

        });
      // Membre member = getMember(position);
        String username = "";
        if (member.username!= null ) {
            if (member.username.length() < 10)
                username = member.username;
            else
                username = member.username.substring(0, 10) + "...";
        }
        RelativeLayout relative1=(RelativeLayout) view.findViewById(R.id.linearLayout1);
      /*  if(member.sexe.equals("F") )

        relative1.setBackgroundResource(R.drawable.bg_rose_phone);
        else
            relative1.setBackgroundResource(R.drawable.bg_green_phone);*/
        ((TextView) view.findViewById(R.id.username)).setText(member.username != null ? username + "," : "");
       ((TextView) view.findViewById(R.id.location)).setText(member.pays == null ? "" : member.pays);
        ((TextView) view.findViewById(R.id.age)).setText(toString().valueOf(member.getAge(member.date_naiss))+" ans");

       SmartImageView imageLogo = (SmartImageView) view.findViewById(R.id.logo);

        if (member.Logo.thumb_file_url != null) {
            //Log.e("logo url",context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
          // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
         // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/"+member.Logo.thumb_file_url);
            Glide.with(this.context).load(context.getResources().getString(R.string.api_base_url) +"/"+member.Logo.thumb_file_url)
                    .bitmapTransform(new CircleTransform(context))
                    .into(imageLogo);
            imageLogo.setVisibility(View.VISIBLE);
        } else {
            imageLogo.setVisibility(View.GONE);
        }
        final SearchResultsAdapter adapter = this;
        return view;
    }

    private Membre getMember(int position) {
        return ((Membre) getItem(position));
    }
    /*update membre visite*/
   /* class updatevisiteTask extends ApiTaskNoDialog<Void, Void, Boolean> {
        updatevisiteTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(Void... credentials) {
            try {
               Boolean isupdate= AppSession.getApi(context).updateVisite();
                //  Log.e("questionnaire",questionnaire.toString());
                return isupdate;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(boolean isupdate) {
            if (isupdate) {
                Log.e("is update","ok");
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
            }
            super.onPostExecute(isupdate);
        }
    }*/
}
/*class DeleteSavedJobTask extends ApiTask<Integer, Void, Boolean> {
    SearchResultsAdapter adapter;

    DeleteSavedJobTask(Context context, SearchResultsAdapter _adapter) {
        super(context, R.string.dialog_deleting);
        adapter = _adapter;
    }

    protected Boolean doInBackground(Integer... data) {
        try {
            int index = data[0];
            Boolean isDeleted = AppSession.getApi(context).deleteSavedJob(adapter.objects.get(index).id, AppSession.currentUser.sid);
            if (isDeleted) {
                adapter.objects.remove(index);
            }
            return isDeleted;
        } catch (SJBException e) {
            error = e.getMessage();
            return false;
        }
    }

    protected void onPostExecute(Boolean isDeleted) {
        if (isDeleted) {
            adapter.notifyDataSetChanged();
            Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
        } else {
            showError();
        }
        super.onPostExecute(isDeleted);
    }
}*/
