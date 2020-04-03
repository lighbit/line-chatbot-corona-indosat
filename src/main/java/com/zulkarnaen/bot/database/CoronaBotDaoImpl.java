package com.zulkarnaen.bot.database;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.zulkarnaen.bot.model.CoronaBotJointEvents;
import com.zulkarnaen.bot.model.CoronaBotUser;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class CoronaBotDaoImpl implements CoronaBotDao {
	// query untuk table user
	private final static String USER_TABLE = "tbl_user";
	private final static String SQL_SELECT_ALL = "SELECT id, user_id, line_id, display_name FROM " + USER_TABLE;
	private final static String SQL_GET_BY_USER_ID = SQL_SELECT_ALL + " WHERE LOWER(user_id) LIKE LOWER(?);";
	private final static String SQL_REGISTER = "INSERT INTO " + USER_TABLE
			+ " (user_id, line_id, display_name) VALUES (?, ?, ?);";

	// query untuk table event
	private final static String EVENT_TABLE = "tbl_event";
	private final static String SQL_SELECT_ALL_EVENT = "SELECT id, event_id, user_id, line_id, display_name FROM "
			+ EVENT_TABLE;
	private final static String SQL_JOIN_EVENT = "INSERT INTO " + EVENT_TABLE
			+ " (event_id, user_id, line_id, display_name) VALUES (?, ?, ?, ?);";
	private final static String SQL_GET_BY_EVENT_ID = SQL_SELECT_ALL_EVENT + " WHERE LOWER(event_id) LIKE LOWER(?);";
	private final static String SQL_GET_BY_JOIN = SQL_SELECT_ALL_EVENT + " WHERE event_id = ? AND user_id = ?;";

	private JdbcTemplate mJdbc;

	@SuppressWarnings("unused")
	private final static ResultSetExtractor<CoronaBotUser> SINGLE_RS_EXTRACTOR = new ResultSetExtractor<CoronaBotUser>() {
		@Override
		public CoronaBotUser extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			while (resultSet.next()) {
				CoronaBotUser user = new CoronaBotUser(resultSet.getLong("id"), resultSet.getString("user_id"),
						resultSet.getString("line_id"), resultSet.getString("display_name"));

				return user;
			}

			return null;
		}
	};

	private final static ResultSetExtractor<List<CoronaBotUser>> MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<CoronaBotUser>>() {
		@Override
		public List<CoronaBotUser> extractData(ResultSet aRs) throws SQLException, DataAccessException {
			List<CoronaBotUser> list = new Vector<CoronaBotUser>();
			while (aRs.next()) {
				CoronaBotUser p = new CoronaBotUser(aRs.getLong("id"), aRs.getString("user_id"), aRs.getString("line_id"),
						aRs.getString("display_name"));
				list.add(p);
			}
			return list;
		}
	};

	@SuppressWarnings("unused")
	private final static ResultSetExtractor<CoronaBotJointEvents> SINGLE_RS_EXTRACTOR_EVENT = new ResultSetExtractor<CoronaBotJointEvents>() {
		@Override
		public CoronaBotJointEvents extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			while (resultSet.next()) {
				CoronaBotJointEvents joinEvents = new CoronaBotJointEvents(resultSet.getLong("id"), resultSet.getString("event_id"),
						resultSet.getString("user_id"), resultSet.getString("line_id"),
						resultSet.getString("display_name"));

				return joinEvents;
			}
			return null;
		}
	};

	private final static ResultSetExtractor<List<CoronaBotJointEvents>> MULTIPLE_RS_EXTRACTOR_EVENT = new ResultSetExtractor<List<CoronaBotJointEvents>>() {
		@Override
		public List<CoronaBotJointEvents> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
			List<CoronaBotJointEvents> list = new Vector<>();
			while (resultSet.next()) {
				CoronaBotJointEvents jointEvents = new CoronaBotJointEvents(resultSet.getLong("id"), resultSet.getString("event_id"),
						resultSet.getString("user_id"), resultSet.getString("line_id"),
						resultSet.getString("display_name"));
				list.add(jointEvents);
			}
			return list;
		}
	};

	public CoronaBotDaoImpl(DataSource aDataSource) {
		mJdbc = new JdbcTemplate(aDataSource);
	}

	public List<CoronaBotUser> get() {
		return mJdbc.query(SQL_SELECT_ALL, MULTIPLE_RS_EXTRACTOR);
	}

	public List<CoronaBotUser> getByUserId(String aUserId) {
		return mJdbc.query(SQL_GET_BY_USER_ID, new Object[] { "%" + aUserId + "%" }, MULTIPLE_RS_EXTRACTOR);
	}

	public int registerLineId(String aUserId, String aLineId, String aDisplayName) {
		return mJdbc.update(SQL_REGISTER, new Object[] { aUserId, aLineId, aDisplayName });
	}

	public int joinEvent(String aEventId, String aUserId, String aLineId, String aDisplayName) {
		return mJdbc.update(SQL_JOIN_EVENT, new Object[] { aEventId, aUserId, aLineId, aDisplayName });
	}

	public List<CoronaBotJointEvents> getEvent() {
		return mJdbc.query(SQL_SELECT_ALL_EVENT, MULTIPLE_RS_EXTRACTOR_EVENT);
	}

	public List<CoronaBotJointEvents> getByEventId(String aEventId) {
		return mJdbc.query(SQL_GET_BY_EVENT_ID, new Object[] { "%" + aEventId + "%" }, MULTIPLE_RS_EXTRACTOR_EVENT);
	}

	public List<CoronaBotJointEvents> getByJoin(String aEventId, String aUserId) {
		return mJdbc.query(SQL_GET_BY_JOIN, new Object[] { aEventId, aUserId }, MULTIPLE_RS_EXTRACTOR_EVENT);
	}
}