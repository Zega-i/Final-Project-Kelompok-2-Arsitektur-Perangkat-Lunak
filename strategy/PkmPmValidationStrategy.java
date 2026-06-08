package strategy;

import model.Proposal;

/**
 * Strategy Pattern — Validasi khusus PKM-PM (Pengabdian Masyarakat).
 * Syarat: surat izin mitra komunitas ada, lokasi kegiatan terdefinisi.
 */
public class PkmPmValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Proposal proposal) {
        System.out.println("[STRATEGY] Menggunakan validasi: " + getStrategyName());

        boolean valid = true;

        System.out.print("[STRATEGY] Surat izin mitra komunitas : ");
        if (proposal.hasSuratIzinMitra()) {
            System.out.println("ditemukan ✓");
        } else {
            System.out.println("TIDAK ADA ✗");
            valid = false;
        }

        System.out.print("[STRATEGY] Lokasi kegiatan            : ");
        if (proposal.hasLokasiKegiatan()) {
            System.out.println("terdefinisi ✓");
        } else {
            System.out.println("TIDAK TERDEFINISI ✗");
            valid = false;
        }

        simulateValidationDelay();

        if (valid) {
            System.out.println("[STRATEGY] ✓ Semua syarat PKM-PM terpenuhi");
        } else {
            System.out.println("[STRATEGY] ✗ Validasi PKM-PM GAGAL — dokumen tidak memenuhi persyaratan pengabdian masyarakat");
        }
        return valid;
    }

    @Override
    public String getStrategyName() { return "PKM-PM Strategy"; }

    private void simulateValidationDelay() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
