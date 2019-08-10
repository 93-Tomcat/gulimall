package com.atguigu.gulimall.commons.utils;



public class AppUtils {

    public static String arrayToStringWithSeperator(String[] arr,String sep){
        StringBuffer sb = new StringBuffer();

        String s = "";
        if (arr != null && arr.length >0){
            for (String str : arr) {
                sb.append(str);
                sb.append(sep);
            }
            s = sb.toString().substring(0,sb.length()-1);
        }


        return s;
    }


}
