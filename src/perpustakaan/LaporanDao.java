package perpustakaan;

import java.sql.*;
import java.time.LocalDate;

/** Simpan hasil laporan ke tabel laporan (tabel tambahan). */
public final class LaporanDao {
    private LaporanDao() {}

    public static void simpan(Data.User user, LocalDate awal, LocalDate akhir,
                              int totalTransaksi, int totalBuku, int totalDenda) throws SQLException {
        String sql = "INSERT INTO laporan (id_user, tgl_awal, tgl_akhir, total_transaksi, total_buku, total_denda) VALUES (?,?,?,?,?,?)";
        try (Connection c = Koneksi.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, user.id);
            ps.setDate(2, Date.valueOf(awal));
            ps.setDate(3, Date.valueOf(akhir));
            ps.setInt(4, totalTransaksi);
            ps.setInt(5, totalBuku);
            ps.setInt(6, totalDenda);
            ps.executeUpdate();
        }
    }
}
