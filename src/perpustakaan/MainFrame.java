package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Jendela utama: sidebar di kiri (menu berbeda untuk Admin dan Petugas) + halaman di kanan. */
public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Map<String, Page> pages = new LinkedHashMap<>();
    private final Map<String, Theme.Btn> nav = new LinkedHashMap<>();

    public MainFrame(Data.User user) {
        super("Sistem Informasi Perpustakaan - " + user.role);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- menu sesuai role -----
        if (user.role.equals("Admin")) {
            pages.put("Dashboard", new AdminDashboardPanel());
            pages.put("Buku", new BukuPanel());
            pages.put("Anggota", new AnggotaPanel());
            pages.put("Kategori", new KategoriPanel());
            pages.put("Laporan", new LaporanPanel());
        } else {
            pages.put("Dashboard", new PetugasDashboardPanel(this::open));
            pages.put("Peminjaman", new PeminjamanPanel());
            pages.put("Pengembalian", new PengembalianPanel());
        }

        // ----- sidebar -----
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Theme.NAVY);
        side.setBorder(new EmptyBorder(26, 16, 16, 16));
        side.setPreferredSize(new Dimension(250, 0));

        JLabel brand = Theme.label("▣  PERPUSTAKAAN", Font.BOLD, 17, Color.WHITE);
        JLabel sub = Theme.label("Sistem Informasi", Font.PLAIN, 12, new Color(0xA9B8CE));
        brand.setBorder(new EmptyBorder(0, 10, 0, 0));
        sub.setBorder(new EmptyBorder(0, 10, 0, 0));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);
        side.add(Box.createVerticalStrut(2));
        side.add(sub);
        side.add(Box.createVerticalStrut(28));

        content.setBackground(Theme.BG);
        for (String name : pages.keySet()) {
            Theme.Btn b = navButton(name);
            b.addActionListener(e -> open(name));
            nav.put(name, b);
            side.add(b);
            side.add(Box.createVerticalStrut(6));
            content.add(pages.get(name), name);
        }
        Theme.Btn out = navButton("Logout");
        out.addActionListener(e -> logout());
        side.add(out);

        setLayout(new BorderLayout());
        add(side, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        open("Dashboard");
        setSize(1240, 760);
        setMinimumSize(new Dimension(1050, 640));
        setLocationRelativeTo(null);
    }

    private Theme.Btn navButton(String text) {
        Theme.Btn b = new Theme.Btn(text, Theme.NAVY, Color.WHITE);
        b.setFont(Theme.font(Font.PLAIN, 15));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(12, 16, 12, 16));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return b;
    }

    public void open(String name) {
        cards.show(content, name);
        pages.get(name).refresh();
        nav.forEach((k, b) -> b.setBg(k.equals(name) ? Theme.BLUE : Theme.NAVY));
    }

    private void logout() {
        int ok = JOptionPane.showConfirmDialog(this, "Keluar dari sistem?", "Logout", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
