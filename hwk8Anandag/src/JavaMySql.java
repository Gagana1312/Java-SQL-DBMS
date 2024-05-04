/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

db.mysql.url="jdbc:mysql://localhost:3306/db?characterEncoding=UTF-8&useSSL=false"
*/

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.sql.PreparedStatement;


/**
 *
 * @author kath
 */
public class JavaMySql {

  /** The name of the MySQL account to use (or empty for anonymous) */
  private final String userName = "root";

  /** The password for the MySQL account (or empty for anonymous) */
  private final String password = "Gags@1312";

  /** The name of the computer running MySQL */
  private final String serverName = "127.0.0.1";

  /** The port of the MySQL server (default is 3306) */
  private final int portNumber = 3306;

  /** The name of the database we are testing with (this default is installed with MySQL) */
  private final String dbName = "sharkdb";

//  /** The name of the table we are testing with */
//  private final String tableName = "township";
//  private final boolean useSSL = false;

  /**
   * Get a new database connection
   *
   * @return
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    Connection conn = null;
    Properties connectionProps = new Properties();
    connectionProps.put("user", this.userName);
    connectionProps.put("password", this.password);

    conn = DriverManager.getConnection("jdbc:mysql://"
                    + this.serverName + ":" + this.portNumber + "/" + this.dbName + "?characterEncoding=UTF-8&useSSL=false",
            connectionProps);

    return conn;
  }

  /**
   * Run a SQL command which does not return a recordset:
   * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
   *
   * @throws SQLException If something goes wrong
   */
  /**
   * Connect to MySQL and do some stuff.
   */
  public void run() {

    Scanner scanner= new Scanner(System.in);
    // Connect to MySQL
    Connection conn = null;
    System.out.println("enter the user name:");
    String username = scanner.next();
    System.out.println("enter the Password:");
    String password = scanner.next();
    if(username.equals(this.userName.toString()) && password.equals(this.password.toString())) {
      try {
        conn = this.getConnection();
        System.out.println("Connected to database");
        Statement statement;
        statement = (Statement) conn.createStatement();
        CallableStatement cs;
        String towns;
        String states;
        boolean counter = true;

        try {
          String sql = "SELECT town,state FROM township;";
          ResultSet resultSet = statement.executeQuery(sql);
          System.out.println("                           TOWN                               State\n");
          while (resultSet.next()) {
            String town = resultSet.getString("town");
            String state = resultSet.getString("state");
            System.out.format("%32s%32s\n", town, state);
          }
          while(counter == true){
          System.out.println("Enter the town name\n");
          towns = scanner.next();
          System.out.println("Enter the state name Eg:MA\n");
          states = scanner.next();
          System.out.println("town =  " + towns);
          System.out.println("State =  " + states);
//          String sql_ = "SELECT town,state FROM township where town='" + towns + "' AND state='" + states + "';";
//          ResultSet rs = statement.executeQuery(sql_);
          String sql_ = "SELECT TOWN, STATE FROM township WHERE TOWN=? AND STATE=?";
          PreparedStatement pstmt = conn.prepareStatement(sql_);
          pstmt.setString(1, towns);
          pstmt.setString(2, states);
          ResultSet rs = pstmt.executeQuery();
          if (rs.next()) {
            cs = conn.prepareCall("{CALL allReceivers(?,?)}");
            cs.setString(1, towns);
            cs.setString(2, states);
            boolean hasResult = cs.execute();
            System.out.println("        rid       location                    sponsor                           area                           deployed                         hauled                           detections  individual_shark_detections      bayside                             town        state");
            if (hasResult) {
              ResultSet res = cs.getResultSet();
              while (res.next()) {
                int rid = (res.getInt("rid"));
                int location = (res.getInt("location"));
                String sponsor = (res.getString("sponsor"));
                String area = (res.getString("area"));
                String deployed = (res.getString("deployed"));
                String hauled = (res.getString("hauled"));
                int detections = (res.getInt("detections"));
                int isd = (res.getInt("individual_sharks_detected"));
                String bayside = (res.getString("bayside"));
                String town = (res.getString("town"));
                String state = (res.getString("state"));
                System.out.println();
                System.out.format("%10d %10d %32s %32s %32s %32s %32d %10d %32s %32s " +
                        "%10s\n", rid, location, sponsor, area, deployed, hauled, detections, isd, bayside, town, state);
              }
              counter=false;
            }

          } else {
            System.out.println("Invalid input");
          }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } catch (SQLException e) {
        System.out.println("ERROR: Could not connect to the database");
        e.printStackTrace();
        return;
      }

    } else {
      System.out.println(" Wrong username or Password. Please provide the valid user name and Password");
    }
  }


  /**
   * Connect to the DB and do some stuff
   * @param args
   */
  public static void main(String[] args) {
    JavaMySql app = new JavaMySql();
    app.run();
  }
}
        