package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Laporan peminjaman dengan filter periode (tanggal awal - tanggal akhir). */
public class LaporanPanel extends Page {
    private final Theme.Field tfAwal = new Theme.Field("dd/MM/yyyy");
    private final Theme.Field tfAkhir = new Theme.Field("dd/MM/yyyy");
    private final JLabel lblTrx = angka(), lblBuku = angka(), lblDenda = angka();
    private final DefaultTableModel model = Theme.model("Tanggal", "Nama Anggota", "Judul Buku", "Denda");

    public LaporanPanel() {
        super("Laporan Peminjaman", "Pilih periode untuk melihat ringkasan peminjaman.");
        tfAwal.setText("01/10/2026");
        tfAkhir.setText("31/10/2026");
        tfAwal.setPreferredSize(new Dimension(260, 40));
        tfAkhir.setPreferredSize(new Dimension(260, 40));

        Theme.Btn proses = new Theme.Btn("Proses Laporan", Theme.BLUE, Color.WHITE);
        proses.setPreferredSize(new Dimension(150, 40));
        proses.addActionListener(e -> proses(true));

        JPanel filter = Theme.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filter.add(grup("Tanggal Awal", tfAwal));
        filter.add(grup("Tanggal Akhir", tfAkhir));
        filter.add(grup(" ", proses));

        JPanel stats = Theme.panel(new GridLayout(1, 3, 30, 0));
        stats.setPreferredSize(new Dimension(0, 110));
        stats.add(kartu("Jumlah Transaksi", lblTrx));
        stats.add(kartu("Jumlah Buku Dipinjam", lblBuku));
        stats.add(kartu("Total Denda", lblDenda));

        JPanel atas = Theme.panel(new BorderLayout(0, 22));
        atas.setBorder(new EmptyBorder(0, 0, 26, 0));
        atas.add(filter, BorderLayout.NORTH);
        atas.add(stats, BorderLayout.CENTER);

        JPanel rincian = Theme.panel(new BorderLayout(0, 12));
        rincian.add(Theme.label("Rincian Peminjaman", Font.BOLD, 20, Theme.NAVY), BorderLayout.NORTH);
        rincian.add(Theme.styleTable(new JTable(model), Theme.BG), BorderLayout.CENTER);

        body.add(atas, BorderLayout.NORTH);
        body.add(rincian, BorderLayout.CENTER);
    }

    private static JLabel angka() { return Theme.label("0", Font.BOLD, 32, Theme.BLUE); }

    private JPanel grup(String label, JComponent isi) {
        JPanel p = Theme.panel(new BorderLayout(0, 6));
        p.setBorder(new EmptyBorder(0, 0, 0, 30));
        p.add(Theme.label(label, Font.PLAIN, 13, Theme.MUTED), BorderLayout.NORTH);
        p.add(isi, BorderLayout.CENTER);
        return p;
    }

    private Theme.Round kartu(String judul, JLabel nilai) {
        Theme.Round c = Theme.card(new GridLayout(2, 1, 0, 4));
        c.setBorder(new EmptyBorder(18, 20, 18, 20));
        c.add(Theme.label(judul, Font.PLAIN, 13, Theme.MUTED));
        c.add(nilai);
        return c;
    }

    private void proses(boolean tampilkanPesan) {
        LocalDate awal, akhir;
        try {
            awal = Data.parse(tfAwal.getText());
            akhir = Data.parse(tfAkhir.getText());
        } catch (DateTimeParseException e) {
            if (tampilkanPesan) warn("Format tanggal harus dd/MM/yyyy.");
            return;
        }
        if (akhir.isBefore(awal)) {
            if (tampilkanPesan) warn("Tanggal akhir tidak boleh sebelum tanggal awal.");
            return;
        }
        model.setRowCount(0);
        int trx = 0, buku = 0, denda = 0;
        for (Data.Transaksi t : Data.transaksi) {
            if (t.tanggal.isBefore(awal) || t.tanggal.isAfter(akhir)) continue;
            trx++;
            for (Data.Detail d : t.detail) {
                buku++;
                denda += d.denda;
                model.addRow(new Object[]{Data.FMT.format(t.tanggal), t.anggota.nama, d.buku.judul, Data.rupiah(d.denda)});
            }
        }
        lblTrx.setText(String.valueOf(trx));
        lblBuku.setText(String.valueOf(buku));
        lblDenda.setText(Data.rupiah(denda));
    }

    @Override public void refresh() { proses(false); }
}
