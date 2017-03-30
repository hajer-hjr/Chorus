package chorus.chorus.com.chorus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by pc on 21/07/2016.
 */
public class MessagesUserListeTask extends ApiTaskNoDialog<String, Void, MessagesResult> {
    //liste des messages avec un autre membre
        private String idautre;
        private String typeAff;

        MessagesUserListeTask(Context context) {
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
                AppSession.usermessages .clear();
                for(int i=0;i<messagesResult.messages.size();i++)
                {
                    AppSession.usermessages.add(0,messagesResult.messages.get(i));
                }
              //  AppSession.usermessages = messagesResult.messages;
                AppSession.currentUserMessagesNumber = messagesResult.listingsNumber;


            } else {
                AppSession.usermessages .clear();
                AppSession.currentUserMessagesNumber = 0;
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
               // showError();
            }
            Intent intent = new Intent(context, DiscussActivity.class);
            intent.putExtra("idautreMembre",idautre);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            // .overridePendingTransition(R.anim.enter, R.anim.exit);
           context.startActivity(intent);

            super.onPostExecute(messagesResult);
        }
    }

