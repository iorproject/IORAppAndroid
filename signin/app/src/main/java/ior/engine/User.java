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
    private int amountPartners;
    private int amountFollowing;
    private int amountReceipts;

    private Bitmap profileImageBitmap;
    private Map<String, Map<String,User>> partners_Followers;

    public User(String email, String name, Date registerDate,String profileImage) {
        this.email = email;
        this.name = name;
        this.registerDate = registerDate;
        this.partners_Followers = new HashMap<>();
        this.profileImageBitmap = IorUtils.getBitmapFromString(profileImage);
    }

    public String getEmail() {
        return email;
    }


    public Date getRegisterDate() {
        return registerDate;
    }

    public void setAmountPartners(int amountPartners) {
        this.amountPartners = amountPartners;
    }

    public void setAmountFollowing(int amountFollowing) {
        this.amountFollowing = amountFollowing;
    }

    public int getAmountPartners() {
        return amountPartners;
    }

    public int getAmountFollowing() {
        return amountFollowing;
    }

    public void setAmountReceipts(int amountReceipts) {
        this.amountReceipts = amountReceipts;
    }

    public int getAmountReceipts() {
        return amountReceipts;
    }

    public String getName() { return name; }

    public void setProfileImage(String profileImage) {

        this.profileImageBitmap = IorUtils.getBitmapFromString(profileImage);
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
}
