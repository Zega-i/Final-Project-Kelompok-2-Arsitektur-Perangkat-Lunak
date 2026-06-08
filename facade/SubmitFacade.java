package facade;

import model.Proposal;
import proxy.DocumentStorage;
import strategy.ValidationStrategy;
import strategy.ValidationStrategyFactory;

/**
 * Facade Pattern — Menyembunyikan seluruh kompleksitas pipeline upload & validasi
 * di balik satu method sederhana: processUpload(proposal).
 *
 * Alur internal yang disembunyikan:
 *   [1] Proxy check  → DocumentStorageProxy (auth, ekstensi, ukuran)
 *   [2] Simpan file  → RealDocumentStorage
 *   [3] State        → DRAFT → UPLOADING
 *   [4] Async delay  → simulasi Message Broker background worker
 *   [5] State        → UPLOADING → VERIFYING
 *   [6] Strategy     → validasi sesuai jenis PKM
 *   [7] State        → VERIFYING → SUBMITTED / REJECTED
 *   [8] Observer     → notifikasi otomatis melalui setState()
 *
 * Desain mengikuti Dependency Inversion Principle:
 *   - Bergantung pada DocumentStorage (interface), bukan DocumentStorageProxy
 *   - Bergantung pada ValidationStrategyFactory, bukan class strategy konkrit
 */
public class SubmitFacade {

    private final DocumentStorage storage;
    private final ValidationStrategyFactory strategyFactory;

    public SubmitFacade(DocumentStorage storage, ValidationStrategyFactory strategyFactory) {
        this.storage         = storage;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Memproses seluruh alur upload dan validasi proposal PKM.
     * @return true jika proposal berhasil SUBMITTED, false jika dibatalkan atau REJECTED
     */
    public boolean processUpload(Proposal proposal) {
        System.out.printf("[FACADE]   Memulai proses upload proposal: %s%n", proposal.getId());
        System.out.printf("[FACADE]   Mahasiswa : %s (%s)%n",
                proposal.getMahasiswaName(), proposal.getMahasiswaNIM());
        System.out.printf("[FACADE]   Jenis PKM : %s | File: %s (%.1f MB)%n",
                proposal.getPkmType(), proposal.getFileName(), proposal.getFileSizeMB());
        System.out.println();

        // ── [1] Proxy check SEBELUM state change ──────────────────────────
        // Jika Proxy menolak, status proposal TETAP DRAFT (tidak ada state change)
        boolean stored = storage.store(proposal, proposal.getFileName(), proposal.getFileSizeMB());
        if (!stored) {
            System.out.println("[STATE]    Status tetap: DRAFT (tidak ada perubahan)");
            System.out.println("[FACADE]   Proses dibatalkan. Status akhir: DRAFT 🚫");
            return false;
        }

        // ── [2-3] File lolos Proxy → DRAFT → UPLOADING ───────────────────
        proposal.upload();

        // ── [4] Simulasi async Message Broker background worker ───────────
        System.out.println("[SYSTEM]   Mengirim tugas validasi ke background worker...");
        System.out.println("[SYSTEM]   (Simulasi async: menunggu 2 detik...)");
        simulateAsyncDelay();

        // ── [5] Worker mengambil tugas → UPLOADING → VERIFYING ───────────
        proposal.verify();

        // ── [6] Validasi sesuai jenis PKM (Strategy Pattern) ─────────────
        ValidationStrategy strategy = strategyFactory.getStrategy(proposal.getPkmType());
        boolean isValid = strategy.validate(proposal);

        // ── [7-8] Update status final → observer diberitahu otomatis ─────
        if (isValid) {
            proposal.submit();
            System.out.println("[FACADE]   Proses selesai. Status akhir: SUBMITTED ✅");
        } else {
            proposal.reject("Dokumen tidak memenuhi persyaratan " + proposal.getPkmType());
            System.out.println("[FACADE]   Proses selesai. Status akhir: REJECTED ❌");
        }

        return isValid;
    }

    private void simulateAsyncDelay() {
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
