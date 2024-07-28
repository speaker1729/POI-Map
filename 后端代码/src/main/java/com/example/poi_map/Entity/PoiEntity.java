package com.example.poi_map.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PoiEntity {
    @JsonProperty(value = "poi_id")
    Integer poi_id;

    @JsonProperty(value = "province")
    String province;

    @JsonProperty(value = "city")
    String city;

    @JsonProperty(value = "district")
    String district;

    @JsonProperty(value = "poi_name")
    String poi_name;

    @JsonProperty(value = "poi_address")
    String poi_address;

    @JsonProperty(value = "poi_type")
    String poi_type;

    @JsonProperty(value = "latitude")
    double latitude;

    @JsonProperty(value = "longitude")
    double longitude;


    public Integer getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(Integer poi_id) {
        this.poi_id = poi_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPoi_name() {
        return poi_name;
    }

    public void setPoi_name(String poi_name) {
        this.poi_name = poi_name;
    }

    public String getPoi_address() {
        return poi_address;
    }

    public void setPoi_address(String poi_address) {
        this.poi_address = poi_address;
    }

    public String getPoi_type() {
        return poi_type;
    }

    public void setPoi_type(String poi_type) {
        this.poi_type = poi_type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
