package com.hospital.app.entity;

import java.io.Serializable;

public class User implements Serializable{

    private String id;
    private String loginnname;
    private String password;
    private String username;
    private String sex;
    private String telephone;
    private String address;
    private String usertype;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginnname() {
        return loginnname;
    }

    public void setLoginnname(String loginnname) {
        this.loginnname = loginnname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public User() {
    }

    public User(String loginnname, String password, String username, String sex, String telephone, String address, String usertype) {
        this.loginnname = loginnname;
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.telephone = telephone;
        this.address = address;
        this.usertype = usertype;
    }

    public User(String id, String loginnname, String password, String username, String sex, String telephone, String address, String usertype) {
        this.id = id;
        this.loginnname = loginnname;
        this.password = password;
        this.username = username;
        this.sex = sex;
        this.telephone = telephone;
        this.address = address;
        this.usertype = usertype;
    }
}
