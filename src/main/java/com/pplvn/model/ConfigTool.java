package com.pplvn.model;

public class ConfigTool {
    String profile;
    String url;
    String data;
    String country;
    String price;
    String quantity;
    String handlingTime;
    String parentAsin;
    String apiKey;
    String SKU;
    String priceCa;
    int timeOutClick;
    int timeOutOpen;
    int timeOutClose;

    public String getPriceCa() {
        return priceCa;
    }

    public void setPriceCa(String priceCa) {
        this.priceCa = priceCa;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public int getTimeOutOpen() {
        return timeOutOpen;
    }

    public void setTimeOutOpen(int timeOutOpen) {
        this.timeOutOpen = timeOutOpen;
    }

    public int getTimeOutClose() {
        return timeOutClose;
    }

    public void setTimeOutClose(int timeOutClose) {
        this.timeOutClose = timeOutClose;
    }

    public int getTimeOutClick() {
        return timeOutClick;
    }

    public void setTimeOutClick(int timeOutClick) {
        this.timeOutClick = timeOutClick;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getHandlingTime() {
        return handlingTime;
    }

    public void setHandlingTime(String handlingTime) {
        this.handlingTime = handlingTime;
    }

    public String getParentAsin() {
        return parentAsin;
    }

    public void setParentAsin(String parentAsin) {
        this.parentAsin = parentAsin;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public String setUrl(String url) {
        this.url = url;
        return url;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
