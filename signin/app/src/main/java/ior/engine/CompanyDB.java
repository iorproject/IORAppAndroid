package ior.engine;

import android.graphics.Bitmap;

public class CompanyDB {

    private String name;
    private String url;


    public CompanyDB(String name, String url) {

        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }


}
