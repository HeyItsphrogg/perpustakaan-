package perpustakaan;

import javax.swing.SwingUtilities;

/** Titik masuk aplikasi. Jalankan file ini (Run) untuk membuka halaman login. */
public class Main {
    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
