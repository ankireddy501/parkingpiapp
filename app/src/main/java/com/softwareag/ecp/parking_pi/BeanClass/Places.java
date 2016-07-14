package com.softwareag.ecp.parking_pi.BeanClass;

/**
 * Created by KAVI on 07-07-2016.
 */
public class Places {
    private String placeName;
    private String vicinity;
    private String lattitude;
    private String longitude;
    private String reference;

    public Places(String placeName, String vicinity, String lattitude, String longitude, String reference){
        this.placeName = placeName;
        this.vicinity = vicinity;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.reference = reference;
    }
    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


}
