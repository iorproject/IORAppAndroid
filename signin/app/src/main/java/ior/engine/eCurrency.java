package ior.engine;

import com.squareup.okhttp.internal.framed.Variant;

public enum eCurrency {

    NIS("NIS"),
    DOLLAR("$"),
    EURO("€");

    private final String name;

    eCurrency(String name) {

        this.name = name;
    }


    @Override
    public String toString() {
        return this.name;
    }

    public static eCurrency createCurrency(String str) {

        eCurrency res = null;

        switch (str) {

            case "$":
            case "US":
                res = eCurrency.DOLLAR;
                break;

            case "€":
                res = eCurrency.EURO;
                break;

            case "NIS":
                res = eCurrency.NIS;
                break;

        }

        return res;


    }
}
