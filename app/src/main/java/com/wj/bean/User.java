package com.wj.bean;

import java.io.Serializable;

/**
 * Created by DaiYiqian on 2016/7/27.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1645783927466187577L;

    public int id;
    public String money;
    public String name;
    public String password;
    public String power;
    public String sex;
    public String telephone;
    public String token;
    public String username;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", money='" + money + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", power='" + power + '\'' +
                ", sex='" + sex + '\'' +
                ", telephone='" + telephone + '\'' +
                ", token='" + token + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
