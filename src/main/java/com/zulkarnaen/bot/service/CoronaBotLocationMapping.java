package com.zulkarnaen.bot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.zulkarnaen.bot.model.CoronaBotLocationModel;

@Service
public class CoronaBotLocationMapping {

	/* Mapping all indonesian RS */
	public static CoronaBotLocationModel handleLocationRSMapping(String replyToken, String msgText) {

		CoronaBotLocationModel coronaBotLocationModel = new CoronaBotLocationModel();

		if (msgText.contains("aceh")) {

			coronaBotLocationModel.setTitle("RSU Dr. Zainoel Abidin Banda Aceh");
			coronaBotLocationModel
					.setAddress("Jl. Teuku Moh. Daud Beureueh No.108, Banda Aceh. Telp: (0651) 34565, 22077, 28148");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("")) {

			coronaBotLocationModel.setTitle("");
			coronaBotLocationModel.setAddress("");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else {

			coronaBotLocationModel.setTitle("tidak sesuai");

		}
		return coronaBotLocationModel;

	}

}
