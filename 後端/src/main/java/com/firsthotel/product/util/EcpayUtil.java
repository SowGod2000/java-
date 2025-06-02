package com.firsthotel.product.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EcpayUtil {

    public static String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIV) throws Exception {
        SortedMap<String, String> sortedParams = new TreeMap<>(params);

        StringBuilder sb = new StringBuilder("HashKey=" + hashKey);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(hashIV);

        String urlEncoded = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.name())
                .toLowerCase()
                .replaceAll("%21", "!")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%2a", "*")
                .replaceAll("%2d", "-")
                .replaceAll("%2e", ".")
                .replaceAll("%5f", "_")
                .replaceAll("%20", "+");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(urlEncoded.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            hex.append(String.format("%02X", b));
        }

        return hex.toString();
    }

    public static Map<String, String> extractFormInputs(String html) {
        Map<String, String> formInputs = new HashMap<>();
        Pattern pattern = Pattern.compile("<input[^>]*name=[\"']?([^\"'>]+)[\"']?[^>]*value=[\"']?([^\"'>]*)[\"']?[^>]*/?>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            formInputs.put(name, value);
        }

        return formInputs;
    }
}
