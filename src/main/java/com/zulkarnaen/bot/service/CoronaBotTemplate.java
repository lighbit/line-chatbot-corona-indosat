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

	public TemplateMessage createButton(String message, String actionTitle, String actionText) {
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(null, null, message,
				Collections.singletonList(new MessageAction(actionTitle, actionText)));

		return new TemplateMessage(actionTitle, buttonsTemplate);
	}

	public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
		String message = "Hai %s! Mari Pantau Perkembangan Corona di Indonesia.";
		String action = "Lihat Perkembangan";

		if (source instanceof GroupSource) {
			message = String.format(message, "Group");
		} else if (source instanceof RoomSource) {
			message = String.format(message, "Room");
		} else if (source instanceof UserSource) {
			message = String.format(message, sender.getDisplayName());
		} else {
			message = "Eh maaf" + sender.getDisplayName() + " ga denger kamu ngomong apa?\r\n"
					+ "aku sedang tidak fokus karena pandemic ini(crying)\r\n"
					+ "kamu mau liat status terkini virus corona di indonesia?";
		}

		return createButton(message, action, action);
	}

	public TemplateMessage carouselEvents(CoronaBotEvents dicodingEvents) {
		int i, b = 0, recover;
		String country, death, confirm, image, date;
		CarouselColumn column;
		List<CarouselColumn> carouselColumn = new ArrayList<>();
		if (dicodingEvents.getData().getTimeline().size() > 30) {

			b = 22;
		} else if (dicodingEvents.getData().getTimeline().size() > 20) {
			b = 12;
		}
		for (i = b; i < dicodingEvents.getData().getTimeline().size(); i++) {
			death = Integer.toString(dicodingEvents.getData().getTimeline().get(i).getDeaths());
			confirm = Integer.toString(dicodingEvents.getData().getTimeline().get(i).getConfirmed());
			country = dicodingEvents.getData().getName();
			date = dicodingEvents.getData().getTimeline().get(i).getDate();
			recover = dicodingEvents.getData().getTimeline().get(i).getRecovered();
			image = "https://bit.ly/2RhmL3Q";

			column = new CarouselColumn(image,
					"Negara " + country.substring(0, (country.length() < 40) ? country.length() : 40),
					"Update tanggal " + date + " Jumlah Penduduk : " + dicodingEvents.getData().getPopulation(),
					Arrays.asList(new MessageAction("Meninggal", "Korban Meninggal : " + death),
							new MessageAction("Terkonfirmasi", "Korban Terkonfirmasi : " + confirm),
							new MessageAction("Sembuh", "Berhasil Sembuh : " + recover)));

			carouselColumn.add(column);
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