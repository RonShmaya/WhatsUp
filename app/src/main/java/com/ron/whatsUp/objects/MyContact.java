package com.ron.whatsUp.objects;

import java.util.Objects;

public class MyContact {
    private String name;
    private String phone;
    private String photo;

    public MyContact() {
    }

    public String getName() {
        return name;
    }

    public MyContact setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public MyContact setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public MyContact setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyContact myContact = (MyContact) o;
        return Objects.equals(name, myContact.name) && Objects.equals(phone, myContact.phone) && Objects.equals(photo, myContact.photo);
    }

}
