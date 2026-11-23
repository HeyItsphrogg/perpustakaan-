import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class test {

    public static boolean isPrima(int angka) {
        if (angka <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(angka); i++) {
            if (angka % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Bilangan prima antara 1 sampai 30:");
        for (int angka = 1; angka <= 30; angka++) {
            if (isPrima(angka)) {
                System.out.print(angka + " ");
            }
        }
        System.out.println();
    }
}