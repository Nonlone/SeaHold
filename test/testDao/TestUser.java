package testDao;

import org.json.JSONObject;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;

@Table(tableName = "testdb")
public class TestUser {
	public final static String TEST = "TEST";

	@DBField(fieldName = "id")
	private Long id;

	@DBField(fieldName = "nick")
	private String nick;

	@DBField(fieldName = "sessionKey")
	private String sessionKey;

	@DBField(fieldName = "testNum")
	private Long testNum;

	@DBField(multiColnum = { "id", "nick", "sessionKey" })
	private JSONObject jsonTest;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public Long getTestNum() {
		return testNum;
	}

	public void setTestNum(Long testNum) {
		this.testNum = testNum;
	}

	public JSONObject getJsonTest() {
		return jsonTest;
	}

	public void setJsonTest(JSONObject jsonTest) {
		this.jsonTest = jsonTest;
	}
}
