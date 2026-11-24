package perpustakaan;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Titik masuk aplikasi. Jalankan file ini (Run) untuk membuka halaman login. */
public class Main {
    public static void main(String[] args) {
        Theme.install();
        try {
            Data.muat();   // baca semua data dari MySQL
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database.\nPastikan MySQL di XAMPP sudah Start.\n\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
