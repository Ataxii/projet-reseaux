package SQL;

import SQL.Connexion;

public class TestConnection {
    public static void main(String[] args) {
            Connexion connexion = new Connexion();
            connexion.connect();
            connexion.close();
        }
}
//
