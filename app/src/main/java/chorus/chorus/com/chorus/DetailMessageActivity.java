package chorus.chorus.com.chorus;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;

import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;



/**
 * Created by pc on 20/07/2016.
 */
public class DetailMessageActivity extends MenuWrappAcivity {
    private String idautreMembre;
    private String message;
    private DetailMessageAdapter adapter;
    private ListView lv;
    private EditText editText1;
    private static Random random;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_message);
        Intent intent = getIntent();
        idautreMembre= intent.getStringExtra("idautreMembre");
        random = new Random();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new MessagesUserListeTask1(getApplicationContext()).execute("",  idautreMembre);
            }
        }, 0, 1000);
/*if(AppSession.currentUser.getAboDateFin()!=true)
{

    AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
    builder.setTitle("Abonnement");
    builder.setMessage("Seuls les abonnés peuvent écrire aux autres membres.\n Voulez-vous vous abonner? ")
            .setCancelable(false)
            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" +"abonnement/ajouter"));
                    startActivity(browserIntent);
                }
            })
            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
    AlertDialog alert = builder.create();
    alert.show();
}*/

        lv = (ListView) findViewById(R.id.listView1);

        adapter = new DetailMessageAdapter(getApplicationContext(), R.layout.detail_message_item);

        lv.setAdapter(adapter);
        lv.setItemsCanFocus(true);
        lv.setOnScrollListener(new LoadMoreMessagesListener());
      //  editText1 = (EditText) findViewById(R.id.editText1);
        /*if(AppSession.currentUser.getAboDateFin()!=true)
        {*/
   //editText1.setVisibility(View.GONE);
         /*     editText1.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                 // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        Calendar c = Calendar.getInstance();
                        int minute = c.get(Calendar.MINUTE);
                        //12 hour format
                        int hour = c.get(Calendar.HOUR);
                        String date = "à" + String.valueOf(hour) + "h" + String.valueOf(minute);
                        message = editText1.getText().toString();
                        if(! message.equals("")) {
                            adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));

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
     /*   }
        else {*/
            editText1.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        Calendar c = Calendar.getInstance((TimeZone.getTimeZone("Europe/Paris")));
                        int minute = c.get(Calendar.MINUTE);
                        //12 hour format
                        int hour = c.get(Calendar.HOUR);
                        String date = "à" + String.valueOf(hour) + "h" + String.valueOf(minute);
                        message = editText1.getText().toString();
                        adapter.add(new OneMessage(false, editText1.getText().toString(), AppSession.currentUser.Logo.thumb_file_url, date));
                        editText1.setText("");

                        new AddMessageToTask(getBaseContext()).execute();
                        return true;
                    }
                    return false;
                }
            });
        //}

        addItems();
    }

    private void addItems() {
     //   adapter.add(new OneMessage(true, "Hello bubbles!"));
        for(int i=0;i<AppSession.usermessages.size();i++)
        {
            boolean left = AppSession.usermessages.get(i).mes_mem_id_dest.equals(AppSession.currentUser.id) ? true : false;
            adapter.add(new OneMessage(left, AppSession.usermessages.get(i).mes_subject,AppSession.usermessages.get(i).image_profil,AppSession.usermessages.get(i).DateMessage));
        }
    }

    private static int getRandomInteger(int aStart, int aEnd) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        long range = (long) aEnd - (long) aStart + 1;
        long fraction = (long) (range * random.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }

    class LoadMoreMessagesListener implements AbsListView.OnScrollListener {


        public Boolean fetching = false;

        public LoadMoreMessagesListener() {
            super();
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
                        || first + count >= AppSession.usermessages.size()) {
                    fetching = true;
                    if (AppSession.usermessages.size() < AppSession.currentUserMessagesNumber) {
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
                AppSession.currentPageMessage++;

                return AppSession.getApi(context).UserMessagesList(AppSession.currentPageMessage,"",idautreMembre);

            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(MessagesResult searchMessagesResult) {
            if (searchMessagesResult != null) {
                AppSession.usermessages.addAll(searchMessagesResult.messages);

                ((DetailMessageAdapter) listView.getAdapter()).notifyDataSetChanged();
                LoadMoreMessagesListener.fetching = false;
                for(int i=0;i<searchMessagesResult.messages.size();i++)
                {
                    boolean left = searchMessagesResult.messages.get(i).mes_mem_id_dest.equals(AppSession.currentUser.id) ? true : false;
                    adapter.add(new OneMessage(left, searchMessagesResult.messages.get(i).mes_subject,searchMessagesResult.messages.get(i).image_profil,searchMessagesResult.messages.get(i).DateMessage));
                }
            } else {
                showError();
            }
            super.onPostExecute(searchMessagesResult);
        }
    }

    //liste des messages avec un autre membre
    class AddMessageToTask extends ApiTaskNoDialog<String, Void, Boolean> {
        AddMessageToTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... criteria) {
            try {
                Boolean messageResult = AppSession.getApi(context).addMessagesTo(message,idautreMembre,"","");
             /*   if (visitesResult.visites.size() == 0|| visitesResult.visites==null) {
                    throw new SJBException("NOTHING_FOUND");
                }*/
                return messageResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(Boolean messageResult) {
            if (messageResult != false) {
               Log.e("sucess","Message is sent with sucess");
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                //showError();
                Log.e("error","Message is not sent with sucess");
            }
            super.onPostExecute(messageResult);
        }
    }
    public class MessagesUserListeTask1 extends ApiTaskNoDialog<String, Void, MessagesResult> {
        //liste des messages avec un autre membre
        private String idautre;
        private String typeAff;
        MessagesUserListeTask1(Context context) {
            super(context);
        }

        protected MessagesResult doInBackground(String... criteria) {
            this.typeAff = criteria[0];
            this.idautre = criteria[1];
            try {
                AppSession.currentPageMessage = 1;
                MessagesResult messagesResult = AppSession.getApi(context).UserMessagesList(AppSession.currentPageMessage,typeAff,idautre);
             /*   if (visitesResult.visites.size() == 0|| visitesResult.visites==null) {
                    throw new SJBException("NOTHING_FOUND");
                }*/
                return messagesResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }
        protected void onPostExecute(MessagesResult messagesResult) {
            if (messagesResult != null) {
                AppSession.usermessages = messagesResult.messages;
                AppSession.currentUserMessagesNumber = messagesResult.listingsNumber;
             //   adapter.notifyDataSetChanged();

            } else {
                AppSession.usermessages .clear();;
                AppSession.currentUserMessagesNumber = 0;
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                // showError();
            }
        }
    }

}
