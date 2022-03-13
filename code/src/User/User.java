package User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class User {
    public String userName;
    private HashMap<String, User> subscribes;
    private HashSet<String> hashtags;

    public User(String userName) {
        this.userName = userName;
        this.subscribes = new HashMap<String, User>();
        this.hashtags = new HashSet<>();
    }

    public HashMap<String, User> getSubscribe() {
        return subscribes;
    }

    public HashSet<String> getHashtag() {
        return hashtags;
    }

    public boolean addSubscribe(User user){
        if (subscribes.containsKey(user.userName)){
            return false;
        }
        subscribes.put(user.userName, user);
        return true;
    }

    public boolean addHashtag(String hashtag){
        if (hashtags.contains(hashtag)){
            return false;
        }
        hashtags.add(hashtag);
        return true;
    }

    @Override
    public String toString() {
        return userName ;
    }
}
