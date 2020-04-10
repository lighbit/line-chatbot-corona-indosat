package com.zulkarnaen.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.util.ServiceUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaBotTemplate {

	@Autowired
	private CoronaBotService botService;

	/* CREATE BUTTON MORE THAN 1 */
	public TemplateMessage createButton(String message, String actionTitle, String actionText, String actionDonasi,
			String image, String corona, String cegah) {
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message,
				Arrays.asList(new MessageAction(corona, corona), new MessageAction(cegah, cegah),
						new MessageAction(actionTitle, actionText), new MessageAction(actionDonasi, actionDonasi)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* CREATE BUTTON SINGLE */
	public static TemplateMessage createButtonSingle(String message, String actionTitle, String actionText,
			String image) {

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message,
				Collections.singletonList(new MessageAction(actionTitle, actionText)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* Handle cara cuci tangan flex_template */
	private void greetingMessageFlex(String replyToken, UserProfileResponse sender) {
		List<Message> messageList = new ArrayList<>();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String encoding = StandardCharsets.UTF_8.name();
			String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_corona_Greeting.json"),
					encoding);

			flexTemplate = String.format(flexTemplate, escape(sender.getDisplayName()));

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

	/* GREETING MESSAGE */
	public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
		String message = "Hai %s! Mari Pantau Corona di Indonesia.";
		String corona = "Apa itu Covid-19?";
		String cegah = "Cara Pencegahan";
		String action = "Lihat Perkembangan";
		String actionDonasi = "Donasi";
		String image = "https://bit.ly/34eUJv7";

		if (source instanceof GroupSource) {
			message = String.format(message, "Group");
		} else if (source instanceof RoomSource) {
			message = String.format(message, "Room");
		} else if (source instanceof UserSource) {
			message = String.format(message, sender.getDisplayName());
		} else {
			message = "UNKNOW FORMAT ROOM";
		}

		return createButton(message, action, action, actionDonasi, image, corona, cegah);
	}

	/* CAROUSEL TEMPLATE */
	public TemplateMessage carouselEvents(CoronaBotEvents coronaBotEvents) {
		int i, recover;
		String country, death, confirm, image, date;
		CarouselColumn column;
		List<CarouselColumn> carouselColumn = new ArrayList<>();
		for (i = 0; i < coronaBotEvents.getData().getTimeline().size(); i++) {

			if (i == 9) {

				break;

			} else {

				death = Integer.toString(coronaBotEvents.getData().getTimeline().get(i).getDeaths());
				confirm = Integer.toString(coronaBotEvents.getData().getTimeline().get(i).getConfirmed());
				country = coronaBotEvents.getData().getName();
				date = coronaBotEvents.getData().getTimeline().get(i).getDate();
				recover = coronaBotEvents.getData().getTimeline().get(i).getRecovered();
				image = "https://bit.ly/2RhmL3Q";

				column = new CarouselColumn(image, "Update tanggal " + ServiceUtil.dateConverter(date),
						"NKRI " + country.substring(0, (country.length() < 40) ? country.length() : 40)
								+ " \nJumlah Penduduk : " + coronaBotEvents.getData().getPopulation() + " Jiwa",
						Arrays.asList(new MessageAction("Meninggal", "Korban Meninggal : " + death),
								new MessageAction("Terkonfirmasi", "Korban Terkonfirmasi : " + confirm),
								new MessageAction("Sembuh", "Berhasil Sembuh : " + recover)));

				carouselColumn.add(column);

			}

		}

		CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumn);
		return new TemplateMessage("Your search result", carouselTemplate);
	}

	/* STICKER HANDLE PUBLIC */
	public void handleStickerMessageEvent(String replyToken) {
		handleSticker(replyToken);
	}

	/* STICKER BASE PRIVATE */
	private void handleSticker(String replyToken) {

		String packageID = "11537";
		String stickerID = "52002755";

		botService.reply(replyToken, new StickerMessage(packageID, stickerID));
	}

	public String escape(String text) {
		return StringEscapeUtils.escapeJson(text.trim());
	}

	public String escapeInt(int text) {
		return StringEscapeUtils.escapeJson(Integer.toString(text));
	}

	public String br2nl(String html) {
		Document document = Jsoup.parse(html);
		document.select("br").append("\\n");
		document.select("p").prepend("\\n");
		String text = document.text().replace("\\n\\n", "\\n").replace("\\n", "\n");

		return StringEscapeUtils.escapeJson(text.trim());
	}
}