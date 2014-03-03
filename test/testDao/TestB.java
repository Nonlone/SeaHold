package testDao;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;

@Table(tableName = "testb")
public class TestB {
	@DBField(fieldName = "id")
	private Long id;
	@DBField(fieldName = "name")
	private String name;
	@DBField(fieldName = "value")
	private Long value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}
}