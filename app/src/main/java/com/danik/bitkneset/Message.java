package com.danik.bitkneset;

public class Message {
    private String user;
    private String body;
    private String date;

    public Message(String user, String body, String date) {
        this.user = user;
        this.body = body;
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

