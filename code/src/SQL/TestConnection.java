package SQL;

import SQL.Connexion;
import User.User;

import java.util.ArrayList;
import java.util.HashMap;

public class TestConnection {
    public static void main(String[] args) {
            Connexion connexion = new Connexion();
            connexion.connect();

        HashMap<String, ArrayList<User>> subscribers = new HashMap<>();
        ArrayList<String> hashtag = new ArrayList<>();
        String[] hashtags = connexion.selectAllHashtagUser("").split("\n");

        /** Récupérations de tous les hashtags et ajout dans une liste **/
        for(String s : hashtags){
            String[] h = s.split("\t");
            if(h.length > 0)
                hashtag.add(h[2]);
        }
        /** Pour chaque hashtag, on cherche tous les users qui sont abonnés **/
        for(String h : hashtag){
            String[] res = connexion.selectAllHashtagUser("where hashtag = '" + h + "'").split("\n");
            ArrayList<User> user = new ArrayList<>();
            for(String s : res){
                if(s.length()>1){
                    System.out.println(s);
                    user.add(new User(s.split("\t")[1]));
                }
            }
            subscribers.put(h,user);

        }
        System.out.println(subscribers);
            connexion.close();
        }
}
//
