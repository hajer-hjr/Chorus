package chorus.chorus.com.chorus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // init global boolean
    private boolean isReached = false;
    //
private SearchCriteria criteria;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
////////////////////////////////////////////////////


///////////////////////////////////////////////////
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;


        //////////////////////////////////////////////////////

    }
   @Override
    public void onResume() {
        super.onResume();
       Gson gson = new Gson();
       SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
       String json = mPrefs.getString("user", "");
       User obj = gson.fromJson(json, User.class);
       if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String imageURI;
        ImageButton geoButt;
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.my_account, container, false);


        if (AppSession.currentUser != null) {
            // final TextView verset = (TextView) view.findViewById(R.id.verset);
            TextView profession = (TextView) view.findViewById(R.id.profession);
            TextView age = (TextView) view.findViewById(R.id.age);
            TextView textUsername = (TextView) view.findViewById(R.id.username);
            TextView distance = (TextView) view.findViewById(R.id.distance);
            final EmojiconEditText VerText = (EmojiconEditText) view.findViewById(R.id.verset);


            // verset.setText(AppSession.currentMember.verset);

            // LinearLayout Linear1=(LinearLayout)view.findViewById(R.id.sexeLayout);
            // RelativeLayout Linear1=(RelativeLayout) view.findViewById(R.id.sexeLayout);
            ImageView Linear1 = (ImageView) view.findViewById(R.id.iv1);
            if (AppSession.currentUser.sexe.equals("F"))
                Linear1.setBackgroundResource(R.drawable.female_bkg2);
            else

                Linear1.setBackgroundResource(R.drawable.male_bkg2);


            final CircularImageView imageLogo = (CircularImageView) view.findViewById(R.id.logo);
            imageLogo.setBorderWidth(0);
            if (AppSession.currentUser != null) {
                profession.setText(AppSession.currentUser.paroisse);
                VerText.setText(AppSession.currentUser.verset);
                age.setText(toString().valueOf(AppSession.currentUser.getAge(AppSession.currentUser.date_naiss)));
                textUsername.setText(AppSession.currentUser.username + ", ");
            }
            if (AppSession.currentUser.Logo.thumb_file_url != null) {

                Log.e("logo url", this.getResources().getString(R.string.api_base_url) + AppSession.currentUser.Logo.thumb_file_url);


                DownloadImageTask ta = new DownloadImageTask(imageLogo);
                ta.execute(getContext().getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url);
                imageLogo.setVisibility(View.VISIBLE);

///////////////////////// //////////
                final ImageButton button = (ImageButton) view.findViewById(R.id.EditButton);
                //final EditText VerText = (EditText)view.findViewById(R.id.verset);
                final View rootView = view.findViewById(R.id.verset_area);
                final ImageView emojiButton = (ImageView) view.findViewById(R.id.emojicon_edit_text);

                final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(VerText, InputMethodManager.SHOW_FORCED);
                        VerText.setEnabled(true);
                        VerText.setCursorVisible(true);
                        VerText.setFocusableInTouchMode(true);
                        VerText.setInputType(InputType.TYPE_CLASS_TEXT);
                        VerText.requestFocus(); //to trigger the soft input
                        //VerText.setBackgroundResource(R.drawable.edittext_custom);

                        //emojiButton.setVisibility(View.VISIBLE);

                        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.);

                            /*InputFilter[] filters = new InputFilter[1];
                            filters[0] = new InputFilter.LengthFilter(20); //Filter to 20 characters
                            VerText.setFilters(filters);
                            //VerText.setScroller(new Scroller(getContext()));
                             VerText.setMaxLines(3);
                            VerText.setVerticalScrollBarEnabled(true);
                            VerText.setMovementMethod(new ScrollingMovementMethod());*/

                    }
                });


// in onCreate method
                /*VerText.addTextChangedListener(new TextWatcher(){
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // if edittext has 10chars & this is not called yet, add new line
                        if(VerText.getText().length() == 40 && !isReached) {
                            VerText.append("\r");
                            isReached = true;
                        }
                        // if edittext has less than 10chars & boolean has changed, reset
                        if(VerText.getText().length() < 40 && isReached)
                            isReached = false;
                    }
                });*/


               /* VerText.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        VerText.requestLayout();
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );

                        return false;
                    }
                });*/

                /*button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.performClick();
                        }
                    }
                });*/


                // VerText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                   /* @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        Log.e("hhh","here");
                        String username = getActivity().getIntent().getStringExtra("username");


                        //new updateVersetTask(this).execute(username.toString(),verset.getText());
                        new updateVersetTask(getContext()).execute(verset.getText().toString());
                        }
                    }
                ); */
                //final EditText editText = (EditText) view.findViewById(R.id.verset);
                VerText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(VerText.getWindowToken(), 0);
                            String message = encrypt(getActivity(), VerText.getText().toString());
                            new updateVersetTask(getContext()).execute(message);
                            //emojiButton.setVisibility(View.INVISIBLE);
                            VerText.setFocusable(false);
                            handled = true;

                        }
                        return handled;
                    }
                });


                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
                    }
                });

                //Will automatically set size according to the soft keyboard size
                popup.setSizeForSoftKeyboard();

                //If the emoji popup is dismissed, change emojiButton to smiley icon
                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
                    }
                });

                //If the text keyboard closes, also dismiss the emoji popup
                popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

                    @Override
                    public void onKeyboardOpen(int keyBoardHeight) {

                    }

                    @Override
                    public void onKeyboardClose() {
                        if (popup.isShowing())
                            popup.dismiss();
                        VerText.setFocusable(false);

                    }
                });

                //On emoji clicked, add it to edittext
                popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

                    @Override
                    public void onEmojiconClicked(Emojicon emojicon) {
                        if (VerText == null || emojicon == null) {
                            return;
                        }

                        int start = VerText.getSelectionStart();
                        int end = VerText.getSelectionEnd();
                        if (start < 0) {
                            VerText.append(emojicon.getEmoji());
                        } else {
                            VerText.getText().replace(Math.min(start, end),
                                    Math.max(start, end), emojicon.getEmoji(), 0,
                                    emojicon.getEmoji().length());
                        }
                    }
                });

                //On backspace clicked, emulate the KEYCODE_DEL key event
                popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

                    @Override
                    public void onEmojiconBackspaceClicked(View v) {
                        KeyEvent event = new KeyEvent(
                                0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                        VerText.dispatchKeyEvent(event);
                    }
                });

                // To toggle between text keyboard and emoji keyboard keyboard(Popup)
                emojiButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        VerText.setEnabled(true);
                        VerText.setCursorVisible(true);
                        VerText.setFocusableInTouchMode(true);
                        VerText.setInputType(InputType.TYPE_CLASS_TEXT);
                        VerText.requestFocus(); //to trigger the soft input
                        //If popup is not showing => emoji keyboard is not visible, we need to show it
                        if (!popup.isShowing()) {

                            //If keyboard is visible, simply show the emoji popup
                            if (popup.isKeyBoardOpen()) {

                                popup.showAtBottom();
                                changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                            }

                            //else, open the text keyboard first and immediately after that show the emoji popup
                            else {
                                VerText.setFocusableInTouchMode(true);
                                VerText.requestFocus();
                                popup.showAtBottomPending();
                                changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(VerText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }

                        //If popup is showing, simply dismiss it to show the undelying text keyboard
                        else {
                            popup.dismiss();
                        }
                    }
                });

                //////////////////////////////////////
                ImageButton like_button = (ImageButton) view.findViewById(R.id.LikeButton);
                final TextView likesText= (TextView) view.findViewById(R.id.likes);

                like_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        Log.d("myTag","here");
                        likesText.setText("hi");
                        //likesText.setText("new");
                        //new LikesTask(getContext()).execute();


                    }


                });


                //////

                ImageButton discusButt = (ImageButton) view.findViewById(R.id.discusButt);
                discusButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), MesMessagesActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);


                    }
                });
                geoButt = (ImageButton) view.findViewById(R.id.geoButt);
                geoButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        criteria = new SearchCriteria();
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                        Double log = Double.parseDouble(mPrefs.getString("longitude", "0.00"));
                        Double alt = Double.parseDouble(mPrefs.getString("altitude", "0.00"));
                        criteria.latitude = alt;
                        criteria.longitude = log;
                        GPSTracker mGPS = new GPSTracker(getContext());
                        if (mGPS.canGetLocation()) {
                            AppSession.members.clear();
                            ;
                            new SearchMemberTask(getContext()).execute(criteria);
                        } else {
                            mGPS.showGPSDisabledAlertToUser();
                        }
                    }
                });
                ImageButton agendaButt = (ImageButton) view.findViewById(R.id.home_button);
                agendaButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                ImageButton contactButt = (ImageButton) view.findViewById(R.id.contactButt);
                contactButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                /*    Intent i=new Intent(getContext(),MainContacts.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);*/
                        new InvitRequestTask(getContext()).execute();
                        new ContactRequestTask(getContext()).execute();
                        new favRequestTask(getContext()).execute();


                    }
                });


                Transformation transformation = new Transformation() {

                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = imageLogo.getWidth();

                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    }

                    @Override
                    public String key() {
                        return "transformation" + " desiredWidth";
                    }
                };

                //  myage.setText(toString().valueOf(AppSession.currentUser.getAge(AppSession.currentUser.date_naiss)) + " ans");

                //button update profil
                //button update profil


            }

        }
        return view;

        }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater =  getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_uploadphoto, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.frm_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
/*
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                //  Log.e("image name", mImageCaptureUri.toString());
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                try {
                    intent.putExtra("return-data", true);

                  startActivityForResult(intent, PICK_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }*/
                return true;
            case R.id.frm_galerie:
                /*Intent i = new Intent();

                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(i, "Terminer l'action avec"), PICK_FROM_FILE);*/
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
     * See the Android Training lesson <a href=
     * <p>
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class SearchMemberTask extends ApiTask<SearchCriteria, Void, SearchMemberResult> {
        SearchCriteria searchCriteria;

        SearchMemberTask(Context context) {
            super(context,R.string.dialog_searching_jobs);
        }

        protected SearchMemberResult doInBackground(SearchCriteria... criteria) {
            try {
                AppSession.currentPage = 1;
                searchCriteria = criteria[0];
                SearchMemberResult searchMemberResult = AppSession.getApi(context).searchMember(AppSession.currentPage, criteria[0]);
                if (searchMemberResult.members.size() == 0) {
                    throw new SJBException("NOTHING_FOUND");
                }
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

            if (searchMemberResult != null) {
                AppSession.members =null;
                AppSession.members = searchMemberResult.members;
                AppSession.currentMembersNumber = searchMemberResult.listingsNumber;
                AppSession.currentSearchCriteria = criteria;
                Log.e("adress long",String.valueOf(criteria.longitude));
                Intent intent = new Intent(context, ShowSearchResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                Log.e("listingsNumber",searchMemberResult.listingsNumber.toString());

            } else {
                Intent intent = new Intent(context, ShowSearchResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
            super.onPostExecute(searchMemberResult);
        }
    }
    class GetCurrentLocationTask extends ApiTaskNoDialog<Double, Void, String> {

        GetCurrentLocationTask(Context context) {
            super(context);
        }

        protected String doInBackground(Double... coords) {
            Log.e("heee",String.valueOf(coords[0]));
            if (coords[0] != 0 && coords[1] != 0) {

                return SJBLocationService.getStringFromLocation(coords[0], coords[1]);
            } else {
                error = "COULD_NOT_GET_CURRENT_LOCATION";
                return null;
            }
        }

        protected void onPostExecute(String address) {
            if (address != null) {
                Intent intent = new Intent(context, ShowSearchResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                // ((EditText)((SearchActivity)context).findViewById(R.id.location)).setText(address);
            } else {
                if (error.equals("COULD_NOT_GET_CURRENT_LOCATION")) error = context.getResources().getString(R.string.error_could_not_get_current_location);
                showError();
            }
            super.onPostExecute(address);
        }
    }
    class InvitRequestTask extends ApiTask<Void, Void,InvitationsList> {
        //  String idmember;
        InvitRequestTask(Context context) {
            super(context,R.string.dialog_loading_contacts);
        }

        protected InvitationsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                InvitationsList result = AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);
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
        protected void onPostExecute(InvitationsList isadded) {
            if (isadded!=null) {
                AppSession.invitations=null;
                AppSession.invitations=isadded.invitations;
                AppSession.currentinvitationNumber=isadded.listingsNumber;





            }

            super.onPostExecute(isadded);
        }

    }
    class ContactRequestTask extends ApiTaskNoDialog<Void, Void,ContactsList> {
        //  String idmember;
        ContactRequestTask(Context context) {
            super(context);
        }

        protected ContactsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                ContactsList result = AppSession.getApi(context).contactRequest(AppSession.currentContactPage);
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
        protected void onPostExecute(ContactsList isadded) {
            AppSession.contacts=null;
            if (isadded!=null) {
                AppSession.contacts = isadded.contacts;

                AppSession.currentcontactsNumber = isadded.listingsNumber;

            }
            super.onPostExecute(isadded);
          /*  Intent i=new Intent(getContext(),ContactsManagerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);*/
            Intent i=new Intent(getContext(),MainContacts.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
        }

    }


    class favRequestTask extends ApiTaskNoDialog<Void, Void,FavorisList> {
        //  String idmember;
        favRequestTask(Context context) {
            super(context);
        }

        protected FavorisList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                FavorisList  result = AppSession.getApi(context).favRequest(AppSession.currentfavPage);
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
        protected void onPostExecute(FavorisList isadded) {
            if (isadded!=null) {
                AppSession.favoris=null;

                AppSession.favoris=isadded.favoris;
                AppSession.currentfavorisNumber=isadded.listingsNumber;

            }
            super.onPostExecute(isadded);

        }

    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class updateVersetTask extends ApiTask<String, Void, Boolean> {
        //  String idmember;
        private String verset;

        boolean a;
        //final TextView vrst = (TextView) view.findViewById(R.id.verset);

        public updateVersetTask(Context context) {
            super(context, R.string.dialog_registering);
        }

        protected Boolean doInBackground(String... data) {
            this.verset=data[0];

            try {

                Boolean res = AppSession.getApi(context).updateverset(this.verset);

                return res;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }

        protected void onPostExecute(Boolean res) {
            if(res){

                SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.verset= decrypt(getActivity(),this.verset);
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
            }
            super.onPostExecute(null);
        }

    }
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    private static String encrypt(Context cont, String value) {
        try {
            return Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        }
        catch (IllegalArgumentException e) {
            // TODO: handle exception
            return "";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
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


}

