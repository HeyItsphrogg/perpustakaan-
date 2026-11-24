package perpustakaan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Data sementara (in-memory) agar UI bisa langsung dicoba.
 * Nanti ganti isi list-list di bawah dengan query database (JDBC / DAO).
 *
 * Akun demo:  admin / admin123   |   petugas01 / petugas123
 */
public final class Data {
    private Data() {}

    public static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final int MASA_PINJAM_HARI = 7;
    public static final int DENDA_PER_HARI = 5000;

    // ===================== MODEL =====================
    public static class User {
        public final int id;                                   // id_user
        public final String username, password, nama, role;   // role = kolom "peran"
        public User(int id, String username, String password, String nama, String role) {
            this.id = id; this.username = username; this.password = password; this.nama = nama; this.role = role;
        }
    }

    public static class Kategori {
        public int id; public String nama;
        public Kategori(int id, String nama) { this.id = id; this.nama = nama; }
        @Override public String toString() { return nama; }
    }

    public static class Buku {
        public int id;   // id_buku
        public String judul = "", pengarang = "", kategori = "";
        public int stok;
        public Buku() {}
        public Buku(String judul, String pengarang, String kategori, int stok) {
            this.judul = judul; this.pengarang = pengarang; this.kategori = kategori; this.stok = stok;
        }
        @Override public String toString() { return judul; }
    }

    public static class Anggota {
        public int id;   // id_anggota
        public String nama = "", jenis = "Mahasiswa", telepon = "", alamat = "";
        public Anggota() {}
        public Anggota(String nama, String jenis, String telepon, String alamat) {
            this.nama = nama; this.jenis = jenis; this.telepon = telepon; this.alamat = alamat;
        }
        @Override public String toString() { return nama; }
    }

    /** Satu baris detail transaksi (satu eksemplar buku yang dipinjam). */
    public static class Detail {
        public int id;   // id_detail
        public final Buku buku;
        public LocalDate tglKembali;   // null = belum dikembalikan
        public int denda;
        public Detail(Buku buku) { this.buku = buku; }
        @Override public String toString() { return buku.judul; }
    }

    /** Header transaksi peminjaman (master) berisi banyak Detail. */
    public static class Transaksi {
        public final int id;
        public final LocalDate tanggal;
        public final Anggota anggota;
        public final List<Detail> detail = new ArrayList<>();
        public Transaksi(int id, LocalDate tanggal, Anggota anggota) {
            this.id = id; this.tanggal = tanggal; this.anggota = anggota;
        }
        public LocalDate jatuhTempo() { return tanggal.plusDays(MASA_PINJAM_HARI); }
        public boolean masihAdaYangDipinjam() { return detail.stream().anyMatch(d -> d.tglKembali == null); }
        @Override public String toString() {
            return String.format("TRX-%03d • %s • %s", id, anggota.nama, FMT.format(tanggal));
        }
    }

    public static class Aktivitas {
        public final LocalDate tanggal; public final String jenis, keterangan;
        public Aktivitas(LocalDate tanggal, String jenis, String keterangan) {
            this.tanggal = tanggal; this.jenis = jenis; this.keterangan = keterangan;
        }
    }

    // ===================== DATA (diisi dari database lewat muat()) =====================
    public static final List<User> users = new ArrayList<>();
    public static final List<Kategori> kategori = new ArrayList<>();
    public static final List<Buku> buku = new ArrayList<>();
    public static final List<Anggota> anggota = new ArrayList<>();
    public static final List<Transaksi> transaksi = new ArrayList<>();

    /** Terbaru di indeks 0. Hanya di memori (skema database tidak punya tabel aktivitas). */
    public static final List<Aktivitas> aktivitas = new ArrayList<>();

    /** User yang sedang login; dipakai sebagai id_user pada peminjaman dan laporan. */
    public static User userLogin;

    /** Baca semua data dari MySQL. Urutan penting: kategori -> buku -> anggota -> peminjaman. */
    public static void muat() throws java.sql.SQLException {
        users.clear();     users.addAll(UserDao.semua());
        kategori.clear();  kategori.addAll(KategoriDao.semua());
        buku.clear();      buku.addAll(BukuDao.semua());
        anggota.clear();   anggota.addAll(AnggotaDao.semua());
        transaksi.clear(); transaksi.addAll(PeminjamanDao.semua());
    }

    // ===================== UTILITAS =====================
    public static int nextTrxId() { return transaksi.stream().mapToInt(t -> t.id).max().orElse(0) + 1; }
    public static int nextKategoriId() { return kategori.stream().mapToInt(k -> k.id).max().orElse(0) + 1; }

    public static void catat(String jenis, String keterangan) {
        aktivitas.add(0, new Aktivitas(LocalDate.now(), jenis, keterangan));
    }

    public static int batasPinjam(String jenisAnggota) {
        switch (jenisAnggota) {
            case "Dosen": return 5;
            case "Mahasiswa": return 3;
            default: return 2;
        }
    }

    public static int hariTerlambat(Transaksi t, LocalDate kembali) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(t.jatuhTempo(), kembali));
    }

    /** @throws java.time.format.DateTimeParseException jika format bukan dd/MM/yyyy */
    public static LocalDate parse(String teks) { return LocalDate.parse(teks.trim(), FMT); }

    public static String rupiah(int nilai) {
        return "Rp" + String.format(Locale.US, "%,d", nilai).replace(',', '.');
    }
}
