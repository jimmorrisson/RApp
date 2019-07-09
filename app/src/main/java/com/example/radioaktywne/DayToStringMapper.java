package com.example.radioaktywne;

import java.util.Calendar;

public class DayToStringMapper {
    public static String map(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return "0";
            case Calendar.TUESDAY:
                return "1";
            case Calendar.WEDNESDAY:
                return "2";
            case Calendar.THURSDAY:
                return "3";
            case Calendar.FRIDAY:
                return "4";
            case Calendar.SATURDAY:
                return "5";
            case Calendar.SUNDAY:
                return "6";
            default:
                return "0";
        }
    }
}
