import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import db.DB;
import db.DbException;
import db.IntegrityException;

public class App {

    public static void recuperarDados() {
        // Declara a conexão já criada
        Connection conn = null;
        // Declara o Statment que é quem envia as querys
        Statement st = null;
        // Declara o ResultSet que é quem traz os dados da query
        ResultSet rs = null;

        try {
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

    public static void inserirDados() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = DB.getConnection();

            st = conn.prepareStatement(
                    "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) Values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, "Carl Purple");
            st.setString(2, "Carl@Purple.com");
            st.setDate(3, new java.sql.Date(sdf.parse("22/10/1990").getTime()));
            st.setDouble(4, 1500.00);
            st.setInt(5, 3);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Done, ID is " + id);
                }
                System.out.println("Done, rows affecteds:" + rowsAffected);                
            } else {
                System.out.println("No rows affecteds");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }

    public static void atualizarDados() {
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = DB.getConnection();

            st = conn.prepareStatement("Update seller SET BaseSalary = BaseSalary + ? WHERE (DepartmentId = ?)");

            st.setDouble(1, 200.00);
            st.setInt(2, 2);

            int rowsAffected = st.executeUpdate();

            System.out.println("Done! Rows affected" + rowsAffected);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }

    public static void deletarDados() {
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = DB.getConnection();

            st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");

            st.setInt(1, 7);

            int rowsAffected = st.executeUpdate();

            System.out.println("Done! Rows affected" + rowsAffected);

        } catch (SQLException e) {
            throw new IntegrityException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }

    public static void transacao() {
        /* Deve ter atomicidade, consistencia, isolada e durável */

        Connection conn = null;
        Statement st = null;

        try {
            conn = DB.getConnection();

            //Segurança dos dados devido a ter que ser feito um commit ou rollback, o commit é feito manualmente
            conn.setAutoCommit(false);

            st = conn.createStatement();

            int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1");
            
            // //Simulnado um erro
            // int x = 1;
            // if (x < 2) {
            //     throw new SQLException("Fake error");
            // }

            int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2");

            //após tudo certo, faz o commit
            conn.commit();

            System.out.println("Rows1: " + rows1);
            System.out.println("Rows2: " + rows2);


        } catch (SQLException e) {
            try {
                //volta tudo para o estado anterior caso de erro
                conn.rollback();
                throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
            } catch (SQLException e1) {
                throw new DbException("Error trying to rollback! Caused by: " + e1.getMessage());
            }
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }

    }

    public static void main(String[] args) throws Exception {
        transacao();
    }
}