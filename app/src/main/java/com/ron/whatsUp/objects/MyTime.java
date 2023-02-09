package com.ron.whatsUp.objects;

import android.icu.util.Calendar;

public class MyTime implements Comparable<MyTime> {
    private int hour;
    private int minutes;
    private int year;
    private int mount;
    private int date;

    public MyTime() {
    }

    public MyTime(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }


    public int getHour() {
        return hour;
    }

    public MyTime setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public MyTime setMinutes(int minutes) {
        this.minutes = minutes;
        return this;
    }

    @Override
    public int compareTo(MyTime time) {
        int compareHour = hour - time.hour;
        if (compareHour != 0) {
            return compareHour;
        }
        return minutes - time.minutes;
    }

    public int getYear() {
        return year;
    }

    public MyTime setYear(int year) {
        this.year = year;
        return this;
    }

    public int getMount() {
        return mount;
    }

    public MyTime setMount(int mount) {
        this.mount = mount;
        return this;
    }

    public int getDate() {
        return date;
    }

    public MyTime setDate(int date) {
        this.date = date;
        return this;
    }
    public Calendar parse_my_time_to_calender(){
        Calendar msg_cal = Calendar.getInstance();
        msg_cal.set(Calendar.MONTH,this.mount);
        msg_cal.set(Calendar.YEAR,this.year);
        msg_cal.set(Calendar.DATE,this.date);
        msg_cal.set(Calendar.HOUR,this.hour);
        msg_cal.set(Calendar.MINUTE,this.minutes);
        return msg_cal;
    }
    public MyTime update_my_time_by_calender(Calendar msg_cal){
        this.mount = msg_cal.get(Calendar.MONTH);
        this.year = msg_cal.get(Calendar.YEAR);
        this.date = msg_cal.get(Calendar.DATE);
        this.hour = msg_cal.get(Calendar.HOUR);
        this.minutes = msg_cal.get(Calendar.MINUTE);
        return this;
    }
}
