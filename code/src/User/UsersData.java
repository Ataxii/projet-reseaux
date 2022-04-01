package User;

import Message.Message;
import SQL.Connexion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class UsersData {

    /**par souci de performence il faut 2 listes:
     *      -utilisateur -> abonement
     *      -utilisateur -> abonné
     */

    //liste d'utilisateur <nom, User> dans User on a ses abonnement
    public HashMap<String, User> userList;

    //liste pour chaque utilisateur, les messages a publier <User qui veut recup les messages, liste des message a recuperer>
    public ConcurrentHashMap<String, ArrayBlockingQueue<Message>> messagesToUpdate;

    //un utilisateur et ses abonnés
    public HashMap<String, ArrayList<User>> subscribesTo;

    //un hashtag et les user qui y sont abonnés
    public HashMap<String, ArrayList<User>>subscribesHashtagTo;

    private final Connexion connexion = new Connexion();
    public UsersData() {
        this.userList = recoverUser(); // Table
        this.messagesToUpdate = new ConcurrentHashMap<String, ArrayBlockingQueue<Message>>();
        this.subscribesTo = recoverSubscribers();
        this.subscribesHashtagTo = recoverHashtagUser();
    }

    public HashMap<String, ArrayList<User>> recoverHashtagUser(){
        HashMap<String, ArrayList<User>> subscribers = new HashMap<>();
        ArrayList<String> hashtag = new ArrayList<>();
        String[] hashtags = connexion.selectAllHashtagUser("").split("\n");

        /** Récupérations de tous les hashtags et ajout dans une liste **/
        for(String s : hashtags){
            String[] h = s.split("\t");
            if(h.length > 1)
                hashtag.add(h[2]);
        }
        /** Pour chaque hashtag, on cherche tous les users qui sont abonnés **/
        for(String h : hashtag){
            String[] res = connexion.selectAllHashtagUser("where hashtag = '" + h + "'").split("\n");
            ArrayList<User> user = new ArrayList<>();
            for(String s : res){
                if(s.length()>1){
                    user.add(new User(s.split("\n")[1]));
                }
            }
            subscribers.put(h,user);

        }
        return subscribers;
    }
    public  HashMap<String, ArrayList<User>> recoverSubscribers(){
        HashMap<String, ArrayList<User>> subscribers = new HashMap<>();
        ArrayList<String> usernames = new ArrayList<>();
        String[] users = connexion.selectAllUser().split("\n");

        for(String s : users){
            String[] user = s.split("\t");
            if(user.length > 0)
                usernames.add(user[1]);
        }
        for(String user : usernames){
            String[] res = connexion.selectAllUserList("where username = '" + user + "'").split("\n");

            ArrayList<User> temp = new ArrayList<>();
            for(String s : res){
                if(s.length() > 1)
                    temp.add(new User(s.split("\t")[2]));
            }
            subscribers.put(user,temp);
        }
        return subscribers;
    }
    public HashMap<String,User> recoverUser(){
        HashMap<String, User> userList = new HashMap<String, User>();
        String users_sql = connexion.selectAllUser();
        String[] users = users_sql.split("\n");
        for(String s : users){
            String[] user = s.split("\t");
            userList.put(user[1],new User(user[1]));
        }
        return userList;
    }

    /*************************************************************************************************
     * un nouveau message arrive dans la TL, il faut donc l'ajouter a tout ce qui ont comme abonnement l'author de ce tweet
     *tous les utilisateur abonné à l'autheur de se message se verront ajouter le message dans message to update
     * @param message a ajouter
     ************************************************************************************************/
    public void addMessage(Message message){

        newUser(new User(message.getAuthor()));

        for (User user: subscribesTo.get(message.getAuthor())){
            messagesToUpdate.get(user.userName).add(message);
        }


        for (String hashtag :
                message.getHashtag()) {
            if(!subscribesHashtagTo.containsKey(hashtag)){
                subscribesHashtagTo.put(hashtag, new ArrayList<User>());
            }
        }

        if (!message.getHashtag().isEmpty()){
            for (String hashtag :message.getHashtag()) {
                if(hashtag != null){
                    for (User user: subscribesHashtagTo.get(hashtag)){
                        if(!messagesToUpdate.get(user.userName).contains(message)){
                            messagesToUpdate.get(user.userName).add(message);
                        }
                    }
                }
            }
        }
    }

    /*************************************************************************************************
     * regarde s'il faut ajouter le User de partout pour éviter les problèmes de nullPointerException
     * @param user l'utilisateur qui va être ajouté
     ************************************************************************************************/
    public void newUser(User user){
        if(!userList.containsKey(user.userName)){
            userList.put(user.userName, user);
        }
        if(!subscribesTo.containsKey(user.userName)){
            subscribesTo.put(user.userName, new ArrayList<User>());
        }
        if(!messagesToUpdate.containsKey(user.userName)){
            messagesToUpdate.put(user.userName, new ArrayBlockingQueue<Message>(300));
        }
        for (String hashtag :
                user.getHashtag()) {
            if(!subscribesHashtagTo.containsKey(hashtag)){
                subscribesHashtagTo.put(hashtag, new ArrayList<User>());
            }
        }

    }

    /*************************************************************************************************
     * On part du principe que quelqu'un qui veut ajouter une autre personne peut ne pas etre inscrite mais l'autre
     * personne est forcement inscrite
     * @param user1 personne voulant s'abonner à quelqu'un
     * @param newFriend la personne qui va etre ajouter a la liste de name
     ************************************************************************************************/
    public void addSubscribe(String user1, User newFriend){

        newUser(newFriend);
        newUser(new User(user1));

        userList.get(user1).addSubscribe(newFriend);
        subscribesTo.get(newFriend.userName).add(userList.get(user1));

    }

    public void delSubscribe(String user1, User oldFriend){
        newUser(oldFriend);
        newUser(new User(user1));

        userList.get(user1).delSubscribe(oldFriend);
        subscribesTo.get(oldFriend.userName).remove(userList.get(user1));
    }

    public void addSubscribe (String user1, String hashtag){

        newUser(new User(user1));

        userList.get(user1).addHashtag(hashtag);
        if(!subscribesHashtagTo.containsKey(hashtag)){
            subscribesHashtagTo.put(hashtag, new ArrayList<User>());
        }
        subscribesHashtagTo.get(hashtag).add(userList.get(user1));

    }


    public void delSubscribe (String user1, String hashtag){
        newUser(new User(user1));

        userList.get(user1).delHashtag(hashtag);

        subscribesHashtagTo.get(hashtag).remove(userList.get(user1));

    }

    public User getUser(String nameUser){
        newUser(new User(nameUser));
        return userList.get(nameUser);
    }
}
