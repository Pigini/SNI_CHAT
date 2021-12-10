package org.unibl.etf.sni.dao;
//DONE

import org.unibl.etf.sni.dto.UserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {
    private static final String SQL_READ_ALL = "SELECT * FROM user";
    private static final String SQL_CREATE =
            "INSERT INTO user (username, password, salt, hashCount, firstName, lastName, email, phoneNumber) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_READ_BY_USERNAME = "SELECT * FROM user WHERE username=?";
    private static final String SQL_CHANGE_STATUS = "UPDATE user SET active=? WHERE id=?";

    public static List<UserDto> readAll() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<UserDto> users = new ArrayList<>();
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_READ_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                UserDto userDto = new UserDto(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"),rs.getInt("salt"),
                        rs.getInt("hashCount"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),rs.getBoolean("active"));
                users.add(userDto);
            }

        } catch (SQLException e) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, e.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps);
        }
        return users;
    }


    public static UserDto readByUsername(String username) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_READ_BY_USERNAME);
            ps.setString(1,username);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserDto userDto = new UserDto(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"),rs.getInt("salt"),
                        rs.getInt("hashCount"),
                        rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),rs.getBoolean("active"));
                return userDto;
            }

        } catch (SQLException e) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, e.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps);
        }
        return null;
    }

    public static UserDto create(UserDto userDto) {
        Connection conn = null;
        PreparedStatement ps = null;
        int ret = 0;
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_CREATE);
            ps.setString(1,userDto.getUsername());
            ps.setString(2,userDto.getPassword());
            ps.setInt(3,userDto.getSalt());
            ps.setInt(4,userDto.getHashCount());
            ps.setString(5,userDto.getFirstName());
            ps.setString(6,userDto.getLastName());
            ps.setString(7,userDto.getEmail());
            ps.setString(8, userDto.getPhoneNumber());
            ret = ps.executeUpdate();
            if (ret != 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    userDto.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, e.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps);
        }
        return ret==0 ? null:userDto;
    }

    public static UserDto update(UserDto userDto) {
        Connection conn = null;
        PreparedStatement ps = null;
        int ret = 0;
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_CHANGE_STATUS);
            ps.setBoolean(1,userDto.isActive());
            ps.setInt(2,userDto.getId());
            ret = ps.executeUpdate();
        } catch (SQLException e) {
            Logger.getLogger(UserDao.class.getName()).log(Level.SEVERE, e.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps);
        }
        return ret == 0 ? null:userDto;
    }
}
