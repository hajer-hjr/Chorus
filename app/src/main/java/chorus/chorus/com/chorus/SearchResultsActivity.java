package chorus.chorus.com.chorus;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 20/05/2016.
 */
public class SearchResultsActivity  extends AppCompatActivity {
    private String opt_tri;
    private ListView listview;
    private Toolbar toolbar;
    private SearchResultsAdapter searchResultsAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppSession.members.size() <= 0)
        {
            setContentView(R.layout.aucun_resultat);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setToolbarTitle();
        }
        else {
            setContentView(R.layout.search_results);
            searchResultsAdapter = new SearchResultsAdapter(this, AppSession.members, false);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setToolbarTitle();




        listview = (ListView) findViewById(R.id.listingsList);
         searchResultsAdapter = new SearchResultsAdapter(this, AppSession.members, false);
        listview.setAdapter(searchResultsAdapter);

    }}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profil, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:

                return true;
            case R.id.action_search:

                return true;
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return true;
        }
    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    private void setToolbarTitle() {

        // getSupportActionBar().setTitle("Mon profil");
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
  /*  public void goToSaveAlertPage(View view) {
        if (AppSession.currentUser == null) {
            Toast.makeText(this, R.string.error_save_job_alert_not_signed_in, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, SaveAlertActivity.class);
            startActivity(intent);
        }
    }*/
  class TriMemberListeTask extends ApiTaskNoDialog<String, Void, SearchMemberResult> {
      TriMemberListeTask(Context context) {
          super(context);
      }

      protected SearchMemberResult doInBackground(String... criteria) {
         try{
            AppSession.currentPage=1;

          return AppSession.getApi(context).searchMember(AppSession.currentPage, AppSession.currentSearchCriteria);


      } catch (SJBException e) {
          error = e.getMessage();
          return null;
      }
      }

      protected void onPostExecute(SearchMemberResult searchJobsResult) {
        if(searchJobsResult!=null){
            AppSession.members=null;
          AppSession.members=SearchMemberResult.members;

           // ((SearchResultsAdapter) listview.getAdapter()).notifyDataSetChanged();
            searchResultsAdapter = new SearchResultsAdapter(context, SearchMemberResult.members,false);
            // listview = (ListView) findViewById(R.id.listingsList);
            listview.setAdapter(searchResultsAdapter);
            searchResultsAdapter.notifyDataSetChanged();
      } else {
          showError();
      }
      super.onPostExecute(searchJobsResult);
  }
  }
}

class LoadMoreJobsListener implements AbsListView.OnScrollListener {

    private Boolean isAlert;
    public Boolean fetching = false;

    public LoadMoreJobsListener(Boolean _isAlert) {
        super();
        isAlert = _isAlert;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        if (!fetching) {
            int first = listView.getFirstVisiblePosition();
            int count = listView.getChildCount();

            if ((scrollState == SCROLL_STATE_IDLE && listView.getLastVisiblePosition() >= listView.getCount() - 1)
                    || first + count >= AppSession.members.size()) {
                fetching = true;
                    if (AppSession.members.size() < AppSession.currentMembersNumber) {
                    new LoadMoreMembersTask(listView.getContext(), listView, isAlert, this).execute();
                }
            }
        }
    }
}

class LoadMoreMembersTask extends ApiTask<Void, Void, SearchMemberResult> {
    private AbsListView listView;
    private Boolean isAlert;
    private LoadMoreJobsListener LoadMoreMembersListener;

    LoadMoreMembersTask(Context context, AbsListView _listView, Boolean _isAlert, LoadMoreJobsListener _loadMoreJobsListener) {
        super(context, R.string.dialog_loading_more_jobs);
        listView = _listView;
        isAlert = _isAlert;
        LoadMoreMembersListener = _loadMoreJobsListener;
    }

    protected SearchMemberResult doInBackground(Void... voids) {
        try {
            AppSession.currentPage++;

                return AppSession.getApi(context).searchMember(AppSession.currentPage, AppSession.currentSearchCriteria);

        } catch (SJBException e) {
            error = e.getMessage();
            return null;
        }
    }

    protected void onPostExecute(SearchMemberResult searchJobsResult) {
        if (searchJobsResult != null) {
            AppSession.members.addAll(SearchMemberResult.members);

            ((SearchResultsAdapter) listView.getAdapter()).notifyDataSetChanged();

            LoadMoreMembersListener.fetching = false;
        } else {
            showError();
        }
        super.onPostExecute(searchJobsResult);
    }
}

