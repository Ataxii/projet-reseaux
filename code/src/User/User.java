package User;

import SQL.Connexion;

import java.util.HashMap;
import java.util.HashSet;

public class User {
    public String userName;
    private HashMap<String, User> subscribes;
    private HashSet<String> hashtags;
    private final Connexion connexion = new Connexion();

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

    public void delSubscribe(User user){
        subscribes.remove(user.userName);
    }

    public boolean addHashtag(String hashtag){
        if (hashtags.contains(hashtag)){
            return false;
        }
        hashtags.add(hashtag);
        return true;
    }

    public void delHashtag(String hashtag){
        hashtags.remove(hashtag);
    }

    @Override
    public String toString() {
        return userName ;
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }
}
