package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class ContactsManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_manager);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        MyPagerAdapter adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: // Fragment # 0 - This will show FirstFragment

                    case 1: // Fragment # 0 - This will show FirstFragment different title
                        //new InvitRequestTask(getApplicationContext()).execute();
                    case 2: // Fragment # 1 - This will show SecondFragment
                     //   new favRequestTask(getApplicationContext()).execute();
                    default:
                     //   new ContactsManagerActivity.ContactRequestTask(getApplicationContext()).execute();


            }}

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


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return ContactsFragment.newInstance(1, "Page # 2");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return InvitationsFragment.newInstance(0, "Page # 1");
                case 2: // Fragment # 1 - This will show SecondFragment
                    return FavorisFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return "CONTACTS ";
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return "INVITATIONS";
                case 2: // Fragment # 1 - This will show SecondFragment
                    return "FAVORIS";
                default:
                    return null;
            }
        }

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
        public void onPostExecute(InvitationsList isadded) {


            if (isadded!=null) {
                AppSession.invitations.clear();

                AppSession.invitations=isadded.invitations;
                AppSession.currentinvitationNumber=isadded.listingsNumber;

            }
            Intent intent=new Intent(getApplicationContext(),ContactsManagerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
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

        public void onPostExecute(ContactsList isadded) {

            if (isadded != null) {
                AppSession.contacts.clear();
                AppSession.contacts = isadded.contacts;
                AppSession.currentcontactsNumber = isadded.listingsNumber;
            }
            super.onPostExecute(isadded);
        }

    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return true;
        }
    }
    class favRequestTask extends ApiTaskNoDialog<Void, Void, FavorisList> {
        //  String idmember;
        favRequestTask(Context context) {
            super(context);
        }

        protected FavorisList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                FavorisList result = AppSession.getApi(context).favRequest(AppSession.currentfavPage);
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

        public void onPostExecute(FavorisList isadded) {

            if (isadded != null) {
                AppSession.favoris.clear();
                AppSession.favoris = isadded.favoris;
                AppSession.currentfavorisNumber = isadded.listingsNumber;


            }
            super.onPostExecute(isadded);
        }

    }





}