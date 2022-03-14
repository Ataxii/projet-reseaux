import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        if(args.length != 2){
            System.out.println("Usage : Java Client.java host port");
            return;
        }
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        PrintStream out = new PrintStream(socket.getOutputStream());

        //si jamais le client ecrit pas un chiffre je sais pas comment faire a part de faire un try catch

        System.out.println("Que veux tu faire ? /n connection au flux (1) : envoie de requete (2)");
        Scanner scanner =  new Scanner(System.in);
        String input = scanner.nextLine();
        try{
            if(Integer.parseInt(input) == 1){

                flux(out, in, socket);
                scanner.close();
            }
            if(Integer.parseInt(input) == 2){

                request(out, in, socket);
                scanner.close();
            }
            else System.out.println("connection au flux (1) : envoie de requete (2)");
        }catch (Exception e){
            System.out.println("connection au flux (1) : envoie de requete (2)");
        }


    }

    /**************************************************************************************************
     * permet de faire le passage en mode flux et d'eviter de faire un nouveau client
     *
     *************************************************************************************************/
    private static void flux(PrintStream out, BufferedReader in, Socket socket) throws IOException {
        System.out.println("vous etes dans la section flux \n si vous voulez sortir faites [stop]");
        System.out.println("quel est votre pseudo ? \n");
        Scanner scanner =  new Scanner(System.in);

        String pseudo = scanner.nextLine();
        //demande au serveur de se connecter au flux
        out.println("fluxconnect " + pseudo );
        //jsute pour qu'on puisse interompre propement le flux avec une entrée utlisateur

        //TODO : corriger le probleme, le thread n'est pas mis sur le coté pour laisser place au scanner
        // (potentiel solution : faire un pool de thred pour que lexecution continue)
        MyFlux flux = new MyFlux(in);
        flux.run();
        String responseCLient;
        System.out.println("passage du thread");
        while(scanner.hasNextLine()){

            System.out.println("entré dans le scanner");
            responseCLient = scanner.nextLine();
            if(responseCLient.equals("stop"))
                break;
        }

    }


    /**************************************************************************************************
     * gestion de la recuperation de la commande du client et de l'envoi au serveur
     * @param out envoie de messages
     * @param in recection de message
     * @param socket de connection au serveur
     * @throws IOException pour readLine
     *************************************************************************************************/
    public static void request(PrintStream out, BufferedReader in, Socket socket) throws IOException {

        System.out.println("vous etes dans la section requete \n");
        Scanner scanner =  new Scanner(System.in);

        //envoie du message
        while(scanner.hasNextLine()){

            String data = scanner.nextLine();

            String message_formated = command_format(data, data.split(" ")[0]);

            while (message_formated == null){
                data = scanner.nextLine();
                message_formated = command_format(data, data.split(" ")[0]);
            }
            out.println(message_formated);
            String response;
            while(true){
                response = in.readLine();
                if(response != null)
                    break;
            }
            System.out.println(response);

            out.println("ACK");

            in.close();
            out.close();
            socket.close();
            return;
        }
    }


    /**************************************************************************************************
     * regarde si la commande est bien ecrite
     *      si [] alors on peut ne pas le mettre sinon ca doit y etre
     * @param request l'entrée de l'utilisateur
     * @param command la commande invoqué par l'utisateur
     * @return le bon format en fonction de la requete
     *************************************************************************************************/
    public static String command_format(String request, String command){
        Scanner scanner =  new Scanner(System.in);

        switch(command){
            case "PUBLISH":
                while(request.split(" ").length < 3 || !request.contains("author:@")){
                    System.out.println("Usage : PUBLISH author:@user Message.Message");
                    request = scanner.nextLine();
                }
                String pseudo = request.split(" ")[1];
                return command + " " + pseudo + "\r\n" + request.split(pseudo)[1].split("\n")[0] +" ";

            case "RCV_IDS":
                while(request.split(" ").length < 1 || (request.contains("author:") && !request.contains("author:@"))){

                    System.out.println("Usage : RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n]");
                    request = scanner.nextLine();

                }
                return request + " ";

            case "RCV_MSG":
                while(request.split(" ").length == 3 || !request.contains("msg_id:")){
                    System.out.println("Usage : RCV_MSG msg_id:id");
                    request = scanner.nextLine();
                }
                return request+ " ";

            case "REPLY":
                while(request.split(" ").length < 4 || !request.contains("author:@") || !request.contains("reply_to_id:") ){
                    System.out.println("Usage : REPLY author:@user reply_to_id:id msg ");
                    request = scanner.nextLine();
                }
                String author = request.split(" ")[1];
                String reply_id = request.split(" ")[2];
                return command + " " + author + " " + reply_id + "\r\n" + request.split(reply_id)[1].split("\n")[0] +" ";

            case "REPUBLISH":
                while(request.split(" ").length < 3 || !request.contains("author:@") && !request.contains("msg_id:")){
                    System.out.println("Usage : REPUBLISH author:@user msg_id:id");
                    request = scanner.nextLine();
                }
                author = request.split(" ")[1];
                String msg_id = request.split(" ")[2];
                return command + " " + author + " " + msg_id + " \r\n";


            case "SUBSCRIBE", "UNSUBSCRIBE": //TODO : refaire la verification des commandes
                while(request.split(" ").length < 3 || !request.contains("author:@") && !request.contains("msg_id:")){
                    System.out.println("Usage : (UN)SUBSCRIBE author:@author user:@user || tag:tag");
                    request = scanner.nextLine();
                }
                author = request.split(" ")[1];
                String info = request.split(" ")[2];
                return command + " " + author + " " + info + " \r\n";


            default:
                System.out.println("Commande inconnu, \r\n " +
                        "Usage : PUBLISH author:@user Message.Message \r\n" +
                        "Usage : RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n] \r\n" +
                        "Usage : RCV_MSG msg_id:id \r\n" +
                        "Usage : REPLY author:@user reply_to_id:id Message.Message \r\n " +
                        "Usage : REPUBLISH author:@user msg_id:id \r\n" +
                        "Usage : (UN)SUBSCRIBE author:@author user:@user || tag:tag");
                return null;
        }
    }

    public static class MyFlux implements Runnable{

        BufferedReader in;
        public MyFlux(BufferedReader in){
            this.in = in;
        }

        @Override
        public void run() {
            while(true){
                try {
                    System.out.println(in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
