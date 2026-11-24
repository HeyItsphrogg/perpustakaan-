package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Kerangka halaman: judul, subjudul, garis pemisah, lalu isi (body). */
public abstract class Page extends JPanel {
    protected final JPanel body = Theme.panel(new BorderLayout());

    protected Page(String title, String subtitle) {
        super(new BorderLayout());
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(28, 32, 24, 32));

        JPanel texts = Theme.panel(new GridLayout(2, 1, 0, 2));
        texts.setBorder(new EmptyBorder(0, 0, 14, 0));
        texts.add(Theme.label(title, Font.BOLD, 26, Theme.NAVY));
        texts.add(Theme.label(subtitle, Font.PLAIN, 13, Theme.MUTED));

        JPanel line = new JPanel();
        line.setBackground(Theme.BORDER);
        line.setPreferredSize(new Dimension(0, 1));

        JPanel head = Theme.panel(new BorderLayout());
        head.add(texts, BorderLayout.NORTH);
        head.add(line, BorderLayout.SOUTH);

        body.setBorder(new EmptyBorder(24, 0, 0, 0));
        add(head, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    /** Dipanggil setiap halaman ditampilkan, supaya data selalu terbaru. */
    public void refresh() { }

    protected void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    protected void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }
}
