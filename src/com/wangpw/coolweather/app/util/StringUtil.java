package com.wangpw.coolweather.app.util;

public class StringUtil {
	
	  /**
     * ȥ���ַ������ض��ַ���Ȼ�󷵻��е��ַ���
     * @param str �����е��ַ���
     *            
     * @param cut ��Ҫȥ�����ַ�
     *            
     * @return result ת������ַ���
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
