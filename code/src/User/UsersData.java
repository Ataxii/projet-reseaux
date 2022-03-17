package User;

import Message.Message;

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
    public ConcurrentHashMap<User, ArrayBlockingQueue<Message>> messagesToUpdate;

    //un utilisateur et ses abonnés
    public HashMap<String, ArrayList<User>> subscribesTo;

    //un hashtag et les user qui y sont abonnés
    public HashMap<String, ArrayList<User>>subscribesHashtagTo;

    public UsersData() {
        this.userList = new HashMap<String, User>(); // Table
        this.messagesToUpdate = new ConcurrentHashMap<User, ArrayBlockingQueue<Message>>();
        this.subscribesTo = new HashMap<>();
        this.subscribesHashtagTo = new HashMap<>();
    }

    /*************************************************************************************************
     * un nouveau message arrive dans la TL, il faut donc l'ajouter a tout ce qui ont comme abonnement l'author de ce tweet
     *tous les utilisateur abonné à l'autheur de se message se verront ajouter le message dans message to update
     * @param message a ajouter
     ************************************************************************************************/
    public void addMessage(Message message){

        newUser(new User(message.getAuthor()));

        for (User user: subscribesTo.get(message.getAuthor())){
            messagesToUpdate.get(user).add(message);
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
        if(!messagesToUpdate.containsKey(user)){
            messagesToUpdate.put(user, new ArrayBlockingQueue<Message>(300));
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
        return userList.get(nameUser);
    }
}
