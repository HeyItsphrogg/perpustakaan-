package perpustakaan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Halaman master data generik: pencarian + tabel + tombol Tambah / Edit / Hapus.
 * Dipakai oleh BukuPanel, AnggotaPanel, dan KategoriPanel.
 */
public abstract class CrudPanel<T> extends Page {
    private static final int PAD = 12, W_EDIT = 50, W_HAPUS = 60;

    private final List<T> items;
    private final List<T> shown = new ArrayList<>();
    private final String logName;
    private final DefaultTableModel model;
    private final Theme.Field search;
    private final int nCols;

    protected CrudPanel(String title, String subtitle, String searchHint, String addLabel,
                        String logName, List<T> items, String... columns) {
        super(title, subtitle);
        this.items = items;
        this.logName = logName;
        this.nCols = columns.length + 1;

        String[] cols = Arrays.copyOf(columns, nCols);
        cols[nCols - 1] = "Aksi";
        model = Theme.model(cols);
        JTable table = new JTable(model);
        JScrollPane scroll = Theme.styleTable(table, Theme.BG);
        table.getColumnModel().getColumn(nCols - 1).setMinWidth(190);
        table.getColumnModel().getColumn(nCols - 1).setMaxWidth(190);
        table.getColumnModel().getColumn(nCols - 1).setCellRenderer(aksiRenderer());

        final int last = nCols - 1;
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint()), c = table.columnAtPoint(e.getPoint());
                if (r < 0 || c != last || r >= shown.size()) return;
                int dx = e.getX() - table.getCellRect(r, c, false).x;
                if (dx >= PAD && dx < PAD + W_EDIT) ubah(shown.get(r));
                else if (dx >= PAD + W_EDIT && dx < PAD + W_EDIT + W_HAPUS) hapus(shown.get(r));
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                boolean di = table.rowAtPoint(e.getPoint()) >= 0 && table.columnAtPoint(e.getPoint()) == last;
                table.setCursor(Cursor.getPredefinedCursor(di ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        search = new Theme.Field(searchHint);
        Theme.Btn btnAdd = new Theme.Btn("+ " + addLabel, Theme.BLUE, Color.WHITE);
        Theme.Btn btnCari = new Theme.Btn("Cari", Theme.NAVY, Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(btnAdd.getPreferredSize().width + 8, 40));
        btnCari.setPreferredSize(new Dimension(90, 40));
        btnAdd.addActionListener(e -> tambah());
        btnCari.addActionListener(e -> reload());
        search.addActionListener(e -> reload());

        JPanel btns = Theme.panel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        btns.add(btnAdd);
        btns.add(btnCari);
        JPanel row = Theme.panel(new BorderLayout(12, 0));
        row.add(search, BorderLayout.CENTER);
        row.add(btns, BorderLayout.EAST);

        JPanel top = Theme.panel(new BorderLayout(0, 6));
        top.setBorder(new EmptyBorder(0, 0, 22, 0));
        top.add(Theme.label("Pencarian", Font.PLAIN, 13, Theme.MUTED), BorderLayout.NORTH);
        top.add(row, BorderLayout.CENTER);

        body.add(top, BorderLayout.NORTH);
        body.add(scroll, BorderLayout.CENTER);
    }

    // ---------- yang harus diisi oleh subclass ----------
    /** Nilai tiap kolom (tanpa kolom Aksi). */
    protected abstract Object[] toRow(T item);

    /** Apakah item cocok dengan kata kunci (sudah huruf kecil)? */
    protected abstract boolean matches(T item, String keyword);

    /**
     * Tampilkan form. existing == null berarti tambah baru, selain itu ubah (isi objek yang sama).
     * Kembalikan objek hasil, atau null jika dibatalkan.
     */
    protected abstract T form(T existing);

    /** Aturan bisnis sebelum hapus; lempar BusinessRuleException jika tidak boleh dihapus. */
    protected void cekHapus(T item) throws BusinessRuleException { }

    // ---------- logika bersama ----------
    @Override public void refresh() { reload(); }

    private void reload() {
        String q = search.getText().trim().toLowerCase();
        shown.clear();
        model.setRowCount(0);
        for (T t : items) {
            if (q.isEmpty() || matches(t, q)) {
                shown.add(t);
                model.addRow(Arrays.copyOf(toRow(t), nCols));
            }
        }
    }

    private void tambah() {
        T t = form(null);
        if (t != null) {
            items.add(t);
            Data.catat(logName, "Ditambahkan: " + t);
            reload();
        }
    }

    private void ubah(T t) {
        if (form(t) != null) {
            Data.catat(logName, "Diperbarui: " + t);
            reload();
        }
    }

    private void hapus(T t) {
        try {
            cekHapus(t);
        } catch (BusinessRuleException e) {
            warn(e.getMessage());
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus data \"" + t + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            items.remove(t);
            Data.catat(logName, "Dihapus: " + t);
            reload();
        }
    }

    /** Dialog form sederhana: label di atas, input di bawah. */
    protected boolean ask(String title, String[] labels, JComponent... fields) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        for (int i = 0; i < labels.length; i++) {
            fields[i].setPreferredSize(new Dimension(330, 40));
            Theme.addField(p, labels[i], fields[i]);
        }
        return JOptionPane.showConfirmDialog(this, p, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private TableCellRenderer aksiRenderer() {
        return new TableCellRenderer() {
            private final JPanel p = Theme.panel(new FlowLayout(FlowLayout.LEFT, 0, (Theme.ROW_H - 20) / 2));
            private final JLabel edit = Theme.label("Edit", Font.PLAIN, 12, Theme.BLUE);
            private final JLabel hapus = Theme.label("Hapus", Font.PLAIN, 12, Theme.RED);
            {
                p.setBorder(new EmptyBorder(0, PAD, 0, 0));
                edit.setPreferredSize(new Dimension(W_EDIT, 20));
                hapus.setPreferredSize(new Dimension(W_HAPUS, 20));
                p.add(edit);
                p.add(hapus);
            }
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                return p;
            }
        };
    }
}
