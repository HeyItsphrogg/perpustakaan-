package perpustakaan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class KategoriDao {
    private KategoriDao() {}

    public static List<Data.Kategori> semua() throws SQLException {
        List<Data.Kategori> out = new ArrayList<>();
        String sql = "SELECT id_kategori, nama_kategori FROM kategori ORDER BY id_kategori";
        try (Connection c = Koneksi.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(new Data.Kategori(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
        }
        return out;
    }

    public static void tambah(Data.Kategori k) throws SQLException {
        try (Connection c = Koneksi.get();
             PreparedStatement ps = c.prepareStatement("INSERT INTO kategori (nama_kategori) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.nama);
            ps.executeUpdate();
            try (ResultSet r = ps.getGeneratedKeys()) { if (r.next()) k.id = r.getInt(1); }
        }
    }

    public static void ubah(Data.Kategori k) throws SQLException {
        try (Connection c = Koneksi.get();
             PreparedStatement ps = c.prepareStatement("UPDATE kategori SET nama_kategori=? WHERE id_kategori=?")) {
            ps.setString(1, k.nama);
            ps.setInt(2, k.id);
            ps.executeUpdate();
        }
    }

    public static void hapus(Data.Kategori k) throws SQLException {
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM kategori WHERE id_kategori=?")) {
            ps.setInt(1, k.id);
            ps.executeUpdate();
        }
    }
}
