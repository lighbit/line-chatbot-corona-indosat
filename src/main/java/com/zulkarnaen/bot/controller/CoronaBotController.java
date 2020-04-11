package com.zulkarnaen.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.model.CoronaBotGoogleArticles;
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
	private CoronaBotGoogleArticles coronaBotGoogleArticles = null;

	/* GET JSON */
	@RequestMapping(value = "/flex", method = RequestMethod.GET)
	public void getflex() {

		handleHowToWashHandFlex("none");

	}

	/* GET JSON */
	@RequestMapping(value = "/corona", method = RequestMethod.GET)
	public CoronaBotEvents getCoronaJson() {

		getEventsData("none");

		return coronaBotEvents;

	}

	/* GET JSON */
	@RequestMapping(value = "/news", method = RequestMethod.GET)
	public CoronaBotGoogleArticles getNewsCorona() {

		getEventDataGoogleNews("none");

		return coronaBotGoogleArticles;

	}

	/* GET JSON */
	@RequestMapping(value = "/newss", method = RequestMethod.GET)
	public CoronaBotGoogleArticles getDataFlex() {

		carouselEventsNewsTemplate("none", "asu");

		return coronaBotGoogleArticles;

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
				} else if (event instanceof PostbackEvent) {
					handlePostbackEvent((PostbackEvent) event);
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

	/* Handle Postback Event (semua message handle disini dulu) */
	private void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		Source source = event.getSource();
		String senderId = source.getSenderId();
		String data = event.getPostbackContent().getData();
		sender = botService.getProfile(senderId);

		String msgText = data.toLowerCase();

		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken, "Vira dapat informasi ini dari www.corona-api.com");
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, data);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanationFlex(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHandFlex(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else if (msgText.contains("news") || msgText.contains("berita") || msgText.contains("terbaru")) {
			carouselEventsNewsTemplate(replyToken, "Vira dapat informasi ini dari www.newsapi.org");
		} else if (msgText.contains("dashboard") || msgText.contains("menu") || msgText.contains("utama")) {
			greetingMessageFlex(replyToken, sender);
		} else if (msgText.contains("gejala") || msgText.contains("98780mulai") || msgText.equals("98780ya1")
				|| msgText.equals("98780tidak1") || msgText.equals("98780ya2") || msgText.equals("98780tidak2")
				|| msgText.equals("98780ya3") || msgText.equals("98780tidak3") || msgText.equals("98780ya4")
				|| msgText.equals("98780tidak4") || msgText.equals("98780hasil")) {
			handleGejalalogic(replyToken, msgText);
		} else if (msgText.equals("psbb")) {
			handlePSBB(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new UserSource(sender.getUserId()));
		}

	}

	/* Handle Message Event (semua message handle disini dulu) */
	private void handleMessageEvent(MessageEvent<?> event) {
		String replyToken = event.getReplyToken();
		MessageContent content = event.getMessage();
		Source source = event.getSource();
		String senderId = source.getSenderId();
		sender = botService.getProfile(senderId);

		if (content instanceof TextMessageContent) {
			handleTextMessage(replyToken, (TextMessageContent) content, source);
		} else if (content instanceof LocationMessageContent) {
			handleLocationMessage(replyToken, (LocationMessageContent) content, source);
		} else if (content instanceof StickerMessageContent) {
			handleStickerMessage(replyToken, (StickerMessageContent) content, source);
		} else if (content instanceof ImageMessageContent) {
			handleImageMessage(replyToken, (ImageMessageContent) content, source);
		} else if (content instanceof AudioMessageContent) {
			handleAudioMessage(replyToken, (AudioMessageContent) content, source);
		} else if (content instanceof VideoMessageContent) {
			handleVideoMessage(replyToken, (VideoMessageContent) content, source);
		} else if (content instanceof FileMessageContent) {
			handleFileMessage(replyToken, (FileMessageContent) content, source);
		} else {
			greetingMessageCoronaDefault(replyToken, source, null);
		}
	}

	/* Handle Audio message (bisa dikembangkan..) */
	private void handleAudioMessage(String replyToken, AudioMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();
		String packageID = "11537";
		String stickerID = "52002752";

		messageList.add(new TextMessage("Terimakasih sudah share Suara indah-nya!"));
		messageList.add(new TextMessage("Suara kamu indah banget!!"));
		messageList.add(new StickerMessage(packageID, stickerID));

		botService.reply(replyToken, messageList);

	}

	/* Handle Image message (bisa dikembangkan..) */
	private void handleImageMessage(String replyToken, ImageMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();

		messageList.add(new TextMessage("Terimakasih sudah share Foto-nya!"));
		messageList.add(new TextMessage("Vira kirim balik foto indahmu!"));
		messageList.add(new ImageMessage(sender.getPictureUrl(), sender.getPictureUrl()));
		messageList.add(new TextMessage("Indah bukan ;) ?"));

		botService.reply(replyToken, messageList);

	}

	/* Handle Video message (bisa dikembangkan..) */
	private void handleVideoMessage(String replyToken, VideoMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();
		String packageID = "11537";
		String stickerID = "52002762";

		messageList.add(new TextMessage("Terimakasih sudah share Video-nya!"));
		messageList.add(new TextMessage("Kamu cocok jadi vlogger!"));
		messageList.add(new StickerMessage(packageID, stickerID));

		botService.reply(replyToken, messageList);

	}

	/* Handle File message (bisa dikembangkan..) */
	private void handleFileMessage(String replyToken, FileMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();
		long sizeInMb = content.getFileSize();
		long cek = sizeInMb / (1024 * 1024);
		String penjelasan;

		if (cek == 0) {

			sizeInMb = sizeInMb / 1024;
			penjelasan = "kb";

		} else {
			sizeInMb = sizeInMb / (1024 * 1024);
			penjelasan = "mb";
		}

		messageList.add(new TextMessage("Terimakasih sudah share File-nya!"));
		messageList.add(new TextMessage("Karena ini privasi, jadi Vira cuma bisa baca judulnya!"));
		messageList.add(new TextMessage("Judulnya file nya adalah '" + content.getFileName() + "'"));
		messageList.add(new TextMessage("ukurannya sebesar '" + sizeInMb + penjelasan + "'"));

		botService.reply(replyToken, messageList);

	}

	/* Handle location message (bisa dikembangkan..) */
	private void handleLocationMessage(String replyToken, LocationMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();

		messageList.add(new TextMessage("Terimakasih sudah share lokasi-nya!"));
		messageList.add(new TextMessage("kalo tidak salah nama jalannya adalah " + content.getAddress()));
		messageList.add(new TextMessage("apa Vira benar ;) ?"));

		botService.reply(replyToken, messageList);

	}

	/* Handle Sticker Event (bisa dikembangkan) */
	private void handleStickerMessage(String replyToken, StickerMessageContent content, Source source) {

		List<Message> messageList = new ArrayList<>();
		String packageID = content.getPackageId();
		String stickerID = content.getStickerId();

		messageList.add(new TextMessage("Terimakasih sudah share sticker-nya!"));
		messageList.add(new TextMessage("kalo tidak salah gambarnya seperti ini ya"));
		messageList.add(new StickerMessage(packageID, stickerID));
		messageList.add(new TextMessage("apa Vira benar ;) ?"));

		botService.reply(replyToken, messageList);

	}

	/* Handle Text message */
	private void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
		if (source instanceof GroupSource) {
			handleGroupChats(replyToken, content.getText(), ((GroupSource) source).getGroupId());
		} else if (source instanceof RoomSource) {
			handleRoomChats(replyToken, content.getText(), ((RoomSource) source).getRoomId());
		} else if (source instanceof UserSource) {
			handleOneOnOneChats(replyToken, content.getText());
		} else {
			botService.replyText(replyToken,
					"Maaf Source tidak di kenal. Pastikan kamu chat one to one atau group atau room untuk memulai");
		}
	}

	/* Handle Group Chats */
	private void handleGroupChats(String replyToken, String textMessage, String groupId) {
		String msgText = textMessage.toLowerCase();

		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken, "Vira dapat informasi ini dari www.corona-api.com");
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanationFlex(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHandFlex(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else if (msgText.contains("news") || msgText.contains("berita") || msgText.contains("terbaru")) {
			carouselEventsNewsTemplate(replyToken, "Vira dapat informasi ini dari www.newsapi.org");
		} else if (msgText.contains("dashboard") || msgText.contains("menu") || msgText.contains("utama")) {
			greetingMessageFlex(replyToken, sender);
		} else if (msgText.contains("gejala") || msgText.contains("98780mulai") || msgText.equals("98780ya1")
				|| msgText.equals("98780tidak1") || msgText.equals("98780ya2") || msgText.equals("98780tidak2")
				|| msgText.equals("98780ya3") || msgText.equals("98780tidak3") || msgText.equals("98780ya4")
				|| msgText.equals("98780tidak4") || msgText.equals("98780hasil")) {
			handleGejalalogic(replyToken, msgText);
		} else if (msgText.equals("psbb")) {
			handlePSBB(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new GroupSource(groupId, sender.getUserId()));
		}
	}

	/* Handle Room Chats */
	private void handleRoomChats(String replyToken, String textMessage, String roomId) {
		String msgText = textMessage.toLowerCase();

		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken, "Vira dapat informasi ini dari www.corona-api.com");
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanationFlex(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHandFlex(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else if (msgText.contains("dashboard") || msgText.contains("menu") || msgText.contains("utama")) {
			greetingMessageFlex(replyToken, sender);
		} else if (msgText.contains("news") || msgText.contains("berita") || msgText.contains("terbaru")) {
			carouselEventsNewsTemplate(replyToken, "Vira dapat informasi ini dari www.newsapi.org");
		} else if (msgText.contains("gejala") || msgText.contains("98780mulai") || msgText.equals("98780ya1")
				|| msgText.equals("98780tidak1") || msgText.equals("98780ya2") || msgText.equals("98780tidak2")
				|| msgText.equals("98780ya3") || msgText.equals("98780tidak3") || msgText.equals("98780ya4")
				|| msgText.equals("98780tidak4") || msgText.equals("98780hasil")) {
			handleGejalalogic(replyToken, msgText);
		} else if (msgText.equals("psbb")) {
			handlePSBB(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new RoomSource(roomId, sender.getUserId()));
		}
	}

	/* handle one to one chat */
	private void handleOneOnOneChats(String replyToken, String textMessage) {
		String msgText = textMessage.toLowerCase();
		if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken, "Vira dapat informasi ini dari www.corona-api.com");
		} else if (msgText.contains("meninggal :") || msgText.contains("terkonfirmasi :")
				|| msgText.contains("sembuh :")) {
			showEventSummaryDeclaration(replyToken, textMessage);
		} else if (msgText.contains("donasi")) {
			handleDonasiTemplate(replyToken);
		} else if (msgText.contains("apa itu corona") || msgText.contains("corona") || msgText.contains("covid")) {
			handlecoronaVirusExplanationFlex(replyToken);
		} else if (msgText.contains("pencegah") || msgText.contains("basmi") || msgText.contains("bunuh")
				|| msgText.contains("tangan") || msgText.contains("masker") || msgText.contains("isolasi")) {
			handleHowToWashHandFlex(replyToken);
		} else if (msgText.contains("pencipta") || msgText.contains("pembuatmu") || msgText.contains("creator")) {
			handleCreator(replyToken);
		} else if (msgText.contains("dicoding")) {
			handleDicodingThanks(replyToken);
		} else if (msgText.contains("lokasi")) {
			handleLocationRS(replyToken, msgText);
		} else if (msgText.contains("call") || msgText.contains("nomer") || msgText.contains("nomor")) {
			handleCallCenter(replyToken);
		} else if (msgText.contains("dashboard") || msgText.contains("menu") || msgText.contains("utama")) {
			greetingMessageFlex(replyToken, sender);
		} else if (msgText.contains("news") || msgText.contains("berita") || msgText.contains("terbaru")) {
			carouselEventsNewsTemplate(replyToken, "Vira dapat informasi ini dari www.newsapi.org");
		} else if (msgText.contains("gejala") || msgText.contains("98780mulai") || msgText.equals("98780ya1")
				|| msgText.equals("98780tidak1") || msgText.equals("98780ya2") || msgText.equals("98780tidak2")
				|| msgText.equals("98780ya3") || msgText.equals("98780tidak3") || msgText.equals("98780ya4")
				|| msgText.equals("98780tidak4") || msgText.equals("98780hasil")) {
			handleGejalalogic(replyToken, msgText);
		} else if (msgText.equals("psbb")) {
			handlePSBB(replyToken);
		} else {
			HandleSalam(msgText, replyToken, new UserSource(sender.getUserId()));
		}
	}

	/* Handle Greeting flex_template */
	private void greetingMessageFlex(String replyToken, UserProfileResponse sender) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_corona_Greeting.json"),
					encoding);

			flexTemplate = String.format(flexTemplate, coronaBotTemplate.escape(sender.getDisplayName()));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken,
					new FlexMessage("Apa itu Covid-19", flexContainer));
			botService.reply(replyMessage);
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* Handle logic gejala flex */
	private void handleGejalalogic(String replyToken, String msgText) {

		msgText = msgText.toLowerCase();
		String urlImage, title, description, ya = null, tidak;

		if (msgText.contains("98780mulai")) {

			urlImage = "https://bit.ly/2y9TAZN";
			title = "Test 1";
			description = "\nApakah kamu saat ini mengalami Demam diatas 38 derajat?";
			ya = "98780ya1";
			tidak = "98780tidak1";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780ya1")) {

			urlImage = "https://bit.ly/2wtGqGG";
			title = "Test 2";
			description = "\nApakah kamu saat ini mengalami batuk, pilek, sakit tenggorokan?";
			ya = "98780ya2";
			tidak = "98780tidak2";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780tidak1")) {

			urlImage = "https://bit.ly/2wtGqGG";
			title = "Test 2";
			description = "\nApakah kamu saat ini mengalami batuk, pilek, sakit tenggorokan?";
			ya = "98780ya2";
			tidak = "98780tidak2";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780ya2")) {

			urlImage = "https://bit.ly/2xniULT";
			title = "Test 3";
			description = "\nApakah kamu pernah berpergian ke luar negeri dalam kurun waktu 2 minggu terakhir?";
			ya = "98780ya3";
			tidak = "98780tidak3";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780tidak2")) {

			urlImage = "https://bit.ly/2xniULT";
			title = "Test 3";
			description = "\nApakah kamu pernah berpergian ke luar negeri dalam kurun waktu 2 minggu terakhir?";
			ya = "98780ya3";
			tidak = "98780tidak3";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780ya3")) {

			urlImage = "https://bit.ly/2VmCiAy";
			title = "Test 4";
			description = "\nApakah kamu pernah berpergian ke luar provinsi dalam kurun waktu 2 minggu terakhir?";
			ya = "98780ya4";
			tidak = "98780tidak4";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780tidak3")) {

			urlImage = "https://bit.ly/2VmCiAy";
			title = "Test 4";
			description = "\nApakah kamu pernah berpergian ke luar provinsi dalam kurun waktu 2 minggu terakhir?";
			ya = "98780ya4";
			tidak = "98780tidak4";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780ya4")) {

			urlImage = "https://bit.ly/2ViuFLz";
			title = "Test 5";
			description = "\nApakah kamu pernah berinteraksi/bertemu dengan orang yang terkena COVID-19 (Virus Corona)?";
			ya = "98780hasil";
			tidak = "98780hasil";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780tidak4")) {

			urlImage = "https://bit.ly/2ViuFLz";
			title = "Test 5";
			description = "\nApakah kamu pernah berinteraksi/bertemu dengan orang yang terkena COVID-19 (Virus Corona)?";
			ya = "98780hasil";
			tidak = "98780hasil";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else if (msgText.contains("98780hasil")) {

			urlImage = "https://bit.ly/3bb3QQt";
			title = "Hasilmu!";
			description = "\nJika kamu jawab Ya 1 - 2, Kamu tidak perlu ke dokter. Isolasikan dirimu sendiri minimal 14 hari untuk jaga-jaga agar orang yang kamu kasihi tetap sehat bersama mu.\n\nJika kamu pilih ya 2 - 5, disarankan kamu cek ke dokter dengan cara cari rumah sakit dekat sini, atau ketik : lokasi untuk cari lokasi rumah sakit terdekat tempat kamu berasal.\n\nMau cek Lokasi Rumah sakit?";
			ya = "lokasi";
			tidak = "dashboard";

			handleGejalaFlex(replyToken, sender, urlImage, title, description, ya, tidak);

		} else {

			urlImage = "https://bit.ly/2JXz6Gw";
			title = "Sebelum Test";
			description = "\nMari cek tingkat kesehatanmu untuk melawan COVID-19 (Virus Corona)";
			ya = "98780mulai";

			handleGejalaFlexMulai(replyToken, sender, urlImage, title, description, ya);

		}

	}

	/* Handle Gejala flex_template */
	private void handleGejalaFlex(String replyToken, UserProfileResponse sender, String urlImage, String title,
			String description, String ya, String tidak) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_corona_base_gejala.json"),
					encoding);

			flexTemplate = String.format(flexTemplate, coronaBotTemplate.escape(urlImage),
					coronaBotTemplate.escape(title), coronaBotTemplate.escape(description),
					coronaBotTemplate.escape(ya), coronaBotTemplate.escape(tidak));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("TEST COVID-19", flexContainer));
			botService.reply(replyMessage);
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* Handle Gejala flex_template */
	private void handleGejalaFlexMulai(String replyToken, UserProfileResponse sender, String urlImage, String title,
			String description, String ya) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils
					.toString(classLoader.getResourceAsStream("flex_corona_base_gejala_mulai.json"), encoding);

			flexTemplate = String.format(flexTemplate, coronaBotTemplate.escape(urlImage),
					coronaBotTemplate.escape(title), coronaBotTemplate.escape(description),
					coronaBotTemplate.escape(ya));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("TEST COVID-19", flexContainer));
			botService.reply(replyMessage);
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* handle salam */
	private void HandleSalam(String msgText, String replyToken, Source source) {

		if (msgText.contains("assala") || msgText.contains("asala") || msgText.contains("mikum")) {
			greetingMessageCoronaDefault(replyToken, source, "Waalaikumsalam " + sender.getDisplayName()
					+ ", untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.equals("hai") || msgText.equals("hallo") || msgText.equals("hei")
				|| msgText.equals("halo")) {
			greetingMessageCoronaDefault(replyToken, source, msgText + " " + sender.getDisplayName()
					+ "!, untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("pagi")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Pagi " + sender.getDisplayName()
					+ "!, untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("siang")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Siang " + sender.getDisplayName()
					+ "!, untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else if (msgText.contains("malam")) {
			greetingMessageCoronaDefault(replyToken, source, "Selamat Malam " + sender.getDisplayName()
					+ "!, untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		} else {
			greetingMessageCoronaDefault(replyToken, source, "Hai " + sender.getDisplayName()
					+ "!, untuk melihat Kondisi COVID-19 di Indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
		}

	}

	/* handle Dicoding Thanks (Special) */
	private void handleDicodingThanks(String replyToken) {

		String kenalan = "Hasil Karya Zulkarnaen!";
		String salam = "Thanks dicoding Sudah Bantu Saya sejauh ini salam hangat Zulkarnaen :) ";

		List<Message> messages = new ArrayList<>();
		messages.add(new TextMessage(kenalan));
		messages.add(new TextMessage(salam));

		botService.reply(replyToken, messages);

	}

	/* Handle PSBB nya */
	private void handlePSBB(String replyToken) {
		String urlCorona = "https://bit.ly/3a0LqAv";
		String thumbnailImageUrl = "https://bit.ly/2y9fH2Q";
		String title = "Apa itu PSBB?";
		String text = "PSBB (Pembatasan Sosial Berskala Besar) Baca Selengkapnya..";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona)));

		TemplateMessage templateMessage = new TemplateMessage("PSBB", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	/* Handle Creator nya */
	private void handleCreator(String replyToken) {
		String urlCorona = "https://bit.ly/39N9V3R";
		String thumbnailImageUrl = "https://bit.ly/2UN5uld";
		String title = "Hai Nama Saya Zulkarnaen";
		String text = "Lihat Aku di Linkedin!";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona)));

		TemplateMessage templateMessage = new TemplateMessage("Creator", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	/* Handle cara cuci tangan flex_template */
	private void handlecoronaVirusExplanationFlex(String replyToken) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_corona_explanation.json"));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken,
					new FlexMessage("Apa itu Covid-19", flexContainer));
			botService.reply(replyMessage);
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* Handle Virus Explanation (digantikan Flex) */
	@SuppressWarnings("unused")
	private void handlecoronaVirusExplanation(String replyToken) {
		String urlCorona = "https://bit.ly/3dX6riJ";
		String thumbnailImageUrl = "https://bit.ly/39HVXjO";
		String title = "Apa itu corona virus? (COVID-19)";
		String text = "Penjelasan Seputar corona virus (COVID-19) ada disini.";

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(thumbnailImageUrl, title, text,
				Arrays.asList(new URIAction("Baca Selengkapnya..", urlCorona)));

		TemplateMessage templateMessage = new TemplateMessage("Apa Itu Corona", buttonsTemplate);
		botService.reply(replyToken, templateMessage);

	}

	/* Handle cara cuci tangan flex_template */
	private void handleHowToWashHandFlex(String replyToken) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String flexTemplate = IOUtils
					.toString(classLoader.getResourceAsStream("flex_corona_explanationCegah.json"));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken,
					new FlexMessage("Pencegahan Corona", flexContainer));
			botService.reply(replyMessage);
		} catch (IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam menyiapkan data :("));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* Show Carousel Events */
	private void carouselEventsNewsTemplate(String replyToken, String additionalInfo) {

		if ((coronaBotGoogleArticles == null)) {
			getEventDataGoogleNews(replyToken);
		}

		/* TAMPILKAN HASIl */
		TemplateMessage carouselEvents = coronaBotTemplate.carouselEventsNews(coronaBotGoogleArticles);

		if (additionalInfo == null) {
			botService.reply(replyToken, carouselEvents);
			return;
		}

		List<Message> messageList = new ArrayList<>();
		messageList.add(new TextMessage(additionalInfo));
		messageList.add(carouselEvents);
		botService.reply(replyToken, messageList);
	}

	/* Handle cara cuci tangan (digantikan flex) */
	@SuppressWarnings("unused")
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

	/* Handle donasi */
	private void handleDonasiTemplate(String replyToken) {
		String urlCoronaKitabisa = "https://kitabisa.com/campaign/Indonesialawancorona";
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

	/* Handle Call Center */
	private void handleCallCenter(String replyToken) {

		List<Message> messageList = new ArrayList<>();

		String packageID = "11538";
		String stickerID = "51626496";

		messageList.add(new TextMessage("119"));
		messageList.add(new TextMessage("Hallo " + sender.getDisplayName()
				+ "! 119 adalah nomor hotline seputar COVID-19. Jika kamu merasakan gejala tersebut, langsung telpon dan jangan takut"));
		messageList.add(new TextMessage("Vira yakin kamu tidak akan kenapa-kenapa dan itu pasti!"));
		messageList.add(new StickerMessage(packageID, stickerID));

		botService.reply(replyToken, messageList);

	}

	/* Handle Location Rumah Sakit */
	public void handleLocationRS(String replyToken, String msgText) {

		/* HANDLE MAPPING ALL RS IN Indonesia */
		CoronaBotLocationModel coronaBotLocationModel;
		coronaBotLocationModel = CoronaBotLocationMapping.handleLocationRSMapping(replyToken, msgText);

		if (coronaBotLocationModel.getTitle().equals("tidak sesuai")) {

			List<Message> messageList = new ArrayList<>();

			String packageID = "11538";
			String stickerID = "51626501";

			messageList.add(new TextMessage("Hallo " + sender.getDisplayName()
					+ "! untuk mencari lokasi rumah sakit ketik 'lokasi<spasi><nama kota>' contoh 'lokasi bandung' terimakasih."));
			messageList.add(new StickerMessage(packageID, stickerID));

			botService.reply(replyToken, messageList);

		} else if (coronaBotLocationModel.getTitle().equals("salah")) {

			List<Message> messageList = new ArrayList<>();

			String packageID = "11538";
			String stickerID = "51626523";

			messageList.add(new TextMessage(
					"Hallo " + sender.getDisplayName() + "!, maaf Vira tidak bisa menemukan rumah sakitnya."));
			messageList.add(new StickerMessage(packageID, stickerID));
			messageList.add(new TextMessage(
					"Untuk mencari lokasi rumah sakit lain, ketik 'lokasi<spasi><nama kota>' contoh 'lokasi bandung' terimakasih."));

			botService.reply(replyToken, messageList);
		}

		botService.reply(replyToken,
				new LocationMessage(coronaBotLocationModel.getTitle(), coronaBotLocationModel.getAddress(),
						coronaBotLocationModel.getLatitude(), coronaBotLocationModel.getLongtitude()));
	}

	/* Show Carousel Events */
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

	/* Handle Get corona API */
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

	/* Handle Get NEWS API GOOGLE */
	private void getEventDataGoogleNews(String replyToken) {
		// Act as client with GET method
		String URI = "http://newsapi.org/v2/everything?q=corona&language=id&apiKey=c1eda55382224fe881f78d4b24b4877e";
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
			coronaBotGoogleArticles = objectMapper.readValue(jsonResponse, CoronaBotGoogleArticles.class);
		} catch (InterruptedException | ExecutionException | IOException e) {
			messageList.add(new TextMessage("Ada Kesalahan dalam mengambil data ke sumber terkait"));
			messageList.add(new TextMessage(
					"Mohon untuk kontak developer di email -> sekaizulka.sz@gmail.com terimakasih banyak sudah membantu!"));
			botService.reply(replyToken, messageList);
		}
	}

	/* Handle Declaration Api Corona Indonesia */
	private void showEventSummaryDeclaration(String replyToken, String userTxt) {

		List<Message> messageList = new ArrayList<>();
		try {

			int total = Integer.parseInt(userTxt.substring(userTxt.lastIndexOf(":") + 2));

			if (userTxt.contains("Meninggal")) {

				String packageID = "11537";
				String stickerID = "52002740";

				if (total < 10) {

					messageList.add(new TextMessage("Korban Meninggal Kurang dari 10 orang yaitu: " + total));
					messageList.add(new TextMessage("Stay Safe dan Stay Health ya dengan cara dirumah aja :)"));
					botService.reply(replyToken, messageList);
				} else if (total > 300 && total < 400) {

					messageList.add(new TextMessage("Korban Meninggal lebih dari 300 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Mari berdoa supaya korban meninggal dalam keadaan syahid, amal ibadah korban diterima di sisi tuhan yang maha esa dan keluarga diberi ketabahan aamiin!"));
					messageList.add(new TextMessage(
							"Kamu harus ikuti anjuran pemerintah #DirumahSaja, pakai masker, tetap tenang dan jangan panik ya!"));
					messageList.add(new StickerMessage(packageID, stickerID));
					messageList.add(new TextMessage(
							"Oh ya, bantu korban dan para medis yuk dengan cara berdonasi Bersama. Ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);

				} else if (total > 100 && total < 200) {

					messageList.add(new TextMessage("Korban Meninggal lebih dari 100 Orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Kamu harus ikuti anjuran pemerintah, pakai masker, tetap tenang dan jangan panik ya. Kamu pasti bisa!"));
					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban Meninggal yaitu: " + total));
					messageList.add(new TextMessage(
							"Mungkin kamu ada keinginan ingin keluar rumah karena ada suatu hal, tapi untuk sekarang jangan keluar rumah dulu ya Vira sayang kamu! \nMari berdoa supaya korban meninggal dalam keadaan syahid, amal ibadah korban diterima di sisi tuhan yang maha esa dan keluarga diberi ketabahan aamiin!"));
					messageList.add(new TextMessage(
							"Kamu harus ikuti anjuran pemerintah #DirumahSaja, pakai masker, tetap tenang dan jangan panik ya!"));
					messageList.add(new StickerMessage(packageID, stickerID));
					messageList.add(new TextMessage(
							"Oh ya, bantu korban dan para medis yuk dengan cara berdonasi Bersama. Ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);

				}

			} else if (userTxt.contains("Terkonfirmasi")) {

				String packageID = "11537";
				String stickerID = "52002735";
				String stickerIDsedih = "52002755";

				if (total < 10) {

					messageList.add(new TextMessage("Korban terkonfirmasi kurang dari 10 orang yaitu: " + total));
					messageList.add(new TextMessage("Mari kita #DirumahSaja agar semakin hilang virus COVID-19 nya!"));
					botService.reply(replyToken, messageList);
				} else if (total > 1000 && total < 1500) {

					messageList.add(new TextMessage("Korban terkonfirmasi lebih dari 1000 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Sudah Banyak Yang Terkonfirmasi Diharapkan Untuk Kamu, Keluarga dan Teman tetap dirumah dan jangan kemana mana ya Vira gamau kamu kenapa kenapa!!"));
					messageList.add(new StickerMessage(packageID, stickerIDsedih));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));

					botService.reply(replyToken, messageList);

				} else if (total > 500 && total < 1000) {

					messageList.add(new TextMessage("Korban terkonfirmasi lebih dari 500 orang yaitu: " + total));
					messageList.add(new TextMessage(
							"Vira Tidak Mau kamu, Keluarga dan Teman Teman mu kena Mari ikuti apa kata Pemerintah untuk tetap dirumah stay safe and healty!"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));

					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban Terkonfirmasi yaitu: " + total));
					messageList.add(new TextMessage(
							"Vira tidak ingin kamu, keluarga dan teman-teman kamu juga mengalami. Kamu tau ga sih Vira tuh sayang kamu! jangan buat Vira sedih. Untuk Sekarang mari ikuti apa kata Pemerintah untuk tetap #DirumahSaja stay safe and healty!"));
					messageList.add(new StickerMessage(packageID, stickerIDsedih));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));

					botService.reply(replyToken, messageList);

				}

			} else if (userTxt.contains("Sembuh")) {

				String packageID = "11537";
				String stickerID = "52002735";

				if (total < 100) {

					messageList.add(new TextMessage("Korban yang sembuh sudah mencapai : " + total));
					messageList.add(new TextMessage("Mari doakan teman-teman kita dan para medis yuk! berdoa dimulai"));
					messageList.add(new TextMessage(
							"Bantu Korban dan para medis yuk dengan berdonasi Bersama! ketik donasi atau klik button Mari Berdonasi untuk berdonasi!"));
					botService.reply(replyToken, messageList);
				} else if (total > 100 && total < 200) {

					messageList.add(new TextMessage("Korban yang sembuh sudah mencapai : " + total));
					messageList.add(new TextMessage(
							"Alhamdulillah, Puji Tuhan, Sudah banyak yang sembuh mari terus kita doakan terutama para medis"));
					botService.reply(replyToken, messageList);

				} else if (total > 200 && total < 300) {

					messageList.add(new TextMessage("Korban yang sembuh sudah mencapai : " + total));
					messageList.add(new TextMessage(
							"Alhamdulillah, Puji Tuhan, Sudah banyak yang sembuh mari terus kita doakan terutama para medis pahlawan super hero kita!"));
					messageList.add(new TextMessage(
							"Semakin banyak kamu berdonasi semakin banyak nyawa yang tertolong mari berdonasi dengan cara ketik donasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));
					botService.reply(replyToken, messageList);

				} else {

					messageList.add(new TextMessage("Korban yang sembuh sudah mencapai : " + total));
					messageList.add(new TextMessage(
							"Alhamdulillah, Puji Tuhan, Sudah banyak yang sembuh mari terus kita doakan terutama para medis pahlawan super hero kita!"));
					messageList.add(new TextMessage(
							"Semakin banyak kamu berdonasi semakin banyak nyawa yang tertolong mari berdonasi dengan cara ketik donasi!"));
					messageList.add(new StickerMessage(packageID, stickerID));
					botService.reply(replyToken, messageList);

				}

			}

		} catch (Exception e) {
			messageList.add(new TextMessage("Maaf Vira tidak mengerti"));
			messageList.add(new TextMessage(
					"ketik status atau perkembangan dan klik button pada template untuk melihat status korban"));
			botService.reply(replyToken, messageList);
		}
	}

}