package perpustakaan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO tabel buku (join ke kategori). Pola yang sama dipakai untuk AnggotaDao & KategoriDao. */
public final class BukuDao {
    private BukuDao() {}

    public static List<Data.Buku> semua() throws SQLException {
        String sql = "SELECT b.id_buku, b.judul, b.pengarang, k.nama_kategori, b.stok " +
                     "FROM buku b JOIN kategori k ON k.id_kategori = b.id_kategori ORDER BY b.id_buku";
        List<Data.Buku> out = new ArrayList<>();
        try (Connection c = Koneksi.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Data.Buku b = new Data.Buku(rs.getString("judul"), rs.getString("pengarang"),
                        rs.getString("nama_kategori"), rs.getInt("stok"));
                b.id = rs.getInt("id_buku");
                out.add(b);
            }
        }
        return out;
    }

    public static void tambah(Data.Buku b) throws SQLException {
        String sql = "INSERT INTO buku (judul, pengarang, id_kategori, stok) " +
                     "VALUES (?, ?, (SELECT id_kategori FROM kategori WHERE nama_kategori = ?), ?)";
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.judul);
            ps.setString(2, b.pengarang);
            ps.setString(3, b.kategori);
            ps.setInt(4, b.stok);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) b.id = k.getInt(1); }
        }
    }

    public static void ubah(Data.Buku b) throws SQLException {
        String sql = "UPDATE buku SET judul=?, pengarang=?, " +
                     "id_kategori=(SELECT id_kategori FROM kategori WHERE nama_kategori=?), stok=? WHERE id_buku=?";
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.judul);
            ps.setString(2, b.pengarang);
            ps.setString(3, b.kategori);
            ps.setInt(4, b.stok);
            ps.setInt(5, b.id);
            ps.executeUpdate();
        }
    }

    public static void hapus(Data.Buku b) throws SQLException {
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM buku WHERE id_buku=?")) {
            ps.setInt(1, b.id);
            ps.executeUpdate();
        }
    }
}
