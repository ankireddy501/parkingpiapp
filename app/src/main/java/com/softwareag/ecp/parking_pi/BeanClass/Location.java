package com.softwareag.ecp.parking_pi.BeanClass;

/**
 * Created by KAVI on 21-06-2016.
 */
public class Location {

    private String branchName;
    private double lattitude;
    private double longitute;
    private int total;
    private int available;
    private String layout;
    private boolean isActive;

    private String name;
    private String status;
    private String ownerId;

    private String name1;
    private String status1;
    private String ownerId1;

    public Location() {

    }

    public Location(String branchName, double lattitude, double longitute, int total, int available, boolean isActive,
                    String layout, String name, String status, String ownerId, String name1, String status1,
                    String ownerId1) {

        this.branchName = branchName;
        this.lattitude = lattitude;
        this.longitute = longitute;
        this.total = total;
        this.available = available;
        this.isActive = isActive;
        this.layout = layout;
        this.name = name;
        this.status = status;
        this.ownerId = ownerId;

        this.name1 = name1;
        this.status1 = status1;
        this.ownerId1 = ownerId1;

    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

    public String getOwnerId1() {
        return ownerId1;
    }

    public void setOwnerId1(String ownerId1) {
        this.ownerId1 = ownerId1;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}
