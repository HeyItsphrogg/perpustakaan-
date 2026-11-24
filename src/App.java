import javax.swing.SwingUtilities;

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
        Theme.install();                                             // terapkan tema warna & font
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));   // buka login di thread Swing
    }
}
