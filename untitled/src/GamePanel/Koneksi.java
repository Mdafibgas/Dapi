package GamePanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
class Koneksi {
    public static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/tic tac toe"; // Ganti sesuai dengan URL MySQL Anda
        String user = "root"; // Ganti sesuai dengan nama pengguna MySQL Anda
        String password = ""; // Ganti sesuai dengan kata sandi MySQL Anda

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Perhatikan perubahan nama driver
            Connection con = DriverManager.getConnection(url, user, password);
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
}
};