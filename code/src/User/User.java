package User;

import java.util.ArrayList;
import java.util.HashSet;

public class User {
    public String userName;
    private ArrayList<User> subscribes;
    private HashSet<String> hashtags;

    public User(String userName) {
        this.userName = userName;
        this.subscribes = new ArrayList<>();
        this.hashtags = new HashSet<>();
    }

    public ArrayList<User> getSubscribe() {
        return subscribes;
    }

    public HashSet<String> getHashtag() {
        return hashtags;
    }

    public boolean addSubscribe(User user){
        if (subscribes.contains(user)){
            return false;
        }
        subscribes.add(user);
        return true;
    }

    public boolean addHashtag(String hashtag){
        if (hashtags.contains(hashtag)){
            return false;
        }
        hashtags.add(hashtag);
        return true;
    }


}
