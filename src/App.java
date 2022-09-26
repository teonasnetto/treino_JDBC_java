import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DB;

public class App {
    public static void main(String[] args) throws Exception {
        //Declara a conexão já criada
        Connection conn = null;
        //Declara o Statment que é quem envia as querys
        Statement st = null;
        //Declara o ResultSet que é quem traz os dados da query
        ResultSet rs = null;

        try{
            conn = DB.getConnection();
            st = conn.createStatement();

            rs = st.executeQuery("select * from department");

            while (rs.next()) {
                System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
            DB.closeConnection();
            }
    }
}