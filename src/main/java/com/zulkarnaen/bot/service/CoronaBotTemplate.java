package com.zulkarnaen.bot.service;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.zulkarnaen.bot.model.CoronaBotEvents;
import com.zulkarnaen.bot.util.ServiceUtil;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CoronaBotTemplate {

	/* CREATE BUTTON MORE THAN 1 */
	public TemplateMessage createButton(String message, String actionTitle, String actionText, String actionDonasi,
			String image) {
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message, Arrays
				.asList(new MessageAction(actionTitle, actionText), new MessageAction(actionDonasi, actionDonasi)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* CREATE BUTTON SINGLE */
	public static TemplateMessage createButtonSingle(String message, String actionTitle, String actionText,
			String image) {

		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(image, null, message,
				Collections.singletonList(new MessageAction(actionTitle, actionText)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	/* BASE GREETING MESSAGE */
	public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
		String message = "Hai %s! Mari Pantau Corona di Indonesia.";
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

		return createButton(message, action, action, actionDonasi, image);
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
						"Negara " + country.substring(0, (country.length() < 40) ? country.length() : 40)
								+ " Jumlah Penduduk : " + coronaBotEvents.getData().getPopulation(),
						Arrays.asList(new MessageAction("Meninggal", "Korban Meninggal : " + death),
								new MessageAction("Terkonfirmasi", "Korban Terkonfirmasi : " + confirm),
								new MessageAction("Sembuh", "Berhasil Sembuh : " + recover)));

				carouselColumn.add(column);

			}

		}

		CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumn);
		return new TemplateMessage("Your search result", carouselTemplate);
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