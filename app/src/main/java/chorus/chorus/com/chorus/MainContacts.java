package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.gson.Gson;

public class MainContacts extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

                

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if (obj != null && mPrefs.getBoolean("islogin", true)) AppSession.currentUser = obj;
        if(AppSession.favoris==null)
            new favRequestTask(this).execute();
        if(AppSession.contacts==null)
            new ContactRequestTask(this).execute();
        if(AppSession.invitations==null)
            new InvitRequestTask(this).execute();



        //String json2 = mPrefs.getString("membre", "");
        // Membre obj2 = gson.fromJson(json2, Membre.class);
        // Log.e("membre id",obj2.id);

    }
    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Contacts");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //  getSupportActionBar().setDisplayUseLogoEnabled(true);
        //  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logogris);
        // getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        //   getSupportActionBar().setIcon(R.drawable.globe);
    }
    class InvitRequestTask extends ApiTaskNoDialog<Void, Void,InvitationsList> {
        //  String idmember;
        InvitRequestTask(Context context) {
            super(context);
        }

        protected InvitationsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                InvitationsList result = AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(InvitationsList isadded) {
            if (isadded!=null) {
                AppSession.invitations=null;
                AppSession.invitations=isadded.invitations;
                AppSession.currentinvitationNumber=isadded.listingsNumber;





            }

            super.onPostExecute(isadded);
        }

    }
    class ContactRequestTask extends ApiTaskNoDialog<Void, Void,ContactsList> {
        //  String idmember;
        ContactRequestTask(Context context) {
            super(context);
        }

        protected ContactsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                ContactsList result = AppSession.getApi(context).contactRequest(AppSession.currentContactPage);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(ContactsList isadded) {
            if (isadded!=null) {
                AppSession.contacts=null;
                AppSession.contacts = isadded.contacts;
                AppSession.currentcontactsNumber = isadded.listingsNumber;

            }
            super.onPostExecute(isadded);
          /*  Intent i=new Intent(getContext(),ContactsManagerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);*/
          /*  Intent i=new Intent(context,MainContacts.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
           overridePendingTransition(R.anim.enter, R.anim.exit);*/
        }
    }
    class favRequestTask extends ApiTaskNoDialog<Void, Void,FavorisList> {
        //  String idmember;
        favRequestTask(Context context) {
            super(context);
        }

        protected FavorisList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                FavorisList  result = AppSession.getApi(context).favRequest(AppSession.currentfavPage);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(FavorisList isadded) {
            if (isadded!=null) {
                AppSession.favoris=null;

                AppSession.favoris=isadded.favoris;
                AppSession.currentfavorisNumber=isadded.listingsNumber;

            }
            super.onPostExecute(isadded);

        }

    }
}