import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;

public class Serveur {

    public static void main(String[] args) throws IOException {
        ServerSocket socketServeur = new ServerSocket(1234);
        int compteur = 0;
        System.out.println("Lancement du serveur");
        while(true){
            System.out.println("Attente de connexion");
            Socket client = socketServeur.accept();
            System.out.println("Client connecté");
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (true) {
                String data = reader.readLine();

                if (data != null && data.equals("q") ) {
                    System.out.println("Fermeture de la connexion avec le client");
                    client.close();
                    break;
                }
                compteur ++;
                System.out.println(data + " est la " + compteur + "ème ligne reçu");

                PrintStream writer = new PrintStream(client.getOutputStream());

                writer.println(compteur + " ème ligne bien reçu");
            }
        }


    }
}
