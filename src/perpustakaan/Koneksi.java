package perpustakaan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Satu pintu untuk membuka koneksi MySQL (XAMPP: host localhost, port 3306, user root, password kosong). */
public final class Koneksi {
    private Koneksi() {}

    private static final String URL =
            "jdbc:mysql://localhost:3306/perpustakaan?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Makassar";
    private static final String USER = "root";
    private static final String PASS = "";

    /** Selalu pakai try-with-resources agar koneksi tertutup otomatis. */
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
