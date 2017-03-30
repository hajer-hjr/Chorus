package chorus.chorus.com.chorus;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class User {
    public String id;
    public String pays;
    public String state;
    public String username;
    public String profession;
    public String firstname;
    public String lastname;
    public String date_naiss;
    public String email;
    public String password;
    public String sexe;
    public String active;
    public String last_connection;
    public String activation_date;
    public String paroisse;
    public String origine;
    public String verset;
    public String longitude;
    public String latitude;
    public String tel;
    public String distance;
    public String mem_search_age_min;
    public String mem_search_age_max;
    public Integer infos_nb_new_messages=0;
    public  String date_last_activity;
    public Integer mem_search_homme;
    public Integer mem_search_femme;
    public  ImageProfil Logo = new ImageProfil();
    public int getAge(String date_nais) {
        String[] date=date_nais.split("-");

        int DOByear=Integer.parseInt(date[0]);
        int DOBmonth=Integer.parseInt(date[1]);
        int DOBday=Integer.parseInt(date[2]);
        int age;
        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);
        age = currentYear - DOByear;

        if(DOBmonth > currentMonth){
            --age;
        }
        else if(DOBmonth == currentMonth){
            if(DOBday > todayDay){
                --age;
            }
        }

        return age;
    }

    public Boolean isConnected() {
        String dateFin =date_last_activity;
        //*  Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date datenow=new Date();

        try {
            Date date = sdf.parse(dateFin);
            long timeInMillisecondsFin = date.getTime();
            Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                    rightNow.get(Calendar.DST_OFFSET);
            Log.e("end time is", String.valueOf(date.getTime()));
            Log.e("date fin",String.valueOf(timeInMillisecondsFin));
            Log.e("diff",String.valueOf(datenow.getTime()-timeInMillisecondsFin));
            long sinceMid = (datenow.getTime() + offset) %
                    (24 * 60 * 60 * 1000);
            // Log.e( "time fin",String.valueOf(rightNow.setTime(date)));

            if (datenow.getTime()-timeInMillisecondsFin<=300000) return true;

            else return false;


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
