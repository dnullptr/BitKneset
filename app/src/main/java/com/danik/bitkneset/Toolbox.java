package com.danik.bitkneset;

import java.util.ArrayList;

public class Toolbox { //this class is a useful toolbox of functions that are good for me alongside many fragments and situations.

    public static boolean isDateValid(String date) //my custom func to parse date strings that I support along my app.
    {
        if(date.length() != 10) return false;
        //format is dd/mm/yyyy only!
        // char ind 01 2 34 5 6789
        char[] dateAsCharArr = date.toCharArray();
        int day = Integer.parseInt(String.valueOf(dateAsCharArr[0])+String.valueOf(dateAsCharArr[1]));
        int month = Integer.parseInt(String.valueOf(dateAsCharArr[3])+String.valueOf(dateAsCharArr[4]));
        int year = Integer.parseInt(String.valueOf(dateAsCharArr[6])+String.valueOf(dateAsCharArr[7])+String.valueOf(dateAsCharArr[8]+String.valueOf(dateAsCharArr[9])));
        if(dateAsCharArr[2] != '/' || dateAsCharArr[5] != '/') return false;
        if(day < 0 || day > 31) return false;
        if(month < 0 || month > 12) return false;
        if(year < 0) return false;
        return true;

    }

    public static String buildDateFromInts(int dd,int mm,int yyyy)
    {
        String day,month,year;
        day=""+(dd<10?"0"+dd:dd);
        month=""+(mm<10?"0"+mm:mm);
        year=""+yyyy;
        return day+"/"+month+"/"+year;
    }

    public static ArrayList<Bill> deDupeList(ArrayList<Bill> arr) //my custom func to de-duplicate lists that came to Adapters , usually this is the culprit on androidx..
    {
        for (int i = 0; i < arr.size(); i++) {
            for (int j = i+1 ; j < arr.size(); j++) {
                if(arr.get(i).getDesc().equals(arr.get(j).getDesc()) && arr.get(i).getType().equals(arr.get(j).getType()) && arr.get(i).getAmount().equals(arr.get(j).getAmount()))
                    arr.remove(j);
            }
        }
        return arr;
    }
}
