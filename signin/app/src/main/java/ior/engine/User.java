package ior.engine;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.IorUtils;

public class User {
    private String email;
    private Date registerDate;
    private String name;
    private Bitmap profileImageBitmap;
    private Map<String, Map<String,User>> partners_Followers;
    private Map<String, Integer> profileDetails;
    private Date lastEmailScan = null;

    public User(String email, String name, Date registerDate,String profileImage) {
        this.email = email;
        this.name = name;
        this.registerDate = registerDate;
        this.partners_Followers = new HashMap<>();
        this.profileImageBitmap = IorUtils.getBitmapFromString(profileImage);
        this.profileDetails = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }


    public Date getRegisterDate() {
        return registerDate;
    }

//    public void setAmountPartners(int amountPartners) {
//        this.partners = amountPartners;
//    }

//    public void setFollowersAmount(int followersAmount) {
//        this.followersAmount = followersAmount;
//    }

    public int getPartnersAmount() {
        return this.profileDetails.get("partners");
    }

    public int getFollowersAmount() {
        return this.profileDetails.get("followers");
    }

    public int getRecieptsAmount()
    {
       return this.profileDetails.get("reciepts");
    }


    public String getName() { return name; }

    public void setProfileImage(Bitmap profileImage) {

        this.profileImageBitmap = profileImage;
    }


    public Bitmap getProfileImage(){return this.profileImageBitmap;}

    public List<User> getPartners(){
        List<User> result = new ArrayList<>();
        for(Map.Entry<String,User> entry : partners_Followers.get("partners").entrySet())
        {
            result.add(entry.getValue());
        }

        return result;

    }

    public List<User> getFollowers(){

        List<User> result = new ArrayList<>();
        for(Map.Entry<String,User> entry : partners_Followers.get("followers").entrySet())
        {
            result.add(entry.getValue());
        }

        return result;
    }

    public List<User> getUsersRequest(){

        List<User> result = new ArrayList<>();
        for(Map.Entry<String,User> entry : partners_Followers.get("requestusers").entrySet())
        {
            result.add(entry.getValue());
        }

        return result;
    }

    public  Map<String, Map<String, User>> getPartners_Followers(){return this.partners_Followers;}

    public void setPartners_Followers(Map<String, Map<String,User>> partners_Followers){this.partners_Followers = partners_Followers;}

    public void setProfileDetails(int receiptsNum, int partnersNum, int followersNum)
    {
        this.profileDetails.put("reciepts",receiptsNum);
        this.profileDetails.put("partners",partnersNum);
        this.profileDetails.put("followers",followersNum);
    }

    public Date getLastEmailScan() {
        return lastEmailScan;
    }

    public void setLastEmailScan(Date lastEmailScan) {
        this.lastEmailScan = lastEmailScan;
    }
}
