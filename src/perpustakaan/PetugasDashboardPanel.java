package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PetugasDashboardPanel extends Page {
    public PetugasDashboardPanel(Consumer<String> buka) {
        super("Dashboard Petugas", "Pilih layanan untuk melanjutkan pekerjaan.");
        JPanel row = Theme.panel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        row.add(kartu("↗", "Peminjaman", "Catat buku yang dipinjam", () -> buka.accept("Peminjaman")));
        row.add(kartu("↩", "Pengembalian", "Proses buku yang kembali", () -> buka.accept("Pengembalian")));
        body.add(row, BorderLayout.NORTH);
    }

    private Theme.Round kartu(String ikon, String judul, String ket, Runnable aksi) {
        Theme.Round c = Theme.boxCard();
        c.setBorder(new EmptyBorder(30, 34, 30, 34));
        c.setPreferredSize(new Dimension(420, 270));

        Theme.Btn tombol = new Theme.Btn("Buka Menu", Theme.BLUE, Color.WHITE);
        tombol.addActionListener(e -> aksi.run());

        JLabel i = Theme.label(ikon, Font.BOLD, 30, Theme.BLUE);
        JLabel j = Theme.label(judul, Font.BOLD, 22, Theme.NAVY);
        JLabel k = Theme.label(ket, Font.PLAIN, 16, Theme.MUTED);
        for (JComponent x : new JComponent[]{i, j, k, tombol}) x.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.add(i);
        c.add(Box.createVerticalStrut(16));
        c.add(j);
        c.add(Box.createVerticalStrut(14));
        c.add(k);
        c.add(Box.createVerticalStrut(22));
        c.add(tombol);
        return c;
    }
}
