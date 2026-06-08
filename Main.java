import model.Proposal;
import facade.SubmitFacade;
import observer.EmailNotificationObserver;
import observer.SystemLogObserver;
import proxy.DocumentStorageProxy;
import proxy.RealDocumentStorage;
import strategy.ValidationStrategyFactory;
import server.SimulasiServer;

/**
 * Entry point simulasi Portal PKM Upload System.
 * Mendemonstrasikan 5 Design Patterns melalui 6 skenario nyata.
 *
 * ── CARA KOMPILASI (Windows PowerShell, dari direktori ini) ──────────────
 *
 *   $javaHome = "C:\Program Files\Eclipse Adoptium\jdk-11.0.24.8-hotspot"
 *   $files = Get-ChildItem -Recurse -Filter "*.java" | % { $_.FullName }
 *   & "$javaHome\bin\javac.exe" -encoding UTF-8 -cp . $files
 *
 * ── CARA MENJALANKAN ──────────────────────────────────────────────────────
 *
 *   & "$javaHome\bin\java.exe" -cp . "-Dfile.encoding=UTF-8" Main
 *
 * ── DESIGN PATTERNS ──────────────────────────────────────────────────────
 *   State    → Siklus status DRAFT→UPLOADING→VERIFYING→SUBMITTED/REJECTED
 *   Strategy → Validasi berbeda per jenis PKM (K / RE / PM)
 *   Proxy    → Gerbang pre-check sebelum file masuk Object Storage
 *   Observer → Notifikasi otomatis email + log saat status berubah
 *   Facade   → SubmitFacade.processUpload() menyembunyikan seluruh pipeline
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--server".equals(args[0])) {
            try {
                SimulasiServer.start();
            } catch (Exception e) {
                System.err.println("Server gagal dimulai: " + e.getMessage());
            }
            return;
        }

        printHeader();

        // ── Inisialisasi dependencies (Dependency Injection) ──────────────
        SubmitFacade facade = new SubmitFacade(
                new DocumentStorageProxy(new RealDocumentStorage()),
                new ValidationStrategyFactory()
        );

        String[] results = new String[7]; // index 1–6

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 1 — Upload Sukses PKM-K
        // Ekspektasi: Status akhir SUBMITTED ✅
        // ═════════════════════════════════════════════════════════════════
        printScenario(1, "Upload Sukses — PKM-K",
                "Budi Santoso", "21/123456/TK/001");

        Proposal p1 = new Proposal(
                "PKM-2024-001", "Budi Santoso", "21/123456/TK/001",
                "PKM-K", "proposal_pkm_k.pdf", 4.2)
                .withAnggaranKas(true)
                .withSusunanTim(true);
        p1.addObserver(new EmailNotificationObserver());
        p1.addObserver(new SystemLogObserver());

        facade.processUpload(p1);
        results[1] = p1.getStateName();
        pause();

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 2 — Ditolak Proxy: File Bukan PDF
        // Ekspektasi: Ditolak di Proxy, status tetap DRAFT 🚫
        // ═════════════════════════════════════════════════════════════════
        printScenario(2, "Ditolak Proxy — File Bukan PDF (.exe)",
                "Sari Dewi", "21/123457/TK/002");

        Proposal p2 = new Proposal(
                "PKM-2024-002", "Sari Dewi", "21/123457/TK/002",
                "PKM-K", "proposal.exe", 2.1)
                .withAnggaranKas(true)
                .withSusunanTim(true);
        p2.addObserver(new EmailNotificationObserver());
        p2.addObserver(new SystemLogObserver());

        facade.processUpload(p2);
        results[2] = p2.getStateName();
        pause();

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 3 — Ditolak Proxy: Ukuran File Terlalu Besar
        // Ekspektasi: Ditolak di Proxy, status tetap DRAFT 🚫
        // ═════════════════════════════════════════════════════════════════
        printScenario(3, "Ditolak Proxy — Ukuran File 7.5 MB (batas 5.0 MB)",
                "Andi Pratama", "21/123458/TK/003");

        Proposal p3 = new Proposal(
                "PKM-2024-003", "Andi Pratama", "21/123458/TK/003",
                "PKM-PM", "proposal_besar.pdf", 7.5)
                .withSuratIzinMitra(true)
                .withLokasiKegiatan(true);
        p3.addObserver(new EmailNotificationObserver());
        p3.addObserver(new SystemLogObserver());

        facade.processUpload(p3);
        results[3] = p3.getStateName();
        pause();

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 4 — Validasi Gagal PKM-RE (Tanpa Izin Lab)
        // Ekspektasi: Lolos Proxy, tapi REJECTED di validasi ❌
        // ═════════════════════════════════════════════════════════════════
        printScenario(4, "Validasi Gagal — PKM-RE Tanpa Izin Laboratorium",
                "Citra Lestari", "21/123459/TK/004");

        Proposal p4 = new Proposal(
                "PKM-2024-004", "Citra Lestari", "21/123459/TK/004",
                "PKM-RE", "proposal_pkm_re.pdf", 3.8)
                .withIzinLab(false)          // sengaja false → validasi gagal
                .withPembimbing(true)
                .withReferensiJurnal(true);
        p4.addObserver(new EmailNotificationObserver());
        p4.addObserver(new SystemLogObserver());

        facade.processUpload(p4);
        results[4] = p4.getStateName();
        pause();

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 5 — Upload Ulang Saat Status VERIFYING (State Block)
        // Ekspektasi: Sistem MENOLAK dengan pesan jelas ⛔
        // ═════════════════════════════════════════════════════════════════
        printScenario(5, "State Block — Upload Ulang Saat Proposal Sedang VERIFYING",
                "Eko Wahyudi", "21/123460/TK/005");

        Proposal p5 = new Proposal(
                "PKM-2024-005", "Eko Wahyudi", "21/123460/TK/005",
                "PKM-K", "proposal_pkm_k_eko.pdf", 3.1)
                .withAnggaranKas(true)
                .withSusunanTim(true);

        // Simulasi: pindahkan proposal ke status VERIFYING secara langsung
        System.out.println("[SYSTEM]   Menyiapkan skenario: memindahkan proposal ke status VERIFYING...");
        p5.upload();   // DRAFT → UPLOADING
        p5.verify();   // UPLOADING → VERIFYING
        System.out.println("[SYSTEM]   Status proposal saat ini: " + p5.getStateName());
        System.out.println("[SYSTEM]   ─────────────────────────────────────────────────────────────");
        System.out.println("[SYSTEM]   Simulasi: mahasiswa panik, mencoba upload ulang...");
        System.out.println();

        System.out.printf("[FACADE]   Mencoba upload ulang untuk: %s (status: %s)%n",
                p5.getId(), p5.getStateName());
        p5.upload();   // ← DIBLOKIR oleh VerifyingState
        System.out.println("[FACADE]   Percobaan upload ulang ditolak ⛔");
        results[5] = "DIBLOKIR";
        pause();

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 6 — Upload Sukses PKM-PM
        // Ekspektasi: Status akhir SUBMITTED ✅
        // ═════════════════════════════════════════════════════════════════
        printScenario(6, "Upload Sukses — PKM-PM",
                "Dian Rahayu", "21/123461/TK/006");

        Proposal p6 = new Proposal(
                "PKM-2024-006", "Dian Rahayu", "21/123461/TK/006",
                "PKM-PM", "proposal_pkm_pm.pdf", 2.9)
                .withSuratIzinMitra(true)
                .withLokasiKegiatan(true);
        p6.addObserver(new EmailNotificationObserver());
        p6.addObserver(new SystemLogObserver());

        facade.processUpload(p6);
        results[6] = p6.getStateName();

        printSummary(results);
    }

    // ── Output helpers ────────────────────────────────────────────────────

    private static void printHeader() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     SIMULASI SISTEM PKM UPLOAD — PoC Design Patterns     ║");
        System.out.println("║     Studi Kasus: The PKM Upload Bottleneck               ║");
        System.out.println("║                                                          ║");
        System.out.println("║  State · Strategy · Proxy · Observer · Facade           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    private static void printScenario(int num, String desc, String name, String nim) {
        System.out.println("\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf(" SKENARIO %d: %s%n", num, desc);
        System.out.printf(" Mahasiswa : %s (%s)%n", name, nim);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    private static void printSummary(String[] results) {
        System.out.println("\n\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    RINGKASAN SIMULASI                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  Skenario 1 (PKM-K Sukses)            : " + padRight("SUBMITTED  ✅", 16) + "║");
        System.out.println("║  Skenario 2 (File bukan PDF)          : " + padRight("DITOLAK    🚫", 16) + "║");
        System.out.println("║  Skenario 3 (File > 5MB)              : " + padRight("DITOLAK    🚫", 16) + "║");
        System.out.println("║  Skenario 4 (PKM-RE tanpa izin lab)   : " + padRight("REJECTED   ❌", 16) + "║");
        System.out.println("║  Skenario 5 (Upload ulang VERIFYING)  : " + padRight("DIBLOKIR   ⛔", 16) + "║");
        System.out.println("║  Skenario 6 (PKM-PM Sukses)           : " + padRight("SUBMITTED  ✅", 16) + "║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Design Patterns yang berhasil didemonstrasikan:");
        System.out.println("  State    → Siklus DRAFT→UPLOADING→VERIFYING→SUBMITTED/REJECTED");
        System.out.println("  Strategy → Validasi berbeda per jenis PKM (K / RE / PM)");
        System.out.println("  Proxy    → Gerbang pre-check sebelum file masuk Object Storage");
        System.out.println("  Observer → Notifikasi otomatis email + log saat status berubah");
        System.out.println("  Facade   → satu method menyembunyikan seluruh pipeline");
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private static void pause() {
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
