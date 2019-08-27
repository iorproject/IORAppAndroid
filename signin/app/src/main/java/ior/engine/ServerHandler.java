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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import utils.ParameterStringBuilder;

public class ServerHandler {
    private static final long TIME_TO_FETCH = 5;
    private static final ServerHandler ourInstance = new ServerHandler();
    private Runnable onProgressFetchingData = null;
    private Date partnersLastFetch = null;
    private Date companiesLastFetch = null;
    private Date requestsLastFetch = null;
    private Map<String, Map<String, Date>> companiesReceiptsLastFetch = new HashMap<>();
    private User signInUser;
    private List<String> partners;
    private List<String> requests;
    private List<Company> companies;
    private Map<String, Company> companyMap = new HashMap<>();
    private Map<String, Map<String, List<Receipt>>> usersReceipts = new HashMap<>();
    private Map<String, User> usersInfoMap = new HashMap<>();


    private ServerHandler() {
    }


    public static ServerHandler getInstance() {
        return ourInstance;
    }

    public void setOnProgressFetchingData(Runnable onProgressFetchingData) {
        this.onProgressFetchingData = onProgressFetchingData;
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

    public Optional<Company> getCompanyByName(String companyName){
        return companies.stream()
                .filter(company -> company.getName().equals(companyName))
                .findFirst();
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
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/registerUser");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);
                    parameters.put("access_token", accessToken);
                    parameters.put("refresh_token", refreshToken);
                    parameters.put("name", name);
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
                    Date now = Calendar.getInstance().getTime();
                    String data = dateFormat.format(now);
                    parameters.put("register_date", data);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();
                    if (responseCode == 500) {


                    }

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

    }

    public void fetchUserInfo(String email, Runnable onFinish) {

        if (!usersInfoMap.containsKey(email)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/userInfo");
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

                        if (responseCode == 500) {


                        }

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer content = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();

                        Gson gson = new Gson();
                        Map<String, String> userMap = gson.fromJson(content.toString(), Map.class);
                        String email = userMap.get("email");
                        String name = userMap.get("name");
                        String dateStr = userMap.get("registerDate");
                        String profileImage = userMap.containsKey("profileImage") ? userMap.get("profileImage"):"";
                        Date registerDate = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss", Locale.ENGLISH).parse(dateStr);

                        ServerHandler.getInstance().signInUser = new User(email, name, registerDate,profileImage);
                        ServerHandler.getInstance().usersInfoMap.put(email, signInUser);
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
                    if (onProgressFetchingData != null)
                        onProgressFetchingData.run();
                    fetchCompanies(email, () -> {

                        onFinish.run();

                    });
                }
            }.execute();
        } else {

            onFinish.run();
        }
    }

    public void fetchUserPartners(String email, Runnable onFinish) {

        Date date = new Date();
        Date lastFetch = partnersLastFetch;
        if (lastFetch == null || isTimeToFetch(date, lastFetch)) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/userPartners");
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

                        if (responseCode == 500) {


                        }

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer content = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();

                        Gson gson = new Gson();
                        Map<String, ArrayList<LinkedTreeMap<String, Object>>> resultMapDB = gson.fromJson(content.toString(), Map.class);
                        Map<String, Map<String,User>> partners_Followers = new HashMap<>();
                        partners_Followers.put("partners",new HashMap<>());
                        partners_Followers.put("followers",new HashMap<>());
                        partners_Followers.put("requestusers",new HashMap<>());
                        for(Map.Entry<String, ArrayList<LinkedTreeMap<String,Object>>> entry: resultMapDB.entrySet())
                        {
                            Map <String,User> tempUsers = new HashMap<>();
                            ArrayList<LinkedTreeMap<String, Object>> arrayList =  entry.getValue();
                            for (LinkedTreeMap<String, Object> objectLinkedTreeMap: arrayList)
                            {
                                String email = objectLinkedTreeMap.get("email").toString();
                                String name= objectLinkedTreeMap.get("name").toString();
                                String profileImage= objectLinkedTreeMap.get("profileImage") != null ?  objectLinkedTreeMap.get("profileImage") .toString(): null;
                                tempUsers.put(email, new User(email, name, null,profileImage));
                            }

                            partners_Followers.put(entry.getKey(), tempUsers);

                        }

                        signInUser.setPartners_Followers(partners_Followers);
                        // fetchRequests(email, onFinish);
                        int x = 0;

                    } catch (ProtocolException e1) {

                    } catch (IOException e2) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    partnersLastFetch = date;
                    if (onProgressFetchingData != null)
                        onProgressFetchingData.run();
                    if (onFinish != null)
                        onFinish.run();

                }
            }.execute();
        } else if (onFinish != null){
            onFinish.run();

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
                        URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/userCompanies");
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

                        if (responseCode == 500) {


                        }
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
                protected void onPostExecute(Void aVoid)
                {
                    if (onProgressFetchingData != null)
                        onProgressFetchingData.run();

                    fetchUserAllReceipts(email, onFinish);
                }
            }.execute();

        } else
            onFinish.run();
    }

    private void fetchBitmaps(String userEmail, Runnable onFinish) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {


                for (String companyName : usersReceipts.get(userEmail).keySet()) {
                    if (companyMap.containsKey(companyName)) {

                        Company company = companyMap.get(companyName);
                        if (company.getBitmap() == null) {
                            loadBitmap(company);
                        }
                    }
                }

                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (onProgressFetchingData != null)
                    onProgressFetchingData.run();

                ServerHandler.getInstance().setOnProgressFetchingData(null);

                onFinish.run();
            }
        }.execute();
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
                        URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/companyReceiptsByUser");
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

                        if (responseCode == 500) {


                        }

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
                            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH);

                            try {

                                receiptDate = formatter.parse(receiptDateStr);
                                Receipt temp = new Receipt(receiptsEmail, receiptCompany,
                                        receiptNumber, receiptDate,
                                        receiptPrice, receiptCurrency
                                        ,receiptFileName, "");

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

    public float getAveragePurchase(String email, Optional<Date> startDate, Optional<Date> endDate){
        if(usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty()) {
            OptionalDouble averagePurchase =  usersReceipts.get(email).values().stream()
                    .flatMap(Collection::stream)
                    .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                    .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                    .mapToDouble(Receipt::getTotalPrice)
                    .average();
            if(averagePurchase.isPresent()){
                return (float)averagePurchase.getAsDouble();
            }
        }
        return 0;

    }

    public float getAveragePurchasePerCompany(String email, String company, Optional<Date> startDate, Optional<Date> endDate){
        if(usersReceipts.containsKey(email) && usersReceipts.get(email).containsKey(company)) {
            OptionalDouble averagePurchase = usersReceipts.get(email).get(company)
                    .stream()
                    .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                    .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                    .mapToDouble(Receipt::getTotalPrice)
                    .average();
            if(averagePurchase.isPresent()){
                return (float)averagePurchase.getAsDouble();
            }
        }
        return 0;
    }

    public float getMostExpensivePurchase(String email, Optional<Date> startDate, Optional<Date> endDate){
        if(usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty()){
            Optional<Float> mostExpensivePurchase = usersReceipts.get(email).values().stream()
                    .flatMap(Collection::stream)
                    .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                    .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                    .map(receipt -> receipt.getTotalPrice())
                    .max(Comparator.comparingDouble(Float::valueOf));
            if(mostExpensivePurchase.isPresent()){
                return mostExpensivePurchase.get();
            }
        }
        return 0;
    }

    public float getLatestPurchase(String email, Optional<Date> startDate, Optional<Date> endDate){
        List<Receipt> receipts = usersReceipts.get(email).values()
        .stream()
                .flatMap(List::stream)
                .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                .collect(Collectors.toList());

        Receipt latestReceipt = !receipts.isEmpty() ? receipts.get(0) : null;
        if(latestReceipt == null){
            return 0;
        }

        for(Receipt receipt : receipts){
            if(receipt.getCreationDate().after(latestReceipt.getCreationDate())){
                latestReceipt = receipt;
            }
        }
        return latestReceipt.getTotalPrice();
    }


    public int getAmountOfPurchases(String email, Optional<Date> startDate, Optional<Date> endDate) {
        return usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty() ?
                usersReceipts.get(email).values().stream()
                        .flatMap(Collection::stream)
                        .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                        .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                        .collect(Collectors.toList()).size() : 0;
    }

    public int getAmountOfPurchasesPerCompany(String email, String company, Optional<Date> startDate, Optional<Date> endDate){
        return usersReceipts.containsKey(email) && usersReceipts.get(email).containsKey(company) ?
                usersReceipts.get(email).get(company).stream()
                        .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                        .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                        .collect(Collectors.toList()).size() : 0;
    }

    public float getTotalPurchases(String email, Optional<Date> startDate, Optional<Date> endDate){
        if(usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty()) {
            Optional<Float> totalPurchases =  usersReceipts.get(email).values().stream()
                    .flatMap(Collection::stream)
                    .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                    .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                    .map(Receipt::getTotalPrice)
                    .reduce((a,b) -> a+b);
            if(totalPurchases.isPresent()){
                return totalPurchases.get();
            }
        }
        return 0;
    }

    public List<Receipt> getCompanyReceipts(String email, String company) {

        List<Receipt> result = new ArrayList<>();

        if (usersReceipts.containsKey(email) && usersReceipts.get(email).containsKey(company))
            result.addAll(usersReceipts.get(email).get(company));

        return result;

    }

    public float getTotalPurchasesPerCompany(String email, String company,Optional<Date> startDate, Optional<Date> endDate) {
        if (usersReceipts.containsKey(email) && usersReceipts.get(email).containsKey(company)) {
            Optional<Float> totalPurchases = usersReceipts.get(email).get(company)
                    .stream()
                    .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                    .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                    .map(Receipt::getTotalPrice)
                    .reduce((a, b) -> a + b);
            if(totalPurchases.isPresent()){
                return totalPurchases.get();
            }
        }
        return 0;
    }

    public void fetchUserAllReceipts(String userEmail, Runnable onFinish) {

        if (usersReceipts.containsKey(userEmail))
            usersReceipts.remove(userEmail);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                try {
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/userAllReceipts");
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

                    if (responseCode == 500) {


                    }

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    Gson gson = new Gson();
                    List<LinkedTreeMap<String, Object>> receiptsDb = gson.fromJson(content.toString(), List.class);

                    signInUser.setAmountReceipts(receiptsDb.size());
                    if (!usersReceipts.containsKey(userEmail)) {
                        usersReceipts.put(userEmail, new LinkedHashMap<>());
                    }

                    for (LinkedTreeMap<String, Object> receiptDB : receiptsDb) {

                        String receiptsEmail = userEmail;
                        String receiptCompany = receiptDB.get("companyName").toString();
                        String receiptNumber = receiptDB.get("receiptNumber").toString();
                        String receiptDateStr = receiptDB.get("creationDate").toString();
                        String receiptCurrencyStr = receiptDB.get("currency").toString();
                        float receiptPrice = (float)((double)(receiptDB.get("totalPrice")));
                        String receiptFileName = receiptDB.containsKey("fileName") ? receiptDB.get("fileName").toString() : "";
                        String attUrl = receiptDB.containsKey("attachmentURL") ? receiptDB.get("attachmentURL")
                                .toString() : "";
                        Date receiptDate = null;
                        eCurrency receiptCurrency = eCurrency.createCurrency(receiptCurrencyStr);
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.ENGLISH);

                        try {

                            receiptDate = formatter.parse(receiptDateStr);
                            Receipt temp = new Receipt(receiptsEmail, receiptCompany,
                                    receiptNumber, receiptDate,
                                    receiptPrice, receiptCurrency
                                    ,receiptFileName, attUrl);


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
                if (onProgressFetchingData != null)
                    onProgressFetchingData.run();
                fetchBitmaps(userEmail, onFinish);
            }
        }.execute();

    }
    public void setUserProfileImage(String profileImage, String email)
    {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/setUserProfileImage");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("email", email);
                    parameters.put("profileImage", profileImage);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    con.setRequestMethod("POST");

                    int responseCode = con.getResponseCode();

                    if (responseCode == 500) {


                    }

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }
                return null;
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

    public void accecptFriendship(String friendEmail)
    {
        arrangeDataAfterAcceptFriendship(friendEmail);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                try {
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/acceptFriendship");
                    //URL url = new URL( "http://192.168.1.39:8080/ior/registerUser");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("useremail", signInUser.getEmail());
                    parameters.put("friendemail",friendEmail);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();

                    if (responseCode == 500) {


                    }

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();

    }

    public void unfolloweRequest(String friendEmail)
    {
        arrangeDataAfterUnfollowRequest(friendEmail);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {


                try {
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/unfollowRequest");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("useremail", signInUser.getEmail());
                    parameters.put("friendemail",friendEmail);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    int responseCode = con.getResponseCode();

                    if (responseCode == 500) {


                    }

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();

    }

    public void rejectFriendshipRequest(String friendEmail)
    {
        arrangeDataAfterRejectFriendship(friendEmail);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    URL url = new URL("http://ior-env-1.cbapj2vrpq.eu-central-1.elasticbeanstalk.com/rejectFriendshipRequest/reject");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("useremail", signInUser.getEmail());
                    parameters.put("friendemail", friendEmail);

                    con.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(con.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                    con.setRequestMethod("POST");

                    int responseCode = con.getResponseCode();

                    if (responseCode == 500) {


                    }

                } catch (ProtocolException e1) {

                } catch (IOException e2) {

                }
                return null;
            }
        }.execute();

    }

    private void arrangeDataAfterRejectFriendship(String friendEmail)
    {
        signInUser.getPartners_Followers().get("requestusers").remove(friendEmail);
    }

    private void arrangeDataAfterAcceptFriendship(String friendEmail)
    {
        User friendUser = signInUser.getPartners_Followers().get("requestusers").get(friendEmail);
        signInUser.getPartners_Followers().get("followers").put(friendEmail,friendUser);
        signInUser.getPartners_Followers().get("requestusers").remove(friendEmail);
    }

    private void arrangeDataAfterUnfollowRequest(String friendEmail)
    {
        signInUser.getPartners_Followers().get("partners").remove(friendEmail);
    }

    public  void reset() {

        this.usersInfoMap.clear();
        this.companyMap.clear();
        this.usersReceipts.clear();
        this.signInUser = null;
        this.companiesLastFetch = null;
        this.partnersLastFetch = null;
    }


    public List<Receipt> getReceiptsFiltered(
            String email, List<String> companies, Date startDate, Date endDate,
            float minPrice, float maxPrice, List<eCurrency> currencies) {

        List<Receipt> receipts =
            usersReceipts.get(email).values().stream().flatMap(List::stream)
                    .filter(receipt -> companies.contains(receipt.getCompany()))
                    .filter(receipt -> !receipt.getCreationDate().before(startDate) && !receipt.getCreationDate().after(endDate))
                    .filter(receipt -> currencies.contains(receipt.getCurrency()))
                    .filter(receipt -> receipt.getTotalPrice() >= minPrice && receipt.getTotalPrice() <= maxPrice)
                    .collect(Collectors.toList());


//        receipts = usersReceipts.get(userEmail).values().stream().flatMap(List::stream).collect(Collectors.toList())
//                .stream()
//                .filter(receipt -> companies.contains(receipt.getCompany()))
//                .filter(receipt -> !receipt.getCreationDate().before(startDate) && !receipt.getCreationDate().after(endDate))
//                .filter(receipt -> currencies.contains(receipt.getCurrency()))
//                .filter(receipt -> receipt.getTotalPrice() >= minPrice && receipt.getTotalPrice() <= maxPrice)
//                .collect(Collectors.toList());


        return receipts;
    }


    public List<eCurrency> getUserCurrencies(String email) {

        Set<eCurrency> set = new HashSet<>();
        List<eCurrency> res = new ArrayList<>();

        for (List<Receipt> list : usersReceipts.get(email).values()) {

            for (Receipt r : list)
                set.add(r.getCurrency());

        }

        res.addAll(set);
        return res;

    }

    public float getUserMaxPriceReceipt(String userEmail) {

        float maxPrice = 0;
        List<Receipt> receipts = usersReceipts.get(userEmail).values().stream().flatMap(List::stream).collect(Collectors.toList());
        for (Receipt receipt : receipts) {

            if (receipt.getTotalPrice() > maxPrice)
                maxPrice = receipt.getTotalPrice();
        }

        return maxPrice;
    }

    public List<String> getCompaniesName(String email, Optional<Date> startDate, Optional<Date> endDate) {
        Map<String,Integer> companyNamesMap = new HashMap<>();
        if(usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty()){
            Map<String,List<Receipt>> receiptsMap = usersReceipts.get(email);
            for(Map.Entry<String,List<Receipt>> receiptEntry : receiptsMap.entrySet()){
                for(Receipt receipt : receiptEntry.getValue()){
                    if(((!startDate.isPresent() && !endDate.isPresent()) || (receipt.getCreationDate().after(startDate.get()) && receipt.getCreationDate().before(endDate.get())))
                            && !companyNamesMap.containsKey(receiptEntry.getKey())){
                        companyNamesMap.put(receiptEntry.getKey(),1);
                        break;
                    }
                }

            }
        }
        return new ArrayList<>(companyNamesMap.keySet());
    }

    public List<Float> getCompaniesTotalPrice(String email, Optional<Date> startDate, Optional<Date> endDate) {
        List<Float> companyTotalPriceList = new ArrayList<>();
        if(usersReceipts.containsKey(email) && !usersReceipts.get(email).isEmpty())
            for(Map.Entry<String,List<Receipt>> companyReceiptsMap : usersReceipts.get(email).entrySet()){
                Optional<Float> companyTotalPrice = companyReceiptsMap.getValue().stream()
                        .filter(receipt -> !startDate.isPresent() || receipt.getCreationDate().after(startDate.get()))
                        .filter(receipt -> !endDate.isPresent() || receipt.getCreationDate().before(endDate.get()))
                        .map(Receipt::getTotalPrice).reduce((a, b) -> a+b);
                if(companyTotalPrice.isPresent()){
                    companyTotalPriceList.add(companyTotalPrice.get());
                }
            }
        return companyTotalPriceList;
    }

}
