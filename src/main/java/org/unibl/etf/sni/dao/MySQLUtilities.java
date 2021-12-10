package org.unibl.etf.sni.dao;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class MySQLUtilities {

    private static MySQLUtilities instance;

    public static MySQLUtilities getInstance() {
        if (instance == null)
            instance = new MySQLUtilities();
        return instance;
    }

    public static int getSalt() {
        return new Random().nextInt();
    }
    public static byte[] getSaltByte(int saltInt) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(saltInt);
        return buffer.array();
    }
    public static int getHashCount() {
        return new Random().nextInt(1000)+1000;
    }
    public static String getHash(String password,int salt,int hashCount){
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        digest.update(getSaltByte(salt));
        byte[] hash=null;
        try {
            hash=password.getBytes("UTF-8");
            for(int i=0;i<hashCount;i++)
                hash= digest.digest(hash);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytesToHex(hash);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private MySQLUtilities() {
    }

    public void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(Statement s) {
        if (s != null) {
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(Connection conn, Statement s) {
        close(s);
        close(conn);
    }

    public void close(Connection conn, ResultSet rs) {
        close(rs);
        close(conn);
    }

    public void close(Statement s, ResultSet rs) {
        close(rs);
        close(s);
    }

    public void close(Connection conn, Statement s, ResultSet rs) {
        close(rs);
        close(s);
        close(conn);
    }


}
