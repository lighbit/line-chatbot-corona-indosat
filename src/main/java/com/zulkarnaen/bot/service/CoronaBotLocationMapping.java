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

		} else if (msgText.contains("jkt") || msgText.contains("dki") || msgText.contains("jakarta")) {

			coronaBotLocationModel.setTitle("RSPI Prof. Dr. Sulianti Saroso");
			coronaBotLocationModel
					.setAddress("Jl. Sunter Permai Raya, Tanjung Priok, Jakarta Utara. Telp: (021) 6506559");
			coronaBotLocationModel.setLatitude(-6.129851);
			coronaBotLocationModel.setLongtitude(106.862252);

		} else if (msgText.contains("bekasi") || msgText.contains("bks") || msgText.contains("planet bekasi")
				|| msgText.contains("planet-bekasi")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Bekasi");
			coronaBotLocationModel.setAddress("Jl. Raya Teuku Umar No.202, Bekasi. Telp: (021) 883 74 444");
			coronaBotLocationModel.setLatitude(-6.266471);
			coronaBotLocationModel.setLongtitude(107.083071);

		} else if (msgText.contains("bdg") || msgText.contains("bandung")) {

			coronaBotLocationModel.setTitle("RSUP dr. Hasan Sadikin");
			coronaBotLocationModel.setAddress("Jl. Pasteur No.38, Pasteur, Bandung. Telp: (022) 2551111");
			coronaBotLocationModel.setLatitude(-6.896126);
			coronaBotLocationModel.setLongtitude(107.598748);

		} else if (msgText.contains("bgr") || msgText.contains("bogor")) {

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

		} else if (msgText.contains("tgr") || msgText.contains("tanggerang") || msgText.contains("tangerang")
				|| msgText.contains("banten")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Tangerang");
			coronaBotLocationModel.setAddress("Jl. Jend. Ahmad Yani No.9. Telp: (021) 5523507");
			coronaBotLocationModel.setLatitude(-6.169454);
			coronaBotLocationModel.setLongtitude(106.635654);

		} else if (msgText.contains("semarang")) {

			coronaBotLocationModel.setTitle("RSUP dr. Kariadi semarang");
			coronaBotLocationModel.setAddress("Jl. Dr. Sutomo No.16, Semarang. Telp: (024) 8413993, 8413476");
			coronaBotLocationModel.setLatitude(-6.9939673);
			coronaBotLocationModel.setLongtitude(110.40754);

		} else if (msgText.contains("klaten")) {

			coronaBotLocationModel.setTitle("RS dr. Seoradji Tirtonegoro Klaten");
			coronaBotLocationModel.setAddress("Jl. Dr. Soeradji Tirtonegoro No.1, Klaten. Telp: (0272) 321041");
			coronaBotLocationModel.setLatitude(-7.714226);
			coronaBotLocationModel.setLongtitude(110.588687);

			/* update set latitude dan longitude dari sini */
		} else if (msgText.contains("salatiga")) {

			coronaBotLocationModel.setTitle("RS Paru dr. Ario Wirawan salatiga");
			coronaBotLocationModel.setAddress("Jl. Hasanudin No.806, Mangunsari, Salatiga. Telp: (0298) 326130");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("purwokerto")) {

			coronaBotLocationModel.setTitle("RSUD Prof. Dr. Margono Soekarjo");
			coronaBotLocationModel.setAddress("Jl. Dr. Gumbreg No.1, Purwokerto. Telp: (0281) 632708");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("surakarta")) {

			coronaBotLocationModel.setTitle("RSUD dr. Moewardi Surakarta");
			coronaBotLocationModel.setAddress("Jl. Kolonel Sutarto No.132, Surakarta. Telp: (0271) 634634");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("magelang")) {

			coronaBotLocationModel.setTitle("RSUD Tidar Magelang");
			coronaBotLocationModel.setAddress("Jl. Tidar No.30 A, Magelang. Telp: (0293) 36226");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("banyumas")) {

			coronaBotLocationModel.setTitle("RSUD Banyumas");
			coronaBotLocationModel
					.setAddress("Jl. Rumah Sakit No.1, Karangpucung, Kabupaten Banyumas. Telp: (0281) 796031");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kudus")) {

			coronaBotLocationModel.setTitle("RSU dr. Loekmonohadi");
			coronaBotLocationModel.setAddress("Jl. Dr. Lukmonohadi No.19, Kabupaten Kudus. Telp:  (0291) 444001");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kraton")) {

			coronaBotLocationModel.setTitle("RSUD Kraton");
			coronaBotLocationModel.setAddress("Jl. Veteran No.31, Pekalongan. Telp: (0285) 421621");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("slawi")) {

			coronaBotLocationModel.setTitle("RSUD dr. Soeselo Slawi");
			coronaBotLocationModel.setAddress("Jl. Dr. Sutomo No.63, Slawi. Telp: (0283) 491016");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kendal")) {

			coronaBotLocationModel.setTitle("RSUD RAA Soewondo Kendal");
			coronaBotLocationModel.setAddress("Jl. Laut No.21, Kendal. Telp: (0294) 381433");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("jogja") || msgText.contains("yogya") || msgText.contains("bantu")) {

			coronaBotLocationModel.setTitle("RSUP dr. Sardjito");
			coronaBotLocationModel.setAddress("Jl. Kesehatan No.1, Yogyakarta. Telp: (0274) 631190");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sby") || msgText.contains("surabaya")) {

			coronaBotLocationModel.setTitle("RSUD dr. Soetomo");
			coronaBotLocationModel.setAddress("Jl. Mayjen Prof. Dr. Moestopo No.6 – 8, Surabaya. Telp: (031) 5501078");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("madiun")) {

			coronaBotLocationModel.setTitle("RSUD dr. Soedono Madiun");
			coronaBotLocationModel.setAddress("Jl. Dr. Sutomo No.59, Madiun. Telp: (0351) 454657");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("malang")) {

			coronaBotLocationModel.setTitle("RSUD dr. Saiful Anwar");
			coronaBotLocationModel.setAddress("Jl. Jaksa Agung Suprapto No.2, Malang. Telp: (0341) 362101");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("jember")) {

			coronaBotLocationModel.setTitle("RSUD dr. Soebandi Jember");
			coronaBotLocationModel.setAddress("Jl. Dr. Soebandi No.124, Jember. Telp: 0823 0159 8557");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("pare")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Kediri Pare");
			coronaBotLocationModel.setAddress("Jl. Pahlawan Kusuma Bangsa No.1, Pare. Telp: (0354) 391718");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("tuban")) {

			coronaBotLocationModel.setTitle("RSUD dr. R. Koesma Tuban");
			coronaBotLocationModel.setAddress("Jl. Dr. Wahidin Sudirohusodo No.800, Tuban. Telp: (0356) 321010");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("banyuwangi") || msgText.contains("bayuwangi")) {

			coronaBotLocationModel.setTitle("RSUD Blambangan");
			coronaBotLocationModel.setAddress("Jl. Letkol Istiqlah No.49, Banyuwangi. Telp: (0333) 421118");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("bojonegoro")) {

			coronaBotLocationModel.setTitle("RSUD Dr. R. Sosodoro Djatikoesoemo");
			coronaBotLocationModel.setAddress("Jl. Dr. Wahidin No.36, Bojonegoro. Telp: (0353) 881193");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("tulungagung") || msgText.contains("tulung agung")
				|| msgText.contains("tulung-agung")) {

			coronaBotLocationModel.setTitle("RSUD Dr. Iskak Tulungagung");
			coronaBotLocationModel
					.setAddress("Jl. Dr. Wahidin Sudiro Husodo, Kabupaten Tulungagung. Telp: (0355) 322609");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("denpasar") || msgText.contains("bali")) {

			coronaBotLocationModel.setTitle("RSUP Sanglah");
			coronaBotLocationModel.setAddress("Jl. Diponegoro, Denpasar, Bali. Telp: (0361) 227912");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sidoarjo")) {

			coronaBotLocationModel.setTitle("RSUD Sidoarjo");
			coronaBotLocationModel.setAddress("Jl. Mojopahit No.667, Kabupaten Sidoarjo. Telp: (031) 8961649");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("gianyar")) {

			coronaBotLocationModel.setTitle("RSUD Sanjiwani Gianyar");
			coronaBotLocationModel.setAddress("Jl. Ciung Wanara-Gianyar No.2, Gianyar. Telp: (0361) 943020");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("tabanan")) {

			coronaBotLocationModel.setTitle("RSUD Tabanan");
			coronaBotLocationModel.setAddress("Jl. Pahlawan No.14, Tabanan. Telp: (0361) 811027");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("buleleng")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Buleleng");
			coronaBotLocationModel.setAddress("Jl. Ngurah Rai No.30, Astina, Buleleng. Telp: (0362) 22046");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("mataram") || msgText.contains("ntb")) {

			coronaBotLocationModel.setTitle("RSUD NTB");
			coronaBotLocationModel.setAddress("Jl. Prabu Rangkasari, Dasan Cermen, Mataram. Telp: (0370) 7502424");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("bima")) {

			coronaBotLocationModel.setTitle("RSUD Kota Bima");
			coronaBotLocationModel.setAddress("Jl. Langsat No.1, Raba, Bima. Telp: (0374) 43142");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("selong")) {

			coronaBotLocationModel.setTitle("RSUD Dr. R. Sudjono");
			coronaBotLocationModel.setAddress("Jl. Prof. M. Yamin SH No.55, Selong. Telp: (0376) 21118");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sumbawa")) {

			coronaBotLocationModel.setTitle("RSUD H. L. Manambai Abdul Kadir");
			coronaBotLocationModel
					.setAddress("Jl. Lintas Sumbawa-Bima Km 5, Seketeng, Kabupaten Sumbawa. Telp: (0371) 2628078");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kupang")) {

			coronaBotLocationModel.setTitle("RSUD Prof. dr. W. Z. Johannes");
			coronaBotLocationModel.setAddress("Jl. Dr. Moch. Hatta No.19, Kupang. Telp: (0380) 832892");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sikka")) {

			coronaBotLocationModel.setTitle("RSU dr. TC. Hillers Maumere");
			coronaBotLocationModel
					.setAddress("Jl. Wairklau, No. 1, Kota Baru, Alok Timur, Kabupaten Sikka. (0382) 21314");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("bajo")) {

			coronaBotLocationModel.setTitle("RSUD Komodo Labuan Bajo");
			coronaBotLocationModel.setAddress("Jl. Trans Ruteng - Labuan Bajo, Desa Golo Bilas, Komodo.");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("pontianak") || msgText.contains("ponti anak") || msgText.contains("ponti-anak")) {

			coronaBotLocationModel.setTitle("RSUD dr. Soedarso Pontianak");
			coronaBotLocationModel.setAddress("Jl. Dr. Soedarso No.1, Pontianak. Telp: (0561) 737701");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("singkawang")) {

			coronaBotLocationModel.setTitle("RSUD dr. Abdul Azis Singkawang");
			coronaBotLocationModel.setAddress("Jl. Dr. Soetomo No.28, Singkawang. Telp: (0562) 631748");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sintang")) {

			coronaBotLocationModel.setTitle("RSUD Ade Mohamad Djoen Sintang");
			coronaBotLocationModel.setAddress("Jl. Pattimura No.1, Sintang. Telp: (0565) 22022");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("ketapang")) {

			coronaBotLocationModel.setTitle("RSUD dr. Agoesdjam Ketapang");
			coronaBotLocationModel
					.setAddress("Jl. DI Panjaitan No.51, Sampit, Kabupaten Ketapang. Telp: (0534) 3037239");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("palangkaraya") || msgText.contains("palangka raya")
				|| msgText.contains("palangka-raya")) {

			coronaBotLocationModel.setTitle("RSUD dr. Doris Sylvanus Palangkaraya");
			coronaBotLocationModel.setAddress("Jl. Tambun Bungai No.4, Palangka Raya. Telp: (0536) 3221717");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kotawaringin")) {

			coronaBotLocationModel.setTitle("RSUD dr. Murjani Sampit");
			coronaBotLocationModel.setAddress("Jl. H. Muhammad Arsyad No.65, Kotawaringin Timur. Telp: (0531) 21010");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("banjarmasin")) {

			coronaBotLocationModel.setTitle("RSUD Ulin Banjarmasin");
			coronaBotLocationModel.setAddress("Jl. Ahmad Yani No.43, Banjarmasin. Telp: (0511) 3252229");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("palaihari")) {

			coronaBotLocationModel.setTitle("RSUD H. Boejasin Pelaihari");
			coronaBotLocationModel.setAddress("Jl. A. Syahrani, Pelaihari. Telp: (0512) 21082");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("samarinda")) {

			coronaBotLocationModel.setTitle("RSUD Abdul Wahab Sjahrani");
			coronaBotLocationModel.setAddress("Jl. Palang Merah No.1, Samarinda. Telp: (0541) 738118");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("balikpapan") || msgText.contains("balik papan")
				|| msgText.contains("balik-papan")) {

			coronaBotLocationModel.setTitle("RSUD dr. Kanujoso Djatiwibowo");
			coronaBotLocationModel.setAddress("Jl. MT Haryono No.656, Balikpapan. Telp: (0542) 873901");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("bontang")) {

			coronaBotLocationModel.setTitle("RSU Taman Husada Bontang");
			coronaBotLocationModel.setAddress("Jl. Letjen S. Parman No.1, Bontang. Telp: (0548) 22111");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("kutai") || msgText.contains("kartanegara")) {

			coronaBotLocationModel.setTitle("RSUD Aji Muhammad Parikesit");
			coronaBotLocationModel
					.setAddress("Jl. Ratu Agung No.1, Tlk. Dalam, Kutai Kartanegara. Telp: (0541) 661015");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("tarakan")) {

			coronaBotLocationModel.setTitle("RSUD Kota Tarakan");
			coronaBotLocationModel.setAddress("Jl. Pulau Irian No.1, Kp. Satu Skip, Tarakan. Telp: (0551) 21166");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("gorontalo")) {

			coronaBotLocationModel.setTitle("RSUD Prof. dr. H. Aloei Saboe");
			coronaBotLocationModel.setAddress("Jl. S. Batutihe No.7, Gorontalo. Telp: (0435) 821019");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("manado")) {

			coronaBotLocationModel.setTitle("RSUP Prof. dr. R. D Kandou");
			coronaBotLocationModel.setAddress("Jl. Raya Tanawangko No.56, Manado. Telp: (0431) 8383058");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("minahasa")) {

			coronaBotLocationModel.setTitle("RSU Ratatotok Buyat");
			coronaBotLocationModel
					.setAddress("jl. J. W. Lasut Ratatotok II, Ratatotok,  Minahasa. Telp: (0431) 3177610");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("mamuju")) {

			coronaBotLocationModel.setTitle("RSUD Provinsi Sulawesi Barat");
			coronaBotLocationModel.setAddress("Jl. RE Martadinata, Simboro, Kabupaten Mamuju. Telp: 0823 9621 2345");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("palu")) {

			coronaBotLocationModel.setTitle("RSUD Undata Palu");
			coronaBotLocationModel.setAddress("Jl. Trans Sulawesi Tondo, Palu. Telp: (0451) 4908020");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("luwuk")) {

			coronaBotLocationModel.setTitle("RSUD Banggai Luwuk");
			coronaBotLocationModel.setAddress("Jl. lmam Bonjol No.14, Luwuk. Telp: (0461) 21820");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("makasar")) {

			coronaBotLocationModel.setTitle("RSUP dr. Wahidin Sudirohusodo");
			coronaBotLocationModel.setAddress("Jl. Perintis Kemerdekaan Km.11, Makassar. Telp: (0411) 510675");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("toraja")) {

			coronaBotLocationModel.setTitle("RSU Lakipadada Toraja");
			coronaBotLocationModel.setAddress("Jl. Pongtiku No. 486, Kabupaten Tana Toraja. (0423) 22264");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sinjai")) {

			coronaBotLocationModel.setTitle("RSUD Kabupaten Sinjai");
			coronaBotLocationModel.setAddress("Jl. Jend. Sudirman No.47, Sinjai. Telp: (0482) 21132");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("watubangga")) {

			coronaBotLocationModel.setTitle("RS Bahtera Mas Kendari");
			coronaBotLocationModel.setAddress("Jl. Kapten Piere Tendean, Watubangga, Kendari. Telp: (0401) 3195611");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("maluku")) {

			coronaBotLocationModel.setTitle("RSUP dr. J. Leimena");
			coronaBotLocationModel.setAddress("Rumah Tiga, Tlk. Ambon, Ambon.");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("papua") || msgText.contains("jayapura")) {

			coronaBotLocationModel.setTitle("RSU Jayapura");
			coronaBotLocationModel.setAddress("Jl. Kesehatan No.1, Jayapura. Telp: (0967) 533616");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else if (msgText.contains("sorong")) {

			coronaBotLocationModel.setTitle("RSUD Sorong");
			coronaBotLocationModel.setAddress("Kp. Baru, Sorong. Telp: (0951) 321850");
			coronaBotLocationModel.setLatitude(5.563991);
			coronaBotLocationModel.setLongtitude(95.337631);

		} else {

			if (msgText.equals("lokasi")) {
				coronaBotLocationModel.setTitle("tidak sesuai");
			} else {
				coronaBotLocationModel.setTitle("salah");
			}

		}
		return coronaBotLocationModel;

	}

}
