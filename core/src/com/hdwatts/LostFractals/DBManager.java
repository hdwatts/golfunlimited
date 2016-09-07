package com.hdwatts.LostFractals;

import com.badlogic.gdx.utils.Base64Coder;
import com.hdwatts.LostFractals.Screens.ScreenManager;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

/**
 * Created by Dean Watts on 3/9/2015.
 */
public class DBManager {

    //USERNAME: golfunlimited
    //PASSWORD: verygolf5678

    private final String USERNAME = "golfunlimited";
    private final String PASSWORD = "verygolf5678";
    private final String SERVER = "mysql.hdwatts.com:3306";
    private final String DB = "hdwatts_golfscore";

    private ScreenManager sM;
    public boolean isConnected;
    private int UserID;
    public String username;
    public boolean isLoggedIn;

    private Connection con;

    public DBManager(ScreenManager sM){
        isLoggedIn = false;
        this.sM = sM;
        try {

            Class.forName("com.mysql.jdbc.Driver");

            con = (Connection) DriverManager.getConnection("jdbc:mysql://"+SERVER+"/"+DB, USERNAME, PASSWORD);

            /*//CallableStatement proc_stmt = con.prepareCall("{ call generateID(?) }");
            String query =
                    "select * "+
                            "from hdwatts_golfscore.Users";

            Statement stmt2 = con.createStatement();
            ResultSet rs = stmt2.executeQuery(query);

            while(rs.next()){
                int userid = rs.getInt("UserID");
                String username = rs.getString("UserName");
                String password = rs.getString("Password");
                String saltdb = rs.getString("Salt");
                String email = rs.getString("Email");
                Date datejoined = rs.getDate("DateJoined");
                Date LastLogin = rs.getDate("LastLogin");
                int deleted = rs.getInt("Deleted");
                System.out.println("UserID: "+userid);
                System.out.println("username: "+username);
                System.out.println("password: "+password);
                System.out.println("saltdb: "+saltdb);
                System.out.println("email: "+email);
                System.out.println("datejoined: "+datejoined);
                System.out.println("LastLogin: "+LastLogin);
                System.out.println("Deleted: "+deleted);


            }*/
            isConnected = true;
        }catch(ClassNotFoundException e){
            System.out.println("Cannot find driver");
        }catch(SQLException e){
            System.out.println("Now a sql error:\n"+e.toString());
            isConnected = false;
        }
    }

    public boolean addScore(int hole, int score) throws SQLException{
        if(isConnected && isLoggedIn) {
            String query = "SELECT UserID from Users WHERE UserName = ? AND Deleted = 0;";
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.first();

            int UserID = rs.getInt("UserID");

            query = "SELECT Score FROM GolfScores WHERE Hole = " + hole + " AND UserID = " + UserID;

            Statement stmt2 = con.createStatement();
            rs = stmt2.executeQuery(query);

            if (rs.next() == false) {
                System.out.println("FALSE FOR : " + hole + ", " + score + ", " + UserID);
                //INSERT
                query = "INSERT INTO GolfScores(UserID, Hole, Score, DateModified, Deleted) VALUES(" + UserID + "," + hole + "," + score + ",NOW(),0)";
                stmt = con.prepareStatement(query);
                stmt.executeUpdate();
            } else {
                System.out.println("TRUE FOR : " + hole + ", " + score + ", " + UserID);
                //UPDATE
                query = "UPDATE GolfScores SET Score = " + score + " WHERE hole = " + hole + " AND UserID = " + UserID;
                stmt = con.prepareStatement(query);
                stmt.executeUpdate();
            }

            return true;
        }else{
            return false;
        }
    }


    public boolean login(String username, String password) throws SQLException{
        if(isConnected) {
            String query = "SELECT Salt, Password, UserID from Users WHERE UserName = ? AND Deleted = 0";


            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if(rs.next() == false){
                return false;
            }

            String pwToCheck = password + "" + rs.getString("Salt");

            byte[] hash2 = new byte[20];
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hash2 = digest.digest(pwToCheck.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {

                System.out.println("OY WTF ALGORITHM DOESNT WORK");

            } catch (UnsupportedEncodingException e) {
                System.out.println("ENCODING DONT WORK?  DA FUCK");
            }
            String decodedHashCheck = new String(Base64Coder.encode(hash2));
            String hashToCheck = rs.getString("Password");

            if (hashToCheck.equals(decodedHashCheck)) {
                this.username = username;
                this.UserID = rs.getInt("UserID");
                this.isLoggedIn = true;
                updateScores();
                return true;
            }
        }
        return false;
    }

    private void updateScores() throws SQLException{
        int x = 1;

        while(sM.fM.getScore(x)!=-1){
            System.out.println("ADDING SCORE: "+sM.fM.getScore(x)+" AT "+x);
            addScore(x, sM.fM.getScore(x));
            x++;
        }
    }


    public void printResultSetUsers(ResultSet rs){
        try {
            while (rs.next()) {
                int userid = rs.getInt("UserID");
                String username = rs.getString("UserName");
                String password = rs.getString("Password");
                String saltdb = rs.getString("Salt");
                String email = rs.getString("Email");
                Date datejoined = rs.getDate("DateJoined");
                Date LastLogin = rs.getDate("LastLogin");
                int deleted = rs.getInt("Deleted");
                System.out.println("UserID: " + userid);
                System.out.println("username: " + username);
                System.out.println("password: " + password);
                System.out.println("saltdb: " + saltdb);
                System.out.println("email: " + email);
                System.out.println("datejoined: " + datejoined);
                System.out.println("LastLogin: " + LastLogin);
                System.out.println("Deleted: " + deleted);

            }
        }catch(SQLException e){
            e.toString();
        }
    }

    public boolean addUser(String username, String password, String email) throws SQLException{
        if(isConnected) {

            String query = "SELECT UserID FROM Users WHERE Users.UserName = ? OR Users.Email = ?;";

            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();

            boolean exists = rs.next();

            if(exists){
                System.out.println("Username or Email Already Exists!");
                return false;
            }

            String pw = password;
            SecureRandom sR = new SecureRandom();
            byte[] salt = new byte[32];
            sR.nextBytes(salt);
            String encodedSalt = new String(Base64Coder.encode(salt));
            pw = pw + "" + encodedSalt;

            byte[] hash = new byte[20];
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hash = digest.digest(pw.getBytes("UTF-8"));
            } catch (NoSuchAlgorithmException e) {
                System.out.println("OY WTF ALGORITHM DOESNT WORK");
                return false;
            } catch (UnsupportedEncodingException e) {
                System.out.println("ENCODING DONT WORK?  DA FUCK");
                return false;
            }
            String encodedHash = new String(Base64Coder.encode(hash));


            //INSERT
            query = "INSERT INTO Users(UserName, Password, Salt, Email, DateJoined, LastLogin, Deleted) "
                    +"VALUES(?,'"+encodedHash+"','"+encodedSalt+"',?,NOW(),NOW(),0);";
            stmt = con.prepareStatement(query);
            stmt.setString(1,username);
            stmt.setString(2,email);
            stmt.executeUpdate();


            return true;


            /*PreparedStatement stmt = null;
            String query = "IF NOT EXISTS(SELECT * FROM GolfUsers WHERE UserName = ?)" +
                    "        BEGIN " +
                    "        INSERT INTO " + DB + ".Users(UserName, Password, Salt, Email, DateJoined, LastLogin, Deleted) " +
                    "        VALUES(?,'" + encodedHash + "', '" + encodedSalt + "',?, NOW(), NOW(), 0) END";
            stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, email);

            ResultSet rs = stmt.executeQuery();*/

        }else{
            return false;
        }
    }

    public String getAverageScore(int hole) throws SQLException{
        if(isConnected) {
            String avg;

            String query = "SELECT AVG(Score) AS Average FROM GolfScores WHERE Hole = " + hole;
            PreparedStatement stmt = null;
            stmt = con.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            rs.first();

            avg = rs.getString("Average");
            if (avg == null){
                return "Calculating...";
            }
            avg = avg.substring(0,avg.indexOf(".")+2);
            return avg;
        }
        return "Calculating...";
    }
}
