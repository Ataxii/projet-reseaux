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

    public UsersData() {
        this.userList = new HashMap<String, User>(); // Table
        this.messagesToUpdate = new ConcurrentHashMap<User, ArrayBlockingQueue<Message>>();
        this.subscribesTo = new HashMap<>();
    }

    /**
     * un nouveau message arrive dans la TL, il faut donc l'ajouter a tout ce qui ont comme abonnement l'author de ce tweet
     *
     * @param message
     */
    public void addMessage(Message message){

        newUser(new User(message.getAuthor()));

        for (User user: subscribesTo.get(message.getAuthor())){
            if (!messagesToUpdate.containsKey(user)){
                messagesToUpdate.put(user, new ArrayBlockingQueue<Message>(300));
            }
            messagesToUpdate.get(user).add(message);
        }
    }

    /**
     * regarde si il faut ajouter le user de partout pour eviter les problemes de nullPointerException
     * @param user l'utilisateur qui va etre ajouté
     */
    public void newUser(User user){
        if(!userList.containsKey(user.userName)){
            userList.put(user.userName, user);
        }
        if(!subscribesTo.containsKey(user.userName)){
            subscribesTo.put(user.userName, new ArrayList<User>());
        }
        if(!messagesToUpdate.containsKey(user)){
            messagesToUpdate.put(user, new ArrayBlockingQueue<Message>(50));
        }
    }

    /**
     * On part du principe que quelqu'un qui veut ajouter une autre personne peut ne pas etre inscrite mais l'autre
     * personne est forcement inscrite
     * @param name personne voulant ajouter quelqu'un
     * @param user la personne qui va etre ajouter a la liste de name
     */
    public void addSubscribe(String name, User user){

        //par précaution
        newUser(user);
        newUser(new User(name));

        if (userList.containsKey(name)){
            userList.get(name).addSubscribe(user);
            if (!subscribesTo.containsKey(user.userName)){
                subscribesTo.put(user.userName, new ArrayList());
            }
            subscribesTo.get(user.userName).add(userList.get(name));

        }else {
            User newUser = new User(name);
            userList.put(name, newUser);
            newUser.addSubscribe(user);
            userList.put(user.userName, userList.get(name));
            if (!subscribesTo.containsKey(user.userName)){
                subscribesTo.put(user.userName, new ArrayList());
            }
            subscribesTo.get(user.userName).add(userList.get(name));

        }
    }


    public User getUser(String nameUser){
        return userList.get(nameUser);
    }
}
