package com.firsthotel.restaurant.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


//匯出CSV會是亂碼，這裡是解決亂碼的問題
public class tools {
	
	public static String escapeCsv(String value) {
	    if (value == null) {
	        return "";
	    }
	    // 如果是純數字，且長度>=8，前面加單引號
	    if (value.matches("\\d{8,}")) {
	        value = "'" + value;
	    }
	    boolean containsSpecial = value.contains(",") || value.contains("\"") || value.contains("\n");
	    if (containsSpecial) {
	        value = value.replace("\"", "\"\"");
	        return "\"" + value + "\"";
	    } else {
	        return value;
	    }
	}

	
	
}
