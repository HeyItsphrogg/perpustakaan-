package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final Theme.Field tfUser = new Theme.Field("Masukkan username");
    private final Theme.PassField tfPass = new Theme.PassField("Masukkan password");
    private final Theme.Round errorBox =
            new Theme.Round(new GridLayout(2, 1, 0, 2), 12, new Color(0xFDECEA), new Color(0xF3C6C1));
    private int row = 0;

    public LoginFrame() {
        super("Login - Sistem Informasi Perpustakaan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ----- judul di kiri atas -----
        JPanel head = Theme.panel(new GridLayout(2, 1, 0, 2));
        head.setBorder(new EmptyBorder(40, 70, 0, 70));
        head.add(Theme.label("SISTEM INFORMASI PERPUSTAKAAN", Font.BOLD, 26, Theme.NAVY));
        head.add(Theme.label("Universitas Udayana", Font.PLAIN, 15, Theme.MUTED));

        // ----- kotak pesan error (disembunyikan dulu) -----
        errorBox.setBorder(new EmptyBorder(10, 16, 10, 16));
        errorBox.add(Theme.label("Username atau password salah", Font.BOLD, 13, Theme.RED));
        errorBox.add(Theme.label("Periksa kembali, lalu coba masuk lagi.", Font.PLAIN, 12, Theme.RED));
        errorBox.setVisible(false);

        Theme.Btn masuk = new Theme.Btn("Masuk", Theme.BLUE, Color.WHITE);
        masuk.setFont(Theme.font(Font.BOLD, 14));
        masuk.setPreferredSize(new Dimension(380, 44));
        masuk.addActionListener(e -> login());
        tfUser.setPreferredSize(new Dimension(380, 42));
        tfPass.setPreferredSize(new Dimension(380, 42));

        JLabel judul = Theme.label("Selamat Datang", Font.BOLD, 28, Theme.NAVY);
        JLabel sub = Theme.label("Silakan masuk ke akun Anda", Font.PLAIN, 15, Theme.MUTED);
        JLabel catatan = Theme.label("Gunakan akun Admin atau Petugas yang terdaftar.", Font.PLAIN, 12, Theme.MUTED);
        judul.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        catatan.setHorizontalAlignment(SwingConstants.CENTER);

        Theme.Round card = Theme.card(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 44, 32, 44));
        put(card, judul, 0);
        put(card, sub, 4);
        put(card, errorBox, 18);
        put(card, Theme.label("Username", Font.PLAIN, 13, Theme.MUTED), 22);
        put(card, tfUser, 6);
        put(card, Theme.label("Password", Font.PLAIN, 13, Theme.MUTED), 16);
        put(card, tfPass, 6);
        put(card, masuk, 26);
        put(card, catatan, 18);

        JPanel center = Theme.panel(new GridBagLayout());
        center.add(card);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(head, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(masuk);   // tekan Enter = Masuk

        setSize(1000, 700);
        setMinimumSize(new Dimension(700, 640));
        setLocationRelativeTo(null);
    }

    private void put(JPanel p, Component c, int top) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = row++;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(top, 0, 0, 0);
        p.add(c, g);
    }

    private void login() {
        String u = tfUser.getText().trim();
        String p = new String(tfPass.getPassword());
        for (Data.User user : Data.users) {
            if (user.username.equals(u) && user.password.equals(p)) {
                dispose();
                new MainFrame(user).setVisible(true);
                return;
            }
        }
        errorBox.setVisible(true);
        tfPass.setText("");
        tfPass.requestFocusInWindow();
    }
}
