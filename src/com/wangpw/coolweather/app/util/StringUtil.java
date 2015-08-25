package com.wangpw.coolweather.app.util;

public class StringUtil {
	
	  /**
     * 去掉字符串中特定字符，然后返回行的字符串
     * @param str 待剪切的字符串
     *            
     * @param cut 需要去掉的字符
     *            
     * @return result 转换后的字符串
     */
    public static String cutStringToInt(String str, String cut) {
        String result = null;
        if(cut == ""){
            result = str;
        }else{
            int location = str.indexOf(cut);
            if(location!=-1){
            String newStr = str.substring(0, location);
            result = newStr;
            }else{
            	return str;
            }
        }
        return result;
    }

}
