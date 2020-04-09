package com.zulkarnaen.bot.service;

import org.springframework.stereotype.Service;

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

		} else if (msgText.contains("medan")) {

			coronaBotLocationModel.setTitle("RSUP H. Adam Malik Medan");
			coronaBotLocationModel.setAddress("Jl. Bunga Lau No.17. Telp: (061) 8360381");
			coronaBotLocationModel.setLatitude(3.518719);
			coronaBotLocationModel.setLongtitude(98.608574);

		} else if (msgText.contains("padang")) {

			coronaBotLocationModel.setTitle("RSU Padang Sidempuan");
			coronaBotLocationModel
					.setAddress("Jl. Dr. Ferdinand Lumban Tobing No.10, Padang Sidempuan. Telp: (0634) 21780, 21251");
			coronaBotLocationModel.setLatitude(1.372669);
			coronaBotLocationModel.setLongtitude(99.272306);

		} else if (msgText.contains("bukit tinggi") || msgText.contains("bukittinggi")
				|| msgText.contains("bukit-tinggi")) {

			coronaBotLocationModel.setTitle("RSUD Dr. Achmad Mochtar Bukittinggi");
			coronaBotLocationModel.setAddress("Jl. Dr. Abdul Rivai No.1, Bukittinggi. Telp: (0752) 21720, 21831");
			coronaBotLocationModel.setLatitude(-0.299530);
			coronaBotLocationModel.setLongtitude(100.366101);

		} else if (msgText.contains("riau")) {

			coronaBotLocationModel.setTitle("RSU Arifin Achmad");
			coronaBotLocationModel.setAddress("Jl. Diponegoro No.2, Pekanbaru. Telp: (0761) 21618");
			coronaBotLocationModel.setLatitude(0.523423);
			coronaBotLocationModel.setLongtitude(101.451777);

		} else if (msgText.contains("kepulauan riau") || msgText.contains("kepulawan riau")
				|| msgText.contains("kepulawan-riau") || msgText.contains("kepulauan-riau")) {

			coronaBotLocationModel.setTitle("RSUD Provinsi Kepulauan Riau Tanjung Pinang");
			coronaBotLocationModel
					.setAddress("Jl. WR. Supratman No.100, Air Raja, Tanjung Pinang. Telp: (0771) 7335202");
			coronaBotLocationModel.setLatitude(0.924213);
			coronaBotLocationModel.setLongtitude(104.500110);

		} else if (msgText.contains("jambi")) {

			coronaBotLocationModel.setTitle("RSUD Raden Mattaher Jambi");
			coronaBotLocationModel.setAddress("Jl. Letjen Suprapto No.31, Telanaipura, Jambi. Telp: (0741) 61692");
			coronaBotLocationModel.setLatitude(-1.602624);
			coronaBotLocationModel.setLongtitude(103.580181);

		} else if (msgText.contains("cikarang")) {

			coronaBotLocationModel.setTitle("RS Sentra Medika");
			coronaBotLocationModel
					.setAddress("Jl. Raya Industri Pasir Gombong - Cikarang, Bekasi. Telp: (021) 890 416 064");
			coronaBotLocationModel.setLatitude(-6.278166);
			coronaBotLocationModel.setLongtitude(107.152644);

		} else if (msgText.contains("palembang")) {

			coronaBotLocationModel.setTitle("RSUP M. Hoesin");
			coronaBotLocationModel.setAddress("Jl. Jend. Sudirman Km.3-5, Palembang. Telp: (0711) 30126, 354088");
			coronaBotLocationModel.setLatitude(-2.966499);
			coronaBotLocationModel.setLongtitude(104.750242);

		} else if (msgText.contains("babel") || msgText.contains("bangka") || msgText.contains("belitung")) {

			coronaBotLocationModel.setTitle("RSUD Depati Hamzah");
			coronaBotLocationModel.setAddress("Jl. Soekarno Hatta, Bukitbesar, Pangkal Pinang. Telp: (0717) 422693");
			coronaBotLocationModel.setLatitude(-2.143602);
			coronaBotLocationModel.setLongtitude(106.124649);

		} else if (msgText.contains("bengkulu")) {

			coronaBotLocationModel.setTitle("RSUD M. Yunus Bengkulu");
			coronaBotLocationModel.setAddress("Jl. Bhayangkara, Sidomulyo, Bengkulu. Telp: (0736) 52004, 52008, 51111");
			coronaBotLocationModel.setLatitude(-3.834065);
			coronaBotLocationModel.setLongtitude(102.314134);

		} else if (msgText.contains("lampung")) {

			coronaBotLocationModel.setTitle("RSUD Dr. H. Abdul Moeloek");
			coronaBotLocationModel.setAddress("Jl. Dr. Rivai No.6, Bandar Lampung. Telp: (0721) 703312");
			coronaBotLocationModel.setLatitude(-5.402763);
			coronaBotLocationModel.setLongtitude(105.258550);

		} else if (msgText.equals("jkt") || msgText.contains("dki") || msgText.contains("jakarta")) {

			coronaBotLocationModel.setTitle("RSPI Prof. Dr. Sulianti Saroso");
			coronaBotLocationModel
					.setAddress("Jl. Sunter Permai Raya, Tanjung Priok, Jakarta Utara. Telp: (021) 6506559");
			coronaBotLocationModel.setLatitude(-6.129851);
			coronaBotLocationModel.setLongtitude(106.862252);

		} else if (msgText.contains("bekasi") || msgText.equals("bks") || msgText.contains("planet bekasi")
				|| msgText.contains("planet-bekasi")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Bekasi");
			coronaBotLocationModel.setAddress("Jl. Raya Teuku Umar No.202, Bekasi. Telp: (021) 883 74 444");
			coronaBotLocationModel.setLatitude(-6.266471);
			coronaBotLocationModel.setLongtitude(107.083071);

		} else if (msgText.equals("bdg") || msgText.contains("bandung")) {

			coronaBotLocationModel.setTitle("RSUP dr. Hasan Sadikin");
			coronaBotLocationModel.setAddress("Jl. Pasteur No.38, Pasteur, Bandung. Telp: (022) 2551111");
			coronaBotLocationModel.setLatitude(-6.896126);
			coronaBotLocationModel.setLongtitude(107.598748);

		} else if (msgText.equals("bgr") || msgText.contains("bogor")) {

			coronaBotLocationModel.setTitle("RS Paru dr. M. Goenawan Partowidigdo");
			coronaBotLocationModel.setAddress("Jl. Puncak Raya Km. 83, Cisarua, Bogor. Telp: (0251) 8253630");
			coronaBotLocationModel.setLatitude(-6.687455);
			coronaBotLocationModel.setLongtitude(106.939033);

		} else if (msgText.contains("cirebon")) {

			coronaBotLocationModel.setTitle("RSUD Gunung Jati Cirebon");
			coronaBotLocationModel.setAddress("Jl. Kesambi Raya No.56, Cirebon. Telp: (0231) 206330");
			coronaBotLocationModel.setLatitude(-6.730621);
			coronaBotLocationModel.setLongtitude(108.555399);

		} else if (msgText.contains("sukabumi")) {

			coronaBotLocationModel.setTitle("RSUD R. Syamsudin, SH Sukabumi");
			coronaBotLocationModel.setAddress("Jl. Rumah Sakit No.1, Sukabumi. Telp: (0266) 245703");
			coronaBotLocationModel.setLatitude(-6.914263);
			coronaBotLocationModel.setLongtitude(106.934635);

		} else if (msgText.contains("garut")) {

			coronaBotLocationModel.setTitle("RSUD dr. Slamet Garut");
			coronaBotLocationModel.setAddress("Jl. Rumah Sakit No.10, Garut. Telp: (0262) 232720");
			coronaBotLocationModel.setLatitude(-7.219574);
			coronaBotLocationModel.setLongtitude(107.896972);

		} else if (msgText.contains("indramayu") || msgText.contains("indra mayu")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Indramayu");
			coronaBotLocationModel.setAddress("Jl. Murahnara No.7, Sindang, Indramayu. Telp: (0234) 272655");
			coronaBotLocationModel.setLatitude(-6.312600);
			coronaBotLocationModel.setLongtitude(108.319125);

		} else if (msgText.contains("cimahi")) {

			coronaBotLocationModel.setTitle("RSU Tk. II Dustira");
			coronaBotLocationModel.setAddress("Jl. Dustira No.1, Baros, Cimahi.");
			coronaBotLocationModel.setLatitude(-6.885490);
			coronaBotLocationModel.setLongtitude(107.534942);

		} else if (msgText.equals("tgr") || msgText.contains("tangerang") || msgText.contains("banten")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Tangerang");
			coronaBotLocationModel.setAddress("Jl. Jend. Ahmad Yani No.9. Telp: (021) 5523507");
			coronaBotLocationModel.setLatitude(-6.169454);
			coronaBotLocationModel.setLongtitude(106.635654);

		} else {

			coronaBotLocationModel.setTitle("tidak sesuai");

		}
		return coronaBotLocationModel;

	}

}
