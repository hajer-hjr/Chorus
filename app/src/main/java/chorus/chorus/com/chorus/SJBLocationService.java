package chorus.chorus.com.chorus;


import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Locale;

public class SJBLocationService {

    public static LatLng getLocationFromString(String address) {

        try {
            HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                return new LatLng(lat, lng);
            }

            return null;

        } catch (ClientProtocolException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getStringFromLocation(double lat, double lng) {

        String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true", lat, lng);
        HttpGet httpGet = new HttpGet(address);
        Log.e("adress url", address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();

            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            String resultAddress = "";
            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                JSONObject result = results.getJSONObject(0);

                JSONArray addressComponents = result.getJSONArray("address_components");

                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject addressComponent = addressComponents.getJSONObject(i);

                    JSONArray types = addressComponent.getJSONArray("types");
                    for (int k = 0; k < types.length(); k++) {
                        String type = types.getString(k);
                        if (type.equals("postal_code") || type.equals("locality") || type.equals("administrative_area_level_1")) {
                            resultAddress += addressComponent.getString("short_name") + ", ";
                        }
                    }
                }
            }

            if (!resultAddress.equals("")) {
                return resultAddress.substring(0, resultAddress.length() - 2);
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        } catch (JSONException e) {
            return "";
        }
    }
}
