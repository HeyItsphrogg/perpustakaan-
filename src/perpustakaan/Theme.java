package perpustakaan;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;

/** Warna, font, dan komponen kustom supaya tampilannya sama dengan mockup. */
public final class Theme {
    private Theme() {}

    public static final Color NAVY = new Color(0x1B2F4A);
    public static final Color BG = new Color(0xF5F7FB);
    public static final Color BLUE = new Color(0x3066CF);
    public static final Color BORDER = new Color(0xDCE4F0);
    public static final Color MUTED = new Color(0x6F7F95);
    public static final Color TEXT = new Color(0x2A3B52);
    public static final Color HEADER = new Color(0xEEF3F8);
    public static final Color RED = new Color(0xC62828);
    public static final Color HINT = new Color(0x8A97AB);
    public static final int ROW_H = 44;

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        UIManager.put("OptionPane.messageFont", font(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", font(Font.PLAIN, 13));
    }

    // ---------- helper dasar ----------
    public static Font font(int style, int size) { return new Font("SansSerif", style, size); }

    public static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    public static JPanel panel(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        return p;
    }

    public static Round card(LayoutManager lm) { return new Round(lm, 20, Color.WHITE, BORDER); }

    public static Round boxCard() {
        Round r = card(null);
        r.setLayout(new BoxLayout(r, BoxLayout.Y_AXIS));
        return r;
    }

    /** Label + input bertumpuk vertikal di dalam panel ber-BoxLayout. */
    public static void addField(JPanel p, String label, JComponent c) {
        JLabel l = label(label, Font.PLAIN, 13, MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setPreferredSize(new Dimension(200, 40));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(c);
        p.add(Box.createVerticalStrut(16));
    }

    public static JComboBox<Object> combo(Object... items) {
        JComboBox<Object> cb = new JComboBox<>(items);
        cb.setUI(new ComboUI());
        cb.setOpaque(false);
        cb.setFont(font(Font.PLAIN, 13));
        cb.setForeground(TEXT);
        cb.setBackground(Color.WHITE);
        cb.setBorder(new RoundBorder(BORDER, 12));
        cb.setPreferredSize(new Dimension(200, 40));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, false);
                setBorder(new EmptyBorder(6, 12, 6, 12));
                setFont(font(Font.PLAIN, 13));
                if (!sel) setForeground(TEXT);
                return this;
            }
        });
        return cb;
    }

    public static void onChange(JTextComponent c, Runnable r) {
        c.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { r.run(); }
            @Override public void removeUpdate(DocumentEvent e) { r.run(); }
            @Override public void changedUpdate(DocumentEvent e) { r.run(); }
        });
    }

    // ---------- tabel ----------
    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JScrollPane styleTable(JTable t, Color bg) {
        t.setRowHeight(ROW_H);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setBackground(bg);
        t.setForeground(TEXT);
        t.setFocusable(false);
        t.setRowSelectionAllowed(false);
        t.setFillsViewportHeight(true);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tb, v, false, false, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setFont(font(Font.PLAIN, 12));
                setForeground(TEXT);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, ROW_H));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tb, v, false, false, r, c);
                setBackground(HEADER);
                setForeground(NAVY);
                setFont(font(Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setViewportBorder(null);
        sp.getViewport().setBackground(bg);
        return sp;
    }

    // ---------- komponen kustom ----------
    /** Panel dengan sudut membulat (untuk kartu). */
    public static class Round extends JPanel {
        private final int arc;
        private final Color fill, line;

        public Round(LayoutManager lm, int arc, Color fill, Color line) {
            super(lm);
            this.arc = arc; this.fill = fill; this.line = line;
            setOpaque(false);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            if (line != null) {
                g2.setColor(line);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
        }
    }

    /** Tombol ber-sudut membulat. */
    public static class Btn extends JButton {
        private Color bg;

        public Btn(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            setForeground(fg);
            setFont(font(Font.PLAIN, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setBorder(new EmptyBorder(11, 16, 11, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setBg(Color c) { bg = c; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = bg;
            if (getModel().isPressed()) c = bg.darker();
            else if (getModel().isRollover()) c = bg.equals(NAVY) ? new Color(0x2A4468) : bg.brighter();
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Combo box datar dengan sudut membulat dan panah kecil. */
    private static class ComboUI extends BasicComboBoxUI {
        @Override protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(MUTED);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                    g2.drawLine(cx, cy + 2, cx + 4, cy - 2);
                    g2.dispose();
                }
            };
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setBorder(null);
            b.setPreferredSize(new Dimension(34, 0));
            return b;
        }

        @Override protected ComboPopup createPopup() {
            BasicComboPopup p = new BasicComboPopup(comboBox);
            p.setBorder(BorderFactory.createLineBorder(BORDER));
            return p;
        }

        @Override public void paint(Graphics g, JComponent c) {
            fillField(g, c);
            super.paint(g, c);
        }

        @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean hasFocus) { }

        @Override public void paintCurrentValue(Graphics g, Rectangle r, boolean hasFocus) {
            super.paintCurrentValue(g, r, false);
        }
    }

    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int arc;

        public RoundBorder(Color color, int arc) { this.color = color; this.arc = arc; }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
    }

    private static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(new RoundBorder(BORDER, 12), new EmptyBorder(0, 12, 0, 12));
    }

    private static void fillField(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 12, 12);
        g2.dispose();
    }

    private static void paintHint(Graphics g, JTextComponent c, String hint) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(HINT);
        g2.setFont(c.getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hint, c.getInsets().left, (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    private static void styleInput(JTextComponent c) {
        c.setOpaque(false);
        c.setFont(font(Font.PLAIN, 13));
        c.setForeground(TEXT);
        c.setCaretColor(TEXT);
        c.setBorder(fieldBorder());
        c.setPreferredSize(new Dimension(200, 40));
    }

    /** Kolom isian dengan placeholder. */
    public static class Field extends JTextField {
        private final String hint;

        public Field(String hint) { this.hint = hint; styleInput(this); }

        @Override protected void paintComponent(Graphics g) {
            fillField(g, this);
            super.paintComponent(g);
            if (getText().isEmpty()) paintHint(g, this, hint);
        }
    }

    public static class PassField extends JPasswordField {
        private final String hint;

        public PassField(String hint) { this.hint = hint; styleInput(this); setEchoChar('•'); }

        @Override protected void paintComponent(Graphics g) {
            fillField(g, this);
            super.paintComponent(g);
            if (getPassword().length == 0) paintHint(g, this, hint);
        }
    }
}
