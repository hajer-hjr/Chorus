package chorus.chorus.com.chorus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class SreachActivity extends AppCompatActivity {
    private  String urlProfileImg;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sreach);
        imgProfile=(ImageView)findViewById(R.id.logo);
        if (AppSession.currentUser.Logo.thumb_file_url != null) {

            urlProfileImg = this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url;
            Glide.with(this).load(urlProfileImg)
                   // .override(100,100)
                   // .crossFade()
                  //  .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);



        }
    }
}
