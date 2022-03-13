import Message.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MyFlux implements Runnable {

    private ByteBuffer buffer;
    private SocketChannel client;
    private Command command;
    private String pseudo;

    public MyFlux(ByteBuffer buffer, SocketChannel client, Command command, String pseudo) {
        this.buffer = buffer;
        this.client = client;
        this.command = command;
        this.pseudo = pseudo;
    }

    @Override
    public void run() {
        try{
            while (true){//regarde si il y a des message a envoyer au client
                //si oui on les envoie
                //si non TODO : faire une sortie (le client envoie exit)

                //si il n'y a plus de Message dans la pile pour un certain utilisateur, la pill bloquera

                Message message = command.usersData.getMessagesToUpdate().get(pseudo).take();

                byte[] response = (message + "\n").getBytes(StandardCharsets.UTF_8);
                try {
                    client.write(ByteBuffer.wrap(response));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                buffer.clear();

            }
        }catch (Exception e){
            System.out.println(e);
        }

    }
}
