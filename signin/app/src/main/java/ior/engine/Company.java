package ior.engine;

import android.graphics.Bitmap;

public class Company {

    private String name;
    private String url;
    private Bitmap bitmap;

    public Company(String name, String url) {

        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
