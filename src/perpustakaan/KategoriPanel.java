package perpustakaan;

public class KategoriPanel extends CrudPanel<Data.Kategori> {
    public KategoriPanel() {
        super("Data Kategori", "Kelola kategori untuk pengelompokan buku.", "Cari nama kategori...",
                "Tambah Kategori", "Data kategori", Data.kategori, "ID Kategori", "Nama Kategori");
    }

    @Override protected Object[] toRow(Data.Kategori k) {
        return new Object[]{k.id, k.nama};
    }

    @Override protected boolean matches(Data.Kategori k, String q) {
        return k.nama.toLowerCase().contains(q);
    }

    @Override protected void cekHapus(Data.Kategori k) throws BusinessRuleException {
        if (Data.buku.stream().anyMatch(b -> b.kategori.equals(k.nama))) {
            throw new BusinessRuleException("Kategori \"" + k.nama + "\" masih dipakai data buku, tidak dapat dihapus.");
        }
    }

    @Override protected void simpanDb(Data.Kategori k, boolean baru) throws Exception {
        if (baru) KategoriDao.tambah(k); else KategoriDao.ubah(k);
    }

    @Override protected void hapusDb(Data.Kategori k) throws Exception { KategoriDao.hapus(k); }

    @Override protected Data.Kategori form(Data.Kategori k) {
        Theme.Field nama = new Theme.Field("Nama kategori");
        if (k != null) nama.setText(k.nama);
        while (ask(k == null ? "Tambah Kategori" : "Ubah Kategori", new String[]{"Nama Kategori"}, nama)) {
            String n = nama.getText().trim();
            if (n.isEmpty()) {
                warn("Nama kategori wajib diisi.");
                continue;
            }
            if (Data.kategori.stream().anyMatch(x -> x != k && x.nama.equalsIgnoreCase(n))) {
                warn("Kategori \"" + n + "\" sudah ada.");
                continue;
            }
            if (k == null) return new Data.Kategori(0, n);
            String lama = k.nama;
            k.nama = n;
            Data.buku.forEach(b -> { if (b.kategori.equals(lama)) b.kategori = n; });
            return k;
        }
        return null;
    }
}
