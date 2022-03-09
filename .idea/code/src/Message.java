import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Message {
    private ArrayList<Message> response;
    public int id;
    private String author;
    private String message;
    private ArrayList<String> hashtag;

    /**
     * creation avec tout les arguments connu
     * @param id numero du message
     * @param author l'auteur du message
     * @param hashtag tous les hashtag qui sont dans le message
     * @param message le contenu du message
     */
    public Message(int id, String author, ArrayList<String> hashtag, String message) {
        this.id = id;
        this.author = author;
        this.hashtag = hashtag;
        this.message = message;
        this.response = new ArrayList<>();
    }


    public void addResponse(Message response) {
        this.response.add(response);
    }

    public ArrayList<Message> getResponse() {
        return response;
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

    /**
     * donnez la requete en brut et la fonction le tranforme en message
     * @param request la requete avec toutes les informations dedans
     * @param id l'id du message
     */
    public Message(String request, int id){
        this.id = id;

        String[] msarray = request.split(" ");
        this.author = msarray[1].substring(7).replace("\r\n" , "").replace(" ", "");

        this.message = request.split("\r\n")[1];

        this.hashtag = new ArrayList<>();
        if( request.contains("#")){
            String[] split = request.split("#");
            for (String word : split) {
                System.out.println("Word = " + word);
                if(!Objects.equals(word.split(" ")[0], "PUBLISH"))
                    hashtag.add(word.split(" ")[0]);
            }
        }
        this.response = new ArrayList<>();

    }
}
