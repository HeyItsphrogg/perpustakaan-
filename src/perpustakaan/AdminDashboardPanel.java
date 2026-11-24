package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AdminDashboardPanel extends Page {
    private final JLabel lblBuku = angka(), lblAnggota = angka(), lblPinjam = angka();
    private final DefaultTableModel model = Theme.model("Tanggal", "Aktivitas", "Keterangan");

    public AdminDashboardPanel() {
        super("Dashboard Admin", "Kelola data dan pantau aktivitas perpustakaan.");

        JPanel stats = Theme.panel(new GridLayout(1, 3, 30, 0));
        stats.setPreferredSize(new Dimension(0, 150));
        stats.add(kartu("Total Buku", lblBuku, "Judul terdaftar"));
        stats.add(kartu("Anggota", lblAnggota, "Anggota aktif"));
        stats.add(kartu("Peminjaman", lblPinjam, "Transaksi bulan ini"));

        JTable table = new JTable(model);
        Theme.Round log = Theme.card(new BorderLayout(0, 14));
        log.setBorder(new EmptyBorder(24, 26, 24, 26));
        log.add(Theme.label("Aktivitas Terbaru", Font.BOLD, 20, Theme.NAVY), BorderLayout.NORTH);
        log.add(Theme.styleTable(table, Color.WHITE), BorderLayout.CENTER);

        JPanel wrap = Theme.panel(new BorderLayout(0, 30));
        wrap.add(stats, BorderLayout.NORTH);
        wrap.add(log, BorderLayout.CENTER);
        body.add(wrap, BorderLayout.CENTER);
    }

    private static JLabel angka() { return Theme.label("0", Font.BOLD, 34, Theme.BLUE); }

    private Theme.Round kartu(String judul, JLabel nilai, String ket) {
        Theme.Round c = Theme.card(new GridLayout(3, 1, 0, 2));
        c.setBorder(new EmptyBorder(22, 24, 22, 24));
        c.add(Theme.label(judul, Font.PLAIN, 15, Theme.MUTED));
        c.add(nilai);
        c.add(Theme.label(ket, Font.PLAIN, 12, Theme.MUTED));
        return c;
    }

    @Override public void refresh() {
        lblBuku.setText(String.valueOf(Data.buku.size()));
        lblAnggota.setText(String.valueOf(Data.anggota.size()));
        LocalDate now = LocalDate.now();
        long bulanIni = Data.transaksi.stream()
                .filter(t -> t.tanggal.getYear() == now.getYear() && t.tanggal.getMonth() == now.getMonth()).count();
        lblPinjam.setText(String.valueOf(bulanIni));

        model.setRowCount(0);
        for (int i = 0; i < Math.min(5, Data.aktivitas.size()); i++) {
            Data.Aktivitas a = Data.aktivitas.get(i);
            model.addRow(new Object[]{Data.FMT.format(a.tanggal), a.jenis, a.keterangan});
        }
    }
}
