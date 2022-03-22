import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Microblogamu {

    //client final qui pourra juste : publish reply republish (un)subcribe

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage : Java Client.java host port");
            return;
        }
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        PrintStream out = new PrintStream(socket.getOutputStream());

        MyFlux flux = new MyFlux(in);
        flux(out,flux);

        request(out, in, socket, flux);

    }

    /**************************************************************************************************
     * permet de faire le passage en mode flux et d'eviter de faire un nouveau client
     *
     *************************************************************************************************/
    private static void flux(PrintStream out, MyFlux flux) throws IOException {
        System.out.println("Une session de flux a ete ouverte");

        Scanner scanner = new Scanner(System.in);
        System.out.print("quel est votre pseudo ?\n -> ");
        String pseudo = scanner.nextLine();
        //demande au serveur de se connecter au flux
        out.println("fluxconnect " + pseudo);
        //jsute pour qu'on puisse interompre propement le flux avec une entrée utlisateur

        //gestion de l'affichage appart
        ExecutorService executor;
        executor = Executors.newCachedThreadPool();
        executor.execute(flux);

    }


    /**************************************************************************************************
     * gestion de la recuperation de la commande du client et de l'envoi au serveur
     * @param out envoie de messages
     * @param in recection de message
     * @param socket de connection au serveur
     * @param flux
     * @throws IOException pour readLine
     *************************************************************************************************/
    public static void request(PrintStream out, BufferedReader in, Socket socket, MyFlux flux) throws IOException {

        Scanner scanner = new Scanner(System.in);

        //envoie du message

        String data = "";

        String message_formated = null;


        while (true){
            while (message_formated == null) {
                System.out.print("-> ");
                data = scanner.nextLine();

                message_formated = command_format(data, data.split(" ")[0]);
            }
            if (Objects.equals(message_formated, "close")){
                flux.stop();
                close(out, in, socket);
                System.exit(1);
            }

            out.println(message_formated);
            String response;
            do {
                response = in.readLine();
            } while (response == null);
            System.out.println(response);

        }
    }

    public static void close(PrintStream out, BufferedReader in, Socket socket) throws IOException {
        System.out.println("fermeture de la connexion...");
        out.println("ACK");
        in.close();
        out.close();
        socket.close();
        System.out.println("connexion fermée");
    }


    /**************************************************************************************************
     * regarde si la commande est bien ecrite
     *      si [] alors on peut ne pas le mettre sinon ca doit y etre
     * @param request l'entrée de l'utilisateur
     * @param command la commande invoqué par l'utisateur
     * @return le bon format en fonction de la requete
     *************************************************************************************************/
    public static String command_format(String request, String command) {

        switch (command) {
            case "PUBLISH":
                if (request.split(" ").length < 3 || !request.contains("author:@")) {
                    System.out.println("Usage : PUBLISH author:@user Message:Message");
                    return null;
                }
                String pseudo = request.split(" ")[1];
                return command + " " + pseudo + "\r\n" + request.split(pseudo)[1].split("\n")[0] + " ";

            case "REPLY":
                if (request.split(" ").length < 4 || !request.contains("author:@") || !request.contains("reply_to_id:")) {
                    System.out.println("Usage : REPLY author:@user reply_to_id:id msg");
                    return null;
                }
                String author = request.split(" ")[1];
                String reply_id = request.split(" ")[2];
                return command + " " + author + " " + reply_id + "\r\n" + request.split(reply_id)[1].split("\n")[0] + " ";

            case "REPUBLISH":
                if (request.split(" ").length < 3 || !request.contains("author:@") && !request.contains("msg_id:")) {
                    System.out.println("Usage : REPUBLISH author:@user msg_id:id");
                    return null;
                }
                author = request.split(" ")[1];
                String msg_id = request.split(" ")[2];
                return command + " " + author + " " + msg_id + " \r\n";


            case "SUBSCRIBE":
                if (request.split(" ").length < 3 || !request.contains("author:@") && !request.contains("tag:")) {
                    System.out.println("Usage : (UN)SUBSCRIBE author:@author user:@user || tag:tag");
                    return null;
                }
                author = request.split(" ")[1];
                String info = request.split(" ")[2];
                return command + " " + author + " " + info + " \r\n";

            case "UNSUBSCRIBE":
                if (request.split(" ").length < 3 || !request.contains("author:@") && !request.contains("tag:")) {
                    System.out.println("Usage : (UN)SUBSCRIBE author:@author user:@user || tag:tag");
                    return null;
                }
                author = request.split(" ")[1];
                info = request.split(" ")[2];
                return command + " " + author + " " + info + " \r\n";

            case "close":
                return "close";


            default:
                System.out.println("""
                        Commande inconnu, \r
                           Usage : PUBLISH author:@user Message:message \r
                           Usage : REPLY author:@user reply_to_id:id Message:message \r
                           Usage : REPUBLISH author:@user msg_id:id \r
                           Usage : (UN)SUBSCRIBE author:@author user:@user || tag:tag""");
                return null;
        }

    }

    public static class MyFlux implements Runnable {

        boolean kill = false;
        BufferedReader in;

        public MyFlux(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (!kill) {
                try {
                    System.out.println(in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop(){
            kill = true;
            System.out.println("flux fermé");
        }
    }
}

