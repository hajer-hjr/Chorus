package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

/**
 * Created by pc on 13/10/2016.
 */
public class CategoriesAdpter extends BaseAdapter {
    private Context mContext;
    LayoutInflater layoutInflater;
    ArrayList<NewsCategorie> objects;
  private ColorMatrixColorFilter filter;
    private int pos;
    // Constructor
    private  SmartImageView imageView;
    private    View gridView;
    public CategoriesAdpter(Context c, ArrayList<NewsCategorie> categories) {
        mContext = c;
        objects=categories;
    }

    public int getCount() {
        return objects.size();
    }

    public Object getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View gridView;
        pos=position;
        if (convertView == null) {
            gridView = new View(mContext);

            // get layout from grid_item.xml ( Defined Below )

            gridView = layoutInflater.inflate( R.layout.categories_item , null);

            // set value into textview

            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);

            textView.setText(objects.get(position).cat_name);

            // set image based on selected text

           imageView = (SmartImageView) gridView.findViewById(R.id.grid_item_image);
            imageView.setAlpha(150);
           /* if(objects.get(pos).selected==0)
                setGrayScale(imageView);*/
            imageView.setImageUrl(mContext.getResources().getString(R.string.api_base_url) +"/images/news/"+objects.get(pos).cat_ima);
        }
        else
        {
            gridView = (View) convertView;
        }
     /*   imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
                setGrayScale(imageView);
                Toast.makeText(mContext,
                        "pic" + (pos + 1) + " selected",
                        Toast.LENGTH_SHORT).show();
                imageView.setImageResource(0);
           //  imageView.setImageResource(objects.get(pos).cat_ima);
            }
        });*/
        return gridView;
    }

    public static void setGrayScale(SmartImageView v){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
    }
}