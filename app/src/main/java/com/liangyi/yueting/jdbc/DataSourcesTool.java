package com.liangyi.yueting.jdbc;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataSourcesTool {

	static String url ="jdbc:mysql://rm-wz913w66110q1txu2qo.mysql.rds.aliyuncs.com:3306/yueting?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
	static String username ="yueting_user";
	static String password = "user";
	static Connection con=null;

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static Connection getConnection() {
		try {
			System.out.println(" 正在连接数据库");
			Log.d("数据库连接", "run: 正在连接数据库");
			con=DriverManager.getConnection(url,username,password);
			if(con==null)
				Log.d("数据库连接", "getConnection: 连接数据库失败");
			else{
				Log.d("数据库连接", "run: 连接数据库成功");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//返回数据库的链接对象
		return con;
	}

	//定义一个静态方法用于关闭链接对象
	public static void close(Connection con) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(con!=null) {
					try {
						con.close();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}).start();

	}
}
