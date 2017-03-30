package chorus.chorus.com.chorus;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hp on 08/12/2016.
 */

public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    public View view;
    GridView gridview;
    ListView listview;
    private int slectedpos;
    static ContactAdapter contactAdapter;
    static FavorisAdapter favAdapter;
    static SearchInvitationsAdapter searchInvitationsAdapter;
   static ProgressBar bar;
    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
       /// new ContactRequestTask(getContext()).execute();

    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        switch(mPage){
            case 1:
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
                    contactAdapter.notifyDataSetChanged();
                    bar=(ProgressBar) view.findViewById(R.id.progressBar);
                 //   new ContactRequestTask(getContext()).execute();
                   // contactAdapter.notifyDataSetChanged();


               } else {

                    view = inflater.inflate(R.layout.no_data_item, container, false);

                    final TextView noData = (TextView) view.findViewById(R.id.nodata);
                    noData.setText("Aucun contact");

                }
                break;
            case 2:

                int size;
                if(AppSession.invitations!=null) {
                    size = AppSession.invitations.size();
                }
                else{size=0;}
                if(size !=0){
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

                break;
            case 3:
                int siz;
                if(AppSession.favoris!=null) {
                    siz = AppSession.favoris.size();
                }
                else{siz=0;}
                if (siz!=0) {

                    view = inflater.inflate(R.layout.fragment_favoris, container, false);
                    final GridView gridview = (GridView) view.findViewById(R.id.gridview);

                    favAdapter = new FavorisAdapter(getContext(), AppSession.favoris);
                    gridview.setAdapter(favAdapter);
                    // TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
                    // tvLabel.setText(title);}
                    gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                       int position, long arg3) {

                            slectedpos=position;
                            AppSession.currentMember=AppSession.favoris.get(position).getMembre();
                            registerForContextMenu(view);
                            //   startImageGalleryActivity(position);
                            return false;
                        }
                    });
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            Membre membre=AppSession.favoris.get(position).getMembre();
                            Double distance= distance(Double.parseDouble(AppSession.currentUser.latitude),Double.parseDouble(AppSession.currentUser.longitude),Double.parseDouble(membre.latitude),Double.parseDouble(membre.longitude));
                            membre.distance= String.valueOf(distance);
                            AppSession.currentMember=membre;
                            Intent intent = new Intent(getContext(), MemberProfilActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            getContext().startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                        }
                    });

                } else {

                    view = inflater.inflate(R.layout.no_data_item, container, false);
                    final TextView noData = (TextView) view.findViewById(R.id.nodata);
                    noData.setText("Aucun favoris");

                }
                break;
        }
        return view;


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
                // new deleteContactTask(getContext()).execute(AppSession.currentMember.getId());
                if(mPage==1)
                    new deleteContactTask(getContext()).execute(AppSession.currentMember.getId());
                else if(mPage==3)
                    new deleteFavorisTask(getContext()).execute(AppSession.currentMember.getId());


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


                //     favAdapter.currentmemb.isContacts="0";
                AppSession.currentMember.isContacts="0";
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


    class deleteFavorisTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idfav;

        deleteFavorisTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idfav = credentials[0];
                Boolean result = AppSession.getApi(context).deleteFromFavoris(this.idfav);
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


                favAdapter.currentmemb.isFavoris = "0";
                AppSession.currentMember.isFavoris = "0";
                AppSession.favoris.remove(slectedpos);
                favAdapter.notifyDataSetChanged();


            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, R.string.success_delete_Favoris, Toast.LENGTH_SHORT).show();

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

        ContactRequestTask(Context context) {
            super(context);
        }

        protected void onPreExecute() {

    bar.setVisibility(view.VISIBLE);
            contactAdapter.notifyDataSetChanged();
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
         bar.setVisibility(view.GONE);
                contactAdapter.notifyDataSetChanged();

            }
///
//            this.dialog.dismiss();
            super.onPostExecute(isadded);
          /*  Intent i=new Intent(getContext(),ContactsManagerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);*/
         /*   Intent i=new Intent(getContext(),MainContacts.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);*/
            bar.setVisibility(view.INVISIBLE);
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