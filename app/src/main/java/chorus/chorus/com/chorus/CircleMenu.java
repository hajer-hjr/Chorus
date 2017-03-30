package chorus.chorus.com.chorus;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class CircleMenu extends View {
private Bitmap image;
    public static interface IMenuListener{

        public void onMenuClick(MenuCircle item);
    }

    public static class MenuCircle{
        private int x,y,radius;
        public int id;
        Membre membre;
        public String text;

    }

    private Paint mainPaint;
    private Paint secondPaint;
    private Paint textPaint;
    private int radius_main =70;
        private Canvas canvas1;
    private int menuInnerPadding =-20;
    private int radialCircleRadius = 40;
    private int textPadding = 25;
    private double startAngle = - Math.PI/2f;
    private ArrayList<MenuCircle> elements;
    private IMenuListener listener;
    private int centerX,centerY;
    private    Bitmap icon;
    private String url="";
    private int  icon_x;
    private int icon_y,main_height,main_width;
    public void setListener(IMenuListener listener){
        this.listener = listener;
    }
    public void clear(){
        elements.clear();
        listener=null;
    }
    public CircleMenu(Context context) {
        super(context);
        init();
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        elements = new ArrayList<MenuCircle>();
    }
    public void addMenuItem(String text,int id,Membre membre){
        MenuCircle item = new MenuCircle();
        item.id = id;
        item.text=text;
        item.membre=membre;
        elements.add(item);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mainPaint = new Paint();
        mainPaint.setColor(Color.BLUE);
        secondPaint = new Paint();
        secondPaint.setColor(Color.DKGRAY);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas1=canvas;
        Log.e("width",String.valueOf(canvas.getWidth()));
        centerX = canvas.getWidth()/2 ;
         centerY= canvas.getHeight()/2;
   main_width=R.integer.main_circle_width;
       main_height=R.integer.main_circle_width;
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                if( AppSession.currentUser!=null) {
                    image = getBitmapFromURL(getContext().getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url);
                    Resources res = getResources();
                    image = getResizeBitmap(image,res.getInteger(R.integer.main_circle_width) ,res.getInteger(R.integer.main_circle_height) );
                }

            }});

        t2.start(); // spawn thread
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        image=getBitmapFromURL("http://192.168.1.18/chorus/images/users/187_homme.jpg");


       /* Glide.with(getContext())
                .load(imageView);
       // imageView.getDrawingCache();
       image= Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);*/
   /*     int width1 = image.getWidth();
        int height1 = image.getHeight();
        float xScale1 = ((float) 4) / width1;
        float yScale1 = ((float) 4) / height1;
        float scale1 = (xScale1 <= yScale1) ? xScale1 : yScale1;

        Matrix matrix1 = new Matrix();
        matrix1.postScale(scale1, scale1);*/
        image= getCircleBitmap(image);
        //    canvas.drawCircle( centerX,centerY,radialCircleRadius,mainPaint);
        canvas1.drawBitmap(image,centerX-(image.getWidth()/2) ,centerY-(image.getHeight()/2),mainPaint);
        int radius;
        if(image.getWidth() < image.getHeight())
            radius = image.getWidth()/2;
        else
            radius = image.getHeight()/2;
        radius_main=radius;

        for(int i=0;i<elements.size();i++){
            double angle =0;
            if(i==0){
                angle = startAngle;
            }else{
                angle = startAngle+(i * ((2 * Math.PI) / elements.size()));
            }
            elements.get(i).x = (int) (centerX + Math.cos(angle)*(radius_main+menuInnerPadding+radialCircleRadius));
            elements.get(i).y = (int) (centerY + Math.sin(angle)*(radius_main+menuInnerPadding+radialCircleRadius));

            int dist_x=(int)Math.ceil(Double.parseDouble(elements.get(i).membre.distance))*5;
            Log.e("distance",String.valueOf(dist_x));

            Resources res = getResources();
            icon_x=(int)(res.getInteger(R.integer.small_circle_width)-dist_x+5);
            icon_y=(int)(res.getInteger(R.integer.small_circle_height)-dist_x+5);
          // canvas.drawCircle( elements.get(i).x,elements.get(i).y,radialCircleRadius,secondPaint);


            float tW = textPaint.measureText(elements.get(i).text);

                url=getContext().getResources().getString(R.string.api_base_url) + "/" + elements.get(i).membre.Logo.thumb_file_url;


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                        icon = getBitmapFromURL(url);
                        icon = getResizeBitmap(icon, icon_x, icon_x);



                }});

            t.start(); // spawn thread
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int width = icon.getWidth();
            int height = icon.getHeight();

            float xScale = ((float) 4) / width;
            float yScale = ((float) 4) / height;
            float scale = (xScale <= yScale) ? xScale : yScale;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            icon= getCircleBitmap2(icon);
          //  canvas.drawBitmap(icon,elements.get(i).x,elements.get(i).y,secondPaint);
            canvas.drawBitmap(icon,elements.get(i).x-(icon.getWidth()/2),elements.get(i).y-(icon.getHeight()/2),secondPaint);
            //canvas.drawText(elements.get(i).text,elements.get(i).x-tW/2,elements.get(i).y+radialCircleRadius+textPadding,textPaint);
        }

    }
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            for(MenuCircle mc : elements){
                double distance =  Math.hypot(event.getX()-mc.x,event.getY()-mc.y);
                if(distance<= radialCircleRadius){
                    //touched
                    if(listener!=null)
                        listener.onMenuClick(mc);
                    return true;
                }
            }

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.WHITE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, bitmap.getWidth(),bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    private Bitmap getCircleBitmap2(Bitmap bitmap) {
        int sice = Math.min((bitmap.getWidth()), (bitmap.getHeight()));

        Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.ARGB_8888);
        Log.e("width",String.valueOf(bitmap.getWidth()));
        Log.e("height",String.valueOf(bitmap.getHeight()));
       /* final Bitmap output = Bitmap.createBitmap(200,
                200, Bitmap.Config.ARGB_8888);*/
        final Canvas canvas = new Canvas(output);

        final int color = Color.WHITE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, bitmap1.getWidth(),bitmap1.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    class BitmapTask extends ApiTaskNoDialog<String, Void, Bitmap> {
        String pwd;
        BitmapTask(Context context) {
            super(context);
        }

        protected Bitmap doInBackground(String... credentials) {

                return getBitmapFromURL(credentials[0]);


        }
        protected void onPostExecute(Bitmap user) {
            super.onPostExecute(user);
        }
    }
}