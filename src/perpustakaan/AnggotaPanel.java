package perpustakaan;

import javax.swing.*;

public class AnggotaPanel extends CrudPanel<Data.Anggota> {
    public AnggotaPanel() {
        super("Data Anggota", "Kelola informasi anggota perpustakaan.", "Cari nama anggota...",
                "Tambah Anggota", "Data anggota", Data.anggota, "Nama Anggota", "Jenis", "Telepon", "Alamat");
    }

    @Override protected Object[] toRow(Data.Anggota a) {
        return new Object[]{a.nama, a.jenis, a.telepon, a.alamat};
    }

    @Override protected boolean matches(Data.Anggota a, String q) {
        return a.nama.toLowerCase().contains(q);
    }

    @Override protected void cekHapus(Data.Anggota a) throws BusinessRuleException {
        boolean masihPinjam = Data.transaksi.stream().anyMatch(t -> t.anggota == a && t.masihAdaYangDipinjam());
        if (masihPinjam) {
            throw new BusinessRuleException("Anggota \"" + a.nama + "\" masih memiliki buku yang belum dikembalikan.");
        }
        if (Data.transaksi.stream().anyMatch(t -> t.anggota == a)) {
            throw new BusinessRuleException("Anggota \"" + a.nama + "\" sudah punya riwayat peminjaman, tidak dapat dihapus.");
        }
    }

    @Override protected void simpanDb(Data.Anggota a, boolean baru) throws Exception {
        if (baru) AnggotaDao.tambah(a); else AnggotaDao.ubah(a);
    }

    @Override protected void hapusDb(Data.Anggota a) throws Exception { AnggotaDao.hapus(a); }

    @Override protected Data.Anggota form(Data.Anggota a) {
        Theme.Field nama = new Theme.Field("Nama lengkap");
        Theme.Field telepon = new Theme.Field("Nomor telepon");
        Theme.Field alamat = new Theme.Field("Alamat");
        JComboBox<Object> jenis = Theme.combo("Mahasiswa", "Dosen", "Umum");
        if (a != null) {
            nama.setText(a.nama);
            jenis.setSelectedItem(a.jenis);
            telepon.setText(a.telepon);
            alamat.setText(a.alamat);
        }
        while (ask(a == null ? "Tambah Anggota" : "Ubah Anggota",
                new String[]{"Nama Anggota", "Jenis", "Telepon", "Alamat"}, nama, jenis, telepon, alamat)) {
            if (nama.getText().isBlank() || alamat.getText().isBlank()) {
                warn("Nama dan alamat wajib diisi.");
                continue;
            }
            if (!telepon.getText().trim().matches("\\d{8,15}")) {
                warn("Telepon harus berupa angka 8-15 digit.");
                continue;
            }
            Data.Anggota r = (a != null) ? a : new Data.Anggota();
            r.nama = nama.getText().trim();
            r.jenis = jenis.getSelectedItem().toString();
            r.telepon = telepon.getText().trim();
            r.alamat = alamat.getText().trim();
            return r;
        }
        return null;
    }
}
