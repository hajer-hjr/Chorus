package chorus.chorus.com.chorus;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

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

            InvitationsList result=   AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);
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
