package chorus.chorus.com.chorus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 19/07/2016.
 */
public class MesMessagesActivity extends AppCompatActivity {
    public String typeAff;
    private Toolbar toolbar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    // private  Spinner spin;
    private ListView listview;
    private MessagesBaseAdapter messagesAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.messages_results);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // seekBar=(RangeSeekBar)findViewById(R.id.rangeSeekbar) ;
        setSupportActionBar(toolbar);
        setToolbarTitle();
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if (obj != null && mPrefs.getBoolean("islogin", true)) AppSession.currentUser = obj;
        Intent intent = getIntent();
        Boolean isAlert = intent.getBooleanExtra("isAlert", false);
        String membersWord = AppSession.currentMessagesNumber == 1 ? getResources().getString(R.string.search_results_visit_found) : getResources().getString(R.string.search_results_visits_found);
      /*  Spinner spin=(Spinner)findViewById(R.id.type_aff);

        final List<String> type_aff=new ArrayList<String>();
        type_aff.add("Tous");
        type_aff.add("Non lus");
        type_aff.add("En attente de réponse");
       // type_aff.add("Lus");
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_aff);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin.setAdapter(adapter);
        //spin.setPrompt("-----------");
            spin.setSelection(0);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    typeAff="tous";
                }
                else if (position == 1) {
                    typeAff="non_lus";
                }
                else {
                    typeAff="non_rep";

                }
                new UpdateMessagesListeTask(getApplicationContext()).execute(typeAff);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/
        new UpdateMessagesListeTask(getApplicationContext()).execute("");

        messagesAdapter = new MessagesBaseAdapter(this, AppSession.messages);
        listview = (ListView) findViewById(R.id.listingsList);
        listview.setAdapter(messagesAdapter);
        listview.setItemsCanFocus(true);
        listview.setOnScrollListener(new LoadMoreJobsListener(isAlert));

    }
    private void setToolbarTitle() {

       // getSupportActionBar().setTitle("Paramètres");
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
    }
    class LoadMoreMessagesListener implements AbsListView.OnScrollListener {

        private Boolean isAlert;
        public Boolean fetching = false;

        public LoadMoreMessagesListener(Boolean _isAlert) {
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
                        || first + count >= AppSession.messages.size()) {
                    fetching = true;
                    if (AppSession.messages.size() < AppSession.currentMessagesNumber) {
                        new LoadMoreMessagesTask(listView.getContext(), listView, this).execute();
                    }
                }
            }
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
                AppSession.currentPage++;

                return AppSession.getApi(context).UserMessagesList(AppSession.currentPage,typeAff,"");

            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(MessagesResult searchMessagesResult) {
            if (searchMessagesResult != null) {
                AppSession.messages.addAll(searchMessagesResult.messages);

                ((SearchResultsAdapter) listView.getAdapter()).notifyDataSetChanged();

                LoadMoreMessagesListener.fetching = false;
            } else {
                showError();
            }
            super.onPostExecute(searchMessagesResult);
        }
    }
    class UpdateMessagesListeTask extends ApiTaskNoDialog<String, Void, MessagesResult> {
        UpdateMessagesListeTask(Context context) {
            super(context);
        }

        protected MessagesResult doInBackground(String... criteria) {
            try {
                AppSession.currentPage = 1;
                MessagesResult messagesResult = AppSession.getApi(context).UserMessagesList(AppSession.currentPage, criteria[0],"");
                if (messagesResult.messages.size() == 0) {
                    throw new SJBException("NOTHING_FOUND");
                }
                return messagesResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(MessagesResult messagesResult) {
            if (messagesResult != null) {
                AppSession.messages = messagesResult.messages;
                AppSession.currentMessagesNumber = messagesResult.listingsNumber;
                if (messagesAdapter != null) {
                    messagesAdapter.notifyDataSetChanged();

                    messagesAdapter = new MessagesBaseAdapter(context,messagesResult.messages);
                    // listview = (ListView) findViewById(R.id.listingsList);
                    listview.setAdapter(messagesAdapter);


                }
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
               // showError();
            }
            super.onPostExecute(messagesResult);
        }
    }

}
