package testdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class TestInsertItemCatagory {
	public static void main(String[] args) {
		File[] fileList = new File(TestInsertItemCatagory.class.getResource("/catData").getPath()).listFiles();
		List<Thread> threadList = new ArrayList<Thread>();
		for (File file : fileList) {
			InsertCatDataRunner icdr = new InsertCatDataRunner(file);
			Thread thread = new Thread(icdr);
			thread.setDaemon(true);
			thread.setName("icdr-" + file.getName() + "-" + (threadList.size()));
			threadList.add(thread);
			thread.start();
		}
		boolean runFlag = true;
		while (runFlag) {
			for (Thread thread : threadList) {
				if (thread.getState() == State.TERMINATED) {
					runFlag = false;
				} else {
					runFlag = true;
					break;
				}
			}
		}
	}
}

class InsertCatDataRunner implements Runnable {

	private File file = null;

	public InsertCatDataRunner(File file) {
		for (File cvsFile : file.listFiles()) {
			if (cvsFile.getName().equals("categoryCsv")) {
				this.file = cvsFile;
			}
		}

	}

	@Override
	public void run() {
		if (file == null)
			return;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine();
			String line;
			ItemCatDao icDao = new ItemCatDao();
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				ItemCat ic = new ItemCat();
				ic.setCid(Long.parseLong(values[0].split("\"")[1]));
				ic.setParentCid(Long.parseLong(values[1].split("\"")[1]));
				ic.setName(values[2].split("\"")[1]);
				if(Boolean.parseBoolean(values[3].split("\"")[1])){
					ic.setIsLeaf(ItemCat.ISLEAF_TRUE);
				}else{
					ic.setIsLeaf(ItemCat.ISLEAF_FALSE);
				}
				System.out.println(ic.toJSONObject());
				icDao.insert(ic);
//				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

class ItemCatDao {

	public Long insert(ItemCat itemCat) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "insert into itemcat(cid,parentCid,name,isLeaf)value(?,?,?,?)";
		try {
			conn = DBPool.getConnection();
			pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, itemCat.getCid());
			pstmt.setLong(2, itemCat.getParentCid());
			pstmt.setString(3, itemCat.getName());
			pstmt.setInt(4, itemCat.getIsLeaf());
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBPool.close(rs);
			DBPool.close(pstmt);
			DBPool.close(conn);
		}
		return 0L;
	}
}

class ItemCat {
	public static int ISLEAF_TRUE = 1;
	public static int ISLEAF_FALSE = 0;
	private Long cid;
	private Long parentCid;
	private String name;
	private int isLeaf;

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public Long getParentCid() {
		return parentCid;
	}

	public void setParentCid(Long parentCid) {
		this.parentCid = parentCid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("cid", cid);
		json.put("parentCid", parentCid);
		json.put("name", name);
		json.put("isLeaf", isLeaf);
		return json;
	}

}