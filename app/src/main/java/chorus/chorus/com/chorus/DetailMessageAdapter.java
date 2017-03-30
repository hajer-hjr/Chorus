package chorus.chorus.com.chorus;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by pc on 20/07/2016.
 */
public class DetailMessageAdapter extends ArrayAdapter<OneMessage> {
    private TextView message;
    private TextView date;
    private List<OneMessage> messages = new ArrayList<OneMessage>();
    private LinearLayout wrapper;

    @Override
    public void add(OneMessage object) {
        messages.add(object);
        super.add(object);
    }

    public DetailMessageAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.messages.size();
    }

    public OneMessage getItem(int index) {
        return this.messages.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OneMessage coment = getItem(position);
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(coment.left )
            row = inflater.inflate(R.layout.detail_message_item_left, parent, false);
            else
                row = inflater.inflate(R.layout.detail_message_item, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        //OneMessage coment = getItem(position);
//String msg="<div>"+coment.comment+" <br /><font color=\"#ffffff\"><b>"+coment.date+"</b></font></div>";
        message = (TextView) row.findViewById(R.id.comment);
        /*final String resultText = coment.comment + "         " + coment.date;
        final SpannableString styledResultText = new SpannableString(resultText);
        styledResultText.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)), coment.comment.length() + 2, coment.comment.length() + 2 +coment.date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setText(Html.fromHtml(msg));*/



        message.setText(coment.comment);
      //  if(coment.left) message.setPadding(10,0,0,0); else message.setPadding(0,0,10,0);
        date = (TextView) row.findViewById(R.id.date);

        date.setText(coment.date);
      //  message.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
       wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);
            ImageView imageLogo = (ImageView) row.findViewById(R.id.logo);
            Picasso.with(getContext()).load(getContext().getResources().getString(R.string.api_base_url) + "/" + coment.image_profil).fit().transform(new RoundedTransformation(100, 0))
                    .fit().into(imageLogo);
           // ImageView imageLogo1 = (ImageView) row.findViewById(R.id.logo1);
           // imageLogo1.setVisibility(View.GONE);
            Log.e("url",coment.image_profil);
      /*  if (getMember(position).image_profil != null) {
            //  Log.e("logo url",context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
            // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+ getMember(position).image_profil);
            imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
            Log.e("image url", context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
            imageLogo.setVisibility(View.VISIBLE);
        } else {
            imageLogo.setVisibility(View.GONE);
        }*/
      /* }
        else {
            ImageView imageLogo1 = (ImageView) row.findViewById(R.id.logo);
            Picasso.with(getContext()).load(getContext().getResources().getString(R.string.api_base_url) + "/" + coment.image_profil).fit().transform(new RoundedTransformation(100, 0))
                    .fit().centerCrop().into(imageLogo1);
           // ImageView imageLogo = (ImageView) row.findViewById(R.id.logo);
           // imageLogo.setVisibility(View.GONE);
            Log.e("url",coment.image_profil);
           // message.setGravity(Gravity.CENTER);
      /*  if (getMember(position).image_profil != null) {
            //  Log.e("logo url",context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
            // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+ getMember(position).image_profil);
            imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
            Log.e("image url", context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
            imageLogo.setVisibility(View.VISIBLE);
        } else {
            imageLogo.setVisibility(View.GONE);
        }*/

     //   }
        DetailMessageAdapter adapter=this;
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}
