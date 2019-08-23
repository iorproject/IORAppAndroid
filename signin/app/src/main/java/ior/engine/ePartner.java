package ior.engine;

public enum  ePartner {

    FOLLOWING("following"),
    FOLLOWER("follow by"),
    REQUEST("Accept");

    private final String messege;

    ePartner(String name) {

        this.messege = name;
    }


    @Override
    public String toString() {
        return this.messege;
    }
}
