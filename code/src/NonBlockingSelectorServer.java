import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingSelectorServer {
    public static void main(String[] args) throws IOException {
        int id = 0;

        Command command = new Command();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(12345));
        server.socket().setReuseAddress(true);
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(128);

        while (true) {
            int channelCount = selector.select();
            if (channelCount > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isAcceptable()) {
                        System.out.println("client connected");
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        SelectionKey newkey = client.register(selector, SelectionKey.OP_READ, client.socket().getPort());
                        newkey.attach(false);
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        if (client.read(buffer) < 0) {
                            key.cancel();
                        } else {

                            String msg = new String(buffer.array(), 0,  buffer.position());

                            //verification de la fermeture du serveur
                            //requete envoyé depuis le client
                            if(msg.equals("ACK")){
                                //System.out.println("Connexion close");
                                client.write(ByteBuffer.wrap("close".getBytes(StandardCharsets.UTF_8)));
                                client.close();
                                buffer.clear();
                                continue;
                            }

                            if(msg.split(" ")[0].equals("fluxconnect")){
                                String pseudo = msg.split(" ")[1].replace("\n", "").replace(" ", "");
                                MyFlux flux = new MyFlux(buffer, client, command, pseudo);
                                flux.run();
                            }
                            else {
                                ///////choix du client///////
                                String responseServ = command.getChoice(msg, id++);
                                buffer.flip();
                                byte[] response = (responseServ + "\n").getBytes(StandardCharsets.UTF_8);
                                client.write(ByteBuffer.wrap(response));

                            }


                            buffer.clear();
                        }
                    }
                    iterator.remove();
                }
            }
        }
    }
}
