package com.cross.juntalk2.model;

import java.util.GregorianCalendar;

public class CalendarModel {
    GregorianCalendar gregorianCalendar;
    int type ;


    public void setGregorianCalendar(GregorianCalendar gregorianCalendar) {
        this.gregorianCalendar = gregorianCalendar;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GregorianCalendar getGregorianCalendar() {
        return gregorianCalendar;
    }

    public int getType() {
        return type;
    }
}
