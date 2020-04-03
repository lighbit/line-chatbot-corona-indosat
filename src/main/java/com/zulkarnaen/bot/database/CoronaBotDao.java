package com.zulkarnaen.bot.database;

import java.util.List;

import com.zulkarnaen.bot.model.CoronaBotJointEvents;
import com.zulkarnaen.bot.model.CoronaBotUser;

public interface CoronaBotDao {
	public List<CoronaBotUser> get();

	public List<CoronaBotUser> getByUserId(String aUserId);

	public int registerLineId(String aUserId, String aLineId, String aDisplayName);

	public int joinEvent(String aEventId, String aUserId, String aLineId, String aDisplayName);

	public List<CoronaBotJointEvents> getEvent();

	public List<CoronaBotJointEvents> getByEventId(String aEventId);

	public List<CoronaBotJointEvents> getByJoin(String aEventId, String aUserId);

}
