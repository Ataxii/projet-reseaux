import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NonBlockingSelectorServer {
    public int id = 0;
    boolean isMaster = false;
    public int serverPort;

    public void execute() throws IOException {

        //principe :
        //  client envoie a un serveur lambda, le serveur redirige vers master,
        //  le master renvoie la commnade traité pour que le serveur lambda puisse lafficher
        //  le serveur lambda va recevoir aussi les messages des autres serveurs

        Command command = new Command(this);

        ServerSocketChannel server = ServerSocketChannel.open();

        //distribution des ports
        portAttribution();


        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverPort);


        server.socket().bind(inetSocketAddress);
        if (inetSocketAddress.getPort() == 12345) {
            isMaster = true;
        }

        server.socket().setReuseAddress(true);
        server.configureBlocking(false);


        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(128);

        //puis lancer les clients faire leur requete en dessous
        //il y aura donc qu'un seul Thread pour faire l'affichage des messages du master
        BufferedReader in;
        PrintStream out = null;


        ArrayList<SocketChannel> listServer = new ArrayList<>();


        if (!isMaster) {
            //creation du Thread qui va gerer l'affichage des messages envoyé par le master

            //connection au master
            Socket socket = new Socket("localhost", 12345);

            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintStream(socket.getOutputStream());
            ExecutorService poolForMaster = Executors.newCachedThreadPool();
            poolForMaster.execute(new MasterFlux(in, out, command));
            out.print("SERVERCONNECT");
        }

        while (true) {

            int channelCount = selector.select();
            if (channelCount > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

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

                            String msg = new String(buffer.array(), 0, buffer.position());

                            //message envoyé d'un serveur au serveur master pour lui notifier que cest un serveur et non un client
                            if (msg.replace("\n", "").equals("SERVERCONNECT")) {
                                listServer.add(client);
                                buffer.clear();
                                continue;
                            }
                            //verification de la fermeture du serveur
                            //requete envoyé depuis le client
                            if (msg.replace("\n", "").equals("ACK")) {
                                System.out.println("Client deconected ");
                                client.write(ByteBuffer.wrap("close".getBytes(StandardCharsets.UTF_8)));
                                client.close();
                                buffer.clear();
                                continue;
                            }

                            if (msg.split(" ")[0].equals("fluxconnect")) {

                                ExecutorService executor;
                                executor = Executors.newCachedThreadPool();
                                String pseudo = msg.split(" ")[1].replace("\n", "").replace(" ", "");
                                executor.execute(new MyFlux(buffer, client, command, pseudo));
                            } else {
                                if (isMaster) {
                                    ///////choix du client///////
                                    command.getChoice(msg, id);
                                    //ajout de l'id pour que tous les autres serveurs aient le meme
                                    msg = (id - 1) + "\n" + msg + "\n";
                                    buffer.flip();
                                    //renvoie à tous les autres serveurs le message en brut pour qu'ils le gerent eux meme
                                    for (SocketChannel channel : listServer) {
                                        channel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
                                    }

                                } else {
                                    //envoie de la requete sur le serveur maitre
                                    assert out != null;
                                    out.println(msg);
                                }
                            }
                            buffer.clear();
                        }
                    }
                }
            }
        }
    }


    public void portAttribution() throws IOException {
        File file = null;
        try {

            file = new File("./pairs.cfg");

            if (file.createNewFile()) {
                System.out.println("Fichier créé!");
            } else {
                System.out.println("Fichier existe déjà.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Scanner obj = new Scanner(file);

        StringBuilder document = new StringBuilder();

        if (reader.readLine() == null) {

            serverPort = 12345;
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.print(0 + "=" + serverPort + ";");
            writer.close();
            System.out.println("Création du serveur Master port : " + serverPort);
            return;
        }

        while (obj.hasNextLine()) {
            document.append(obj.nextLine());
        }
        //structure du fichier :
        // master=12345;1=12346;2=12347

        String last = document.toString().split(";")[document.toString().split(";").length - 1];
        int lastNb = Integer.parseInt(last.split("=")[0]);
        int lastPort = Integer.parseInt(last.split("=")[1]);
        serverPort = lastPort + 1;
        System.out.println("Création d'un serveur peer n° " + (lastNb + 1) + ", port : " + serverPort);
        PrintWriter writer = new PrintWriter(new FileWriter(file));
        writer.append(document.toString()).append(String.valueOf(lastNb + 1)).append("=").append(String.valueOf(serverPort)).append(";");
        writer.close();
    }


}
