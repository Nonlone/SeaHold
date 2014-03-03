package testDao.model;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;

@Table(tableName = "testuser")
public class User {

	@DBField(fieldName = "id",isAutoGenerate=true,isKey=true)
	private Long id;

	@DBField(fieldName = "nick")
	private String nick;

	@DBField
	private String sessionKey;

	private String testOtherValue;
	
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

	public String getTestOtherValue() {
		return testOtherValue;
	}

	public void setTestOtherValue(String testOtherValue) {
		this.testOtherValue = testOtherValue;
	}
	
}
