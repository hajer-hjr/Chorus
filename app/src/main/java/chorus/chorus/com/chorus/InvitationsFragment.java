package chorus.chorus.com.chorus;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

public class InvitationsFragment  extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private String opt_tri;
    private ListView listview;
    private Toolbar toolbar;
    private SearchInvitationsAdapter searchInvitationsAdapter;

    // newInstance constructor for creating fragment with arguments
    public static InvitationsFragment newInstance(int page, String title) {
        InvitationsFragment fragmentFirst = new InvitationsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = "Invitations";
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int sizec;
        if(AppSession.invitations!=null) {
            sizec = AppSession.invitations.size();
        }
        else{sizec=0;}
        if(sizec !=0){
            view = inflater.inflate(R.layout.invitations_fragment, container, false);
            // TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
            //  tvLabel.setText(title);
             searchInvitationsAdapter = new SearchInvitationsAdapter(getContext(), AppSession.invitations, false);

            listview = (ListView) view.findViewById(R.id.listingsList);
            searchInvitationsAdapter. notifyDataSetChanged();
            listview.setAdapter(searchInvitationsAdapter);
            listview.setItemsCanFocus(true);
            listview.setOnScrollListener(new LoadMoreJobsListener(false));

        }
        else{

            view = inflater.inflate(R.layout.no_data_item, container, false);
            final TextView noData = (TextView) view.findViewById(R.id.nodata);
            noData.setText("Aucune invitation");


        }



        return view;
    }



    class TriMemberListeTask extends ApiTaskNoDialog<String, Void, InvitationsList> {
        TriMemberListeTask(Context context) {
            super(context);
        }

        protected InvitationsList doInBackground(String... criteria) {
            try {
                AppSession.currentInvitPage = 1;

                return AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);


            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(InvitationsList invitations) {
            if (invitations != null) {
                AppSession.invitations = invitations.invitations;
                AppSession.currentinvitationNumber = invitations.listingsNumber;

                // ((SearchResultsAdapter) listview.getAdapter()).notifyDataSetChanged();


                SearchInvitationsAdapter   searchInvitationsAdapter = new SearchInvitationsAdapter(context, invitations.invitations, false);
                // listview = (ListView) findViewById(R.id.listingsList);
                searchInvitationsAdapter.notifyDataSetChanged();
                listview.setAdapter(searchInvitationsAdapter);

            } else {
                showError();
            }
            super.onPostExecute(invitations);
        }
    }


    class LoadMoreInvitationsTask extends ApiTask<Void, Void, InvitationsList> {
        private AbsListView listView;
        private Boolean isAlert;
        private LoadMoreJobsListener LoadMoreMembersListener;

        LoadMoreInvitationsTask(Context context, AbsListView _listView, Boolean _isAlert, LoadMoreJobsListener _loadMoreJobsListener) {
            super(context, R.string.dialog_loading_more_jobs);
            listView = _listView;
            isAlert = _isAlert;
            LoadMoreMembersListener = _loadMoreJobsListener;
        }

        protected InvitationsList doInBackground(Void... credentials) {
            try {
                AppSession.currentPage++;

                InvitationsList result = AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(InvitationsList searchInvit) {
            if (searchInvit != null) {
                AppSession.invitations.addAll(InvitationsList.invitations);

                ((SearchInvitationsAdapter) listView.getAdapter()).notifyDataSetChanged();

                LoadMoreMembersListener.fetching = false;
            } else {
                showError();
            }
            super.onPostExecute(searchInvit);
        }
    }
}