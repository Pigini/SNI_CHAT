package org.unibl.etf.sni.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.sni.dao.MySQLUtilities;
import org.unibl.etf.sni.dao.UserDao;
import org.unibl.etf.sni.dto.UserDto;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class UserBean implements Serializable {

    private static final long serialVersionUID = -8181182985155662423L;

    private UserDto userDto = new UserDto();
    private boolean loggedId = false;
    private boolean token = false;


    public boolean signIn(String username, String password) {
        userDto = UserDao.readByUsername(username);
        if (userDto != null) {
            if(userDto.getPassword().equals
                    (MySQLUtilities.getHash(password,userDto.getSalt(),userDto.getHashCount())))
                loggedId = true;
        }
        return loggedId;
    }

    public void logout() {
        userDto = new UserDto();
        loggedId = false;
        token = false;
    }

    public boolean signup() {
        String password = userDto.getPassword();
        userDto.setSalt(MySQLUtilities.getSalt());
        userDto.setHashCount(MySQLUtilities.getHashCount());
        userDto.setPassword(MySQLUtilities.getHash(password,userDto.getSalt(),userDto.getHashCount()));
        return UserDao.create(userDto)!=null;
    }

}
