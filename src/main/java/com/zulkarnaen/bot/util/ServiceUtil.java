package com.zulkarnaen.bot.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
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

	public static String populationFormat(String population) {
		String pattern = "###,###.###";
		int number = Integer.parseInt(population);

		population = Integer.toString(number);

		DecimalFormat decimalFormat = new DecimalFormat(pattern);

		String format = decimalFormat.format(population);

		return format;
	}

	public static void main(String[] args) {
		String inputDate = "123456.23";

		String awe = populationFormat(inputDate);

		System.out.println(awe);
	}

}
