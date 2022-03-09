import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Data {

    HashMap<Integer, Message> messageHashMap;
    HashMap<Integer, Message> messages;

    public Data() {
        messages = new HashMap<>();
    }

    /**
     * ajout de message dans la Data, la position du message est son id
     * @param message le message avec toutes les données qu'il doit contenir
     */
    public void add(Message message){
        messages.put(message.id, message);
    }

    /**
     * recuperation de message
     * @param id a partir du quel on veut recuperer les messages
     * @return liste de message
     */
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

    public boolean containsID(int id){
        boolean result = false;
        for (Map.Entry<Integer, Message> message : messages.entrySet()) {
            if(message.getValue().id == id){
                result = true;
            }
        }
        return result;
    }


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
