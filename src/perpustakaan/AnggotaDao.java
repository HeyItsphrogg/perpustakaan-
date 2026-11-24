package perpustakaan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class AnggotaDao {
    private AnggotaDao() {}

    public static List<Data.Anggota> semua() throws SQLException {
        List<Data.Anggota> out = new ArrayList<>();
        String sql = "SELECT id_anggota, nama, jenis, telepon, alamat FROM anggota ORDER BY id_anggota";
        try (Connection c = Koneksi.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Data.Anggota a = new Data.Anggota(rs.getString("nama"), rs.getString("jenis"),
                        rs.getString("telepon"), rs.getString("alamat"));
                a.id = rs.getInt("id_anggota");
                out.add(a);
            }
        }
        return out;
    }

    public static void tambah(Data.Anggota a) throws SQLException {
        String sql = "INSERT INTO anggota (nama, jenis, alamat, telepon) VALUES (?,?,?,?)";
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.nama);
            ps.setString(2, a.jenis);
            ps.setString(3, a.alamat);
            ps.setString(4, a.telepon);
            ps.executeUpdate();
            try (ResultSet r = ps.getGeneratedKeys()) { if (r.next()) a.id = r.getInt(1); }
        }
    }

    public static void ubah(Data.Anggota a) throws SQLException {
        String sql = "UPDATE anggota SET nama=?, jenis=?, alamat=?, telepon=? WHERE id_anggota=?";
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.nama);
            ps.setString(2, a.jenis);
            ps.setString(3, a.alamat);
            ps.setString(4, a.telepon);
            ps.setInt(5, a.id);
            ps.executeUpdate();
        }
    }

    public static void hapus(Data.Anggota a) throws SQLException {
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM anggota WHERE id_anggota=?")) {
            ps.setInt(1, a.id);
            ps.executeUpdate();
        }
    }
}
