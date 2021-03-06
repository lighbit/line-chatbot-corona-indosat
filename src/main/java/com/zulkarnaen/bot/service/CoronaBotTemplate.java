package com.zulkarnaen.bot.service;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.model.CoronaBotGoogleArticles;
import com.zulkarnaen.bot.util.ServiceUtil;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaBotTemplate {

	@Autowired
	private CoronaBotService botService;

	/* CREATE BUTTON MORE THAN 1 */
	public TemplateMessage createButton(String message, String actionTitle, String actionText, String actionDashboard,
			String image, String corona, String cegah) {
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message,
				Arrays.asList(new PostbackAction(corona, corona), new PostbackAction(cegah, cegah),
						new PostbackAction(actionTitle, actionText), new PostbackAction(actionDashboard, "Dashboard")));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* CREATE BUTTON SINGLE */
	public static TemplateMessage createButtonSingle(String message, String actionTitle, String actionText,
			String image) {

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message,
				Collections.singletonList(new MessageAction(actionTitle, actionText)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* GREETING MESSAGE */
	public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
		String message = "Hai %s! Mari Pantau Corona di Indonesia.";
		String corona = "Apa itu Covid-19?";
		String cegah = "Cara Pencegahan";
		String action = "Lihat Perkembangan";
		String actionDashboard = "Cek Fitur";
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

		return createButton(message, action, action, actionDashboard, image, corona, cegah);
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
						Arrays.asList(new PostbackAction("Meninggal", "Korban Meninggal : " + death),
								new PostbackAction("Terkonfirmasi", "Korban Terkonfirmasi : " + confirm),
								new PostbackAction("Sembuh", "Berhasil Sembuh : " + recover)));

				carouselColumn.add(column);

			}

		}

		CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumn);
		return new TemplateMessage("Your search result", carouselTemplate);
	}

	/* Handle Greeting flex_template */
	public TemplateMessage carouselEventsNews(CoronaBotGoogleArticles coronaBotGoogleArticles) {
		int i;
		String title, description, urlToImage, url;
		CarouselColumn column;
		List<CarouselColumn> carouselColumn = new ArrayList<>();
		for (i = 0; i < coronaBotGoogleArticles.getArticles().size(); i++) {

			if (i == 9) {

				break;

			} else {

				title = coronaBotGoogleArticles.getArticles().get(i).getTitle();
				description = coronaBotGoogleArticles.getArticles().get(i).getDescription();
				urlToImage = coronaBotGoogleArticles.getArticles().get(i).getUrlToImage();
				url = coronaBotGoogleArticles.getArticles().get(i).getUrl();

				column = new CarouselColumn(urlToImage,
						title.substring(0, (title.length() < 40) ? title.length() : 37) + "...",
						description.substring(0, (description.length() < 60) ? description.length() : 57) + "...",
						Arrays.asList(new URIAction("Baca Selengkapnya..", url)));

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