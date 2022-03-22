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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonBlockingSelectorServer {
    public int id = 0;
    boolean isMaster = false;

    public void execute() throws IOException {

        Command command = new Command();

        ServerSocketChannel server = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(12345);


        server.socket().bind(inetSocketAddress);
        if (inetSocketAddress.getPort()==12345){
            isMaster = true;
        }

        server.socket().setReuseAddress(true);
        server.configureBlocking(false);


        if (!isMaster){
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
                                if(msg.replace("\n", "").equals("ACK")){
                                    System.out.println("Client deconected");
                                    client.write(ByteBuffer.wrap("close".getBytes(StandardCharsets.UTF_8)));
                                    client.close();
                                    buffer.clear();
                                    continue;
                                }

                                if(msg.split(" ")[0].equals("fluxconnect")){
                                    ExecutorService executor;
                                    executor = Executors.newCachedThreadPool();
                                    String pseudo = msg.split(" ")[1].replace("\n", "").replace(" ", "");
                                    executor.execute(new MyFlux(buffer, client, command, pseudo));

                                }
                                else {
                                    ///////choix du client///////
                                    String responseServ = command.getChoice(msg, id, this);
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
                            if(msg.replace("\n", "").equals("ACK")){
                                System.out.println("Client deconected");
                                client.write(ByteBuffer.wrap("close".getBytes(StandardCharsets.UTF_8)));
                                client.close();
                                buffer.clear();
                                continue;
                            }

                            if(msg.split(" ")[0].equals("fluxconnect")){
                                ExecutorService executor;
                                executor = Executors.newCachedThreadPool();
                                String pseudo = msg.split(" ")[1].replace("\n", "").replace(" ", "");
                                executor.execute(new MyFlux(buffer, client, command, pseudo));

                            }
                            else {
                                ///////choix du client///////
                                String responseServ = command.getChoice(msg, id, this);
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
