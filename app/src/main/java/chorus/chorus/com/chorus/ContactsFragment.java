package chorus.chorus.com.chorus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by hp on 29/11/2016.
 */

public class ContactsFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private int slectedpos;
    private ContactAdapter contactAdapter;
    // newInstance constructor for creating fragment with arguments
    public static ContactsFragment newInstance(int page, String title) {
        ContactsFragment contactsFragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        contactsFragment.setArguments(args);
        return contactsFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = "Contacts";

      //  new ContactsFragment.ContactRequestTask(getContext()).execute();

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int sizec;
        if(AppSession.contacts!=null) {
            sizec = AppSession.contacts.size();
        }
        else{sizec=0;}
        if (sizec !=0) {

            view = inflater.inflate(R.layout.fragment_contacts, container, false);
            final GridView gridview = (GridView) view.findViewById(R.id.gridview);

         contactAdapter = new ContactAdapter(getContext(), AppSession.contacts);
            gridview.setAdapter(contactAdapter);
            // TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
            // tvLabel.setText(title);}
            gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long arg3) {

                    slectedpos=position;
                    AppSession.currentMember=AppSession.contacts.get(position).getMembre();
                    registerForContextMenu(view);
                    //   startImageGalleryActivity(position);
                    Log.e("hhhhhh","hhhhhhhhhhhhhhhhhhhhhhhhhh");

                    return false;
                }
            });
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Membre membre=AppSession.contacts.get(position).getMembre();
                    Double distance= distance(Double.parseDouble(AppSession.currentUser.latitude),Double.parseDouble(AppSession.currentUser.longitude),Double.parseDouble(membre.latitude),Double.parseDouble(membre.longitude));
                    membre.distance= String.valueOf(distance);
                    AppSession.currentMember=membre;
                    Intent intent = new Intent(getContext(), MemberProfilContactActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    getContext().startActivity(intent);
                   getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });

        } else {

            view = inflater.inflate(R.layout.no_data_item, container, false);
            final TextView noData = (TextView) view.findViewById(R.id.nodata);
            noData.setText("Aucun contact");

        }
        return view;
    }
    class ContactRequestTask extends ApiTask<Void, Void,ContactsList> {
        //  String idmember;
        ContactRequestTask(Context context) {
            super(context,R.string.dialog_loading_contacts);
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
            if (isadded != null) {
                AppSession.contacts = isadded.contacts;
                AppSession.currentcontactsNumber = isadded.listingsNumber;
                Intent i = new Intent(this.context,ContactsManagerActivity.class);
                startActivity(i);


            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
            }
            super.onPostExecute(isadded);
        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater =  getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_supprimer, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                new deleteContactTask(getContext()).execute(AppSession.currentMember.getId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    class deleteContactTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idfav;
        deleteContactTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idfav = credentials[0];
                Boolean result = AppSession.getApi(context).deleteFromContact(this.idfav);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {


                FavorisAdapter.currentmemb.isContacts="0";
                AppSession.contacts.remove(slectedpos);
                contactAdapter.notifyDataSetChanged();


            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Membre retiré de vos contacts", Toast.LENGTH_SHORT).show();

            super.onPostExecute(isadded);
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