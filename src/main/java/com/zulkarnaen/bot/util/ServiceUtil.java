package com.zulkarnaen.bot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class ServiceUtil {

	public static String dateConverter(String inputDate) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String result = "";
		try {
			date = df.parse(inputDate);
			result = new SimpleDateFormat("dd MMMMMMMM yyyy").format(date);
			// df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toUpperCase();
	}

	public static void main(String[] args) {
		String inputDate = "2020-03-15";
		System.out.println(dateConverter(inputDate));
	}

}
