import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connexion {
    private final String URL = "jdbc:sqlite:bdd.sqlite";
    private Connection connection = null;
    private Statement statement = null;

    public Connexion() {
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            statement = connection.createStatement();
            System.out.println("Connexion avec succès");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connecxion");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void createNewRequest(String request) {
        try{
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            stmt.execute(request);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTableUser(){
        String request = "create table User\n" +
                "(\n" +
                "    id         integer\n" +
                "        constraint User_pk\n" +
                "            primary key,autoincrement,\n" +
                "    username   varchar(50),\n" +
                "    subscriber varchar(50)\n" +
                ");\n" +
                "\n" +
                "create unique index User_id_uindex\n" +
                "    on User (id);";
        createNewRequest(request);
    }
    public void createTableHashtagUser(){
        String request = "create table HashtagUser\n" +
                "(\n" +
                "    id       integer\n" +
                "        constraint HashtagUser_pk\n" +
                "            primary key,autoincrement,\n" +
                "    username varchar(50),\n" +
                "    hashtag  varchar(50)\n" +
                ");";
        createNewRequest(request);
    }

    public void createTableHashtagMessage(){
        String request = "create table HashtagMessage\n" +
                "(\n" +
                "    id         integer\n" +
                "        constraint HashtagMessage_pk\n" +
                "            primary key,autoincrement,\n" +
                "    id_message integer,\n" +
                "    hashtag    varchar(50)\n" +
                ");\n";
        createNewRequest(request);
    }
    public void createTableUserList(){
        String request = "create table UserList\n" +
                "(\n" +
                "    id                 integer\n" +
                "        constraint UserList_pk\n" +
                "            primary key,autoincrement,\n" +
                "    username           varchar(50),\n" +
                "    usernameSubscriber varchar(50)\n" +
                ");\n" +
                "\n";
        createNewRequest(request);
    }

    public void createTableMessageResponses(){
        String request = "create table UserList\n" +
                "(\n" +
                "    id                 integer\n" +
                "        constraint MessageResponses\n" +
                "            primary key,autoincrement,\n" +
                "    id_message          integer,\n" +
                "    id_response  integer\n" +
                ");\n" +
                "\n";
        createNewRequest(request);
    }
    public void createTableMessage(){
        String request = "create table Message\n" +
                "(\n" +
                "    id              integer\n" +
                "        constraint Message_pk\n" +
                "            primary key autoincrement,\n" +
                "    author          varchar(50),\n" +
                "    message         varchar(256),\n" +
                "    HastagMessageId integer,\n" +
                "    Republish       integer,\n" +
                "    ReplyTo         integer\n" +
                ");\n";
        createNewRequest(request);
    }

    public void close() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}