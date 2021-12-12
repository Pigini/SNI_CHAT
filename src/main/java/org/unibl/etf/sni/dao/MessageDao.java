package org.unibl.etf.sni.dao;
//DONE
import org.unibl.etf.sni.dto.MessageDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageDao {
    private static final String SQL_SELECT_BY_RECIPIENT_AND_SENDER =
            "SELECT * FROM message WHERE (sender=? AND recipient=?) OR (sender=? AND recipient=?) ORDER BY timestamp";
    private static final String SQL_INSERT =
            "INSERT INTO `message` (content, `timestamp`, recipient, sender) VALUES (?, ?, ?, ?)";

    public static List<MessageDto> selectByReceiverAndSender(int userId, int otherUserId) {
        List<MessageDto> retVal = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_SELECT_BY_RECIPIENT_AND_SENDER);
            ps.setInt(1,userId);
            ps.setInt(2,otherUserId);
            ps.setInt(3,otherUserId);
            ps.setInt(4,userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                retVal.add(new MessageDto(rs.getInt("id"),
                        rs.getString("content"), rs.getTimestamp("timestamp"), rs.getInt("sender"), rs.getInt("recipient")));
            }
        } catch (SQLException exp) {
            Logger.getLogger(MessageDao.class.getName()).log(Level.SEVERE, exp.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps,rs);
        }
        return retVal;
    }

    public static MessageDto insert(MessageDto messageDto) {
        Connection conn = null;
        PreparedStatement ps = null;
        int ret = 0;
        try {
            conn = ConnectionPool.getInstance().checkOut();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,messageDto.getText());
            ps.setTimestamp(2,messageDto.getCreateTime());
            ps.setInt(3,messageDto.getRecipient());
            ps.setInt(4,messageDto.getSender());

            ret = ps.executeUpdate();
            if(ret!=0){
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next())
                    messageDto.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            Logger.getLogger(MessageDao.class.getName()).log(Level.SEVERE, e.toString());
        } finally {
            ConnectionPool.getInstance().checkIn(conn);
            MySQLUtilities.getInstance().close(ps);
        }
        return ret==0?null:messageDto;
    }


}
