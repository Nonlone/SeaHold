package testDao;

import testDao.model.User;

import com.seahold.dao.sql.impl.SqlC;

public class TestSqlC {
	
	public static void main(String[] args){
		SqlC sqlC = SqlC.count(User.class);
		System.out.println(sqlC.getSql());
	}

}
