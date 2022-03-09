import java.io.*;
import java.net.ServerSocket;
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


        Scanner scanner =  new Scanner(System.in);
        System.out.println("Que veux tu faire ?");

        //envoie du message
        while(scanner.hasNextLine()){

            String data = scanner.nextLine();

            String message_formated = command_format(data, data.split(" ")[0]);

            while (message_formated == null){
                data = scanner.nextLine();
                message_formated = command_format(data, data.split(" ")[0]);
            }
            out.println(message_formated);
            String response,close;
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

    public static String command_format(String request, String command){
        Scanner scanner =  new Scanner(System.in);

        switch(command){
            case "PUBLISH":
                while(request.split(" ").length < 3){
                    System.out.println("Usage : PUBLISH [author:@user] Message");
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
                while(request.split(" ").length == 3){
                    System.out.println("Usage : RCV_MSG [msg_id:id]");
                    request = scanner.nextLine();
                }
                return request+ " ";

            case "REPLY":
                while(request.split(" ").length < 4){
                    System.out.println("Usage : REPLY [author:@user] [reply_to_id:id]");
                    request = scanner.nextLine();
                }
                String author = request.split(" ")[1];
                String reply_id = request.split(" ")[2];
                return command + " " + author + " " + reply_id + "\r\n" + request.split(reply_id)[1].split("\n")[0] +" ";

            default:
                System.out.println("Commande inconnu, \r\n Usage : PUBLISH [author:@user] Message \r\n" +
                        " Usage : RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n]");
                return null;
        }
    }
}
