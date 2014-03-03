package testDao.model;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;

@Table(tableName="testcategorypropvalue")
public class CategoryPropValue {
	@DBField(fieldName="cid",isKey=true)
	private Long cid;
	
	@DBField(fieldName="pid",isKey=true)
	private Long pid;
	
	@DBField(fieldName="vid",isKey=true)
	private Long vid;
	
	@DBField(fieldName="propName")
	private String propName;
	
	@DBField(fieldName="name")
	private String name;
	
	@DBField(fieldName="childPid")
	private Long childPid;

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Long getVid() {
		return vid;
	}

	public void setVid(Long vid) {
		this.vid = vid;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getChildPid() {
		return childPid;
	}

	public void setChildPid(Long childPid) {
		this.childPid = childPid;
	}
}
