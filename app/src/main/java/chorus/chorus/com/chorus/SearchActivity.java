package chorus.chorus.com.chorus;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class SearchActivity extends AppCompatActivity {
    private Context mContext;
    private Spinner s1;
    private String sexe;
    private Spinner age1;
    private Spinner age2;
    private Spinner country;
    private String selecteditem;
    private Spinner state;
    private int countryIndex;
    private int stateIndex;
    private String selectestate;
    private int pos;
    private  List<Integer> ages;
   private AutoCompleteTextView actv;
    private TextView depart;
    private TextView txt_pseudo,txt_sexe;
   private  ArrayAdapter<Membre> pseudoArrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
      //  setupUI(findViewById(R.id.parent));
        mContext = this; // to use all around this class
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;





    }
    @Override
    protected void onResume() {
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
//        Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
    //  String json2 = mPrefs.getString("countries", null);
    //    Type type = new TypeToken<ArrayList<Country>>(){}.getType();
     /*   ArrayList<Country>countrie=new ArrayList<Country>();
        int size=mPrefs.getInt("size_country",0);
        for(int j=0;j<size;j++)
        {
            String json3 = mPrefs.getString("country"+j, "");
            Country obj2 = gson.fromJson(json3, Country.class);
            countrie.add(obj2);
        }


        if(countrie!=null&&countrie.size()>0)AppSession.countries=countrie;*/
        if(AppSession.countries==null) {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
   /* @Override
    public void onRestart(){
        super.onRestart();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }*/
/***Pour verifer si le pays sélectionné possede des départements**/
   /*
        SearchCriteria criteria = new SearchCriteria();
        criteria.sexe = sexe;
        criteria.pays = selecteditem;
        criteria.state=selectestate;
        criteria.age1 = age1.getSelectedItem().toString();
        criteria.age2 = age2.getSelectedItem().toString();
        SharedPreferences mprefs= PreferenceManager.getDefaultSharedPreferences(this);
        String opt_tri=mprefs.getString("search_opt_tri","0");
        //Log.e("opt_tri",opt_tri);
        criteria.opt_tri=opt_tri;
        new SearchMemberTask(this).execute(criteria);*/
    }









