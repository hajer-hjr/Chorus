package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FavorisFragment  extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    static int slectedpos;
private FavorisAdapter favAdapter;
    // newInstance constructor for creating fragment with arguments
    public static FavorisFragment newInstance(int page, String title) {
        FavorisFragment fragmentFirst = new FavorisFragment();
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
        favAdapter=new  FavorisAdapter (getContext(),AppSession.favoris);
        title = "Favoris";
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int sizec;
        if(AppSession.favoris!=null ) {

            sizec = AppSession.favoris.size();
        }
        else{sizec=0;}

  if(sizec==0){
            view = inflater.inflate(R.layout.no_data_item, container, false);
            final TextView noData = (TextView) view.findViewById(R.id.nodata);
            noData.setText("Aucun Favoris");



        }
        else{

            view = inflater.inflate(R.layout.fragment_favoris, container, false);
            //TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
            // tvLabel.setText(title);
            final GridView gridview = (GridView) view.findViewById(R.id.gridview);

            gridview.setAdapter(favAdapter);
     //   registerForContextMenu(gridview);
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

          /*  gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    //currentmemb=AppSession.currentMember= (Membre)getMember(position);
                  //  currentFav=String.valueOf(getCurrentFav(position).id);





                    Intent intent = new Intent(getContext(), MemberProfilContactActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);


                }
            });*/





        }
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
favAdapter.notifyDataSetChanged();
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
                new deleteFavorisTask(getContext()).execute(AppSession.currentMember.getId());
                return true;
            default:
                return super.onContextItemSelected(item);
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


                FavorisAdapter.currentmemb.isFavoris="0";
                AppSession.currentMember.isFavoris="0";
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

    }  public double distance(Double latitude, Double longitude, double e, double f) {
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