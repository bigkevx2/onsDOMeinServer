import java.sql.*;

/**
 * Class to connect to the db, implements Runnable for multi threading.
 */
public class DbConnect implements Runnable{
    private final String SERVER = "DESKTOP-MSBUMQV";
    private final String USERNAME = "sa";
    private final String PASSWORD = "bigkevx216993399";
    private Connection connection = null;
    private String query;
    private String connectionUrl =
            "jdbc:sqlserver://" + SERVER + ";"
                    + "database=OnsDomein;"
                    + "user=" + USERNAME + ";"
                    + "PASSWORD=" + PASSWORD + ";"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";

    /**
     * Constructor
     * @param query, the query to fire on the db
     */
    DbConnect(String query) {
        this.query = query;
    }

    @Override
    public void run() {
        PreparedStatement preparedStatement = null;
        try {
            // connect to the db
            connection = DriverManager.getConnection(connectionUrl);
            // make the query sql injection save
            preparedStatement = connection.prepareStatement(query);
            // Loaded? check! target in site? check! FIRE!!!
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Er ging iets fout met het maken van de connection: " + e);
            // close the db connection, clean up time
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.out.println("Fout bij sluiten preparedstatement: " + e);
                }
            }
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Fout bij sluiten connection: " + e);
            }
        }
    }
}
