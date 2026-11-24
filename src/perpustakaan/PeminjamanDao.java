package perpustakaan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** peminjaman (header) + detail_peminjaman (detail). */
public final class PeminjamanDao {
    private PeminjamanDao() {}

    /** Baca semua transaksi. Anggota dan Buku harus sudah dimuat (objek yang sama dipakai ulang). */
    public static List<Data.Transaksi> semua() throws SQLException {
        Map<Integer, Data.Anggota> ma = new HashMap<>();
        for (Data.Anggota a : Data.anggota) ma.put(a.id, a);
        Map<Integer, Data.Buku> mb = new HashMap<>();
        for (Data.Buku b : Data.buku) mb.put(b.id, b);

        Map<Integer, Data.Transaksi> mt = new LinkedHashMap<>();
        try (Connection c = Koneksi.get()) {
            String h = "SELECT id_peminjaman, id_anggota, tgl_pinjam FROM peminjaman ORDER BY id_peminjaman";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(h)) {
                while (rs.next()) {
                    int id = rs.getInt("id_peminjaman");
                    mt.put(id, new Data.Transaksi(id, rs.getDate("tgl_pinjam").toLocalDate(),
                            ma.get(rs.getInt("id_anggota"))));
                }
            }
            String d = "SELECT id_detail, id_peminjaman, id_buku, tgl_kembali, denda FROM detail_peminjaman ORDER BY id_detail";
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(d)) {
                while (rs.next()) {
                    Data.Detail x = new Data.Detail(mb.get(rs.getInt("id_buku")));
                    x.id = rs.getInt("id_detail");
                    Date k = rs.getDate("tgl_kembali");
                    x.tglKembali = (k == null) ? null : k.toLocalDate();
                    x.denda = rs.getInt("denda");
                    mt.get(rs.getInt("id_peminjaman")).detail.add(x);
                }
            }
        }
        return new ArrayList<>(mt.values());
    }

    /** Simpan satu transaksi pinjam. Stok di database dikurangi di transaksi yang sama. */
    public static Data.Transaksi simpan(Data.Anggota a, Data.User petugas, Map<Data.Buku, Integer> keranjang)
            throws SQLException {
        LocalDate tgl = LocalDate.now();
        LocalDate tempo = tgl.plusDays(Data.MASA_PINJAM_HARI);
        try (Connection c = Koneksi.get()) {
            c.setAutoCommit(false);
            try {
                int idPinjam;
                String h = "INSERT INTO peminjaman (id_anggota, id_user, tgl_pinjam, tgl_jatuh_tempo) VALUES (?,?,?,?)";
                try (PreparedStatement ps = c.prepareStatement(h, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, a.id);
                    ps.setInt(2, petugas.id);
                    ps.setDate(3, Date.valueOf(tgl));
                    ps.setDate(4, Date.valueOf(tempo));
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); idPinjam = k.getInt(1); }
                }

                Data.Transaksi t = new Data.Transaksi(idPinjam, tgl, a);
                for (Map.Entry<Data.Buku, Integer> e : keranjang.entrySet()) {
                    Data.Buku b = e.getKey();
                    int qty = e.getValue();

                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE buku SET stok = stok - ? WHERE id_buku = ? AND stok >= ?")) {
                        ps.setInt(1, qty); ps.setInt(2, b.id); ps.setInt(3, qty);
                        if (ps.executeUpdate() == 0)
                            throw new SQLException("Stok buku \"" + b.judul + "\" tidak mencukupi.");
                    }
                    for (int i = 0; i < qty; i++) {
                        try (PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO detail_peminjaman (id_peminjaman, id_buku) VALUES (?,?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            ps.setInt(1, idPinjam); ps.setInt(2, b.id);
                            ps.executeUpdate();
                            Data.Detail d = new Data.Detail(b);
                            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); d.id = k.getInt(1); }
                            t.detail.add(d);
                        }
                    }
                }
                c.commit();
                return t;
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    /** Catat pengembalian satu buku: isi tgl_kembali + denda, lalu stok buku + 1. */
    public static void kembali(Data.Detail d, LocalDate tgl, int denda) throws SQLException {
        try (Connection c = Koneksi.get()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE detail_peminjaman SET tgl_kembali=?, denda=? WHERE id_detail=?")) {
                    ps.setDate(1, Date.valueOf(tgl)); ps.setInt(2, denda); ps.setInt(3, d.id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE buku SET stok = stok + 1 WHERE id_buku=?")) {
                    ps.setInt(1, d.buku.id);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            }
        }
    }
}
