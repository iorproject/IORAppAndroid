package ior.engine;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.ParameterStringBuilder;

public class ServerHandler {
    private static final long TIME_TO_FETCH = 2;
    private static final ServerHandler ourInstance = new ServerHandler();
    private Date partnersLastFetch = null;
    private Date requestsLastFetch = null;

    public static ServerHandler getInstance() {
        return ourInstance;
    }

    private User user;
    private List<String> partners;
    private List<String> requests;

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


    public void fetchUserInfo(String email) {

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


            } catch (ProtocolException e1) {

            } catch (IOException e2) {

            }
    }

    public List<String> getUserCompanies(String email) {

        List<String> res = null;

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
            res = gson.fromJson(content.toString(), List.class);



        }
        catch (ProtocolException e1) {

        }
        catch (IOException e2) {

        }

        return res;
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


    private boolean isTimeToFetch(Date now, Date lastTimeFetch) {

        long diff = now.getTime() - lastTimeFetch.getTime();
        long diffInMinutes = diff / (60 * 1000) % 60;

        return diffInMinutes >= TIME_TO_FETCH;

    }


}
