//package com.liangyi.yueting.dao;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//
//import java.util.List;
//
//import com.liangyi.yueting.User;
//import com.liangyi.yueting.jdbc.DataSourcesTool;
//
//public class UserDao {
//
//    // 添加注册用户
//    public boolean insertUser(User user) {
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            System.out.println("获取到数据库链接对象：" + con);
//
//            String sql = "insert into user(u_name,u_number.u_password,u_sex,u_status) values(?,?,?,?,?);";
//            PreparedStatement ps = con.prepareStatement(sql);
//            // 给sql语句注入动态参数,第一个参数为占位符的位置，第二个参数为数据
//            ps.setString(1, user.getUserName());
//            ps.setString(2, user.getUserNumber());
//            ps.setString(3, user.getUserPassword());
//            ps.setString(4, user.getUserSex());
//            ps.setString(5, user.getUserStatus());
//            // 执行sql语句，添加，删除，修改操作使用executeUpdate方法
//            int row = ps.executeUpdate();
//            // 判断是否添加成功的条数
//            if (row > 0) {
//                return true;
//            }
//            // 关闭连接
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // 删除数据
//    public boolean deleteUser(int id) {
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            String sql = "delete from user where u_id=?;";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setInt(1, id);
//            int row = ps.executeUpdate();
//            if (row > 0) {
//                return true;
//            }
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // 更新
//    public boolean updateUser(User user) {
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            String sql = "Update user set u_name=?,u_number=?,u_password=?,u_sex=?,u_status=? where u_id=?;";
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setString(1, user.getUserName());
//            ps.setString(2, user.getUserNumber());
//            ps.setString(3, user.getUserPassword());
//            ps.setString(4, user.getUserSex());
//            ps.setString(5, user.getUserStatus());
//            ps.setInt(6, user.getId());
//
//            int row = ps.executeUpdate();
//            DataSourcesTool.close(con);
//            if (row > 0) {
//                return true;
//            }
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    // 查询所有user
//    // 查询所有user
//    public List<User> queryUser() {
//        List<User> list = new ArrayList<>();
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            String sql = "select * from user;";
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ResultSet set = ps.executeQuery();
//
//            while (set.next()) {
//                int id = set.getInt("u_id");
//                String name = set.getString("u_name");
//                String number = set.getString("u_number");
//                String password = set.getString("u_password");
//                String sex = set.getString("u_sex");
//                String status = set.getString("u_status");
//                String email=set.getString("u_email");
//                User user = new User(id, number, name, password, sex, status,email);
//                list.add(user);
//            }
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    // 查询某一用户
//    public List<User> queryUserByName(String user_name) {
//        List<User> list = new ArrayList<>();
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            String sql = "select * from user where u_number=?;";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, user_name);
//            ResultSet set = ps.executeQuery();
//
//            while (set.next()) {
//                int id = set.getInt("u_id");
//                String name = set.getString("u_name");
//                String number = set.getString("u_number");
//                String password = set.getString("u_password");
//                String sex = set.getString("u_sex");
//                String status = set.getString("u_status");
//                String email=set.getString("u_email");
//                User user = new User(id, number, name, password, sex, status,email);
//                list.add(user);
//            }
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    // 登录查找用户
//    public User loginUser(User user) {
//
//        try {
//            Connection con = DataSourcesTool.getConnection();
//            String sql = "select * from user where u_number=? and u_password=?;";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, user.getUserNumber());
//            ps.setString(2, user.getUserPassword());
//            ResultSet set = ps.executeQuery();
//
//            while (set.next()) {
//                int id = set.getInt("u_id");
//                String name = set.getString("u_name");
//                String number = set.getString("u_number");
//                String password = set.getString("u_password");
//                String sex = set.getString("u_sex");
//                String status = set.getString("u_status");
//                String email=set.getString("u_email");
//                User re_user = new User();
//                // 查询到用户直接return
//                re_user = new User(id, name, number, password, sex, status,email);
//                return re_user;
//            }
//            DataSourcesTool.close(con);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//        // 如果没查到用户
//        return null;
//    }
//
//}
