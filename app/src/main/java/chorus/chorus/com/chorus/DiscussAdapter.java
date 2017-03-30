package chorus.chorus.com.chorus;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by pc on 17/08/2016.
 */
public class DiscussAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Message> objects;
    private LinearLayout wrapper;
    DiscussAdapter(Context _context, ArrayList<Message> messages) {
        context = _context;
        objects = messages;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Message message = getMessage(position);
        Log.e("id user",message.mes_mem_id_dest);
        if (view == null) {

            view = layoutInflater.inflate(R.layout.detail_message_item_left, parent, false);
        }

        if(message.mes_mem_id_dest.equals(AppSession.currentUser.id)) {
            Log.e("id user1",message.mes_mem_id_dest);
            if(message.mes_type.equals("1"))
            view = layoutInflater.inflate(R.layout.detail_message_item_left, parent, false);
            else{
                view = layoutInflater.inflate(R.layout.detail_message_item_image_left, parent, false);
            }
        }
        else {
            Log.e("id user2",message.mes_mem_id_dest);
            if(message.mes_type.equals("1"))
            view = layoutInflater.inflate(R.layout.detail_message_item, parent, false);
            else{
                view = layoutInflater.inflate(R.layout.detail_message_item_image, parent, false);
            }

        }
        wrapper = (LinearLayout) view.findViewById(R.id.wrapper);
        if(message.mes_type.equals("1")) {
            //OneMessage coment = getItem(position);
//String msg="<div>"+coment.comment+" <br /><font color=\"#ffffff\"><b>"+coment.date+"</b></font></div>";
            TextView message1 = (TextView) view.findViewById(R.id.comment);
        /*final String resultText = coment.comment + "         " + coment.date;
        final SpannableString styledResultText = new SpannableString(resultText);
        styledResultText.setSpan((new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE)), coment.comment.length() + 2, coment.comment.length() + 2 +coment.date.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setText(Html.fromHtml(msg));*/


            String messg = "";

            messg = decrypt(context, message.mes_subject);

            message1.setText(messg);
        }
        else {
            ImageView imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
            VideoView vidPreview = (VideoView) view.findViewById(R.id.videoPreview);
           if (message.mes_type.equals("2")) {

                imgPreview.setVisibility(View.VISIBLE);
                vidPreview.setVisibility(View.GONE);
                // bimatp factory
                BitmapFactory.Options options = new BitmapFactory.Options();

                // down sizing image as it throws OutOfMemory Exception for larger
                // images
                options.inSampleSize = 8;

              //  final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
              // Picasso.with(context).load(context.getResources().getString(R.string.api_base_url) + "/user_files/messages/" + message.mes_file_url).fit().into(imgPreview);
                 Glide.with(context).load(context.getResources().getString(R.string.api_base_url) + "/user_files/messages/" + message.mes_file_url)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgPreview);

               // imgPreview.setImageBitmap(bitmap);

            } else if (message.mes_type.equals("3")) {
                imgPreview.setVisibility(View.GONE);
                vidPreview.setVisibility(View.VISIBLE);
                vidPreview.setVideoPath(context.getResources().getString(R.string.api_base_url) + "/user_files/messages/" + message.mes_file_url);
                // start playing
               Log.e("video path",context.getResources().getString(R.string.api_base_url) + "/user_files/messages/" + message.mes_file_url);
                vidPreview.start();
            }

        }
        //message1.setPadding(5,5,5,5);
        //  if(coment.left) message.setPadding(10,0,0,0); else message.setPadding(0,0,10,0);
        TextView   date = (TextView) view.findViewById(R.id.date);

        date.setText(message.DateMessage);

        //message1.setBackgroundResource(message.mes_mem_id_dest.equals(AppSession.currentUser.id)? R.drawable.bubble_yellow : R.drawable.bubble_green);
        //wrapper.setGravity(message.mes_mem_id_dest.equals(AppSession.currentUser.id) ? Gravity.LEFT : Gravity.RIGHT);
        ImageView imageLogo = (ImageView) view.findViewById(R.id.logo);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageLogo.getLayoutParams();
        params.gravity = message.mes_mem_id_dest.equals(AppSession.currentUser.id) ?Gravity.LEFT : Gravity.RIGHT;
       // imageLogo.setLayoutParams(params);
        Picasso.with(context).load(context.getResources().getString(R.string.api_base_url) + "/" + message.image_profil).fit().transform(new RoundedTransformation(100, 0))
                .fit().into(imageLogo);

        // ImageView imageLogo1 = (ImageView) row.findViewById(R.id.logo1);
        // imageLogo1.setVisibility(View.GONE);
       // Log.e("url",coment.image_profil);
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
        final DiscussAdapter adapter = this;
        return view;
    }

    //View of Spinner on dropdown Popping

    private static String decrypt(Context cont, String value) {
        try {
            return new String(Base64.decode(value, Base64.DEFAULT));
        }
        catch (IllegalArgumentException e) {
            // TODO: handle exception
            return "";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private Message getMessage(int position) {
        return ((Message) getItem(position));
    }
    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
