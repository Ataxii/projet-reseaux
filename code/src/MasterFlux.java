import java.io.BufferedReader;

public class MasterFlux extends Thread {

    private BufferedReader in;

    public MasterFlux(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try{
            while (true){
                //regarde tout le temps si le serveur envoie quelque chose au quel cas on l'affiche
                System.out.println(in.readLine());

            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
