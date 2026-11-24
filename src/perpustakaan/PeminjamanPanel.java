package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/** Transaksi master-detail: satu header (anggota + tanggal) berisi banyak buku di keranjang. */
public class PeminjamanPanel extends Page {
    private final JComboBox<Object> cbAnggota = Theme.combo(), cbBuku = Theme.combo();
    private final Theme.Field tfJenis = new Theme.Field("Terisi otomatis");
    private final Theme.Field tfStok = new Theme.Field("Terisi otomatis");
    private final Map<Data.Buku, Integer> keranjang = new LinkedHashMap<>();
    private final DefaultTableModel model = Theme.model("No", "Judul Buku", "Jumlah");
    private final JLabel lblTotal = Theme.label("Total buku: 0", Font.BOLD, 24, Theme.NAVY);

    public PeminjamanPanel() {
        super("Peminjaman Buku", "Pilih anggota dan masukkan buku ke keranjang.");
        tfJenis.setEditable(false);
        tfStok.setEditable(false);

        // ----- kartu kiri: form -----
        Theme.Round form = Theme.boxCard();
        form.setBorder(new EmptyBorder(24, 28, 24, 28));
        JLabel t1 = Theme.label("Form Peminjaman", Font.BOLD, 20, Theme.NAVY);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(t1);
        form.add(Box.createVerticalStrut(14));
        Theme.addField(form, "Anggota", cbAnggota);
        Theme.addField(form, "Jenis Anggota", tfJenis);
        Theme.addField(form, "Buku", cbBuku);
        Theme.addField(form, "Stok Tersedia", tfStok);
        Theme.Btn tambah = new Theme.Btn("Tambah ke Keranjang", Theme.BLUE, Color.WHITE);
        tambah.setAlignmentX(Component.LEFT_ALIGNMENT);
        tambah.addActionListener(e -> tambahKeKeranjang());
        form.add(tambah);

        // ----- kartu kanan: keranjang -----
        Theme.Round kanan = Theme.card(new BorderLayout(0, 16));
        kanan.setBorder(new EmptyBorder(24, 28, 24, 28));
        kanan.add(Theme.label("Keranjang Peminjaman", Font.BOLD, 20, Theme.NAVY), BorderLayout.NORTH);
        JTable table = new JTable(model);
        kanan.add(Theme.styleTable(table, Color.WHITE), BorderLayout.CENTER);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setMaxWidth(110);

        JPanel bawah = Theme.panel(null);
        bawah.setLayout(new BoxLayout(bawah, BoxLayout.Y_AXIS));
        Theme.Btn simpan = new Theme.Btn("Simpan Transaksi", Theme.BLUE, Color.WHITE);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        simpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        simpan.addActionListener(e -> simpan());
        bawah.add(lblTotal);
        bawah.add(Box.createVerticalStrut(14));
        bawah.add(simpan);
        kanan.add(bawah, BorderLayout.SOUTH);

        JPanel row = Theme.panel(new GridLayout(1, 2, 28, 0));
        row.setPreferredSize(new Dimension(0, 540));
        row.add(form);
        row.add(kanan);
        body.add(row, BorderLayout.NORTH);

        cbAnggota.addActionListener(e -> infoAnggota());
        cbBuku.addActionListener(e -> infoBuku());
    }

    private void infoAnggota() {
        tfJenis.setText(cbAnggota.getSelectedItem() instanceof Data.Anggota a ? a.jenis : "");
    }

    private void infoBuku() {
        tfStok.setText(cbBuku.getSelectedItem() instanceof Data.Buku b
                ? String.valueOf(b.stok - keranjang.getOrDefault(b, 0)) : "");
    }

    /**
     * Aturan bisnis 1: stok buku harus tersedia.
     * Aturan bisnis 2: jumlah buku per transaksi dibatasi menurut jenis anggota.
     */
    private void cekAturan(Data.Anggota a, Data.Buku baru) throws BusinessRuleException {
        if (baru != null && baru.stok - keranjang.getOrDefault(baru, 0) <= 0) {
            throw new BusinessRuleException("Stok buku \"" + baru.judul + "\" tidak tersedia.");
        }
        int total = keranjang.values().stream().mapToInt(Integer::intValue).sum() + (baru == null ? 0 : 1);
        int batas = Data.batasPinjam(a.jenis);
        if (total > batas) {
            throw new BusinessRuleException("Anggota " + a.jenis + " maksimal meminjam " + batas + " buku per transaksi.");
        }
    }

    private void tambahKeKeranjang() {
        if (!(cbAnggota.getSelectedItem() instanceof Data.Anggota a) || !(cbBuku.getSelectedItem() instanceof Data.Buku b)) {
            warn("Pilih anggota dan buku terlebih dahulu.");
            return;
        }
        try {
            cekAturan(a, b);
        } catch (BusinessRuleException e) {
            warn(e.getMessage());
            return;
        }
        keranjang.merge(b, 1, Integer::sum);
        tampilKeranjang();
        infoBuku();
    }

    private void simpan() {
        if (!(cbAnggota.getSelectedItem() instanceof Data.Anggota a)) {
            warn("Pilih anggota terlebih dahulu.");
            return;
        }
        if (keranjang.isEmpty()) {
            warn("Keranjang masih kosong.");
            return;
        }
        try {
            cekAturan(a, null);
        } catch (BusinessRuleException e) {
            warn(e.getMessage());
            return;
        }
        Data.Transaksi t = new Data.Transaksi(Data.nextTrxId(), LocalDate.now(), a);
        int total = 0;
        for (Map.Entry<Data.Buku, Integer> e : keranjang.entrySet()) {
            for (int i = 0; i < e.getValue(); i++) t.detail.add(new Data.Detail(e.getKey()));
            e.getKey().stok -= e.getValue();
            total += e.getValue();
        }
        Data.transaksi.add(t);
        Data.catat("Peminjaman", total + " buku dipinjam");
        keranjang.clear();
        cbAnggota.setSelectedIndex(0);
        cbBuku.setSelectedIndex(0);
        refresh();
        info("Transaksi berhasil disimpan.\nJatuh tempo: " + Data.FMT.format(t.jatuhTempo()));
    }

    private void tampilKeranjang() {
        model.setRowCount(0);
        int no = 1, total = 0;
        for (Map.Entry<Data.Buku, Integer> e : keranjang.entrySet()) {
            model.addRow(new Object[]{no++, e.getKey().judul, e.getValue()});
            total += e.getValue();
        }
        lblTotal.setText("Total buku: " + total);
    }

    @Override public void refresh() {
        Object pilihA = cbAnggota.getSelectedItem(), pilihB = cbBuku.getSelectedItem();
        Vector<Object> a = new Vector<>();
        a.add("Pilih anggota...");
        a.addAll(Data.anggota);
        Vector<Object> b = new Vector<>();
        b.add("Pilih buku...");
        b.addAll(Data.buku);
        cbAnggota.setModel(new DefaultComboBoxModel<>(a));
        cbBuku.setModel(new DefaultComboBoxModel<>(b));
        if (a.contains(pilihA)) cbAnggota.setSelectedItem(pilihA);
        if (b.contains(pilihB)) cbBuku.setSelectedItem(pilihB);
        infoAnggota();
        infoBuku();
        tampilKeranjang();
    }
}
