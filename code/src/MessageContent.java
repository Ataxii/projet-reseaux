import java.util.ArrayList;

public class MessageContent {

    Data data = new Data();

    public MessageContent() {
    }

    public String getChoice(String request, int id){
        String[] data = request.split(" ");
        String command = data[0];
        System.out.println("Command = " + command);
        switch(command){
            case "PUBLISH":
                return response(publish(request, id));

            case "RCV_IDS":
                return rcv_ids(request);

            case "RCV_MSG":
                return rcv_msg(request);

            case "REPLY":
                return reply_to_id(request, id);

            default:
                return "Command not found";
        }
    }

    private String reply_to_id(String request, int initId) {
        //TODO: ya un probleme iciException in thread "main" java.lang.NumberFormatException: For input string: "0
        //"
        //        at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
        //        at java.base/java.lang.Integer.parseInt(Integer.java:668)
        //        at java.base/java.lang.Integer.parseInt(Integer.java:786)
        //        at MessageContent.reply_to_id(MessageContent.java:33)
        //        at MessageContent.getChoice(MessageContent.java:25)
        //        at NonBlockingSelectorServer.main(NonBlockingSelectorServer.java:59)
        int id = Integer.parseInt(request.split("reply_to_id:")[1].split(" ")[0]);

        if (data.containsID(id)){
            data.responseId(id).addResponse(new Message(request, initId));
            return "OK";
        }
        return "ERROR";
    }


    private String rcv_msg(String request) {
        int id = Integer.parseInt(request.split("msg_id:")[1]);
        if(!data.containsID(id)){
            return "ERROR";
        }

        return data.messages.get(id).getMessage();
    }

    public String response(Boolean condition){
        if(!condition)
            return "ERROR";
        return "OK";
    }

    public Boolean publish(String request, int id){

        Boolean verif = true;
        String entete = request.split("\r\n")[0];
        String body = request.split("\r\n")[1];
        String[] msarray = request.split(" ");


        /** On peut peut-être mettre en une ligne avec des "||" ? **/
        if(!entete.contains("author:"))
            verif = false;
        if(entete.length() <= "author:@".length())
            verif = false;
        if(body.toCharArray().length > 280 )
            verif = false;

        if (verif){
            data.add(new Message(request, id));
            System.out.println("author:" + msarray[1].substring(7).replace("\r\n" , ""));
            System.out.println("Message = " + body + "\r\nid = " + id);
        }

        return verif;
    }

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

        System.out.println("Data = " + data);
        System.out.println("Database = " + dataBase);
        System.out.println("Database size = " + dataBase.size());

        if(dataBase.size() == 0){
            return "Auncun message trouvé";
        }
        else {
            for(int i=0; i < Math.min(dataBase.size(), limite); i++){
                if(dataBase.get(i) != null){
                    result.add(dataBase.get(i).getMessage().replace("\n",""));
                }
            }
            System.out.println(result.toString());
            return result.toString();
        }
    }
}
