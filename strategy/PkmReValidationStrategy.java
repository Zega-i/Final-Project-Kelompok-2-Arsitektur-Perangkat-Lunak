package strategy;

import model.Proposal;

/**
 * Strategy Pattern — Validasi khusus PKM-RE (Riset Eksakta).
 * Syarat: surat izin laboratorium ada, pembimbing terdaftar, referensi jurnal tersedia.
 */
public class PkmReValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Proposal proposal) {
        System.out.println("[STRATEGY] Menggunakan validasi: " + getStrategyName());

        boolean valid = true;

        System.out.print("[STRATEGY] Surat izin laboratorium : ");
        if (proposal.hasIzinLab()) {
            System.out.println("ditemukan ✓");
        } else {
            System.out.println("TIDAK ADA ✗");
            valid = false;
        }

        System.out.print("[STRATEGY] Pembimbing              : ");
        if (proposal.hasPembimbing()) {
            System.out.println("terdaftar ✓");
        } else {
            System.out.println("TIDAK TERDAFTAR ✗");
            valid = false;
        }

        System.out.print("[STRATEGY] Referensi jurnal        : ");
        if (proposal.hasReferensiJurnal()) {
            System.out.println("ditemukan ✓");
        } else {
            System.out.println("TIDAK ADA ✗");
            valid = false;
        }

        simulateValidationDelay();

        if (valid) {
            System.out.println("[STRATEGY] ✓ Semua syarat PKM-RE terpenuhi");
        } else {
            System.out.println("[STRATEGY] ✗ Validasi PKM-RE GAGAL — dokumen tidak memenuhi persyaratan riset eksakta");
        }
        return valid;
    }

    @Override
    public String getStrategyName() { return "PKM-RE Strategy"; }

    private void simulateValidationDelay() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
