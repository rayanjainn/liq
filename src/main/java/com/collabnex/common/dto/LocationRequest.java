package com.collabnex.common.dto;

public class LocationRequest {

    private Long userId;
    private double latitude;
    private double longitude;
    private String source;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
