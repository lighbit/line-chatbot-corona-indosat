package com.zulkarnaen.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotDatum;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.model.CoronaBotLineEventsModel;
import com.zulkarnaen.bot.model.CoronaBotLocationModel;
import com.zulkarnaen.bot.service.CoronaBotLocationMapping;
import com.zulkarnaen.bot.service.CoronaBotService;
import com.zulkarnaen.bot.service.CoronaBotTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class CoronaBotController {

	@Autowired
	@Qualifier("lineSignatureValidator")
	private LineSignatureValidator lineSignatureValidator;

	@Autowired
	private CoronaBotService botService;

	@Autowired
	private CoronaBotTemplate coronaBotTemplate;

	private UserProfileResponse sender = null;
	private CoronaBotEvents coronaBotEvents = null;

	/* GET JSON */
	@RequestMapping(value = "/corona", method = RequestMethod.GET)
	public CoronaBotEvents getCoronaJson() {

		getEventsData("none");

		return coronaBotEvents;

	}

	/* WEBHOOK REQUEST MAPPING */
	@RequestMapping(value = "/webhook", method = RequestMethod.POST)
	public ResponseEntity<String> callback(String xLineSignature, @RequestBody String eventsPayload) {
		try {

//			 validasi line signature. matikan validasi ini jika masih dalam pengembangan
//			if (!lineSignatureValidator.validateSignature(eventsPayload.getBytes(), xLineSignature)) {
//				throw new RuntimeException("Invalid Signature Validation");
//			}

			System.out.println(eventsPayload);
			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			CoronaBotLineEventsModel eventsModel = objectMapper.readValue(eventsPayload,
					CoronaBotLineEventsModel.class);

			eventsModel.getEvents().forEach((event) -> {
				if (event instanceof JoinEvent || event instanceof FollowEvent) {
					String replyToken = ((ReplyEvent) event).getReplyToken();
					handleJointOrFollowEvent(replyToken, event.getSource());
				} else if (event instanceof MessageEvent) {
					handleMessageEvent((MessageEvent<?>) event);
				}
			});

			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	/* GREATING MESSAGE HANDLE BASE */
	private void greetingMessageCoronaDefault(String replyToken, Source source, String additionalMessage) {
		if (sender == null) {
			String senderId = source.getSenderId();
			sender = botService.getProfile(senderId);
		}

		TemplateMessage greetingMessage = coronaBotTemplate.greetingMessage(source, sender);

		if (additionalMessage != null) {
			List<Message> messages = new ArrayList<>();
			messages.add(new TextMessage(additionalMessage));
			messages.add(greetingMessage);
			botService.reply(replyToken, messages);
		} else {
			botService.reply(replyToken, greetingMessage);
		}
	}

	private void handleJointOrFollowEvent(String replyToken, Source source) {
		greetingMessageCoronaDefault(replyToken, source, null);
	}

	private void handleMessageEvent(MessageEvent<?> event) {
		String replyToken = event.getReplyToken();
		MessageContent content = event.getMessage();
		Source source = event.getSource();
		String senderId = source.getSenderId();
		sender = botService.getProfile(senderId);

		if (content instanceof TextMessageContent) {
			handleTextMessage(replyToken, (TextMessageContent) content, source);
		} else {
			greetingMessageCoronaDefault(replyToken, source, null);
		}
	}

	private void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
		if (source instanceof GroupSource) {
			handleGroupChats(replyToken, content.getText(), ((GroupSource) source).getGroupId());
		} else if (source instanceof RoomSource) {
			handleRoomChats(replyToken, content.getText(), ((RoomSource) source).getRoomId());
		} else if (source instanceof UserSource) {
			handleOneOnOneChats(replyToken, content.getText());
		} else {
			botService.replyText(replyToken, "Unknown Message Source!");
		}
	}

	private void handleGroupChats(String replyToken, String textMessage, String groupId) {
		String msgText = textMessage.toLowerCase();

		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummaryCorona(replyToken, textMessage);
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanation(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHand(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new GroupSource(groupId, sender.getUserId()));
		}
	}

	private void handleRoomChats(String replyToken, String textMessage, String roomId) {
		String msgText = textMessage.toLowerCase();

		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummaryCorona(replyToken, textMessage);
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanation(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHand(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new RoomSource(roomId, sender.getUserId()));
		}
	}

	private void handleOneOnOneChats(String replyToken, String textMessage) {
		String msgText = textMessage.toLowerCase();
		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummaryCorona(replyToken, textMessage);
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanation(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHand(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new UserSource(sender.getUserId()));
		}
	}

	private void HandleSalam(String msgText, String replyToken, Source source) {

		if (msgText.contains("assala") || msgText.contains("asala") || msgText.contains("mikum")) {
			greetingMessageCoronaDefault(replyToken, source, "Waalaikumsalam " + sender.getDisplayName()
					+ ", Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.equals("hai") || msgText.equals("hallo") || msgText.equals("hei")
				|| msgText.equals("halo")) {
			greetingMessageCoronaDefault(replyToken, source, msgText + " " + sender.getDisplayName()
					+ "!, Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("pagi")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Pagi " + sender.getDisplayName()
					+ "!, Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("siang")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Siang " + sender.getDisplayName()
					+ "!, Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("malam")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Malam " + sender.getDisplayName()
					+ "!, Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else {
			greetingMessageCoronaDefault(replyToken, source, "Hai " + sender.getDisplayName()
					+ "!, Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		}

	}

	private void handleDicodingThanks(String replyToken) {

		String kenalan = "Hasil Karya Zulkarnaen!";
		String salam = "Thanks dicoding Sudah Bantu saya sejauh ini salam hangat Zulkarnaen :) ";

		List<Message> messages = new ArrayList<>();
		messages.add(new TextMessage(kenalan));
		messages.add(new TextMessage(salam));

		botService.reply(replyToken, messages);

	}

	private void handleCreator(String replyToken) {
		String urlCorona = "https://bit.ly/39N9V3R";
		String thumbnailImageUrl = "https://bit.ly/2UN5uld";
		String title = "Hai aku Zulkarnaen";
		String text = "Lihat Aku di Linkedin!";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona)));

		TemplateMessage templateMessage = new TemplateMessage("Creator", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	private void handlecoronaVirusExplanation(String replyToken) {
		String urlCorona = "https://bit.ly/3dX6riJ";
		String thumbnailImageUrl = "https://bit.ly/39HVXjO";
		String title = "Apa itu corona virus? (Covid-19)";
		String text = "Penjelasan Seputar corona virus (covid-19) ada disini.";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona)));

		TemplateMessage templateMessage = new TemplateMessage("Apa Itu Corona", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	private void handleHowToWashHand(String replyToken) {
		String urlCorona = "https://bit.ly/2Xezg45";
		String thumbnailImageUrl = "https://bit.ly/3aP3K0B";
		String title = "Pencegahan Corona Virus (Covid-19)";
		String text = "Cari Tahu cara mencegah & jika merasa cek RS dibawah";
		String rumahSakit = "Lokasi Rumah Sakit";
		String callCenter = "Call Center";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona), new MessageAction(rumahSakit, "Lokasi"),
						new MessageAction(callCenter, callCenter)));

		TemplateMessage templateMessage = new TemplateMessage("Pencegahan Corona", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	private void handleDonasiTemplate(String replyToken) {
		String urlCoronaKitabisa = "https://kitabisa.com/campaign/indonesialawancorona";
		String urlCoronaDompetDhuafa = "https://donasi.dompetdhuafa.org/bersamalawancorona/";
		String thumbnailImageUrl = "https://bit.ly/34eUJv7";
		String title = "Selamatkan Nyawa #BersamaLawanCorona";
		String text = "Mari Bantu Korban & Paramedis dgn Berdonasi di";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Kitabisa.com", urlCoronaKitabisa),
						new URIAction("DompetDhuafa.org", urlCoronaDompetDhuafa)));

		TemplateMessage templateMessage = new TemplateMessage("Berdonasi", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	private void handleCallCenter(String replyToken) {

		List<Message> messageList = new ArrayList<>();

		String packageID = "11538";
		String stickerID = "51626496";

		messageList.add(new TextMessage("119"));
		messageList.add(new TextMessage("Hallo " + sender.getDisplayName()
				+ "! 119 adalah nomor hotline seputar corona jika kamu merasakan gejala seperti corona langsung telpon dan jangan takus"));
		messageList.add(new TextMessage("Aku yakin kamu tidak akan kenapa-kenapa pasti!"));
		messageList.add(new StickerMessage(packageID, stickerID));

		botService.reply(replyToken, messageList);

	}

	private void showCarouselEvents(String replyToken) {
		showCarouselEvents(replyToken, null);
	}

	private void showCarouselEvents(String replyToken, String additionalInfo) {

		if ((coronaBotEvents == null)) {
			getEventsData(replyToken);
		}

		/* TAMPILKAN HASIl */
		TemplateMessage carouselEvents = coronaBotTemplate.carouselEvents(coronaBotEvents);

		if (additionalInfo == null) {
			botService.reply(replyToken, carouselEvents);
			return;
		}

		List<Message> messageList = new ArrayList<>();
		messageList.add(new TextMessage(additionalInfo));
		messageList.add(carouselEvents);
		botService.reply(replyToken, messageList);
	}

	private void getEventsData(String replyToken) {
		// Act as client with GET method
		String URI = "http://corona-api.com/countries/id";
		System.out.println("URI: " + URI);

		List<Message> messageList = new ArrayList<>();

		try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
			client.start();
			// Use HTTP Get to retrieve data
			HttpGet get = new HttpGet(URI);

			Future<HttpResponse> future = client.execute(get, null);
			HttpResponse responseGet = future.get();
			System.out.println("HTTP executed");
			System.out.println("HTTP Status of response: " + responseGet.getStatusLine().getStatusCode());

			// Get the response from the GET request
			InputStream inputStream = responseGet.getEntity().getContent();
			String encoding = StandardCharsets.UTF_8.name();
			String jsonResponse = IOUtils.toString(inputStream, encoding);

			System.out.println("Got result");
			System.out.println(jsonResponse);

			ObjectMapper objectMapper = new ObjectMapper();
			coronaBotEvents = objectMapper.readValue(jsonResponse, CoronaBotEvents.class);
		} catch (InterruptedException | ExecutionException | IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam mengambil data ke sumber terkait"));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	private void showEventSummaryCorona(String replyToken, String userTxt) {

		List<Message> messageList = new ArrayList<>();
		try {
			if (coronaBotEvents == null) {
				getEventsData(replyToken);
			}

			CoronaBotDatum eventData = coronaBotEvents.getData();

			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_event.json"), encoding);

			flexTemplate = String.format(flexTemplate, coronaBotTemplate.escape(eventData.getName()),
					coronaBotTemplate.escape(eventData.getCode()),
					coronaBotTemplate.escapeInt(eventData.getPopulation()),
					coronaBotTemplate.escape(eventData.getUpdated_at()),
					coronaBotTemplate.escapeInt(eventData.getToday().getDeaths()),
					coronaBotTemplate.escapeInt(eventData.getToday().getConfirmed()),
					coronaBotTemplate.escapeInt(eventData.getLatest_data().getDeaths()),
					coronaBotTemplate.escapeInt(eventData.getLatest_data().getConfirmed()),
					coronaBotTemplate.escapeInt(eventData.getLatest_data().getRecovered()),
					coronaBotTemplate.escapeInt(eventData.getLatest_data().getCritical()));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);
			botService.reply(replyToken, new FlexMessage("Corona VS Indonesia", flexContainer));
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	private void showEventSummaryDeclaration(String replyToken, String userTxt) {

		List<Message> messageList = new ArrayList<>();
		try {

			int total = Integer.parseInt(userTxt.substring(userTxt.lastIndexOf(":") + 2));

			if (userTxt.contains("Meninggal")) {

				if (total < 10) {

					messageList.add(new TextMessage("Korban Meninggal Kurang dari 10 orang yaitu: " + total));
					messageList.add(new TextMessage("Stay Safe dan Stay Health ya dengan cara dirumah aja :)"));
					botService.reply(replyToken, messageList);
				} else if (total > 150 && total < 200) {

					messageList.add(new TextMessage("Korban Meninggal lebih dari 150 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"ini URGENT! KAMU HARUS Ikuti Anjuran Pemerintah, Pakai Masker, Tetap Tenang dan Jangan Panik!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);

				} else if (total > 100) {

					messageList.add(new TextMessage("Korban Meninggal lebih dari 100 Orang yaitu: " + total));
					messageList.add(
							new TextMessage("Ikuti Anjuran Pemerintah, Pakai Masker, Tetap Tenang dan Jangan Panik!"));
					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban Meninggal yaitu: " + total));
					messageList.add(new TextMessage(
							"Sangat URGENT!!! Jangan Kemana Mana tetap dirumah dan selalu pakai masker jika keluar!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);

				}

			} else if (userTxt.contains("Terkonfirmasi")) {

				String packageID = "11537";
				String stickerID = "52002735";

				if (total < 10) {

					messageList.add(new TextMessage("Korban Terkonfirmasi Kurang dari 10 orang yaitu: " + total));
					messageList.add(new TextMessage("Mari Dirumah Aja biar Semakin hilang virus corona nya!"));
					botService.reply(replyToken, messageList);
				} else if (total > 1000 && total < 1500) {

					messageList.add(new TextMessage("Korban Terkonfirmasi Lebih dari 1000 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Sudah Banyak Yang Terkonfirmasi Diharapkan Untuk Kamu, Keluarga dan Teman tetap dirumah dan jangan kemana mana please!!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));

					botService.reply(replyToken, messageList);

				} else if (total > 500 && total < 1000) {

					messageList.add(new TextMessage("Korban Terkonfirmasi Kurang dari 1000 Orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Aku Tidak Mau kamu, Keluarga dan Teman Teman mu kena Mari ikuti apa kata Pemerintah untuk tetap dirumah stay safe and healty!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));

					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban Terkonfirmasi yaitu: " + total));
					messageList.add(new TextMessage(
							"Ya Tuhan Sedih Sekali liatnya sudah ribuan orang terkena virus corona bantu saya berdoa bersama ya!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));

					botService.reply(replyToken, messageList);

				}

			} else if (userTxt.contains("Sembuh")) {

				if (total < 10) {

					messageList.add(new TextMessage("Korban Sembuh Kurang dari 10 orang yaitu: " + total));
					messageList.add(new TextMessage("Mari Doakan Teman Teman kita dan para medis yuk! berdoa dimulai"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);
				} else if (total > 20 && total < 30) {

					messageList.add(new TextMessage("Korban Sembuh Lebih dari 20 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Alhamdulillah Sudah Banyak yang sembuh mari terus kita doakan terutama para medis"));
					botService.reply(replyToken, messageList);

				} else if (total > 30 && total < 50) {

					messageList.add(new TextMessage("Korban Sembuh lebih dari 30 Orang yaitu: " + total));
					messageList
							.add(new TextMessage("Alhamdulillah ini semua berkat bantuan kalian Mari Terus doakan!"));
					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban Sembuh yaitu: " + total));
					messageList.add(new TextMessage("Senang Sekali liatnya Yang sembuh. Semoga Sembuh Semua aamiin!"));
					botService.reply(replyToken, messageList);

				}

			}

		} catch (Exception e) {
			messageList.add(new TextMessage("Maaf aku tidak mengerti"));
			messageList.add(new TextMessage(
					"ketik status atau perkembangan dan klik button pada template untuk melihat status korban"));
			botService.reply(replyToken, messageList);
		}
	}

	public void handleLocationRS(String replyToken, String msgText) {

		/* HANDLE MAPPING ALL RS IN INDONESIA */
		CoronaBotLocationModel coronaBotLocationModel;
		coronaBotLocationModel = CoronaBotLocationMapping.handleLocationRSMapping(replyToken, msgText);

		if (coronaBotLocationModel.getTitle().equals("tidak sesuai")) {

			List<Message> messageList = new ArrayList<>();

			String packageID = "11538";
			String stickerID = "51626501";

			messageList.add(new TextMessage("Hallo " + sender.getDisplayName()
					+ "! untuk mencari lokasi rumah sakit ketik 'lokasi<spasi><nama daerah>' contoh 'lokasi bandung' terimakasih."));
			messageList.add(new StickerMessage(packageID, stickerID));

			botService.reply(replyToken, messageList);

		} else if (coronaBotLocationModel.getTitle().equals("salah")) {

			List<Message> messageList = new ArrayList<>();

			String packageID = "11538";
			String stickerID = "51626523";

			messageList.add(new TextMessage(
					"Maaf " + sender.getDisplayName() + "! Maaf Lokasi Rumah sakitnya tidak dapat ditemukan."));
			messageList.add(new StickerMessage(packageID, stickerID));
			messageList.add(new TextMessage(
					"untuk mencari lokasi rumah sakit lain, ketik 'lokasi<spasi><nama daerah>' contoh 'lokasi bandung' terimakasih."));

			botService.reply(replyToken, messageList);
		}

		botService.reply(replyToken,
				new LocationMessage(coronaBotLocationModel.getTitle(), coronaBotLocationModel.getAddress(),
						coronaBotLocationModel.getLatitude(), coronaBotLocationModel.getLongtitude()));
	}

}