package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

public class RegistrationEtape2Activity extends AppCompatActivity {
    String selectedItem;
    int backposition = -1;
    GridView gridview;
    RelativeLayout lay;
    private int num_selected=0;
   ImageView  BackSelectedItem;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registration_etape2);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        NewsCategorie cat;
        gridview = (GridView) findViewById(R.id.gridview);
        btn = (Button) findViewById(R.id.complete);
        if(num_selected<=0){
            btn.setClickable(false);
            btn.setAlpha(.8f);
            btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        }
        gridview.setAdapter(new CategoriesAdpter(this, AppSession.newsCategories));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                lay=(RelativeLayout) gridview.getChildAt(position);
                BackSelectedItem=(ImageView)lay.findViewById(R.id.ischecked);

                if (AppSession.newsCategories.get(position).selected == 1) {
                   if(num_selected>0) num_selected--;
                    BackSelectedItem.setVisibility(View.GONE);
                    lay.setBackgroundColor(Color.parseColor("#1A000000"));
                    AppSession.newsCategories.get(position).selected = 0;
                } else {
                    num_selected++;
                    BackSelectedItem.setVisibility(View.VISIBLE);
                    lay.setBackgroundColor(Color.parseColor("#E6000000"));
                    AppSession.newsCategories.get(position).selected = 1;
                }
                if(num_selected<=0){
                    btn.setClickable(false);
                    btn.setAlpha(.8f);
                    btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
                if(num_selected>0){
                    btn.setClickable(true);
                    btn.setAlpha(1.0f);
                    btn.getBackground().setColorFilter(null);
                }

            }
        });
    }

    public static void setGrayScale(SmartImageView v) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
    }


    public void complete(View view) {
        String selected="";
        for(int i=0;i<AppSession.newsCategories.size();i++)
        {
            if(AppSession.newsCategories.get(i).selected==1)
            {
                selected+=AppSession.newsCategories.get(i).cat_id+"|";
            }

        }
        new savecategorieTask(this).execute(selected);
    }

    class savecategorieTask extends ApiTaskNoDialog<String, Void,Boolean> {
        private String selected;

        savecategorieTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... data) {
           this.selected=data[0];
            try {

                Boolean added = AppSession.getApi(context).saveCatagories(this.selected);
                return added;
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(Boolean added) {
            if (added) {
                //String authField = "email".equals(context.getResources().getString(R.string.auth_via)) ? this.email : this.username;
                //new LoginTask(context).execute(this.email, this.password);
                Intent msgIntent = new Intent(context, UpdateUserLocationService.class);
                startService(msgIntent);
                        Intent intent = new Intent(RegistrationEtape2Activity.this, MenuWrappAcivity.class);
                RegistrationEtape2Activity.this.startActivity(intent);
            } else {
                showError();
            }
            super.onPostExecute(added);
        }
    }
}