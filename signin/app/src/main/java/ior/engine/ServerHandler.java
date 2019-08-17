package ior.engine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utils.ParameterStringBuilder;

public class ServerHandler {
    private static final long TIME_TO_FETCH = 5;
    private static final ServerHandler ourInstance = new ServerHandler();
    private Date partnersLastFetch = null;
    private Date companiesLastFetch = null;
    private Date requestsLastFetch = null;
    private Map<String, Map<String, Date>> companiesReceiptsLastFetch = new HashMap<>();

    public static ServerHandler getInstance() {
        return ourInstance;
    }

    private User signInUser;
    private List<String> partners;
    private List<String> requests;
    private List<Company> companies;
    private Map<String, Company> companyMap = new HashMap<>();
    private Map<String, Map<String, List<Receipt>>> usersReceipts = new HashMap<>();
    private Map<String, User> usersInfoMap = new HashMap<>();


    private ServerHandler() {
    }

    public User getSignInUser() {
        return signInUser;
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

    public List<Company> getUserCompanies(String userEmail) {

        List<Company> result = new ArrayList<>();

        for (String name : usersReceipts.get(userEmail).keySet()) {

            result.add(companyMap.get(name));

        }

        return result;

    }

    public Company getCompany(String name) {

        return companyMap.get(name);
    }

    public void setSignInUser(User signInUser) {
        this.signInUser = signInUser;
    }

    public void registerUser(String email, String name, String accessToken, String refreshToken, Runnable onFinish) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://10.0.2.2:8080/ior/registerUser");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);
                    parameters.put("access_token", accessToken);
                    parameters.put("refresh_token", refreshToken);
                    parameters.put("name", name);
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

                }
                catch (ProtocolException e1) {

                }
                catch (IOException e2) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                fetchCompanies(email, () -> {

                    onFinish.run();

                });
            }
        }.execute();


//        try {
//            URL url = new URL("http://10.0.2.2:8080/ior/registerUser");
//            //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestMethod("GET");
//
//            Map<String, String> parameters = new HashMap<>();
//            parameters.put("email", email);
//            parameters.put("access_token", accessToken);
//            parameters.put("refresh_token", refreshToken);
//            parameters.put("name", name);
//            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//            Date now = Calendar.getInstance().getTime();
//            String data = dateFormat.format(now);
//            parameters.put("register_date", data);
//
//            con.setDoOutput(true);
//            DataOutputStream out = new DataOutputStream(con.getOutputStream());
//            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//            out.flush();
//            out.close();
//            int responseCode = con.getResponseCode();
//            this.user = new User(email, name, now);
//
//        }
//        catch (ProtocolException e1) {
//
//        }
//        catch (IOException e2) {
//
//        }
    }


    public void fetchUserInfo(String email, Runnable onFinish) {

        if (!usersInfoMap.containsKey(email)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
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

                        ServerHandler.getInstance().signInUser = user;
                        ServerHandler.getInstance().usersInfoMap.put(email, user);
                        int x = 5;

                    } catch (ProtocolException e1) {
                        int fff = 4;

                    } catch (IOException e2) {
                        int ssss = 3;
                    } catch (Exception e) {
                        int sadsada = 4;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    fetchCompanies(email, () -> {

                        onFinish.run();

                    });
                }
            }.execute();
        } else {

            onFinish.run();
        }


//        new Thread(() -> {
//
//            try {
//                URL url = new URL("http://10.0.2.2:8080/ior/userInfo");
//                //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setRequestMethod("GET");
//
//                Map<String, String> parameters = new HashMap<>();
//                parameters.put("email", email);
//
//                con.setDoOutput(true);
//                DataOutputStream out = new DataOutputStream(con.getOutputStream());
//                out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//                out.flush();
//                out.close();
//                int responseCode = con.getResponseCode();
//
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuffer content = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    content.append(inputLine);
//                }
//                in.close();
//
//                Gson gson = new Gson();
//                User user = gson.fromJson(content.toString(), User.class);
//
//                this.user = user;
//                onFinish.run();
//
//
//            } catch (ProtocolException e1) {
//
//            } catch (IOException e2) {
//
//            }
//        }).start();

    }


    public void fetchUserPartners(String email, Runnable onFinish) {

        Date date = new Date();
        Date lastFetch = partnersLastFetch;
        partnersLastFetch = date;
        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
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
                        List<LinkedTreeMap<String, Object>> partnersDB = gson.fromJson(content.toString(), List.class);

                        for (LinkedTreeMap<String, Object> partner : partnersDB) {

                            String email = partner.get("email").toString();
                            String name = partner.get("name").toString();
                            Date registerDate = (Date) partner.get("registerDate");
                            usersInfoMap.put(email, new User(email, name, registerDate));

                        }

                        // REMOVEEE
                        partners = gson.fromJson(content.toString(), List.class);
                        fetchRequests(email, onFinish);

                    } catch (ProtocolException e1) {

                    } catch (IOException e2) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    onFinish.run();

                }
            }.execute();
        } else {
            onFinish.run();


//            Thread thread = new Thread(() -> {
//
//                try {
//                    URL url = new URL("http://10.0.2.2:8080/ior/userPartners");
//                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("GET");
//
//                    Map<String, String> parameters = new HashMap<>();
//                    parameters.put("email", email);
//
//                    con.setDoOutput(true);
//                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
//                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//                    out.flush();
//                    out.close();
//                    int responseCode = con.getResponseCode();
//
//                    BufferedReader in = new BufferedReader(
//                            new InputStreamReader(con.getInputStream()));
//                    String inputLine;
//                    StringBuffer content = new StringBuffer();
//                    while ((inputLine = in.readLine()) != null) {
//                        content.append(inputLine);
//                    }
//                    in.close();
//
//                    Gson gson = new Gson();
//                    partners = gson.fromJson(content.toString(), List.class);
//                    fetchRequests(email, onFinish);
//                    //onFinish.run();
//
//                } catch (ProtocolException e1) {
//
//                } catch (IOException e2) {
//
//                }
//            });
//
//            thread.start();
//
//        } else
//            onFinish.run();
        }
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

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    con.setRequestMethod("GET");

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


    }


    public void fetchCompanies(String email, Runnable onFinish) {

        Date date = new Date();
        Date lastFetch = companiesLastFetch;
        companiesLastFetch = date;
        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
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
                        // data: array of : ["companyName" -> "aaa" , "oUrl" -> "httpdsdsa"] , [...]

                        List<LinkedTreeMap<String, String>> companiesDB = gson.fromJson(content.toString(), List.class);
                        companies = new ArrayList<>();

                        for (LinkedTreeMap<String, String> companyDB : companiesDB) {

                            companyMap.put(companyDB.get("companyName"), new Company(companyDB.get("companyName"), companyDB.get("logoUrl")));
                            //companies.add(new Company(companyDB.get("companyName"), companyDB.get("logoUrl")));
                        }
                        //onFinish.run();

                    } catch (ProtocolException e1) {

                    } catch (IOException e2) {

                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    fetchUserAllReceipts(email, onFinish);
                    //fetchBitmaps(email, onFinish);
                }
            }.execute();


//
//            Thread thread = new Thread(() -> {
//
//                try {
//                    URL url = new URL("http://10.0.2.2:8080/ior/userCompanies");
//                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                    con.setRequestMethod("GET");
//
//                    Map<String, String> parameters = new HashMap<>();
//                    parameters.put("email", email);
//
//                    con.setDoOutput(true);
//                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
//                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//                    out.flush();
//                    out.close();
//                    int responseCode = con.getResponseCode();
//
//                    BufferedReader in = new BufferedReader(
//                            new InputStreamReader(con.getInputStream()));
//                    String inputLine;
//                    StringBuffer content = new StringBuffer();
//                    while ((inputLine = in.readLine()) != null) {
//                        content.append(inputLine);
//                    }
//                    in.close();
//
//                    Gson gson = new Gson();
//                    // data: array of : ["companyName" -> "aaa" , "logoUrl" -> "httpdsdsa"] , [...]
//
//                    List<LinkedTreeMap<String, String>> companiesDB = gson.fromJson(content.toString(), List.class);
//                    companies = new ArrayList<>();
//
//                    for (LinkedTreeMap<String, String> companyDB : companiesDB) {
//
//                        companies.add(new Company(companyDB.get("companyName"), companyDB.get("logoUrl")));
//                    }
//                    fetchBitmaps(onFinish);
//                    //onFinish.run();
//
//                } catch (ProtocolException e1) {
//
//                } catch (IOException e2) {
//
//                }
//            });
//
//            thread.start();

        } else
            onFinish.run();
    }

    private void fetchBitmaps(String userEmail, Runnable onFinish) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {


                for (String companyName : usersReceipts.get(userEmail).keySet()) {
                    Company company = companyMap.get(companyName);
                    if (company.getBitmap() == null) {
                        loadBitmap(company);
                    }

                }

                return null;
//
//                for (Company company : companies) {
//
//                    loadBitmap(company);
//                }
//
//                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                onFinish.run();
            }
        }.execute();

//        for (Company company : companies) {
//
//            loadBitmap(company);
//        }
//
//        onFinish.run();

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
        String url = company.getUrl();

        try
        {
            URLConnection conn = new URL(url).openConnection();
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

    public void fetchCompanyReceipts(String userEmail, String company, Runnable onFinish) {

        Date now = new Date();
        if (companiesReceiptsLastFetch.get(userEmail) == null) {
            companiesReceiptsLastFetch.put(userEmail, new HashMap<>());
        }

        Date lastFetch = companiesReceiptsLastFetch.get(userEmail).get(company);
        companiesReceiptsLastFetch.get(userEmail).put(company, now);
        if (lastFetch == null || isTimeToFetch(now, lastFetch)) {

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {


                    try {
                        URL url = new URL("http://10.0.2.2:8080/ior/companyReceiptsByUser");
                        //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");

                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("email", userEmail);
                        parameters.put("company", company);

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
                        // data: array of : ["companyName" -> "aaa" , "logoUrl" -> "httpdsdsa"] , [...]

                        List<LinkedTreeMap<String, Object>> receiptsDb = gson.fromJson(content.toString(), List.class);
                        //companies = new ArrayList<>();

                        if (!usersReceipts.containsKey(userEmail)) {
                            usersReceipts.put(userEmail, new HashMap<>());
                        }

                        List<Receipt> receipts = new ArrayList<>();

                        for (LinkedTreeMap<String, Object> receiptDB : receiptsDb) {

                            String receiptsEmail = userEmail;
                            String receiptCompany = company;
                            String receiptNumber = receiptDB.get("receiptNumber").toString();
                            String receiptDateStr = receiptDB.get("creationDate").toString();
                            String receiptCurrencyStr = receiptDB.get("currency").toString();
                            float receiptPrice = (float)((double)(receiptDB.get("totalPrice")));
                            String receiptFileName = receiptDB.get("fileName").toString();
                            Date receiptDate = null;
                            eCurrency receiptCurrency = eCurrency.createCurrency(receiptCurrencyStr);
                            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");

                            try {

                                receiptDate = formatter.parse(receiptDateStr);
                                Receipt temp = new Receipt(receiptsEmail, receiptCompany,
                                        receiptNumber, receiptDate,
                                        receiptPrice, receiptCurrency
                                ,receiptFileName);

                                receipts.add(temp);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                        usersReceipts.get(userEmail).put(company, receipts);

                    } catch (ProtocolException e1) {

                    } catch (IOException e2) {

                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    onFinish.run();
                }
            }.execute();
        }
        else
            onFinish.run();

    }

    public float getAveragePurchase(String email){
        float totalPrice = 0;
        if(usersReceipts.containsKey(email)) {
            List<Receipt> receipts = usersReceipts.get(email).values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            for(Receipt receipt : receipts){
                totalPrice += receipt.getTotalPrice();
            }
            return totalPrice / (float)receipts.size();
        }
        return totalPrice;

    }

    public int getAmountOfPurchases(String email){
        return usersReceipts.containsKey(email) ?
                usersReceipts.get(email).values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList()).size() : 0;
    }

    public float getTotalPurchases(String email){
        float totalPrice = 0;
        if(usersReceipts.containsKey(email)) {
            List<Receipt> receipts = usersReceipts.get(email).values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            for(Receipt receipt : receipts){
                totalPrice += receipt.getTotalPrice();
            }
        }
        return totalPrice;
    }

    public List<Receipt> getCompanyReceipts(String email, String company) {

        List<Receipt> receipts = !usersReceipts.containsKey(email) ?
                null : usersReceipts.get(email).get(company);

        return receipts;

    }

//    public Company getCompany(String companyName) {
//
//        Company company = null;
//
//        for (Company c : companies) {
//            if (c.getName().equals(companyName))
//                company = c;
//        }
//
//        return company;
//
//    }

    public void fetchUserAllReceipts(String userEmail, Runnable onFinish) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                try {
                    URL url = new URL("http://10.0.2.2:8080/ior/userAllReceipts");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", userEmail);

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
                    // data: array of : ["companyName" -> "aaa" , "logoUrl" -> "httpdsdsa"] , [...]

                    List<LinkedTreeMap<String, Object>> receiptsDb = gson.fromJson(content.toString(), List.class);
                    //companies = new ArrayList<>();

                    signInUser.setAmountReceipts(receiptsDb.size());
                    if (!usersReceipts.containsKey(userEmail)) {
                        usersReceipts.put(userEmail, new HashMap<>());
                    }

                    for (LinkedTreeMap<String, Object> receiptDB : receiptsDb) {

                        String receiptsEmail = userEmail;
                        String receiptCompany = receiptDB.get("companyName").toString();
                        String receiptNumber = receiptDB.get("receiptNumber").toString();
                        String receiptDateStr = receiptDB.get("creationDate").toString();
                        String receiptCurrencyStr = receiptDB.get("currency").toString();
                        float receiptPrice = (float)((double)(receiptDB.get("totalPrice")));
                        String receiptFileName = receiptDB.get("fileName") == null ? "" :
                        receiptDB.get("fileName").toString();
                        Date receiptDate = null;
                        eCurrency receiptCurrency = eCurrency.createCurrency(receiptCurrencyStr);
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");

                        try {

                            receiptDate = formatter.parse(receiptDateStr);
                            Receipt temp = new Receipt(receiptsEmail, receiptCompany,
                                    receiptNumber, receiptDate,
                                    receiptPrice, receiptCurrency
                                    ,receiptFileName);


                            if (!usersReceipts.get(userEmail).containsKey(receiptCompany))
                                usersReceipts.get(userEmail).put(receiptCompany, new ArrayList<>());


                            usersReceipts.get(userEmail).get(receiptCompany).add(temp);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                fetchBitmaps(userEmail, onFinish);
            }
        }.execute();

    }


    public  void downloadFile(Activity activity, String url, String fileName, Runnable onFinish) {

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            DownloadFileTask task = new DownloadFileTask(activity, url, fileName, onFinish);
            task.execute();
        }







    }


    public  void reset() {

        this.usersInfoMap.clear();
        this.companyMap.clear();
        this.usersReceipts.clear();
        this.signInUser = null;
        this.companiesLastFetch = null;
    }

    public List<Receipt> getReceiptsFiltered(String userEmail, List<String> companies, Date startDate, Date endDate, float minPrice, float maxPrice, List<eCurrency> currencies) {

        List<Receipt> receipts = new ArrayList<>();
        for (List<Receipt> list : usersReceipts.get(userEmail).values())
            receipts.addAll(list);

        Stream<Receipt> stream = receipts.stream().filter(receipt -> companies.contains(receipt.getCompany()))
                .filter(receipt -> !receipt.getCreationDate().before(startDate) && !receipt.getCreationDate().after(endDate))
                .filter(receipt -> currencies.contains(receipt.getCurrency()));

        int x = 5;

        return receipts;
    }


}
