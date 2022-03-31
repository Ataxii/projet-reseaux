package Message;

import SQL.Connexion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessagesData {

    public HashMap<Integer, Message> messages; // Table message id PK + string message
    public Connexion connexion = new Connexion();

    public MessagesData() {
        connexion.connect();
        String[] res;
        int i = 2;

        String[] messages_recovered= connexion.selectAllMessage("").split("\n");

        for(String s : messages_recovered){
            res = s.split("\t");
            int id = Integer.parseInt(res[0]);
            String author = res[1];
            ArrayList<String> hashtag = getHashtags(id);
            String message = res[2];
            boolean republish = Integer.parseInt(res[3]) != 0;
            int reply = res[4].equals("0") ? 0 : 1;
            ArrayList<Message> reponses = recoverResponses(id);

            if(reponses.size() > 0 )
                messages.put(id,new Message(id,author,hashtag,message,reponses,republish,reply));
            else
                messages.put(id,new Message(id,author,hashtag,message,republish,reply));
        }
        connexion.close();
    }
    public ArrayList<String> getHashtags(int id){
        ArrayList<String> retour = new ArrayList<>();
        String responses = connexion.selectAllHashtagMessage("where id_message = "+ id);
        String[] res = responses.split("\n");
        for(String s : res){
            String[] ss = s.split("\t");
            if(ss.length > 1 )
                retour.add(ss[2]);
        }
        return retour;
    }
    public ArrayList<Message> recoverResponses(int initial_id) {
        ArrayList<Message> retour = new ArrayList<Message>();
        String[] res;

        String response = connexion.selectAllMessageResponses("where id_message = " + initial_id);
        if (response.length() < 1)
            return retour;
        res = response.split("\t");
        String[] msg_res;
        for (String r : res) {
            String msg = connexion.selectAllMessage("where id = " + r);
            msg_res = msg.split("\t");
            int id = Integer.parseInt(msg_res[0]);
            String author = msg_res[1];
            ArrayList<String> hashtag = getHashtags(id);
            String message = msg_res[2];
            boolean republish = Integer.parseInt(msg_res[3]) != 0;
            int reply = msg_res[4].equals("0") ? 0 : 1;
            retour.add(new Message(id, author, hashtag, message, republish, reply));
        }
        return retour;
    }
    /**************************************************************************************************
     * ajout de message dans la Data, la position du message est son id
     * @param message le message avec toutes les données qu'il doit contenir
     *************************************************************************************************/
    public void add(Message message){
        messages.put(message.id, message);
    }

    /**************************************************************************************************
     * recuperation de message
     * @param id a partir du quel on veut recuperer les messages
     * @return liste de message
     *************************************************************************************************/
    public ArrayList<Message> findId(int id){
        ArrayList<Message> result = new ArrayList<>();
        for (int i = messages.size()-1; i > id ; i--) {
            if (messages.get(i) == null){
                continue;
            }
            result.add(messages.get(i));
        }
        return result;
    }

    /**************************************************************************************************
     * est ce qu'il y a un message avec comme id id
     * @param id que l'on souhaite rechercher
     * @return oui ou non
     *************************************************************************************************/
    public boolean containsID(int id){
        boolean result = false;
        for (Map.Entry<Integer, Message> message : messages.entrySet()) {
            if (message.getValue().id == id) {
                result = true;
                break;
            }
        }
        return result;
    }


    /**************************************************************************************************
     * fait soit un ajout de tout les messages publié par l'author soit une retruction
     * @param dataBase ce qu'on a deja comme message
     * @param author pour trier
     * @return une nouvelle database
     *************************************************************************************************/
    public ArrayList<Message> findAuthor(ArrayList<Message> dataBase, String author) {
        if(dataBase.size() == 0){
            for (Map.Entry<Integer, Message> message : messages.entrySet()) {
                if(Objects.equals(message.getValue().getAuthor(), author))
                    dataBase.add(message.getValue());
            }
        }
        else {
            dataBase.removeIf(message -> !Objects.equals(message.getAuthor(), author));
        }

        return dataBase;
    }

    public ArrayList<Message> findTag(ArrayList<Message> data, String tag) {
        System.out.println("Je rentre dans findTag");
        if(data.size() == 0){
            for (int i = 0; i < messages.size(); i++) {
                System.out.println("Sout jsute avant " + messages.get(i));
                System.out.println("Tag : = " + messages.get(i).getHashtag() + " true ou false : " + messages.get(i).getHashtag().contains(tag));

                if (messages.get(i).getHashtag().contains(tag)) {
                    data.add(messages.get(i));
                }
            }
        }
        else {
            data.removeIf(message -> !message.getHashtag().contains(tag));
        }
        System.out.println("data de tag = " + data);
        return data;
    }

    public ArrayList<Message> findLimite(ArrayList<Message> dataBase, int limite) {
        if(dataBase.size() == 0){
            for (int i = 0; i < Math.min(limite, messages.size()); i++) {
                dataBase.add(messages.get((messages.size()-i)-1));
            }
        }
        else {
            if (!(dataBase.size()<= limite)){
                dataBase.subList(dataBase.size()-limite, dataBase.size());
            }
        }
        return dataBase;
    }

    @Override
    public String toString() {
        String result= "";
        for (Message message :
                messages.values()) {
            result += message.id;
        }
        return result;
    }

    public Message responseId(int id){
        return messages.get(id);
    }
}
