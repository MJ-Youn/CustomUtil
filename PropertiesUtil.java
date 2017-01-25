package com.smartmirror.advertisement.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class PropertiesUtil {

	@Autowired
	private static ReloadableResourceBundleMessageSource source;
	
	public static String getValue(String key) {
		return source.getMessage(key, null, null); 
	}
	
}
