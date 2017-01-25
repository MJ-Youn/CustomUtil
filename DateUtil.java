package com.smartmirror.advertisement.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Date existDate(int hour, int minute, int second) {
		Calendar today = Calendar.getInstance();
		
		today.set(Calendar.HOUR_OF_DAY, hour);
		today.set(Calendar.MINUTE, minute);
		today.set(Calendar.SECOND, second);
		
		return today.getTime();
	}
	
	public static Date add30Minutes(Date date) {
		Calendar currentDate = Calendar.getInstance();
		
		currentDate.setTime(date);
		
		int hour = currentDate.get(Calendar.HOUR_OF_DAY);
		int minute = currentDate.get(Calendar.MINUTE);
		int addHour = (minute + 30) / 60;
		
		minute = (minute + 30) % 60;
		hour = hour + addHour;
		
		currentDate.set(Calendar.HOUR_OF_DAY, hour);
		currentDate.set(Calendar.MINUTE, minute);
		
		return currentDate.getTime();
	}
	
	public static Date addTime(Date date, Date addTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(addTime);
		
		int addHour = calendar.get(Calendar.HOUR);
		int addMinute = calendar.get(Calendar.MINUTE);
		int addSecond = calendar.get(Calendar.SECOND);
		int addMillisecond = calendar.get(Calendar.MILLISECOND);
		
		calendar.setTime(date);
		
		calendar.add(Calendar.MILLISECOND, addMillisecond);
		calendar.add(Calendar.SECOND, addSecond);
		calendar.add(Calendar.MINUTE, addMinute);
		calendar.add(Calendar.HOUR, addHour);
		
		return calendar.getTime();
	}
	
}
