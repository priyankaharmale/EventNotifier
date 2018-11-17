package com.hnweb.eventnotifier.bo;

public class Event {
    String  id;
    String address;
    String transaction_date;
    String event_price;
    String no_of_tickets;
    String event_name;
    String event_descr;
    String event_date;

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    String event_starttime;
    String total_price;
    String event_endtime;
    String event_place;
    String image;
    String status;
    String created_by;
    String created_on;
    String latitude;
    String longitude;
    public String getId() {
        return id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getNo_of_tickets() {
        return no_of_tickets;
    }

    public void setNo_of_tickets(String no_of_tickets) {
        this.no_of_tickets = no_of_tickets;
    }

    public String getAddress() {
        return address;
    }

    public String getEvent_price() {
        return event_price;
    }

    public void setEvent_price(String event_price) {
        this.event_price = event_price;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_descr() {
        return event_descr;
    }

    public void setEvent_descr(String event_descr) {
        this.event_descr = event_descr;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_starttime() {
        return event_starttime;
    }

    public void setEvent_starttime(String event_starttime) {
        this.event_starttime = event_starttime;
    }

    public String getEvent_endtime() {
        return event_endtime;
    }

    public void setEvent_endtime(String event_endtime) {
        this.event_endtime = event_endtime;
    }

    public String getEvent_place() {
        return event_place;
    }

    public void setEvent_place(String event_place) {
        this.event_place = event_place;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }
}
