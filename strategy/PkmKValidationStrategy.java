package strategy;

import model.Proposal;

/**
 * Strategy Pattern — Validasi khusus PKM-K (Kewirausahaan).
 * Syarat: anggaran kas ada, susunan tim lengkap.
 */
public class PkmKValidationStrategy implements ValidationStrategy {

    @Override
    public boolean validate(Proposal proposal) {
        System.out.println("[STRATEGY] Menggunakan validasi: " + getStrategyName());

        boolean valid = true;

        System.out.print("[STRATEGY] Anggaran kas          : ");
        if (proposal.hasAnggaranKas()) {
            System.out.println("ditemukan ✓");
        } else {
            System.out.println("TIDAK ADA ✗");
            valid = false;
        }

        System.out.print("[STRATEGY] Susunan tim           : ");
        if (proposal.isSusunanTimLengkap()) {
            System.out.println("lengkap ✓");
        } else {
            System.out.println("TIDAK LENGKAP ✗");
            valid = false;
        }

        simulateValidationDelay();

        if (valid) {
            System.out.println("[STRATEGY] ✓ Semua syarat PKM-K terpenuhi");
        } else {
            System.out.println("[STRATEGY] ✗ Validasi PKM-K GAGAL — dokumen tidak memenuhi persyaratan kewirausahaan");
        }
        return valid;
    }

    @Override
    public String getStrategyName() { return "PKM-K Strategy"; }

    private void simulateValidationDelay() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
