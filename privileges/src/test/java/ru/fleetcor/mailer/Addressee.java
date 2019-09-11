package ru.fleetcor.mailer;

/**
 * Created by Ivan.Zhirnov on 27.07.2018.
 */
public class Addressee {
    private String name;
    private String address;

    public Addressee(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
