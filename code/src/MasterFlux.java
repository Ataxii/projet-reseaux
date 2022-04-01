import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;


public class MasterFlux extends Thread {

    public BufferedReader in;
    public Command command;
    public PrintStream out;

    public MasterFlux(BufferedReader in, PrintStream out, Command command) {
        this.in = in;
        this.command = command;
        this.out = out;
    }

    @Override
    public void run() {
        try{
            while (true){
                //c'est ici que la connexion au serveur maitre ce fait
                //quand on reçoit une requête on la traite comme avant
                //regarde tout le temps si le serveur envoie quelque chose au quel cas on l'affiche


                ArrayList<String> arrayList = new ArrayList<>();
                String line;

                while(!(line = in.readLine()).isEmpty())
                {

                    arrayList.add(line);
                }
                if(arrayList.size()>1){
                    if (arrayList.get(1).contains("PUBLISH")){
                        arrayList.add(2, "\r\n" );
                    }
                    if (arrayList.get(1).contains("REPLY")){
                        arrayList.add(3, "\r\n" );
                    }
                    else arrayList.add("\r\n");
                }
                else continue;


                String message = "";

                for (int i = 1 ; i < arrayList.size()-1; i++) {
                    message += arrayList.get(i);
                }

                int id = Integer.parseInt(arrayList.get(0));


                command.getChoice(message, id);
            }
        }catch (Exception e){
            System.out.println("problème de reception du message venant du serveur master [" + Arrays.toString(e.getStackTrace()) + "]");
        }
    }
}
