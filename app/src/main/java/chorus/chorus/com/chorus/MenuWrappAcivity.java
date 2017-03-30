package chorus.chorus.com.chorus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
//import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MenuWrappAcivity extends  AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    String ba1;
    private Button buttonconf;
    private Button upload_image;
    private File outPutFile = null;
    private Button buttoncancl;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    //private Bitmap photo ;
    private ArrayList<String>datasend;
    private Uri mImageCaptureUri;

    private ImageView mImageView;
    private FloatingActionButton fab;
    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg ="drawable://" + R.drawable.nav_menu_header_bg2;
    private static  String urlProfileImg = "http://192.168.1.18/chorus/images/users/187_homme.jpg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PROFIL = "profil";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler = new Handler();
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
       // fab = (FloatingActionButton) findViewById(R.id.fab);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
       // txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        upload_image = (Button) navHeader.findViewById(R.id.upload_image);
// create manager instance after the content view is set
     /*   SystemBarTintManager mTintManager = new SystemBarTintManager(this);
// enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setTintColor(getResources().getColor(R.color.colorPrimary));*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
          // getWindow().setStatusBarColor(ContextCompat.getColor(this,(AppSession.currentUser.sexe.equals("M"))? R.color.colorvertclair : R.color.colorrose));
        }
       // registerForContextMenu(upload_image);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        if(AppSession.currentUser!=null) {

            final String[] items = new String[]{"Webcam", "Gallery"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Importer une photo depuis");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) { //pick from camera
                    if (item == 0) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                        //  Log.e("image name", mImageCaptureUri.toString());
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                        try {
                          // intent.putExtra("return-data", true);

                            startActivityForResult(intent, PICK_FROM_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else { //pick from file
                        Intent intent = new Intent();

                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(intent, "Terminer l'action avec"), PICK_FROM_FILE);
                    }
                }
            });

            final AlertDialog dialog = builder.create();
            upload_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  dialog.show();
                    //Intent intent = new Intent();

                    //startActivity(new Intent(MenuWrappAcivity.this,UploadActivity.class));
                }
            });



          /*  Button button = (Button) findViewById(R.id.btn_crop);
            buttonconf = (Button) findViewById(R.id.confirm_crop);
            buttonconf.setVisibility(View.GONE);
            buttoncancl = (Button) findViewById(R.id.cancel_crop);
            buttoncancl.setVisibility(View.GONE);
            mImageView = (ImageView) findViewById(R.id.iv_photo);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });*/
        }
        if (AppSession.currentUser != null) {

            urlProfileImg = this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url;

        if (AppSession.currentUser.Logo.thumb_file_url != null) {

            urlProfileImg = this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url;
        }
        }
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
     /*   String isnew=null;
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
     isnew= extras.getString("isNew");
        if(isnew!=null)
        {
            GPSTracker mGPS = new GPSTracker(getBaseContext());
            if(!mGPS.canGetLocation()){
                showGPSDisabledAlertToUser();

            }
        }*/
        new CountryTask(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
}
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu_uploadphoto, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.frm_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                //  Log.e("image name", mImageCaptureUri.toString());
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                try {
                    intent.putExtra("return-data", true);

                  startActivityForResult(intent, PICK_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.frm_galerie:
                Intent i = new Intent();

                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(i, "Terminer l'action avec"), PICK_FROM_FILE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
       // txtWebsite.setVisibility(View.GONE);
        if (AppSession.currentUser!= null) {

            txtName.setText(AppSession.currentUser.username);
            //txtWebsite.setText("www.androidhive.info");
        }
        else{
            txtName.setText("");
        }
        if (AppSession.currentUser!= null) {
            if (AppSession.currentUser.sexe.equals("F"))
                imgNavHeaderBg.setImageResource(R.drawable.nav_menu_header_bg44);
            else
                imgNavHeaderBg.setImageResource(R.drawable.nav_menu_header_bg3);
        }
        else
        {
            imgNavHeaderBg.setImageResource(R.drawable.nav_menu_header_bg3);
        }
        // loading header background image
      /*  Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
      //  navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    public void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
         //   toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
      //  toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }
public void setNavIndex(int val){
    this.navItemIndex=val;
}
    public Fragment getHomeFragment() {
        Log.e("hhhh","here");
       switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
             //   fab.setVisibility(View.GONE);
                return homeFragment;
         //   case 1:
                // photos
               /* UpdateProfilFragment profilFragment = new UpdateProfilFragment();
                return profilFragment;*/

         /*   case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;*/
            default:
              //  fab.setVisibility(View.GONE);
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {

     // getSupportActionBar().setTitle(activityTitles[navItemIndex]);
     //   getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logogris);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
     //   getSupportActionBar().setIcon(R.drawable.globe);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                       break;
                       case R.id.nav_profil:
                           startActivity(new Intent(MenuWrappAcivity.this, UpdateProfilActivity.class));
                           overridePendingTransition(R.anim.enter, R.anim.exit);
                           drawer.closeDrawers();
                           return true;
                    case R.id.nav_logout:
                        new OfflineTast(getApplicationContext()).execute();

                        return true;
                 case R.id.nav_serach:
                     SearchCriteria criteria=new SearchCriteria();
                     SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                     Double log=Double.parseDouble(mPrefs.getString("longitude","0.00"));
                     Double alt=Double.parseDouble(mPrefs.getString("altitude","0.00"));
                     criteria.latitude=alt;
                     criteria.longitude=log;
                    // LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                   GPSTracker mGPS = new GPSTracker(getBaseContext());
                     if(mGPS.canGetLocation()){
                         new SearchMemberTask(getApplicationContext()).execute(criteria);


                     }else{

                     }

                        break;
                    case R.id.nav_params:
                        startActivity(new Intent(MenuWrappAcivity.this, SearchParamsActivity.class));
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        drawer.closeDrawers();
                        return true;

               /*     case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                     startActivity(new Intent(MenuWrappAcivity.this, MyAccountActivity.class));
                    drawer.closeDrawers();
                    return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();

            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            finish();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);

            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
       /* if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();*/
    }

    public void showDatePickerDialog(View v) {

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo;
        datasend = new ArrayList<String>();
        if (resultCode != Activity.RESULT_OK) return;

        if(requestCode!=0) {

            if(requestCode==PICK_FROM_CAMERA)
                doCrop();


            else if(requestCode==PICK_FROM_FILE) {
                Bundle extras1 = data.getExtras();
                mImageCaptureUri = data.getData();
                //String imagepath = getPath(mImageCaptureUri);
                // photo2=BitmapFactory.decodeFile(mImageCaptureUri);
                if(extras1!=null)
                   //photo= extras1.getParcelable("data");
                    Log.e("image uri",mImageCaptureUri.toString());
                doCrop();
            }

            else if (requestCode==CROP_FROM_CAMERA) {


                try {
                    if (outPutFile.exists()) {
                       photo = decodeFile(outPutFile);
                        //String name = mImageCaptureUri.getPath().substring(mImageCaptureUri.getPath().lastIndexOf("/") + 1);
                        datasend.add("hhhhhhhhh");
                        upload(photo);

                        Bitmap photo1 =decodeFile(outPutFile);
                        String name = mImageCaptureUri.getPath().substring(mImageCaptureUri.getPath().lastIndexOf("/") + 1);
                        datasend.add(name);

                   /* try {
                        if(outPutFile.exists()){
                            Bitmap photo2 = decodeFile(outPutFile);
                            photo=photo2;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        photo1.compress(Bitmap.CompressFormat.PNG,100, bao);

                        Glide.with(this).load( bao.toByteArray())
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(this))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgProfile);
                        upload(photo);

                    } else {
                        Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }







               /* Bundle extras = data.getExtras();

                if (extras != null) {
                    photo = extras.getParcelable("data");
                    Bitmap photo1 = extras.getParcelable("data");
                    String name = mImageCaptureUri.getPath().substring(mImageCaptureUri.getPath().lastIndexOf("/") + 1);
                    datasend.add(name);*/

                   /* try {
                        if(outPutFile.exists()){
                            Bitmap photo2 = decodeFile(outPutFile);
                            photo=photo2;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    /*ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    photo1.compress(Bitmap.CompressFormat.PNG,100, bao);

                    Glide.with(this).load( bao.toByteArray())
                            .crossFade()
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                    upload(photo);*/
                   /* buttoncancl.setVisibility(View.VISIBLE);
                    buttonconf.setVisibility(View.VISIBLE);
                    buttonconf.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            upload();
                        }
                    });
                    buttoncancl.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            //Intent intent = new Intent(MesphotosActivity2.this, MyAccountActivity.class);
                            //MesphotosActivity2.this.startActivity(intent);
                        }
                    });*/

               // }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists()) f.delete();

            }

        }
    }
    private void upload(Bitmap photo) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG,100, bao);
        byte[] ba = bao.toByteArray();
        ba1 = Base64.encodeToString(ba,Base64.NO_WRAP);
        datasend.add(ba1);
     //new uploadToServer(this).execute(datasend);
        new uploadToServer(this).execute(datasend,outPutFile);

    }
    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        //set crop properties
        // intent.putExtra("crop", "true");
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Oops!! Votre appareil ne supporte pas l'action Crop", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            //intent.putExtra("return-data", true);
            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
            if (size == 1) {
                Intent i 		= new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                Log.e("hhhhh","hhhhhhhh");
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Crop avec");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    class uploadToServer extends ApiTaskNoDialog<Object, Void, ImageProfil> {
        uploadToServer(Context context) {
            super(context);
        }
        private final ProgressDialog dialog = new ProgressDialog(MenuWrappAcivity.this);

        protected void onPreExecute() {

            this.dialog.setMessage("Chargement en cours...");
            this.dialog.show();

        }

        protected ImageProfil doInBackground(Object... params) {
            //filePath.substring(filePath.lastIndexOf("."));
            try {
                // ImageResult isuploaded= AppSession.getApi(context).savepicture1(params[0]);
              //  ImageProfil isuploaded= AppSession.getApi(context).savepicture1(params[0]);
                ArrayList<String>data= (ArrayList<String>) params[0];
                File file = (File) params[1];
                // ImageResult isuploaded= AppSession.getApi(context).savepicture1(params[0]);
                ImageProfil isuploaded = AppSession.getApi(context).savepicture2(data,file);
                Log.e("result upload",String.valueOf(isuploaded));
                return isuploaded;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }


        }

        protected void onPostExecute(ImageProfil result) {
//            this.dialog.dismiss();
            if (result!=null) {
                AppSession.currentUser.Logo=result;
               //Log.e("image file",result.thumb_file_url);
                //loadHomeFragment();
                SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.Logo=result;
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
                Toast.makeText(getApplicationContext(), "Votre image est ajouté avec succés", Toast.LENGTH_LONG).show();

               // loadHomeFragment();
                 finish();
              startActivity(getIntent());

                /*AppSession.currentUser.image_list = result.image_list;
                AppSession.currentUser.Logo=result.logo;
                SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.image_list = result.image_list;
                obj.Logo=result.logo;
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
                Intent intent = new Intent(MesphotosActivity2.this, MyAccountActivity.class);
                MesphotosActivity2.this.startActivity(intent);
                MesphotosActivity2.this.finish();*/
            }
            else{

                 showError();
             //   Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);

        }
    }
    class CountryTask extends ApiTaskNoDialog<String, Void, ArrayList<Country>> {

        CountryTask(Context context) {
            super(context);
        }
        protected ArrayList<Country> doInBackground(String... data) {
            try {

                ArrayList<Country>  countries=AppSession.getApi(context).getCountries();
                if (countries.size() == 0) {
                    throw new SJBException("NOTHING_FOUND");
                }

                return countries;
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }
        protected void onPostExecute(ArrayList<Country> countries) {
            if (countries!=null) {
                AppSession.countries = countries;

            } else {
                showError();
            }
            super.onPostExecute(countries);
        }
    }
    class SearchMemberTask extends ApiTaskNoDialog<SearchCriteria, Void, SearchMemberResult> {
        SearchCriteria searchCriteria;
        private final ProgressDialog dialog = new ProgressDialog(MenuWrappAcivity.this);
        SearchMemberTask(Context context) {
            super(context);
        }
        protected void onPreExecute() {

            this.dialog.setMessage("Recherche en cours...");
            this.dialog.show();

        }
        protected SearchMemberResult doInBackground(SearchCriteria... criteria) {
            try {
                AppSession.currentPage = 1;
                searchCriteria = criteria[0];
                SearchMemberResult searchMemberResult = AppSession.getApi(context).searchMember2(AppSession.currentPage, criteria[0]);
                return searchMemberResult;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        protected void onPostExecute(SearchMemberResult searchMemberResult) {
            /*SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putString("max_distance", searchCriteria.max_distance);
            prefsEditor.putString("min_distance", searchCriteria.min_distance);*/
            this.dialog.dismiss();
            if (searchMemberResult != null) {
                AppSession.members = searchMemberResult.members;
                AppSession.currentMembersNumber = searchMemberResult.listingsNumber;
                AppSession.currentSearchCriteria = searchCriteria;
                Log.e("adress long",String.valueOf(searchCriteria.longitude));
                Intent intent = new Intent(context, SearchResultsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
              overridePendingTransition(R.anim.enter, R.anim.exit);

            } else {
                Intent intent = new Intent(context, SearchResultsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
            super.onPostExecute(searchMemberResult);
        }
    }


/*enable gps dialog*/
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialog);
        alertDialogBuilder.setMessage("Chorus ne peut pas obtenir votre emplacement actuel. Voulez-vous modifier vos paramètres de localisation sans fil?")
                .setCancelable(false)
                .setPositiveButton("Continuer",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        /*Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(Color.parseColor("#319b87"));

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#319b87"));*/

    }




    class OfflineTast extends ApiTaskNoDialog<Void, Void, Boolean> {
        //  String idmember;
        OfflineTast(Context context) {
            super(context);
        }

        protected Boolean doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                Boolean result = AppSession.getApi(getBaseContext()).LastActionTime("1");
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        public void onPostExecute(Boolean isadded) {

            AppSession.currentUser = null;
            AppSession.PHPSESSID = null;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
            Intent msgIntent1 = new Intent(MenuWrappAcivity.this, UpdateUserLocationService.class);
            stopService(msgIntent1);



            startActivity(new Intent(MenuWrappAcivity.this, MainActivity.class));

            drawer.closeDrawers();
            finish();


            super.onPostExecute(isadded);
        }

    }
}
