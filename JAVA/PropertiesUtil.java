package com.smartmirror.advertisement.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 *
 * @author MJYoun
 * @since 2017. 03. 06.
 *
 */
/**
 * 이 소스는 빈으로 생성하여 사용하는 util을 다룬것
 * ReloadableResourceBundleMessageSource를 이미 생성되어 있는 bean을 autowired하여 사용해야하는데,
 * static method에선 bean을 사용할 수 없기 때문에 이처럼 구현
 */
public class PropertiesUtil {

	private ReloadableResourceBundleMessageSource source;

	public void setSource(ReloadableResourceBundleMessageSource source) {
		this.source = source;
	}

	public String getValue(String key) {
		return source.getMessage(key, null, null);
	}

	public String getUrl(String key) {
		String value = source.getMessage(key, null, null);

		if (value.substring(value.length()-1).equals("/") == true) {
			value = value.substring(0, value.length()-1);
		}

		return value;
	}

}
