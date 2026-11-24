package perpustakaan;

import javax.swing.*;

public class BukuPanel extends CrudPanel<Data.Buku> {
    public BukuPanel() {
        super("Data Buku", "Tambah, cari, ubah, atau hapus data buku.", "Cari judul atau pengarang...",
                "Tambah Buku", "Data buku", Data.buku, "Judul Buku", "Pengarang", "Kategori", "Stok");
    }

    @Override protected Object[] toRow(Data.Buku b) {
        return new Object[]{b.judul, b.pengarang, b.kategori, b.stok};
    }

    @Override protected boolean matches(Data.Buku b, String q) {
        return b.judul.toLowerCase().contains(q) || b.pengarang.toLowerCase().contains(q);
    }

    @Override protected Data.Buku form(Data.Buku b) {
        Theme.Field judul = new Theme.Field("Judul buku");
        Theme.Field pengarang = new Theme.Field("Nama pengarang");
        Theme.Field stok = new Theme.Field("Jumlah stok");
        JComboBox<Object> kat = Theme.combo(Data.kategori.stream().map(k -> k.nama).toArray());
        if (b != null) {
            judul.setText(b.judul);
            pengarang.setText(b.pengarang);
            kat.setSelectedItem(b.kategori);
            stok.setText(String.valueOf(b.stok));
        }
        while (ask(b == null ? "Tambah Buku" : "Ubah Buku",
                new String[]{"Judul Buku", "Pengarang", "Kategori", "Stok"}, judul, pengarang, kat, stok)) {
            try {
                if (judul.getText().isBlank() || pengarang.getText().isBlank()) {
                    warn("Judul dan pengarang wajib diisi.");
                    continue;
                }
                if (kat.getSelectedItem() == null) {
                    warn("Tambahkan data kategori terlebih dahulu.");
                    return null;
                }
                int s = Integer.parseInt(stok.getText().trim());
                if (s < 0) throw new NumberFormatException();
                Data.Buku r = (b != null) ? b : new Data.Buku();
                r.judul = judul.getText().trim();
                r.pengarang = pengarang.getText().trim();
                r.kategori = kat.getSelectedItem().toString();
                r.stok = s;
                return r;
            } catch (NumberFormatException e) {
                warn("Stok harus berupa angka bulat 0 atau lebih.");
            }
        }
        return null;
    }
}
