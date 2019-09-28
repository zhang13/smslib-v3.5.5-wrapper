package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String dateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static Date strToDate(String strDate){
		return strToDate(strDate,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date strToDate(String strDate,String formatStr){
		SimpleDateFormat format=new SimpleDateFormat(formatStr);
		try {
			return format.parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
