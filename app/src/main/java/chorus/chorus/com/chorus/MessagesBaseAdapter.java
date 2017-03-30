package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.loopj.android.image.SmartImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import chorus.chorus.com.chorus.ApiTaskNoDialog;
import chorus.chorus.com.chorus.AppSession;
import chorus.chorus.com.chorus.Message;
import chorus.chorus.com.chorus.MessagesUserListeTask;
import chorus.chorus.com.chorus.R;

/**
 * Created by pc on 19/07/2016.
 */
public class MessagesBaseAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Message> objects;
    public int selectedItem = 0;
private Bitmap bitmap;
    public String idautremembre = "";

    MessagesBaseAdapter(Context _context, ArrayList<Message> messages) {
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.message_result_item, parent, false);
        }


      /*if (getCount() == 1) {
            view.setBackgroundResource(R.drawable.rounded_listview_single);
        } else if (position == 0) {
            view.setBackgroundResource(R.drawable.rounded_listview_first);
        } else if (position == getCount() - 1) {
            view.setBackgroundResource(R.drawable.rounded_listview_last);
        } else {
            view.setBackgroundResource(R.drawable.rounded_listview_middle);
        }

*/
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppSession.currentMessage = (Message) getItem(position);
                //  AppSession.currentMember =  ((Message) getItem(position)).autre_membre;
                String idautre = ((Message) getItem(position)).idMemAutreMembre;
                idautremembre = idautre;

                new MessagesUserListeTask(context).execute("", idautremembre);
            }

        });

        Message message = getMember(position);
        // Membre member =message.autre_membre;
      // LinearLayout l1=(LinearLayout)view.findViewById(R.id.linearLayout1);
       if(message.isRecentMessage) view.setBackgroundResource(R.drawable.rounded_listview_first_orange);
        else
            view.setBackgroundResource(R.drawable.rounded_listview_first_blanc1);
        ((TextView) view.findViewById(R.id.username)).setText(message.PseudoAutreMembre != null ? message.PseudoAutreMembre : "");

        String mesg = "";
        if (message.mes_subject != null) {
            mesg = decrypt(context, message.mes_subject);
        }
if(message.mes_type.equals("1")) {
    if (!mesg.equals("") && mesg.length() < 30) {
        mesg = mesg;
        //mesg = message.mes_subject;
    } else

    {
        mesg = mesg.substring(0, 30) + "...";
        //mesg = message.mes_subject.substring(0, 30) + "...";
    }
}
        else if (message.mes_type.equals("2")){

    mesg = "Message photo";

        }
else if (message.mes_type.equals("3")){

    mesg = "Message video";

}else if (message.mes_type.equals("4")){

    mesg = "Message audio";

}else if (message.mes_type.equals("3")){

    mesg = "Message fichier";

}
        ((TextView) view.findViewById(R.id.message)).setText(mesg);
      ((TextView) view.findViewById(R.id.date_msg)).setText(message.DateMessage == null ? "" : message.DateMessage);
        // ((TextView) view.findViewById(R.id.myImageViewText)).setText(visite.vis_date);
      /*  ImageView favorisButt=(ImageView)view.findViewById(R.id.addfavoris);
        if(visite.mem_opt_favoris=="on")
            favorisButt.setImageResource(R.drawable.star01);
        else favorisButt.setImageResource(R.drawable.star02);*/
        SmartImageView imageLogo = (SmartImageView) view.findViewById(R.id.logo);
        if (getMember(position).image_profil != null) {
            //  Log.e("logo url",context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+member.Logo.thumb_file_url);
            // imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) +"/jetunoo/"+ getMember(position).image_profil);
          //  imageLogo.setImageUrl(context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    bitmap =getBitmapFromURL(context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
                    bitmap=getResizeBitmap(bitmap,100,100);



                }});

            t.start(); // spawn thread
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Bitmap imageRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(imageRounded);
            Paint mpaint = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight())), 30, 30, mpaint);// Round Image Corner 100 100 100 100
            imageLogo.setImageBitmap(imageRounded);
            Log.e("image url", context.getResources().getString(R.string.api_base_url) + "/" + getMember(position).image_profil);
            imageLogo.setVisibility(View.VISIBLE);

        } else {
            imageLogo.setVisibility(View.GONE);
        }
        final MessagesBaseAdapter adapter = this;
       /* ImageView imagedelete = (ImageView) view.findViewById(R.id.deletemessage);
        imagedelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String idautre = ((Message) getItem(position)).idMemAutreMembre;
                idautremembre = idautre;
                new DeleteMessageTask(context,adapter).execute(String.valueOf(position),idautre);
            }

        });*/
        return view;
    }
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
    //View of Spinner on dropdown Popping
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bitmap getResizeBitmap(Bitmap bitmap, int newHeight, int newWidth)
    {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float)newWidth)/width;
        float scaleHeight = ((float)newHeight)/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizeBitmap;
    }
    private Message getMember(int position) {
        return ((Message) getItem(position));
    }

    class UpdateMessagesListeTask extends ApiTaskNoDialog<String, Void, MessagesResult> {
        UpdateMessagesListeTask(Context context) {
            super(context);
        }

        protected MessagesResult doInBackground(String... criteria) {
            try {
                AppSession.currentPage = 1;
                MessagesResult messagesResult = AppSession.getApi(context).UserMessagesList(AppSession.currentPage, criteria[0], "");
                if (messagesResult.messages.size() == 0) {
                    throw new SJBException("NOTHING_FOUND");
                }
                return messagesResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(MessagesResult messagesResult) {
            if (messagesResult != null) {
                AppSession.messages = messagesResult.messages;
                AppSession.currentMessagesNumber = messagesResult.listingsNumber;

          /*  Intent intent = new Intent(context, VisitesActivity.class);
            startActivity(intent);*/
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
            }
            super.onPostExecute(messagesResult);
        }
    }
}
/*delete user message*/
class DeleteMessageTask extends ApiTaskNoDialog<String, Void, Boolean> {
    String idautre;
    int index;
   // MessagesBaseAdapter adapter;
    DeleteMessageTask(Context context,MessagesBaseAdapter  _adapter) {
        //adapter = _adapter;
        super(context,_adapter);
    }

    protected Boolean doInBackground(String... credentials) {
        try {
            this.index = Integer.parseInt(credentials[0]);
            this.idautre=credentials[1];
           Boolean result = AppSession.getApi(context).deletemessage(this.idautre);
            //  Log.e("questionnaire",questionnaire.toString());
            return result;
        } catch (SJBException e) {
            error = e.getMessage();
            return false;
        }
    }

    protected void onPostExecute(Boolean isdeleted) {
        if (isdeleted) {
            AppSession.messages.remove(index);
            adapter.notifyDataSetChanged();
            Toast.makeText(context, R.string.success_delete_message, Toast.LENGTH_SHORT).show();

        } else {
            //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
         showError();
           // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(isdeleted);
    }

}
