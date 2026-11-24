package perpustakaan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class UserDao {
    private UserDao() {}

    public static List<Data.User> semua() throws SQLException {
        List<Data.User> out = new ArrayList<>();
        String sql = "SELECT id_user, username, password, nama, peran FROM users ORDER BY id_user";
        try (Connection c = Koneksi.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new Data.User(rs.getInt("id_user"), rs.getString("username"),
                        rs.getString("password"), rs.getString("nama"), rs.getString("peran")));
            }
        }
        return out;
    }
}
