package User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class UsersData {


    //liste d'utilisateur
    public HashMap<String, User> userList;

    //liste pour chaque utilisateur, les messages a publier
    private ConcurrentHashMap<User, ArrayList> messagesToUpdate;

    public UsersData() {
        this.userList = new HashMap<String, User>();
    }

    public User getUser(String nameUser){
        return userList.get(nameUser);
    }










}
