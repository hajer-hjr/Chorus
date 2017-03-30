package chorus.chorus.com.chorus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateProfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfilFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener  {
    private Spinner country;
    private Context mContext;
    private DatePicker datePicker;
    private Calendar calendar;
    private EditText date;
    private int year, month, day;
    private String selecteditem;
    private Spinner state;
    private int countryIndex;
    private int stateIndex;
    private String selectestate;
    private int pos;
    private EditText email ;
    private  EditText username ;
    MenuWrappAcivity activity;
    private EditText firstname ;
    private  EditText lastname;
    private  EditText paroisse,origine,verset;
    private Button button;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UpdateProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfilFragment newInstance(String param1, String param2) {
        UpdateProfilFragment fragment = new UpdateProfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        View view = inflater.inflate(R.layout.fragment_update_profil, container, false);
        // Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
        /*initialisation de datepicker*/
        if(AppSession.currentUser!=null) {
            date = (EditText) view.findViewById(R.id.date);
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
            country = (Spinner) view.findViewById(R.id.pays);
            email = (EditText) view.findViewById(R.id.email);
            username = (EditText) view.findViewById(R.id.username);
            firstname = (EditText) view.findViewById(R.id.firstname);
            lastname = (EditText) view.findViewById(R.id.lastname);
            verset = (EditText) view.findViewById(R.id.verset);
            origine = (EditText) view.findViewById(R.id.origine);
            paroisse = (EditText) view.findViewById(R.id.paroisse);
            Button button = (Button) view.findViewById(R.id.update_profil_button);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   saveUser(v);
                }
            });
            //tel = (EditText) findViewById(R.id.tel);
            //  not4.setVisibility(View.GONE);
            if (AppSession.currentUser != null) {
                email.setText(AppSession.currentUser.email);
                username.setText(AppSession.currentUser.username);
                firstname.setText(AppSession.currentUser.firstname);
                lastname.setText(AppSession.currentUser.lastname);
                origine.setText(AppSession.currentUser.origine);
                paroisse.setText(AppSession.currentUser.paroisse);
                verset.setText(AppSession.currentUser.verset);
                //  tel.setText(AppSession.currentUser.tel);
                Log.e("countryyyy", AppSession.currentUser.pays);
                countryIndex = getCountryIndexById(AppSession.currentUser.pays);
                //if(countryIndex!=0)
                // stateIndex=getStateIndexById(countryIndex,AppSession.currentUser.state);
                if (AppSession.currentUser.date_naiss != "") {
                    String[] date2 = AppSession.currentUser.date_naiss.split("-");
                    Log.e("date2", AppSession.currentUser.date_naiss);
                    showDate(Integer.parseInt(date2[0]), Integer.parseInt(date2[1]), Integer.parseInt(date2[2]));
                }


            }

            ArrayAdapter<Country> countryArrayAdapter = new ArrayAdapter<Country>(getContext(), android.R.layout.simple_spinner_item, AppSession.countries);
            countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            country.setAdapter(countryArrayAdapter);
            country.setPrompt(getResources().getString(R.string.hint_country));
            country.setSelection(countryIndex);


            //}

        }
        else
        {


            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);



        }
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      /*  if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {²&
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity=(MenuWrappAcivity)activity;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   /* public void setDate(View view) {
        getActivity().showDialog(999);
       /* Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
             .show();*/
    /*}*/
    private int getCountryIndexById(String id) {
        for (int i = 0; i <AppSession.countries.size(); i++) {
            if (id.equals(AppSession.countries.get(i).id)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    public void populateSetDate(int year, int month, int day) {
        date.setText(month+"/"+day+"/"+year);
    }

    public void showDatePickerDialog(View v) {

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /*protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(getContext(), myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };
*/

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
    /*fonction inscription*/
    public void saveUser(View view) {
        String date1=date.getText().toString();

        String country1=AppSession.countries.get(country.getSelectedItemPosition()).getId();
        new UpdateUserTask(getContext()).execute(email.getText().toString(), username.getText().toString(),firstname.getText().toString(),lastname.getText().toString(),date1,country1,paroisse.getText().toString());
    }
    class UpdateUserTask extends ApiTask<String, Void, User> {
        private String email;
        private String username;
        private String lastname;
        private String firstname;
        private String date_naiss;
        private String pays;
        private String paroisse;
        private String verset;

        UpdateUserTask(Context context) {
            super(context, R.string.dialog_registering);
        }

        protected User doInBackground(String... data) {
            try {
                this.email = data[0];
                this.username = data[1];
                this.firstname = data[2];
                this.lastname = data[3];
                this.date_naiss = data[4];
                this.pays = data[5];
                this.paroisse = data[6];
                this.verset = data[7];

                return AppSession.getApi(context).updateprofil(this.email, this.username, this.firstname,this.lastname,this.date_naiss,this.pays,this.paroisse,this.verset);
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(User user) {
            if (user!=null) {
                //String authField = "email".equals(context.getResources().getString(R.string.auth_via)) ? this.email : this.username;
                //new LoginTask(context).execute(this.email, this.password);
                AppSession.currentUser.username=user.username;
                AppSession.currentUser.email=user.email;
                AppSession.currentUser.firstname=user.firstname;
                AppSession.currentUser.lastname=user.lastname;
                AppSession.currentUser.date_naiss=user.date_naiss;
                AppSession.currentUser.pays=user.pays;
                AppSession.currentUser.paroisse=user.paroisse;
                AppSession.currentUser.origine=user.origine;
                AppSession.currentUser.verset=user.verset;
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.username=user.username;
                obj.email=user.email;
                obj.firstname=user.firstname;
                obj.lastname=user.lastname;
                obj.date_naiss=user.date_naiss;
                obj.pays=user.pays;
                obj.state=user.state;
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();

                int duration = Toast.LENGTH_SHORT;



                Toast toast = Toast.makeText(context,R.string.update_profil_succes , duration);
                toast.show();
               /* MenuWrappAcivity activity1 = (MenuWrappAcivity) getActivity();
               // activity1.myMethod();

                Fragment newFragment = new HomeFragment();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                activity1.navItemIndex=0;
                activity.CURRENT_TAG="HOME_TAG";
                activity1.getHomeFragment();*/

// Commit the transaction
                //



            } else {
                showError();
            }
            super.onPostExecute(user);
        }
    }
}
