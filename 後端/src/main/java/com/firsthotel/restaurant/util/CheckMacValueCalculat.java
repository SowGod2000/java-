package com.firsthotel.restaurant.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CheckMacValueCalculat {

 private static String hashKey = "5294y06JbISpM5x9";
    private static String hashIV = "v77hoKGq4kWxNNIS";
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    
    // 這邊開始有註解都是做加密相關
    /**
  * 產生檢查碼
  * @param key
  * @param iv
  * @param obj
  * @return
  */
 public final static String genCheckMacValue(Map<String, String> params){
  System.out.println(params);
  Set<String> keySet = params.keySet();
  TreeSet<String> treeSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
  treeSet.addAll(keySet);
  String name[] = treeSet.toArray(new String[treeSet.size()]);
  System.out.println(name.length);
  String paramStr = "";
  for (int i = 0; i < name.length; i++) {
   if (!name[i].equals("CheckMacValue")) {
    paramStr += "&" + name[i] + "=" + params.get(name[i]);
   }
   System.out.println(paramStr);
  }
  System.out.println("hashKey: " + hashKey);
  System.out.println("hashIV: " + hashIV);
  String urlEncode = urlEncode("Hashkey=" + hashKey + paramStr + "&HashIV=" + hashIV).toLowerCase();
  urlEncode = netUrlEncode(urlEncode);
  return hash(urlEncode.getBytes(), "SHA-256");
 }
    
    
 
 /**
  * 將資料做 urlEncode編碼
  * @param data
  * @return url encoded string
  */
 public static String urlEncode(String data){
  String result = "";
  try{
   result = URLEncoder.encode(data, "UTF-8");
  } catch(UnsupportedEncodingException e){
   
  }
  return result; 
 }
 
 
 /**
  * 將做完的urlEncode字串做轉換符合 .NET語言的轉換規則
  * @param url
  * @return .Net url encoded string
  */
 private static String netUrlEncode(String url){
  String netUrlEncode = url.replaceAll("%21", "\\!").replaceAll("%28", "\\(").replaceAll("%29", "\\)");
  return netUrlEncode;
 }
 
 
 /**
  * 將 byte array 資料做 hash md5或 sha256 運算，並回傳 hex值的字串資料
  * @param data
  * @param isMD5
  * @return string
  */
 private final static String hash(byte data[], String mode){
  MessageDigest md = null;
  try{
   if(mode == "MD5"){
    md = MessageDigest.getInstance("MD5");
   }
   else if(mode == "SHA-256"){
    md = MessageDigest.getInstance("SHA-256");
   }
  } catch(NoSuchAlgorithmException e){
  }
  return bytesToHex(md.digest(data));
 }
 
 
 /**
  * 將 byte array 資料轉換成 hex字串值
  * @param bytes
  * @return string
  */
 private final static String bytesToHex(byte[] bytes) {
     char[] hexChars = new char[bytes.length * 2];
     for ( int j = 0; j < bytes.length; j++ ) {
         int v = bytes[j] & 0xFF;
         hexChars[j * 2] = hexArray[v >>> 4];
         hexChars[j * 2 + 1] = hexArray[v & 0x0F];
     }
     return new String(hexChars);
 }
}