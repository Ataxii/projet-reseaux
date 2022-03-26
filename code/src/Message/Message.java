package Message;
import SQL.Connexion;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;

public class Message {
    private ArrayList<Message> responses;
    public int id;
    private String author;
    private String message;
    private ArrayList<String> hashtag;
    private Boolean republish;
    private int reply;
    private Connexion connexion;

    /**************************************************************************************************
     * creation avec tout les arguments connu
     * @param id numero du message
     * @param author l'auteur du message
     * @param hashtag tous les hashtag qui sont dans le message
     * @param message le contenu du message
     *************************************************************************************************/
    public Message(int id, String author, ArrayList<String> hashtag, String message, boolean republish, int reply) {
        connexion = new Connexion();
        this.id = id;
        this.author = author;
        this.hashtag = hashtag;
        this.message = message;
        this.responses = new ArrayList<>();
        this.republish = republish;
        this.reply = reply;

        /** TODO à tester mais ça devrait marcher **/
        connexion.insertMessage(author,message,republish,reply);
        //String id_m = connexion.selectAllMessage("WHERE author = " + author + "AND message = " + message + ";").split(" ")[0];
        //connexion.insertMessageResponse(Integer.parseInt(id_m),-1);
    }



    public void addResponse(Message response) {
        this.responses.add(response);
    }

    public void setRepublish(Boolean republish) {
        this.republish = republish;
    }

    public ArrayList<Message> getResponse() {
        return responses;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<String> getHashtag() {
        return hashtag;
    }

    @Override
    public String toString() {
        String result = "";
        result += "author:@" +  author + " msg_id:" + id ;
        if(reply != -1){
            result += " reply_to_id:" + reply;
        }

        if(republish){
            result += " republished:" + republish;
        }

        result += "\n" + message + "\r";
        return result;
    }

    public String inform(){
        String result = "";
        result += "author:@" +  author + " msg_id:" + id ;
        if(reply != -1){
            result += " reply_to_id:" + reply;
        }

        if(republish){
            result += " republished:" + republish;
        }

        result += message + "\r";
        return result;
    }

    /**************************************************************************************************
     * donnez la requete en brut et la fonction le transforme en message
     * @param request la requete avec toutes les informations dedans
     * @param id l'id du message
     *************************************************************************************************/
    public Message(String request, int id, int reply){
        this.id = id;

        String[] msarray = request.split(" ");
        this.author = msarray[1].substring(7).replace("\r\n" , "").replace(" ", "").replace("@" , "");

        this.message = request.split("\r\n")[1];

        this.hashtag = new ArrayList<>();
        if( request.contains("#")){
            String[] split = request.split("#");
            for (String word : split) {
                if(!Objects.equals(word.split(" ")[0], "PUBLISH"))
                    hashtag.add(word.split(" ")[0]);
            }
        }
        this.responses = new ArrayList<>();

        this.republish = false;
        this.reply = reply;
    }
}
