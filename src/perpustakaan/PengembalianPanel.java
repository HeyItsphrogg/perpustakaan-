package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Vector;

public class PengembalianPanel extends Page {
    private final JComboBox<Object> cbTrx = Theme.combo(), cbBuku = Theme.combo();
    private final Theme.Field tfTanggal = new Theme.Field("dd/MM/yyyy");
    private final JLabel lblHari = Theme.label("Hari terlambat: 0 hari", Font.PLAIN, 13, Theme.TEXT);
    private final JLabel lblDenda = Theme.label("Estimasi denda: Rp0", Font.BOLD, 22, Theme.BLUE);

    public PengembalianPanel() {
        super("Pengembalian Buku", "Catat buku kembali dan periksa denda keterlambatan.");

        Theme.Round card = Theme.boxCard();
        card.setBorder(new EmptyBorder(26, 34, 26, 34));
        card.setPreferredSize(new Dimension(660, 500));
        JLabel judul = Theme.label("Form Pengembalian", Font.BOLD, 20, Theme.NAVY);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(judul);
        card.add(Box.createVerticalStrut(14));
        Theme.addField(card, "Transaksi Peminjaman", cbTrx);
        Theme.addField(card, "Buku yang Dikembalikan", cbBuku);
        Theme.addField(card, "Tanggal Kembali", tfTanggal);

        Theme.Round info = new Theme.Round(new GridLayout(2, 1, 0, 4), 14, new Color(0xEAF1FD), null);
        info.setBorder(new EmptyBorder(14, 20, 14, 20));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        info.add(lblHari);
        info.add(lblDenda);
        card.add(info);
        card.add(Box.createVerticalStrut(18));

        Theme.Btn simpan = new Theme.Btn("Simpan Pengembalian", Theme.BLUE, Color.WHITE);
        simpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        simpan.addActionListener(e -> simpan());
        card.add(simpan);

        JPanel row = Theme.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.add(card);
        body.add(row, BorderLayout.NORTH);

        cbTrx.addActionListener(e -> { muatBuku(); hitung(); });
        Theme.onChange(tfTanggal, this::hitung);
    }

    private void muatBuku() {
        Vector<Object> v = new Vector<>();
        v.add("Pilih buku...");
        if (cbTrx.getSelectedItem() instanceof Data.Transaksi t) {
            for (Data.Detail d : t.detail) if (d.tglKembali == null) v.add(d);
        }
        cbBuku.setModel(new DefaultComboBoxModel<>(v));
    }

    private void hitung() {
        try {
            LocalDate kembali = Data.parse(tfTanggal.getText());
            int hari = cbTrx.getSelectedItem() instanceof Data.Transaksi t ? Data.hariTerlambat(t, kembali) : 0;
            lblHari.setText("Hari terlambat: " + hari + " hari");
            lblDenda.setText("Estimasi denda: " + Data.rupiah(hari * Data.DENDA_PER_HARI));
        } catch (DateTimeParseException e) {   // format tanggal belum benar
            lblHari.setText("Hari terlambat: -");
            lblDenda.setText("Estimasi denda: -");
        }
    }

    private void simpan() {
        if (!(cbTrx.getSelectedItem() instanceof Data.Transaksi t) || !(cbBuku.getSelectedItem() instanceof Data.Detail d)) {
            warn("Pilih transaksi dan buku yang dikembalikan.");
            return;
        }
        LocalDate kembali;
        try {
            kembali = Data.parse(tfTanggal.getText());
        } catch (DateTimeParseException e) {
            warn("Format tanggal harus dd/MM/yyyy, contoh 26/10/2026.");
            return;
        }
        try {
            if (kembali.isBefore(t.tanggal)) {
                throw new BusinessRuleException("Tanggal kembali tidak boleh sebelum tanggal pinjam ("
                        + Data.FMT.format(t.tanggal) + ").");
            }
        } catch (BusinessRuleException e) {
            warn(e.getMessage());
            return;
        }
        d.tglKembali = kembali;
        d.denda = Data.hariTerlambat(t, kembali) * Data.DENDA_PER_HARI;
        d.buku.stok++;
        Data.catat("Pengembalian", "1 buku dikembalikan" + (d.denda > 0 ? " (denda " + Data.rupiah(d.denda) + ")" : ""));
        refresh();
        info("Pengembalian tersimpan." + (d.denda > 0 ? "\nDenda: " + Data.rupiah(d.denda) : ""));
    }

    @Override public void refresh() {
        Vector<Object> v = new Vector<>();
        v.add("Pilih transaksi...");
        for (Data.Transaksi t : Data.transaksi) if (t.masihAdaYangDipinjam()) v.add(t);
        cbTrx.setModel(new DefaultComboBoxModel<>(v));
        muatBuku();
        tfTanggal.setText(Data.FMT.format(LocalDate.now()));
        hitung();
    }
}
