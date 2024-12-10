package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

public class ProductPartRequest {
    private String type;
    private int count;
    private String location;

    public ProductPartRequest() {
        super();
    }

    public ProductPartRequest(String type, int count, String location) {
        this.type = type;
        this.count = count;
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
