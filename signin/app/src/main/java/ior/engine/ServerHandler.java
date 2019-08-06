package ior.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import utils.ParameterStringBuilder;

public class ServerHandler {
    private static final long TIME_TO_FETCH = 2;
    private static final ServerHandler ourInstance = new ServerHandler();
    private Date partnersLastFetch = null;
    private Date companiesLastFetch = null;
    private Date requestsLastFetch = null;

    public static ServerHandler getInstance() {
        return ourInstance;
    }

    private User user;
    private List<String> partners;
    private List<String> requests;
    private List<Company> companies;
    //private List<LinkedTreeMap<String, String>> companiesDB;


    private ServerHandler() {
    }

    public User getUser() {
        return user;
    }

    public List<String> getPartners() {
        return partners;
    }

    public List<String> getRequests() {
        return requests;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void registerUser(String email, String accessToken, String refreshToken) {

        try {
            URL url = new URL("http://10.0.2.2:8080/ior/registerUser");
            //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("email", email);
            parameters.put("access_token", accessToken);
            parameters.put("refresh_token", refreshToken);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date now = Calendar.getInstance().getTime();
            String data = dateFormat.format(now);
            parameters.put("register_date", data);

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            int responseCode = con.getResponseCode();
            this.user = new User(email, now);

        }
        catch (ProtocolException e1) {

        }
        catch (IOException e2) {

        }
    }


    public void fetchUserInfo(String email, Runnable onFinish) {

        new Thread(() -> {

            try {
                URL url = new URL("http://10.0.2.2:8080/ior/userInfo");
                //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                Map<String, String> parameters = new HashMap<>();
                parameters.put("email", email);

                con.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                out.flush();
                out.close();
                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                User user = gson.fromJson(content.toString(), User.class);

                this.user = user;
                onFinish.run();


            } catch (ProtocolException e1) {

            } catch (IOException e2) {

            }
        }).start();

    }


    public void fetchUserPartners(String email, Runnable onFinish) {

        Date date = new Date();
        Date lastFetch = partnersLastFetch;
        partnersLastFetch = date;
        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {

            Thread thread = new Thread(() -> {

                try {
                    URL url = new URL("http://10.0.2.2:8080/ior/userPartners");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Gson gson = new Gson();
                    partners = gson.fromJson(content.toString(), List.class);
                    fetchRequests(email, onFinish);
                    //onFinish.run();

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }
            });

            thread.start();

        } else
            onFinish.run();
    }


    public void fetchRequests(String email, Runnable onFinish) {

//        Date date = new Date();
//        Date lastFetch = requestsLastFetch;
//        requestsLastFetch = date;
//        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {


                try {
                    URL url = new URL("http://10.0.2.2:8080/ior/userShareRequests");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Gson gson = new Gson();
                    requests = gson.fromJson(content.toString(), List.class);
                    onFinish.run();

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }

//        } else
//            onFinish.run();

    }


    public void fetchUserCompanies(String email, Runnable onFinish) {

        Date date = new Date();
        Date lastFetch = companiesLastFetch;
        companiesLastFetch = date;
        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {

            Thread thread = new Thread(() -> {

                try {
                    URL url = new URL("http://10.0.2.2:8080/ior/userCompanies");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<CompanyDB>>(){}.getType();

                    // data: array of : ["companyName" -> "aaa" , "logoUrl" -> "httpdsdsa"] , [...]

                    List<LinkedTreeMap<String, String>> companiesDB = gson.fromJson(content.toString(), List.class);
                    companies = new ArrayList<>();

                    for (LinkedTreeMap<String, String> companyDB : companiesDB) {

                        companies.add(new Company(companyDB.get("companyName"), companyDB.get("logoUrl")));
                    }
                    fetchBitmaps(onFinish);
                    //onFinish.run();

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }
            });

            thread.start();

        } else
            onFinish.run();
    }

    private void fetchBitmaps(Runnable onFinish) {

        for (Company company : companies) {

            loadBitmap(company);
        }

        onFinish.run();

    }


    private boolean isTimeToFetch(Date now, Date lastTimeFetch) {

        long diff = now.getTime() - lastTimeFetch.getTime();
        long diffInMinutes = diff / (60 * 1000) % 60;

        return diffInMinutes >= TIME_TO_FETCH;

    }


    public void loadBitmap(Company company)
    {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(company.getUrl()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        company.setBitmap(bm);
    }
}
