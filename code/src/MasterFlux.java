import java.awt.*;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MasterFlux extends Thread {

    private BufferedReader in;
    private Command command;

    public MasterFlux(BufferedReader in, Command command) {
        this.in = in;
        this.command = command;
    }

    @Override
    public void run() {
        try{
            while (true){
                //c'est ici que la connection au serveur maitre ce fait
                //quand on recoi une requete on la traitre comme avant
                //regarde tout le temps si le serveur envoie quelque chose au quel cas on l'affiche
                //System.out.println(in.readLine());


                System.out.println(in.lines().toArray());
                List<String> arrayList = in.lines().collect(Collectors.toList());

                System.out.println(arrayList);
                String message = arrayList.get(0) + arrayList.get(1);
                System.out.println("message provenant du serveur maitre >" + message);
                int id = Integer.parseInt(arrayList.get(3));

                command.getChoice(message, id);

            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
