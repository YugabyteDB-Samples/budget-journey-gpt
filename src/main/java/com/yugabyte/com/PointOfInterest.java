package com.yugabyte.com;

public class PointOfInterest {
    private String name;
    private String info;
    private int cost;

    public PointOfInterest(String name, String info, int cost) {
        this.name = name;
        this.info = info;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "PointOfInterest [name=" + name + ", info=" + info + ", cost=" + cost + "]";
    }
}
