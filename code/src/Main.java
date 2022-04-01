import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        NonBlockingSelectorServer server = new NonBlockingSelectorServer();
        server.execute();

        server.execute();
        server.execute();
    }
}
