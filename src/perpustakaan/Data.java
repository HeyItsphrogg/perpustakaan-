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
        public final String username, password, role;
        public User(String username, String password, String role) {
            this.username = username; this.password = password; this.role = role;
        }
    }

    public static class Kategori {
        public int id; public String nama;
        public Kategori(int id, String nama) { this.id = id; this.nama = nama; }
        @Override public String toString() { return nama; }
    }

    public static class Buku {
        public String judul = "", pengarang = "", kategori = "";
        public int stok;
        public Buku() {}
        public Buku(String judul, String pengarang, String kategori, int stok) {
            this.judul = judul; this.pengarang = pengarang; this.kategori = kategori; this.stok = stok;
        }
        @Override public String toString() { return judul; }
    }

    public static class Anggota {
        public String nama = "", jenis = "Mahasiswa", telepon = "", alamat = "";
        public Anggota() {}
        public Anggota(String nama, String jenis, String telepon, String alamat) {
            this.nama = nama; this.jenis = jenis; this.telepon = telepon; this.alamat = alamat;
        }
        @Override public String toString() { return nama; }
    }

    /** Satu baris detail transaksi (satu eksemplar buku yang dipinjam). */
    public static class Detail {
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

    // ===================== DATA CONTOH =====================
    public static final List<User> users = new ArrayList<>(List.of(
            new User("admin", "admin123", "Admin"),
            new User("petugas01", "petugas123", "Petugas")));

    public static final List<Kategori> kategori = new ArrayList<>(List.of(
            new Kategori(1, "Teknologi"), new Kategori(2, "Pendidikan"),
            new Kategori(3, "Manajemen"), new Kategori(4, "Sastra")));

    public static final List<Buku> buku = new ArrayList<>(List.of(
            new Buku("Pemrograman Java", "Abdul Kadir", "Teknologi", 10),
            new Buku("Basis Data", "Rosa A.S.", "Teknologi", 8),
            new Buku("Matematika Diskrit", "Rinaldi Munir", "Pendidikan", 5),
            new Buku("Algoritma & Pemrograman", "Adi Nugroho", "Teknologi", 12),
            new Buku("Sistem Informasi", "Jogiyanto", "Manajemen", 7)));

    public static final List<Anggota> anggota = new ArrayList<>(List.of(
            new Anggota("Budi Santoso", "Mahasiswa", "081234567890", "Denpasar"),
            new Anggota("Siti Aminah", "Dosen", "081298765432", "Badung"),
            new Anggota("Agus Pratama", "Umum", "085612345678", "Gianyar"),
            new Anggota("Made Putri", "Mahasiswa", "087812345678", "Tabanan")));

    public static final List<Transaksi> transaksi = new ArrayList<>();

    /** Terbaru di indeks 0. */
    public static final List<Aktivitas> aktivitas = new ArrayList<>(List.of(
            new Aktivitas(LocalDate.of(2026, 10, 26), "Peminjaman", "2 buku dipinjam"),
            new Aktivitas(LocalDate.of(2026, 10, 26), "Data buku", "Stok buku diperbarui"),
            new Aktivitas(LocalDate.of(2026, 10, 25), "Pengembalian", "1 buku dikembalikan")));

    static {
        seed(1, LocalDate.of(2026, 10, 5), 0, 0, LocalDate.of(2026, 10, 10));
        seed(2, LocalDate.of(2026, 10, 8), 1, 1, LocalDate.of(2026, 10, 16));
        seed(3, LocalDate.of(2026, 10, 12), 2, 2, LocalDate.of(2026, 10, 21));
        seed(4, LocalDate.now().minusDays(10), 3, 3, null);   // belum kembali -> bisa dicoba di menu Pengembalian
    }

    private static void seed(int id, LocalDate tgl, int a, int b, LocalDate kembali) {
        Transaksi t = new Transaksi(id, tgl, anggota.get(a));
        Detail d = new Detail(buku.get(b));
        if (kembali != null) {
            d.tglKembali = kembali;
            d.denda = hariTerlambat(t, kembali) * DENDA_PER_HARI;
        }
        t.detail.add(d);
        transaksi.add(t);
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
