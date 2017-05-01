package com.simple;

import java.util.List;

/**
 * Created by Adminis on 2017/4/30.
 */

public class User {
    private String name;
    private int age;
    private List<UserInfo> info;
    private long time ;
    private boolean isMember;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public boolean isMember() {
        return isMember;
    }
    public void setMember(boolean member) {
        isMember = member;
    }

    public List<UserInfo> getInfo() {
        return info;
    }

    public void setInfo(List<UserInfo> info) {
        this.info = info;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", info=" + info +
                ", time=" + time +
                ", isMember=" + isMember +
                '}';
    }
}
