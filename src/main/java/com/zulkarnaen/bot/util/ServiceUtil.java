package com.zulkarnaen.bot.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;

@Component
public class ServiceUtil {

	private static LineMessagingClient lineMessagingClient;

	@Autowired
	@Qualifier("lineMessagingClient")
	public void lineMessagingClients(LineMessagingClient lineMessagingClients) {
		lineMessagingClient = lineMessagingClients;
	}

	public static UserProfileResponse getProfile(String userId) {
		try {
			return lineMessagingClient.getProfile(userId).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void sendMulticast(Set<String> sourceUsers, String txtMessage) {
		TextMessage message = new TextMessage(txtMessage);
		Multicast multicast = new Multicast(sourceUsers, message);

		try {
			lineMessagingClient.multicast(multicast).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void push(PushMessage pushMessage) {
		try {
			lineMessagingClient.pushMessage(pushMessage).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void reply(ReplyMessage replyMessage) {
		try {
			lineMessagingClient.replyMessage(replyMessage).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void replyText(String replyToken, String messageToUser) {
		TextMessage textMessage = new TextMessage(messageToUser);
		ReplyMessage replyMessage = new ReplyMessage(replyToken, textMessage);
		reply(replyMessage);
	}

	public static void replySticker(String replyToken, String packageId, String stickerId) {
		StickerMessage stickerMessage = new StickerMessage(packageId, stickerId);
		ReplyMessage replyMessage = new ReplyMessage(replyToken, stickerMessage);
		reply(replyMessage);
	}

	public static MessageContentResponse getContent(String messageId) {
		try {
			return lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void handleOneOnOneChats(MessageEvent<?> event) {
		if (event.getMessage() instanceof AudioMessageContent || event.getMessage() instanceof ImageMessageContent
				|| event.getMessage() instanceof VideoMessageContent
				|| event.getMessage() instanceof FileMessageContent) {
			handleContentMessage(event);
		} else if (event.getMessage() instanceof TextMessageContent) {
			handleTextMessage(event);
		} else {
			replyText(event.getReplyToken(), "Unknown Message");
		}
	}

	public static void handleContentMessage(MessageEvent<?> event) {
		String baseURL = "https://botzulka.herokuapp.com";
		String contentURL = baseURL + "/content/" + event.getMessage().getId();
		String contentType = event.getMessage().getClass().getSimpleName();
		String textMsg = contentType.substring(0, contentType.length() - 14)
				+ " yang kamu kirim bisa diakses dari link:\n " + contentURL;

		replyText(event.getReplyToken(), textMsg);
	}

	public static void handleTextMessage(MessageEvent<?> event) {
		TextMessageContent textMessageContent = (TextMessageContent) event.getMessage();

		if (textMessageContent.getText().toLowerCase().contains("flex")) {
			replyFlexMessage(event.getReplyToken());
		} else {
			replyText(event.getReplyToken(), textMessageContent.getText());
		}
	}

	public static void handleGroupRoomChats(MessageEvent<?> event) {
		if (!event.getSource().getUserId().isEmpty()) {
			String userId = event.getSource().getUserId();
			UserProfileResponse profile = getProfile(userId);
			replyText(event.getReplyToken(), "Hello, " + profile.getDisplayName());
		} else {
			replyText(event.getReplyToken(), "Hello, what is your name?");
		}
	}

	public static void replyFlexMessage(String replyToken) {
		try {
			Class<GetClassLoader> classLoader = GetClassLoader.class;
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_message.json"));

			ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
			FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

			ReplyMessage replyMessage = new ReplyMessage(replyToken,
					new FlexMessage("Dicoding Academy", flexContainer));
			reply(replyMessage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
	
	public static void main (String [] args) {
		String inputDate = "2020-03-15";
		System.out.println(dateConverter(inputDate));
	}

}
