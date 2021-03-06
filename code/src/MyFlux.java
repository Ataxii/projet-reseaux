import Message.Message;
import User.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class MyFlux extends Thread {

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
                //si non il bloque

                User user = command.usersData.getUser(pseudo);

                Message message = command.usersData.messagesToUpdate.get(user.userName).take();
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
