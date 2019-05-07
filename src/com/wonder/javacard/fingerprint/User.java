package com.wonder.javacard.fingerprint;


import java.util.Arrays;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kherman
 */
public class User {

    private static long id;
    private String name;
    private String mobile;
    private String email;
    private String age;
    private String bloogGroup;
    private byte[] template;

    public User() {
        super();
    }

    public User(String name, String mobile, String email, String age, String bloogGroup) {
        id += 1;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.age = age;
        this.bloogGroup = bloogGroup;
    }

    public User(String name, String mobile, String bloogGroup) {
        id += 1;
        this.name = name;
        this.mobile = mobile;
        this.bloogGroup = bloogGroup;
    }

    public static long getId() {
        return id;
    }

    public static void setId(long id) {
        User.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBloogGroup() {
        return bloogGroup;
    }

    public void setBloogGroup(String bloogGroup) {
        this.bloogGroup = bloogGroup;
    }

    public byte[] getTemplate() {
        return template;
    }

    public void setTemplate(byte[] template) {
        this.template = template;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.mobile);
        hash = 29 * hash + Objects.hashCode(this.email);
        hash = 29 * hash + Objects.hashCode(this.age);
        hash = 29 * hash + Objects.hashCode(this.bloogGroup);
        hash = 29 * hash + Arrays.hashCode(this.template);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.mobile, other.mobile)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.age, other.age)) {
            return false;
        }
        if (!Objects.equals(this.bloogGroup, other.bloogGroup)) {
            return false;
        }
        if (!Arrays.equals(this.template, other.template)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "User{" + "\n\tName=" + name + ", \n\tMobile=" + mobile + ", \n\tEmail=" + email + ", \n\tAge=" + age + ", \n\tBloogGroup=" + bloogGroup + ", \n\tTemplate=" + template + "\n"+'}';
    }

    // Custom Method
    // Decrypt Temlpate
    public String decryptTemplate(){
        return Arrays.toString(getTemplate());
    }
    
}
