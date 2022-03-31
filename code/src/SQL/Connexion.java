package SQL;

import java.sql.*;

public class Connexion {
    private final String URL = "jdbc:sqlite:bdd2.sqlite";
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



    public void close() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*** SelectAll ***/
    public String selectAllUser(){
        String sql = "SELECT * FROM User" ;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t").append(rs.getString("username")).append("\t").append(rs.getString("subscriber")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }
    public String selectAllMessage(String where){
        String sql = "SELECT * FROM Message " + where;
        System.out.println("sql = " + sql);
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getString("author")).append("\t")
                        .append(rs.getString("message")).append("\t")
                        .append(rs.getInt("Republish")).append("\t")
                        .append(rs.getInt("ReplyTo")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    public String selectAllHashtagMessage(String where){
        String sql = "SELECT * FROM HashtagMessage " + where ;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getInt("id_message")).append("\t")
                        .append(rs.getString("hashtag")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    public String selectAllHashtagUser(String where){
        String sql = "SELECT * FROM HashtagUser " + where ;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getString("username")).append("\t")
                        .append(rs.getString("hashtag")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    public String selectAllMessageData(){
        String sql = "SELECT * FROM MessageData" ;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getInt("id_m")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    public String selectAllMessageResponses(String where){
        String sql = "SELECT * FROM MessageResponses " + where ;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getInt("id_message")).append("\t")
                        .append(rs.getInt("id_response")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    public String selectAllUserList(String where){
        String sql = "SELECT * FROM UserList " + where;
        StringBuilder response= new StringBuilder();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                response.append(rs.getInt("id")).append("\t")
                        .append(rs.getString("username")).append("\t")
                        .append(rs.getString("usernameSubscriber")).append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return String.valueOf(response);
    }

    /*** Insert dans les tables ***/
    public void insertUserList(String username, String username_subscriber){
        createNewRequest("INSERT INTO UserList (username,usernameSubscriber) VALUES ('"+username+"','"+username_subscriber+"');");
    }

    public void insertUser(String username, String subscriber){
        createNewRequest("INSERT INTO User (username,subscriber) VALUES ('"+username+"','"+subscriber+"');");
    }

    public void insertHashtagUser(String username, String hashtag){
        createNewRequest("INSERT INTO HashtagUser (username,hashtag) VALUES ('"+username+"','"+hashtag+"');");
    }

    public void insertHashtagMessage(int id_message, String hashtag){
        createNewRequest("INSERT INTO HashtagMessage (id_message,hashtag) VALUES ('"+id_message+"','"+hashtag+"');");
    }
    public void insertMessageData(int id_message){
        createNewRequest("INSERT INTO HashtagMessage (id_m) VALUES ("+id_message+");");
    }
    public void insertMessage(String author, String message, boolean republish, int replyTo){
        int r;
        r = !republish ? 0 : 1;
        createNewRequest("INSERT INTO Message (author, message, Republish, ReplyTo) VALUES ('"+author+"','"+message+"','"+r+"',"+replyTo+");");
    }
    public void insertMessageResponse(int id_m, int id_r){
        createNewRequest("INSERT INTO MessageResponses (id_message,id_response) VALUES ("+id_m+","+id_r+");");
    }

    /** UPDATES **/
    public void updateRepublish(Boolean republish, int id){
        int v = republish ? 0:1;
        createNewRequest("UPDATE Message set Republish = " + v +" where id = " + id);
    }


    /***  Créations des tables ***/
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
}