package models.cloudsql;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.sql.PreparedStatement;

@Singleton
public class Connector
{
    // static variable single_instance of type Singleton
    private static Connector single_instance = null;



    // private constructor restricted to this class itself
    @Inject
    private Connector()
    {
        String s = "to be updated later";
        //to insert project id and other private info
    }

    // static method to create instance of Singleton class
    public static Connector getInstance(String[] args) throws Exception {
        if (single_instance == null)
            single_instance = new Connector();
        return single_instance;
    }
    public static String readDatabase(String args) throws IOException, SQLException {
        // TODO: fill this in
        // The instance connection name can be obtained from the instance overview page in Cloud Console
        // or by running "gcloud sql instances describe <instance> | grep connectionName".
        String instanceConnectionName = "civic-brand-207014:us-central1:dialogflow-1";
        // TODO: fill this in
        // The database from which to list tables.
        String databaseName = "supporthub";

        String username = "root";

        // TODO: fill this in
        // This is the password that was set via the Cloud Console or empty if never set
        // (not recommended).
        String password = "****";

        if (instanceConnectionName.equals("<insert_connection_name>")) {
            System.err.println("Please update the sample to specify the instance connection name.");
            System.exit(1);
        }

        if (password.equals("<insert_password>")) {
            System.err.println("Please update the sample to specify the mysql password.");
            System.exit(1);
        }

        //[START doc-example]
        String jdbcUrl = String.format(
                "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                        + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
                databaseName,
                instanceConnectionName);

        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
        //[END doc-example]

        String res = null;
        try (Statement statement = connection.createStatement()) {
            System.out.println("inside singleton before querying"+args);
            String enteredByUser = args;
            String forSql = "%" + enteredByUser + "%";
            forSql = forSql.replace("\"", "");
            String sql = "select ingredients from contents2 where product like ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, forSql);
            ResultSet resultSet = ps.executeQuery();
            //ResultSet resultSet = statement.executeQuery(finalString);
            System.out.println("query"+ps);
            System.out.println(resultSet);
	    while (resultSet.next()) {
                res = res+resultSet.getString(1);
                System.out.println("inside singleton");
                System.out.println(resultSet.getString(1));
            }
        }
	System.out.println("results"+res);
        return res;
    }}
