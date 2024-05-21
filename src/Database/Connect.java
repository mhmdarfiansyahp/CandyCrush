package Database;

import java.sql.*;

public class Connect {
    public Connection conn;
    public Statement stat;
    public ResultSet result;
    public PreparedStatement pstat;

    public Connect(){
        try{

            String url = "jdbc:sqlserver://HP;database=CandyCrush;user=sa;password=12345";
            conn = DriverManager.getConnection(url);
            stat = conn.createStatement();
        }catch (Exception e){
            System.out.printf("Error saat connect database : "+e);
        }
    }

    public static void main(String[] args) {
        Connect connection = new Connect();
        System.out.printf("Connection berhasil");
    }

}
