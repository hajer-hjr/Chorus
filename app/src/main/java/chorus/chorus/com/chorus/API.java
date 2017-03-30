package chorus.chorus.com.chorus;
/*
 * Created by admin on 11/05/2016.
 */


        import java.io.*;
        import java.lang.reflect.Member;
        import java.lang.reflect.Type;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;
        import java.util.Map;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import com.google.gson.*;
        import com.google.gson.reflect.TypeToken;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.methods.HttpRequestBase;
        import org.apache.http.client.utils.URLEncodedUtils;
        import org.apache.http.conn.scheme.Scheme;
        import org.apache.http.conn.scheme.SchemeRegistry;
        import org.apache.http.conn.ssl.SSLSocketFactory;
        import org.apache.http.conn.ssl.X509HostnameVerifier;
        import org.apache.http.cookie.Cookie;
        import org.apache.http.entity.StringEntity;
        import org.apache.http.entity.mime.content.FileBody;
        import org.apache.http.entity.mime.content.StringBody;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.impl.conn.SingleClientConnManager;
        import org.apache.http.message.BasicNameValuePair;
        import org.apache.http.util.EntityUtils;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.preference.PreferenceManager;
        import android.text.format.Time;
        import android.util.Base64;
        import android.util.Log;

        import javax.net.ssl.HostnameVerifier;
        import javax.net.ssl.HttpsURLConnection;
        import javax.net.ssl.SSLContext;
        import org.apache.http.entity.mime.HttpMultipartMode;
        import org.apache.http.entity.mime.MultipartEntity;
public class API {
    private Gson gson = new Gson();
    private Context context;

    public API(Context _context) {
        context = _context;
    }

    private JSONObject call(HttpRequestBase request) throws SJBException {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            throw new SJBException("NO_CONNECTION_ERROR");
        }


        DefaultHttpClient httpclient = new DefaultHttpClient();
        if (request.getURI().toString().startsWith("https://")) {
            try {
                HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                SchemeRegistry registry = new SchemeRegistry();
                SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                registry.register(new Scheme("https", socketFactory, 443));

                SingleClientConnManager mgr = new SingleClientConnManager(httpclient.getParams(), registry);
                httpclient = new DefaultHttpClient(mgr, httpclient.getParams());

                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
            } catch (Exception ex) {
                throw new SJBException("NO_CONNECTION_ERROR");
            }
        }

        HttpResponse response;

        try {
            if (AppSession.PHPSESSID != null)
                request.addHeader("Cookie", " PHPSESSID=" + AppSession.PHPSESSID);

            response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().contentEquals("PHPSESSID")) {
                        AppSession.PHPSESSID = cookie.getValue();
                    }
                }
            }

            if (entity != null) {
                InputStream inputStream = entity.getContent();
                String resultString = convertStreamToString(inputStream);

                JSONObject result = new JSONObject(resultString);

                inputStream.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    private JSONObject get(List<NameValuePair> nameValuePairs) throws SJBException {
        String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
        HttpGet httpGet = new HttpGet(context.getResources().getString(R.string.api_base_url) +"/?op=api&" + paramString);
    //  HttpGet httpGet = new HttpGet(context.getResources().getString(R.string.api_base_url) +"/jetunoo/index.php/apiplugin/index1/?" + paramString);
        Log.e("eroor", context.getResources().getString(R.string.api_base_url) + "/?op=api&" + paramString);
        return call(httpGet);
    }

    private JSONObject post(List<NameValuePair> nameValuePairs) throws SJBException {
        //  HttpPost httpPost = new HttpPost(context.getResources().getString(R.string.api_base_url) + "/system/miscellaneous/api/");
       HttpPost httpPost = new HttpPost(context.getResources().getString(R.string.api_base_url) + "/?op=api");
    //  HttpPost httpPost = new HttpPost(context.getResources().getString(R.string.api_base_url) +"/jetunoo/index.php/apiplugin/index1/");
        Log.e("eroor", context.getResources().getString(R.string.api_base_url) + "/?op=api");
        return call(httpPost);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private Boolean isSuccess(JSONObject result) throws SJBException {
        try {
            return result.getString("result").equals("success");
        } catch (JSONException e) {
            throw new SJBException("SERVICE_ERROR");
        } catch (NullPointerException e) {
            throw new SJBException("SERVICE_ERROR");
        }
    }

    private String getJsonObjectString(JSONObject result) throws SJBException {
        try {
            return result.getJSONObject("function_result").toString();
        } catch (JSONException e) {
            throw new SJBException("SERVICE_ERROR");
        }
    }

    private JSONArray getJsonArray(JSONObject result) throws SJBException {
        try {
            return result.getJSONArray("function_result");
        } catch (JSONException e) {
            throw new SJBException("SERVICE_ERROR");
        }
    }


    public User login(String username, String password) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("function", "authenticate"));
        nameValuePairs.add(new BasicNameValuePair("params[email]", username));
        nameValuePairs.add(new BasicNameValuePair("params[password]", password));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {

            try {
                //Log.e("error",result.getString("function_result"));
                if ("EMPTY_VALUE_EMAIL_TEL".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_EMAIL_TEL");
                }
            } catch (JSONException ignored) {
            }

          /*  try {
               // Log.e("error",result.getString("function_result"));
                if ("NOT_VALID_EMAIL_FORMAT".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_VALID_EMAIL_FORMAT");
                }
            } catch (JSONException ignored) {}
*/
            try {
                //  Log.e("error",result.getString("function_result"));
                if ("EMPTY_VALUE_PASSWORD".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PASSWORD");
                }
            } catch (JSONException ignored) {
            }


            try {
                Log.e("error", result.getString("function_result"));
                if ("INVALID_USERNAME_OR_PASSWORD".equals(result.getString("function_result"))) {
                    throw new SJBException("INVALID_USERNAME_OR_PASSWORD");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("FORBIDDEN_ACCOUNT".equals(result.getString("function_result"))) {
                    throw new SJBException("FORBIDDEN_ACCOUNT");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("FORBIDDEN_ATTENTE".equals(result.getString("function_result"))) {
                    throw new SJBException("FORBIDDEN_ATTENTE");
                }
            } catch (JSONException ignored) {
            }

            return gson.fromJson(getJsonObjectString(result), User.class);
        } else {
            throw new SJBException("API_ERROR");
        }
    }
    public ArrayList<Country> getCountries() throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "get_pays"));
        nameValuePairs.add(new BasicNameValuePair("params[sexe]", ""));
        JSONObject result = get(nameValuePairs);

        if (isSuccess(result)) {
            JSONArray locations = getJsonArray(result);

            ArrayList<Country> countries = new ArrayList<Country>();

            Country stubCountry = new Country();
            stubCountry.id = "";
            stubCountry.caption = context.getResources().getString(R.string.hint_country);
            countries.add(stubCountry);
            try {
                for (int i = 0; i < locations.length(); i++) {
                    Country country = gson.fromJson(locations.getJSONObject(i).toString(), Country.class);
                    countries.add(country);
                }
            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return countries;
        } else {
            throw new SJBException("API_ERROR");
        }
    }

    public User register(String email, String username, String password, String name, String surname, String confirm_password, String date_naiss,String sexe,String pays,String tel,String paroisse,String verset) throws SJBException {
      ///  AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("function", "save_member"));
        nameValuePairs.add(new BasicNameValuePair("params[email]", email));
        nameValuePairs.add(new BasicNameValuePair("params[username]", username));
        nameValuePairs.add(new BasicNameValuePair("params[name]", name));
        nameValuePairs.add(new BasicNameValuePair("params[surname]", surname));
        nameValuePairs.add(new BasicNameValuePair("params[password]", password));
        nameValuePairs.add(new BasicNameValuePair("params[confirm_password]", confirm_password));
        nameValuePairs.add(new BasicNameValuePair("params[date_naiss]", date_naiss));
        nameValuePairs.add(new BasicNameValuePair("params[sexe]", sexe));
        nameValuePairs.add(new BasicNameValuePair("params[pays]", pays));
        nameValuePairs.add(new BasicNameValuePair("params[tel]", tel));
       // nameValuePairs.add(new BasicNameValuePair("params[taille]", taille));
        nameValuePairs.add(new BasicNameValuePair("params[paroisse]", paroisse));
      nameValuePairs.add(new BasicNameValuePair("params[verset]", verset));
        JSONObject result = get(nameValuePairs);

       /* if (isSuccess(result)) {
            try {
                JSONObject errors = result.getJSONObject("function_result").getJSONObject("errors");
                Iterator fields = errors.keys();
                String fieldName = (String)fields.next();
                String errorCode = errors.getString(fieldName);
                throw new SJBException(errorCode, fieldName);
            } catch (JSONException ignored) {}
*/
        if (isSuccess(result)) {
            try {
                Log.e("result",result.getString("function_result"));            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_EMAIL".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_EMAIL");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_USERNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_USERNAME");
                }
            } catch (JSONException ignored) {
            }


            try {
                if ("NOT_VALID_EMAIL_FORMAT".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_VALID_EMAIL_FORMAT");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_PASSWORD".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PASSWORD");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_CONFIRM_PASSWORD".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_CONFIRM_PASSWORD");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_NAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_NAME");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_SURNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_SURNAME");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_PAROISSE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PAROISSE");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_ORIGINE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_ORIGINE");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("NOT_VALID_CONFIRM_PASSWORD".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_VALID_CONFIRM_PASSWORD");
                }
            } catch (JSONException ignored) {
            }

            try {
                Log.e("error", result.getString("function_result"));
                if ("NOT_UNIQUE_VALUE_EMAIL".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_UNIQUE_VALUE_EMAIL");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("NOT_UNIQUE_VALUE_USERNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_UNIQUE_VALUE_USERNAME");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("NOT_UNIQUE_VALUE_TEL".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_UNIQUE_VALUE_TEL");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_PAYS".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PAYS");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_TEL".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_TEL");
                }
            } catch (JSONException ignored) {
            }

                return gson.fromJson(getJsonObjectString(result), User.class);

        } else {
            throw new SJBException("API_ERROR");
        }
    }
    public ArrayList<NewsCategorie> getNewCetagories() throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "getNewsCategories"));
        nameValuePairs.add(new BasicNameValuePair("params[user]",""));
        JSONObject result = get(nameValuePairs);
        ArrayList<NewsCategorie> categories = new ArrayList<NewsCategorie>();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);

            try {
                for (int i = 0; i < reslt.length(); i++) {
                    NewsCategorie categorie = gson.fromJson(reslt.getJSONObject(i).toString(), NewsCategorie.class);
                    categories.add(categorie);
                    Log.e("reference", categorie.cat_name);
                }
            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }
            return categories;
        } else {
            throw new SJBException("API_ERROR");
        }

    }



    public Boolean saveCatagories(String selected) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "saveUserCategories"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[selected]",selected));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public ImageProfil savepicture2(ArrayList<String> data,File image) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        String json=null;
        ImageProfil myClassObj=null;
     //   Log.e("data",data.get(1));
        //  Log.e("result content",result.toString());
        try {
            // ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /// photo.compress(Bitmap.CompressFormat.JPEG, 75, bos);

            //byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(context.getResources().getString(R.string.api_base_url) + "/?op=api&action=save_picture2");
            //ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
            // File file= new File("/mnt/sdcard/forest.png");
            // FileBody bin = new FileBody(file);
            Log.e("url", context.getResources().getString(R.string.api_base_url) + "/?op=api&action=save_picture2");
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("function", new StringBody("save_picture"));
          //  reqEntity.addPart("base64", new StringBody(data.get(1)));
            reqEntity.addPart("user", new StringBody((AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
            reqEntity.addPart("photo", new StringBody("img_"+System.currentTimeMillis() +"_"+AppSession.currentUser.id+ ".png"));
            reqEntity.addPart("image",  new FileBody(image));
            // reqEntity.addPart("upload", new ByteArrayBody(data.get(2).getBytes(),"image/jpeg",System.currentTimeMillis() + ".jpg"));
          //  Log.e("hhhhhhhhhhhh",data.get(1));
            postRequest.setEntity(reqEntity);
            Log.e("request",postRequest.toString());
            HttpResponse response = httpClient.execute(postRequest);
         /*   BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            json = s.toString();*/
            String result = EntityUtils.toString(response.getEntity());

            // CONVERT RESPONSE STRING TO JSON ARRAY
            //  JSONArray ja = new JSONArray(result);

            //ImageProfil logo=new ImageProfil();
            // ITERATE THROUGH AND RETRIEVE CLUB FIELDS

            Gson gson = new Gson();
            myClassObj = gson.fromJson(result, ImageProfil.class);
            Log.e("response",myClassObj.toString());

            //  JSONObject jresponse = new JSONObject(sResponse);
            //String result = EntityUtils.toString(response.getEntity());
            //jo = new JSONObject(result);
            // CONVERT RESPONSE STRING TO JSON ARRAY*/
            // JSONObject jo = new JSONObject(json);
            //JSONArray ja = new JSONArray(result);
            //    Log.e("Response: " , jresponse.toString());
            ////  Gson gson = new GsonBuilder().create();
            //  JsonParser jsonParser = new JsonParser();

            //String response = client.getResponse();
            //  JsonObject jsonResp = jsonParser.parse(result).getAsJsonObject();
            // Important note: JsonObject is the one from GSON lib!
            // now you build as you like e.g.
            return myClassObj;


            // if (isSuccess(jo)) {

            //  Log.e("retour",jo.toString());
            // return gson.fromJson(json,ImageResult.class);




          /*  } else {

                throw new SJBException("API_ERROR");

            }*/

        } catch (Exception e) {
            // handle exception here
            //Log.e("JSON Parser0", "Error parsing data [" + e.getMessage()+"] "+ json);
            //  Log.e(e.getClass().getName(), e.getMessage());
            throw new SJBException("API_ERROR");
            //return null;

        }

    }
    public ImageProfil savepicture1(ArrayList<String> data) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        String json=null;
        ImageProfil myClassObj=null;
        //Log.e("data",data.get(1));
        //  Log.e("result content",result.toString());
        try {
            // ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /// photo.compress(Bitmap.CompressFormat.JPEG, 75, bos);

            //byte[] data = bos.toByteArray();
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(context.getResources().getString(R.string.api_base_url) + "/?op=api&action=save_picture2");
            //ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");
            // File file= new File("/mnt/sdcard/forest.png");
            // FileBody bin = new FileBody(file);
            Log.e("url", context.getResources().getString(R.string.api_base_url) + "/?op=api&action=save_picture2");
            MultipartEntity reqEntity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("function", new StringBody("save_picture"));
            reqEntity.addPart("base64", new StringBody(data.get(1)));
            reqEntity.addPart("user", new StringBody((AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
            reqEntity.addPart("photo", new StringBody("img_"+System.currentTimeMillis() +"_"+AppSession.currentUser.id+ ".png"));
            // reqEntity.addPart("upload", new ByteArrayBody(data.get(2).getBytes(),"image/jpeg",System.currentTimeMillis() + ".jpg"))
            postRequest.setEntity(reqEntity);
            Log.e("request",postRequest.toString());
            HttpResponse response = httpClient.execute(postRequest);
         /*   BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            json = s.toString();*/
                String result = EntityUtils.toString(response.getEntity());

                // CONVERT RESPONSE STRING TO JSON ARRAY
              //  JSONArray ja = new JSONArray(result);

                //ImageProfil logo=new ImageProfil();
                // ITERATE THROUGH AND RETRIEVE CLUB FIELDS

                Gson gson = new Gson();
               myClassObj = gson.fromJson(result, ImageProfil.class);
                Log.e("response",myClassObj.toString());

          //  JSONObject jresponse = new JSONObject(sResponse);
            //String result = EntityUtils.toString(response.getEntity());
            //jo = new JSONObject(result);
            // CONVERT RESPONSE STRING TO JSON ARRAY*/
            // JSONObject jo = new JSONObject(json);
            //JSONArray ja = new JSONArray(result);
        //    Log.e("Response: " , jresponse.toString());
            ////  Gson gson = new GsonBuilder().create();
            //  JsonParser jsonParser = new JsonParser();

            //String response = client.getResponse();
            //  JsonObject jsonResp = jsonParser.parse(result).getAsJsonObject();
            // Important note: JsonObject is the one from GSON lib!
            // now you build as you like e.g.
           return myClassObj;


            // if (isSuccess(jo)) {

            //  Log.e("retour",jo.toString());
            // return gson.fromJson(json,ImageResult.class);




          /*  } else {

                throw new SJBException("API_ERROR");

            }*/

        } catch (Exception e) {
            // handle exception here
            //Log.e("JSON Parser0", "Error parsing data [" + e.getMessage()+"] "+ json);
          //  Log.e(e.getClass().getName(), e.getMessage());
            throw new SJBException("API_ERROR");
            //return null;

        }

    }

    public User updateprofil(String email, String username, String name, String surname, String date_naiss, String pays, String paroisse,String verset) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("function", "updateprofil"));
        nameValuePairs.add(new BasicNameValuePair("params[email]", email));
        nameValuePairs.add(new BasicNameValuePair("params[username]", username));
        nameValuePairs.add(new BasicNameValuePair("params[name]", name));
        nameValuePairs.add(new BasicNameValuePair("params[surname]", surname));
        nameValuePairs.add(new BasicNameValuePair("params[date_naiss]", date_naiss));
        nameValuePairs.add(new BasicNameValuePair("params[pays]", pays));
        nameValuePairs.add(new BasicNameValuePair("params[paroisse]", paroisse));
        nameValuePairs.add(new BasicNameValuePair("params[user]", AppSession.currentUser!=null? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[verset]", verset));
        JSONObject result = get(nameValuePairs);

       /* if (isSuccess(result)) {
            try {
                JSONObject errors = result.getJSONObject("function_result").getJSONObject("errors");
                Iterator fields = errors.keys();
                String fieldName = (String)fields.next();
                String errorCode = errors.getString(fieldName);
                throw new SJBException(errorCode, fieldName);
            } catch (JSONException ignored) {}
*/
        if (isSuccess(result)) {

            try {
                if ("EMPTY_VALUE_EMAIL".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_EMAIL");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_USERNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_USERNAME");
                }
            } catch (JSONException ignored) {
            }


            try {
                if ("NOT_VALID_EMAIL_FORMAT".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_VALID_EMAIL_FORMAT");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_NAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_NAME");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_SURNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_SURNAME");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("EMPTY_VALUE_PAYS".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PAYS");
                }
            } catch (JSONException ignored) {
            }

            try {
                Log.e("error", result.getString("function_result"));
                if ("NOT_UNIQUE_VALUE_EMAIL".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_UNIQUE_VALUE_EMAIL");
                }
            } catch (JSONException ignored) {
            }

            try {
                if ("NOT_UNIQUE_VALUE_USERNAME".equals(result.getString("function_result"))) {
                    throw new SJBException("NOT_UNIQUE_VALUE_USERNAME");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_PAROISSE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_PAROISSE");
                }
            } catch (JSONException ignored) {
            }
            try {
                if ("EMPTY_VALUE_ORIGINE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_ORIGINE");
                }
            } catch (JSONException ignored) {
            }
            return gson.fromJson(getJsonObjectString(result), User.class);
        } else {
            throw new SJBException("API_ERROR");
        }
    }
    public SearchMemberResult searchMember(Integer page, SearchCriteria criteria) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "search_member"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
     /*   nameValuePairs.add(new BasicNameValuePair("params[min_distance]", criteria.min_distance));
        nameValuePairs.add(new BasicNameValuePair("params[max_distance]", criteria.max_distance));*/
        nameValuePairs.add(new BasicNameValuePair("params[max_distance]", AppSession.currentUser.distance));
        nameValuePairs.add(new BasicNameValuePair("params[search_homme]", AppSession.currentUser.mem_search_homme.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[search_femme]", AppSession.currentUser.mem_search_femme.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[age_min]", AppSession.currentUser.mem_search_age_min));
        nameValuePairs.add(new BasicNameValuePair("params[age_max]", AppSession.currentUser.mem_search_age_max));
        nameValuePairs.add(new BasicNameValuePair("params[longitude]",String.valueOf(criteria.longitude)));
        nameValuePairs.add(new BasicNameValuePair("params[altitude]",String.valueOf(criteria.latitude)));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[time]",String.valueOf(System.currentTimeMillis())));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "10"));
        JSONObject result = get(nameValuePairs);
        SearchMemberResult searchResult = new SearchMemberResult();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<Membre> members = new ArrayList<Membre>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");

                for (int i = 1; i < reslt.length(); i++) {
                    Membre member = gson.fromJson(reslt.getJSONObject(i).toString(), Membre.class);
                    members.add(member);
                    Log.e("member.email",member.email);
                }

                searchResult.members = members;
                searchResult.listingsNumber = Integer.parseInt(total);


                if (searchResult.members.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return searchResult;
        } else {
            throw new SJBException("API_ERROR");
        }


    }
    public SearchMemberResult searchMember2(Integer page, SearchCriteria criteria) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "search_member2"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
     /*   nameValuePairs.add(new BasicNameValuePair("params[min_distance]", criteria.min_distance));
        nameValuePairs.add(new BasicNameValuePair("params[max_distance]", criteria.max_distance));*/
        nameValuePairs.add(new BasicNameValuePair("params[max_distance]", (AppSession.currentUser != null) ? AppSession.currentUser.distance : "10"));
        nameValuePairs.add(new BasicNameValuePair("params[longitude]",String.valueOf(criteria.longitude)));
        nameValuePairs.add(new BasicNameValuePair("params[altitude]",String.valueOf(criteria.latitude)));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[time]",String.valueOf(System.currentTimeMillis())));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "10"));
        JSONObject result = get(nameValuePairs);
        SearchMemberResult searchResult = new SearchMemberResult();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<Membre> members = new ArrayList<Membre>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");

                for (int i = 1; i < reslt.length(); i++) {
                    Membre member = gson.fromJson(reslt.getJSONObject(i).toString(), Membre.class);
                    members.add(member);
                }

                searchResult.members = members;
                searchResult.listingsNumber = Integer.parseInt(total);
                if (searchResult.members.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return searchResult;
        } else {
            throw new SJBException("API_ERROR");
        }


    }
    public UserLocation updateUserLocation(Double longitude,double altitude) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "updatelocation"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[longitude]",String.valueOf(longitude)));
        nameValuePairs.add(new BasicNameValuePair("params[altitude]",String.valueOf(altitude)));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {

            return gson.fromJson(getJsonObjectString(result), UserLocation.class);
        } else {
            throw new SJBException("API_ERROR");
        }


    }



    //send coeur/plus request
    public Boolean sendRequest(String mem_id,String type) throws SJBException {
     ///   AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "saveRequest"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[mem_id]",mem_id));
        nameValuePairs.add(new BasicNameValuePair("params[type]",type));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public InvitationsList invitRequest(Integer page) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "invitRequest"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "10"));
        JSONObject result = get(nameValuePairs);
        InvitationsList Searchinvit=new InvitationsList();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<InvitObject> invitations = new ArrayList<InvitObject>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");

                for (int i=1; i < reslt.length(); i++) {
                    InvitObject invit = gson.fromJson(reslt.getJSONObject(i).toString(), InvitObject.class);
                    invitations.add(invit);
                }

                Searchinvit.invitations = invitations;
                Searchinvit.listingsNumber = Integer.parseInt(total);
                if (Searchinvit.invitations.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return Searchinvit;
        } else {
            throw new SJBException("API_ERROR");
        }


    }
    public ContactsList contactRequest(Integer page) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "contactRequest"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[longitude]",String.valueOf(AppSession.currentUser.longitude)));
        nameValuePairs.add(new BasicNameValuePair("params[altitude]",String.valueOf(AppSession.currentUser.latitude)));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "10"));
        JSONObject result = get(nameValuePairs);
       ContactsList Searchcontact=new  ContactsList();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<ContactObjet> contacts = new ArrayList<ContactObjet>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");

                for (int i = 1; i < reslt.length(); i++) {
                    ContactObjet contact = gson.fromJson(reslt.getJSONObject(i).toString(), ContactObjet.class);
                    contacts.add(contact);
                }

                Searchcontact.contacts = contacts;
                Searchcontact.listingsNumber = Integer.parseInt(total);
                if (Searchcontact.contacts.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return Searchcontact;
        } else {
            throw new SJBException("API_ERROR");
        }


    }
    public FavorisList favRequest(Integer page) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "favRequest"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "10"));
        JSONObject result = get(nameValuePairs);
        FavorisList Searchfav=new  FavorisList();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<FavorisObject> favoris = new ArrayList<FavorisObject>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");


                for (int i = 1; i < reslt.length(); i++) {
                    FavorisObject fav = gson.fromJson(reslt.getJSONObject(i).toString(), FavorisObject.class);
                    favoris.add(fav);
                }

                Searchfav.favoris =favoris;
                Searchfav.listingsNumber = Integer.parseInt(total);
                if (Searchfav.favoris.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return Searchfav;
        } else {
            throw new SJBException("API_ERROR");
        }


    }
    public Boolean saveFavoris(String mem_id) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "saveFavoris"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[mem_id]",mem_id));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean deleteFavoris(String id) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "deleteFavoris"));
        nameValuePairs.add(new BasicNameValuePair("params[id]",id));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean saveRequest(String mem_id,String type) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "saveContact"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[mem_id]",mem_id));
        nameValuePairs.add(new BasicNameValuePair("params[type]",type));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean DeleteInvit(String id) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "deleteInvit"));
        nameValuePairs.add(new BasicNameValuePair("params[id]",id));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean saveSearchParams(String distance) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "savesearchParams"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[distance]",distance));

        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean saveSearchParamsAge(String MinValue,String MaxValue) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "update_search_age"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[search_age_min]",MinValue));
        nameValuePairs.add(new BasicNameValuePair("params[search_age_max]",MaxValue));

        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean saveSearchParamsHomme(String mem_search_homme) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "update_search_homme"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[mem_search_homme]",mem_search_homme));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }


    public Boolean saveSearchParamsFemme(String mem_search_femme) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "update_search_femme"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[mem_search_femme]",mem_search_femme));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean deleteFromFavoris(String idmem) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "deleteFromFavoris"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[id_mem]",idmem));
        JSONObject result = get(nameValuePairs);
        String g=result.toString();
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public Boolean deleteFromContact(String idmem) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "deleteFromContact"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[id_mem]",idmem));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }

    }

    public Boolean LastActionTime(String offline) throws SJBException{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "lastActivity"));
        nameValuePairs.add(new BasicNameValuePair("params[id]",AppSession.currentUser.id));
        nameValuePairs.add(new BasicNameValuePair("params[time]",String.valueOf(System.currentTimeMillis())));
        nameValuePairs.add(new BasicNameValuePair("params[offline]",offline));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }


    }


    public MessagesResult UserMessagesList2(String member_to) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "getMessages2"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[user]", AppSession.currentUser.id!=null?AppSession.currentUser.id:""));
        nameValuePairs.add(new BasicNameValuePair("params[member_to]", member_to));
        JSONObject result = get(nameValuePairs);
        MessagesResult searchResult = new MessagesResult();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<Message> messages = new ArrayList<Message>();
            String total,nb_new_messages;

            try {
                total = reslt.getJSONObject(0).getString("total_rows");
                nb_new_messages = reslt.getJSONObject(1).getString("infos_nb_new_messages");
                if (Integer.parseInt(total) != 0) {
                    Log.e("totaall", total);
                    for (int i = 2; i < reslt.length(); i++) {
                        Message message = gson.fromJson(reslt.getJSONObject(i).toString(), Message.class);
                        messages.add(message);
                    }
                    searchResult.messages = messages;
                    searchResult.listingsNumber = Integer.parseInt(total);
                    AppSession.currentUser.infos_nb_new_messages=Integer.parseInt(nb_new_messages);

                } else
                    throw new SJBException("NOTHING_FOUND");
            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return searchResult;
        } else {
            throw new SJBException("API_ERROR");
        }

    }
    public MessagesResult UserMessagesList(Integer page, String type_aff, String member_to) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "getMessages"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[type_aff]", type_aff));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[page]", page.toString()));
        nameValuePairs.add(new BasicNameValuePair("params[member_to]", member_to));
        nameValuePairs.add(new BasicNameValuePair("params[listings_per_page]", "150"));
        JSONObject result = get(nameValuePairs);
        MessagesResult searchResult = new MessagesResult();
        if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<Message> messages = new ArrayList<Message>();
            String total,nb_new_messages;

            try {
                total = reslt.getJSONObject(0).getString("total_rows");
                nb_new_messages = reslt.getJSONObject(1).getString("infos_nb_new_messages");
                Log.e("nb_new_messages",nb_new_messages);
                Log.e("total",total);
                if (Integer.parseInt(total) != 0) {
                    Log.e("totaall", total);
                    for (int i = 2; i < reslt.length(); i++) {
                        Message message = gson.fromJson(reslt.getJSONObject(i).toString(), Message.class);
                        messages.add(message);
                    }
                    searchResult.messages = messages;
                    searchResult.listingsNumber = Integer.parseInt(total);
                    // AppSession.currentUser.infos_nb_new_messages=Integer.parseInt(nb_new_messages);

                } else
                    throw new SJBException("NOTHING_FOUND");
            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return searchResult;
        } else {
            throw new SJBException("API_ERROR");
        }

    }

    //send message from user to other member

    public Boolean addMessagesTo(String message, String member_to,String type, String file_url) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "doaddMessageTo"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        nameValuePairs.add(new BasicNameValuePair("params[message]", message));
        nameValuePairs.add(new BasicNameValuePair("params[user]", AppSession.currentUser.id));
        nameValuePairs.add(new BasicNameValuePair("params[member_to]", member_to));
        nameValuePairs.add(new BasicNameValuePair("params[type]", type));
        nameValuePairs.add(new BasicNameValuePair("params[file_url]", file_url));
        JSONObject result = get(nameValuePairs);
        Boolean issent;
        if (isSuccess(result)) {
            try {
                return "success".equals(result.getString("function_result"));
            } catch (JSONException e) {
                throw new SJBException("API_ERROR");
            }
        } else {
            throw new SJBException("API_ERROR");
        }
    }

    //delete user conversation

    public Boolean deletemessage(String member_to) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "supprimer"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        //nameValuePairs.add(new BasicNameValuePair("params[message]",message));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[member_to]", member_to));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {

            return true;
        } else {
            throw new SJBException("API_ERROR");
        }
    }
    public Boolean changeHommeStatut(String member_to) throws SJBException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("function", "supprimer"));
        // nameValuePairs.add(new BasicNameValuePair("params[listing_type]", criteria.listingType));
        //nameValuePairs.add(new BasicNameValuePair("params[message]",message));
        nameValuePairs.add(new BasicNameValuePair("params[user]", (AppSession.currentUser != null) ? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[member_to]", member_to));
        JSONObject result = get(nameValuePairs);
        if (isSuccess(result)) {

            return true;
        } else {
            throw new SJBException("API_ERROR");
        }
    }


    ////////////////////////////
    public Boolean updateverset(String verset) throws SJBException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("function", "updateverset"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", AppSession.currentUser!=null? AppSession.currentUser.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[verset]", verset));
        JSONObject result = get(nameValuePairs);


       /* if (isSuccess(result)) {
            try {
                JSONObject errors = result.getJSONObject("function_result").getJSONObject("errors");
                Iterator fields = errors.keys();
                String fieldName = (String)fields.next();
                String errorCode = errors.getString(fieldName);
                throw new SJBException(errorCode, fieldName);
            } catch (JSONException ignored) {}
*/
        if (isSuccess(result)) {

            try {
                if ("EMPTY_VALUE_ORIGINE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_ORIGINE");
                }
            } catch (JSONException ignored) {
            }
            return true;
        } else {
            throw new SJBException("API_ERROR");
        }
    }

    ////////////////////////////////
    public String countLikes(String verset) throws SJBException, JSONException {
        AppSession.getApi(context).LastActionTime("0");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("function", "getcountlike"));
        nameValuePairs.add(new BasicNameValuePair("params[user]", AppSession.currentMember!=null? AppSession.currentMember.id : ""));
        nameValuePairs.add(new BasicNameValuePair("params[verset]", AppSession.currentMember.verset));
        JSONObject result = get(nameValuePairs);

        JSONArray reslt = getJsonArray(result);

        Log.d("result bla bla ",reslt+"hh");
///////
        /*if (isSuccess(result)) {
            JSONArray reslt = getJsonArray(result);
            ArrayList<int> liks = new ArrayList<int>();
            String total;
            try {
                total = reslt.getJSONObject(0).getString("total_rows");

                for (int i=1; i < reslt.length(); i++) {
                    InvitObject invit = gson.fromJson(reslt.getJSONObject(i).toString(), InvitObject.class);
                    invitations.add(invit);
                }

                Searchinvit.invitations = invitations;
                Searchinvit.listingsNumber = Integer.parseInt(total);
                if (Searchinvit.invitations.size() == 0)
                    throw new SJBException("NOTHING_FOUND");

            } catch (JSONException e) {
                throw new SJBException("SERVICE_ERROR");
            }

            return Searchinvit;
        } else {
            throw new SJBException("API_ERROR");
        } */

///////////

        if (isSuccess(result)) {

            try {
                if ("EMPTY_VALUE_ORIGINE".equals(result.getString("function_result"))) {
                    throw new SJBException("EMPTY_VALUE_ORIGINE");
                }
            } catch (JSONException ignored) {
            }
            //return count;
            return result.toString();
        } else {
            throw new SJBException("API_ERROR");
        }
    }


}

