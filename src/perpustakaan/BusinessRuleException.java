package perpustakaan;

/** Exception khusus untuk pelanggaran aturan bisnis (stok habis, batas pinjam, dst). */
public class BusinessRuleException extends Exception {
    public BusinessRuleException(String message) {
        super(message);
    }
}
