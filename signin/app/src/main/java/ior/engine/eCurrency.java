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
}
