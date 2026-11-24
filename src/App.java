import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import perpustakaan.Data;
import perpustakaan.LoginFrame;
import perpustakaan.Theme;

/**
 * Titik masuk aplikasi Sistem Informasi Perpustakaan.
 * Jalankan file ini (klik "Run" di atas fungsi main) untuk membuka halaman login.
 *
 * Akun demo:  admin / admin123   |   petugas01 / petugas123
 */
public class App {
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
