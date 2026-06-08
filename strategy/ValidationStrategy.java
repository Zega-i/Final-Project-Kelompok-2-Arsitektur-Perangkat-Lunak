package strategy;

import model.Proposal;

/**
 * Strategy Pattern — Interface.
 * Mendefinisikan kontrak validasi yang dapat ditukar saat runtime sesuai jenis PKM.
 * Mendukung Open/Closed Principle: tambah jenis PKM baru cukup buat class baru
 * tanpa mengubah kode yang sudah ada.
 */
public interface ValidationStrategy {
    boolean validate(Proposal proposal);
    String getStrategyName();
}
