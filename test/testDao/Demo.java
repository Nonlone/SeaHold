package testDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import com.seahold.dao.Dao;
import com.seahold.dao.DaoFunc;
import com.seahold.dao.sql.impl.Orders;
import com.seahold.dao.sql.impl.SqlC;

import testDao.model.CategoryPropValue;
import testDao.model.User;

public class Demo {

	public static void main(String[] args){
		User user = new User();
		List<User> userList = new ArrayList<User>();
		int minSize = 5;
		int maxSize = 10;
		user.setNick(getRandomString(minSize, maxSize));
		user.setSessionKey(getRandomString(30, 50));
		for (int i = 0; i < 10; i++) {
			User u = new User();
			u.setNick(getRandomString(minSize, maxSize));
			u.setSessionKey(getRandomString(30, 50));
			userList.add(u);
		}
		int result=0;
		//Dao 接口
		DaoFunc userDao = null;
		 try {
			//建立Dao Dao作为默认类
			userDao = Dao.getInstance(User.class);
			// 插入实体
//			result = userDao.insert(user);
			//插入集合
//			System.out.println(SqlC.insert(User.class));
//			result = userDao.insert(userList);
			//当前实体搜索
			SqlC sqlC = SqlC.select(User.class).orderBy(Orders.sortAsc("nick"));
			System.out.println(sqlC);
			List<User> resultList = userDao.query(sqlC);
			for(User u:resultList){
				System.out.println(new JSONObject(u));
			}
			//count测试
//			Long count = userDao.count();
//			System.out.println("count : "+count);
			//模糊count测试
//			Long fuzzyCount = 0L;
//			fuzzyCount = userDao.fuzzyCount();
//			System.out.println("fuzzyCount : "+fuzzyCount);
			//快速count测试
//			Long fastCount = 0L;
//			fastCount = userDao.fastCount(10000, 100);
//			System.out.println("fastCount : "+fastCount);
			
//			//快速count测试2
//			Dao categoryPropValueDao = Dao.getIntance(CategoryPropValue.class);
//			Long cpvFastCount = categoryPropValueDao.fastCount(50000, 3000);
//			System.out.println("CategroyPropValueFastCount : "+cpvFastCount);
			
			//更新实体
//			user = resultList.get(0);
//			System.out.println(new JSONObject(user));
//			user.setNick(getRandomString(minSize, maxSize));
//			user.setSessionKey(getRandomString(30, 50));
//			result = userDao.update(user);
			//更新集合
//			for (User u:resultList) {
//				u.setNick(getRandomString(minSize, maxSize));
//				u.setSessionKey(getRandomString(30, 50));
//			}
//			result = userDao.update(userList);
			//删除实实体
//			result = userDao.delete(user);
			//删除集合
//			result = userDao.delete(resultList);
			System.out.println("result:"+result);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	
	private static String getRandomString(int minSize, int maxSize) {
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		int length = 0;
		while (length < minSize) {
			length = random.nextInt(maxSize);
		}
		StringBuffer sbString = new StringBuffer(length);
		for (int i = 0; i < sbString.capacity(); i++) {
			sbString = sbString.append(base.charAt(random.nextInt(base.length())));
		}
		return sbString.toString();
	}

	private static long getRandomLong(int minSize, int maxSize) {
		Random random = new Random();
		int length = 0;
		while (length < minSize) {
			length = random.nextInt(maxSize);
		}
		StringBuffer sbString = new StringBuffer(length);
		for (int i = 0; i < sbString.capacity(); i++) {
			sbString = sbString.append(random.nextInt(10));
		}
		return Long.parseLong(sbString.toString());
	}
}
