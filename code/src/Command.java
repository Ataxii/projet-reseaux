import Message.Message;
import Message.MessagesData;
import User.UsersData;

import java.util.ArrayList;

public class Command {


    MessagesData data = new MessagesData();
    UsersData usersData = new UsersData();

    public Command() {
    }

    //TODO: faire un message d'erreur pour chaque probleme ERROR + id not found ....

    /**************************************************************************************************
     * ce qui fait le choix par rapport à la requette du client
     * @param request le message du client
     * @param id du message
     * @return message d'erreur ou de validation
     *************************************************************************************************/
    public String getChoice(String request, int id){
        String[] data = request.split(" ");
        String command = data[0];

        switch(command){
            case "PUBLISH":
                return response(publish(request, id));

            case "RCV_IDS":
                return rcv_ids(request);

            case "RCV_MSG":
                return rcv_msg(request);

            case "REPLY":
                return reply_to_id(request, id);

            case "REPUBLISH":
                return response(republish(request, id));

            default:
                return "Command not found";
        }
    }

    /*************************************************************************************************
     * ajout d'un nouveau message qui reprend celui de id mais en modifiant l'author et l'id
     * @param request la request du client
     * @param initial est le nouvelle id
     * @return vrai ou faux
     *************************************************************************************************/
    private Boolean republish(String request, int initial) {
        int id = Integer.parseInt(request.split("msg_id:")[1].split(" ")[0].replace("\r\n", ""));

        String author = request.split("author:@")[1].split(" ")[0].replace("\r\n", "");
        if(!data.containsID(id)){
            return false;
        }
        Message originalMessage = data.responseId(id);
        Message newMessage = new Message(initial, author, originalMessage.getHashtag(), originalMessage.getMessage(), true, -1);
        data.add(newMessage);
        System.out.println(newMessage);
        return true;
    }

    /**************************************************************************************************
     *
     * @param request du client
     * @param initId l'id que l'on va donner a la reply pour en faire un nouveau message
     * @return message d'erreur ou de validation
     *************************************************************************************************/
    private String reply_to_id(String request, int initId) {

        //l'id du message au quel on repond

        int id = Integer.parseInt(request.split("reply_to_id:")[1].split(" ")[0].replace("\r\n", ""));


        //on ajoute le message dans les reply du message et on l'ajoute en tant que message lui meme
        if (data.containsID(id)){
            Message newMessage = new Message(request, initId, id);
            data.responseId(id).addResponse(newMessage);
            data.add(newMessage);
            System.out.println(newMessage);
            return "OK";
        }
        return "ERROR";
    }


    /**************************************************************************************************
     * fonction pour recevoir le contenus du message qui a l'id stocké dans la request
     * @param request de l'utilisateur
     * @return soit le message soit ERROR
     *************************************************************************************************/
    private String rcv_msg(String request) {
        int id = Integer.parseInt(request.split("msg_id:")[1].replace(" \n" , ""));
        if(!data.containsID(id)){
            return "ERROR";
        }

        return data.messages.get(id).getMessage();
    }

    /**************************************************************************************************
     * donne la reponse pour publish
     * @param condition pour le message
     * @return un message d'erreur ou de validation
     *************************************************************************************************/
    public String response(Boolean condition){
        if(!condition)
            return "ERROR";
        return "OK";
    }

    /**************************************************************************************************
     * fonction qui verifie et publie le message par l'utilisateur
     * @param request tout le message envoyé par le client
     * @param id du client
     * @return un message d'erreur ou de validation
     *************************************************************************************************/
    public Boolean publish(String request, int id){

        boolean verif = true;
        String entete = request.split("\r\n")[0];
        String body = request.split("\r\n")[1];

        if(!entete.contains("author:") || entete.length() <= "author:@".length()||body.toCharArray().length > 280 )
            verif = false;

        if (verif){
            Message newMessage = new Message(request, id, -1);
            data.add(newMessage);

            System.out.println(newMessage);
        }

        return verif;
    }


    /**************************************************************************************************
     * Principe de la fonction : regarde si les arguments sont dans la request
     *      pour chaque request, on recupere les elements que l'on met dans les fonctions qui regardent si la database
     *      est vide ou si il y a des elements
     *       -si elle est vide on ajoute les elements dans data base
     *       -si elle est pas vide on supprime par rapport au element que l'on a
     * @param request le message en entier
     * @return message pour confirmer ou non la requete
     *************************************************************************************************/
    public String rcv_ids(String request){

        ArrayList<String> result = new ArrayList<>();
        String author;
        int id;
        String tag;
        int limite = 5; //valeur par defaut

        ArrayList<Message> dataBase = new ArrayList<>();
        if(request.contains("since_id:")){
            id = Integer.parseInt(request.split("id:")[1].split(" ")[0]);
            dataBase = data.findId(id);
        }

        if(request.contains("author:")){
            author = request.split("author:")[1].split(" ")[0];
            dataBase = data.findAuthor(dataBase, author);
            System.out.println(dataBase);
        }

        if(request.contains("#")){
            tag = request.split("#")[1].split(" ")[0];
            dataBase = data.findTag(dataBase, tag);
        }

        if(request.contains("limite:")){
            limite = Integer.parseInt(request.split("limite:")[1].split(" ")[0]);
            dataBase = data.findLimite(dataBase, limite);
        }

        if(dataBase.size() == 0){
            return "Auncun message trouvé";
        }
        //TODO probleme lors de la commande RCV_IDS (quand il n'y a pas d'argument)
        else {
            for(int i=0; i < Math.min(dataBase.size(), limite); i++){
                if(dataBase.get(i) != null){
                    result.add(dataBase.get(i).getMessage().replace("\n",""));
                }
            }
            return result.toString();
        }
    }
}
