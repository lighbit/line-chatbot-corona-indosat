package com.zulkarnaen.bot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
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
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotDatum;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.model.CoronaBotJointEvents;
import com.zulkarnaen.bot.model.CoronaBotLineEventsModel;
import com.zulkarnaen.bot.service.CoronaBotService;
import com.zulkarnaen.bot.service.CoronaBotTemplate;
import com.zulkarnaen.bot.service.CoronaBotDBService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
public class CoronaBotController {

	@Autowired
	@Qualifier("lineSignatureValidator")
	private LineSignatureValidator lineSignatureValidator;

	@Autowired
	private CoronaBotService botService;

	@Autowired
	private CoronaBotTemplate botTemplate;

	@Autowired
	private CoronaBotDBService dbService;

	private UserProfileResponse sender = null;
	private CoronaBotEvents coronaBotEvents = null;

	/* GET JSON */
	@RequestMapping(value = "/corona", method = RequestMethod.GET)
	public CoronaBotEvents getCoronaJson() {

		getDicodingEventsData();

		return coronaBotEvents;

	}

	/* WEBHOOK DEFAULT */
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

	private void greetingMessage(String replyToken, Source source, String additionalMessage) {
		if (sender == null) {
			String senderId = source.getSenderId();
			sender = botService.getProfile(senderId);
		}

		TemplateMessage greetingMessage = botTemplate.greetingMessage(source, sender);

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
		greetingMessage(replyToken, source, null);
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
			greetingMessage(replyToken, source, null);
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
		if (msgText.contains("bot leave")) {
			if (sender == null) {
				botService.replyText(replyToken, "Hi, Mari Pantau Bersama Dengan jadikan Aku Sebagai Teman!");
			} else {
				botService.leaveGroup(groupId);
			}

			/* PROCESSING MESSAGE DARI USER */
		} else if (msgText.contains("id") || msgText.contains("find") || msgText.contains("join")
				|| msgText.contains("teman")) {
			processText(replyToken, textMessage);
		} else if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummary(replyToken, textMessage);
		} else {
			handleFallbackMessage(replyToken, new GroupSource(groupId, sender.getUserId()));
		}
	}

	private void handleRoomChats(String replyToken, String textMessage, String roomId) {
		String msgText = textMessage.toLowerCase();
		if (msgText.contains("bot leave")) {
			if (sender == null) {
				botService.replyText(replyToken, "Hi, tambahkan dulu Aku sebagai teman!");
			} else {
				botService.leaveRoom(roomId);
			}
		} else if (msgText.contains("id") || msgText.contains("find") || msgText.contains("join")
				|| msgText.contains("teman")) {
			processText(replyToken, textMessage);
		} else if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummary(replyToken, textMessage);
		} else {
			handleFallbackMessage(replyToken, new RoomSource(roomId, sender.getUserId()));
		}
	}

	private void handleOneOnOneChats(String replyToken, String textMessage) {
		String msgText = textMessage.toLowerCase();
		if (msgText.contains("id") || msgText.contains("find") || msgText.contains("join")
				|| msgText.contains("teman")) {
			processText(replyToken, textMessage);
		} else if (msgText.contains("perkembangan") || msgText.contains("status") || msgText.contains("kondisi")) {
			showCarouselEvents(replyToken);
		} else if (msgText.contains("summary")) {
			showEventSummary(replyToken, textMessage);
		} else {
			handleFallbackMessage(replyToken, new UserSource(sender.getUserId()));
		}
	}

	private void handleFallbackMessage(String replyToken, Source source) {
		greetingMessage(replyToken, source, "Hi " + sender.getDisplayName()
				+ ", Untuk Lihat Kondisi Corona di indonesia bisa ketik: Kondisi, Status ataupun Perkembangan");
	}

	private void processText(String replyToken, String messageText) {
		String[] words = messageText.trim().split("\\s+");
		String intent = words[0];

		if (intent.equalsIgnoreCase("id")) {
			handleRegisteringUser(replyToken, words);
		} else if (intent.equalsIgnoreCase("join")) {
			handleJoinDicodingEvent(replyToken, words);
		} else if (intent.equalsIgnoreCase("teman")) {
			handleShowFriend(replyToken, words);
		}
	}

	private void handleRegisteringUser(String replyToken, String[] words) {
		String target = words.length > 1 ? words[1] : "";

		if (target.length() <= 3) {
		} else {
			String lineId = target.substring(target.indexOf(":") + 2);
			if (sender != null) {
				if (!sender.getDisplayName().isEmpty() && (lineId.length() > 0)) {
					if (dbService.regLineID(sender.getUserId(), lineId, sender.getDisplayName()) != 0) {
						showCarouselEvents(replyToken, "Terimakasih Banyak ya. berikut status corona di indonesia");
					} else {
						userNotFoundFallback(replyToken,
								"Sepertinya namamu salah bisa di ketik lagi seperti contoh ini:");
					}
				} else {
					userNotFoundFallback(replyToken, "Tambahin Aku sebagai Teman dulu ya, lalu ikuti lagi cara ini:");
				}
			} else {
				userNotFoundFallback(replyToken, "Maaf, Tambahkan Aku sebagai temanmu ya!");
			}
		}
	}

	private void handleJoinDicodingEvent(String replyToken, String[] words) {
		String target = words.length > 2 ? words[2] : "";
		String eventId = target.substring(target.indexOf("#") + 1);
		String senderId = sender.getUserId();
		String senderName = sender.getDisplayName();
		String lineId = dbService.findUser(senderId);

		int joinStatus = dbService.joinEvent(eventId, senderId, lineId, senderName);

		if (joinStatus == -1) {
			TemplateMessage buttonsTemplate = botTemplate.createButton("Kamu sudah bergabung di event ini",
					"Lihat Teman", "teman #" + eventId);
			botService.reply(replyToken, buttonsTemplate);
			return;
		}

		if (joinStatus == 1) {
			TemplateMessage buttonsTemplate = botTemplate.createButton(
					"Pendaftaran event berhasil! Berikut teman yang menemani kamu", "Lihat Teman", "teman #" + eventId);
			botService.reply(replyToken, buttonsTemplate);
			broadcastNewFriendJoined(eventId, senderId);
			return;
		}

		botService.replyText(replyToken, "yah, kamu gagal bergabung event :(");
	}

	private void handleShowFriend(String replyToken, String[] words) {
		String target = StringUtils.join(words, " ");
		String eventId = target.substring(target.indexOf("#") + 1).trim();

		List<CoronaBotJointEvents> jointEvents = dbService.getJoinedEvent(eventId);

		if (jointEvents.size() > 0) {
			List<String> friendList = jointEvents.stream()
					.map((jointEvent) -> String.format("Namamu: %s\nLINE ID: %s\n", jointEvent.display_name,
							"http://line.me/ti/p/~" + jointEvent.line_id))
					.collect(Collectors.toList());

			String replyText = "Daftar teman di event #" + eventId + ":\n\n";
			replyText += StringUtils.join(friendList, "\n\n");

			botService.replyText(replyToken, replyText);
		} else {
			botService.replyText(replyToken, "Event tidak ditemukan");
		}
	}

	private void userNotFoundFallback(String replyToken) {
		userNotFoundFallback(replyToken, null);
	}

	/* Jika User tidak terdeteksi */
	private void userNotFoundFallback(String replyToken, String additionalInfo) {
		List<String> messages = new ArrayList<String>();

		if (additionalInfo != null)
			messages.add(additionalInfo);
		messages.add("Aku ingin Mengenalmu. bisa sebutkan namamu (pake \'nama: ?\' ya)");
		messages.add("Contoh: nama: Zulkarnaen");

		botService.replyText(replyToken, messages.toArray(new String[messages.size()]));
		return;
	}

	private void showCarouselEvents(String replyToken) {
		showCarouselEvents(replyToken, null);
	}

	private void showCarouselEvents(String replyToken, String additionalInfo) {
//		String userFound = dbService.findUser(sender.getUserId());

//		if (userFound == null) {
//			userNotFoundFallback(replyToken);
//		}

		if ((coronaBotEvents == null)) {
			getDicodingEventsData();
		}

		/* TAMPILKAN HASIl */
		TemplateMessage carouselEvents = botTemplate.carouselEvents(coronaBotEvents);

		if (additionalInfo == null) {
			botService.reply(replyToken, carouselEvents);
			return;
		}

		List<Message> messageList = new ArrayList<>();
		messageList.add(new TextMessage(additionalInfo));
		messageList.add(carouselEvents);
		botService.reply(replyToken, messageList);
	}

	private void getDicodingEventsData() {
		// Act as client with GET method
		String URI = "http://corona-api.com/countries/id";
		System.out.println("URI: " + URI);

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
			throw new RuntimeException(e);
		}
	}

	private void broadcastNewFriendJoined(String eventId, String newFriendId) {
		List<String> listIds;
		List<CoronaBotJointEvents> jointEvents = dbService.getJoinedEvent(eventId);

		listIds = jointEvents.stream().filter(jointEvent -> !jointEvent.user_id.equals(newFriendId))
				.map((jointEvent) -> jointEvent.user_id).collect(Collectors.toList());

		Set<String> stringSet = new HashSet<String>(listIds);
		String msg = "Hi, ada teman baru telah bergabung di event " + eventId;
		TemplateMessage buttonsTemplate = botTemplate.createButton(msg, "Lihat Teman", "teman #" + eventId);
		botService.multicast(stringSet, buttonsTemplate);
	}

	private void showEventSummary(String replyToken, String userTxt) {
		try {
			if (coronaBotEvents == null) {
				getDicodingEventsData();
			}

			CoronaBotDatum eventData = coronaBotEvents.getData();

			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_event.json"), encoding);

//			flexTemplate = String.format(flexTemplate, botTemplate.escape(eventData.getImagePath()),
//					botTemplate.escape(eventData.getName()), botTemplate.escape(eventData.getOwnerName()),
//					botTemplate.br2nl(eventData.getDescription()), eventData.getQuota(),
//					botTemplate.escape(eventData.getBeginTime()), botTemplate.escape(eventData.getEndTime()),
//					botTemplate.escape(eventData.getCityName()), botTemplate.br2nl(eventData.getAddress()),
//					botTemplate.escape(eventData.getLink()), eventData.getId());

			flexTemplate = String.format(eventData.getName(), eventData.getCode(), eventData.getPopulation(),
					eventData.getUpdated_at(), eventData.getToday().getDeaths(), eventData.getToday().getConfirmed(),
					eventData.getLatest_data().getDeaths(), eventData.getLatest_data().getConfirmed(),
					eventData.getLatest_data().getRecovered(), eventData.getLatest_data().getCritical());

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);
			botService.reply(replyToken, new FlexMessage("Corona VS Indonesia", flexContainer));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}