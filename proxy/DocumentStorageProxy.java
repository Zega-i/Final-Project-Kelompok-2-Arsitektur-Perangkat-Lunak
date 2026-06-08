package proxy;

import model.Proposal;

/**
 * Proxy Pattern — Mencegat semua permintaan upload dan menjalankan 3 pre-check
 * sebelum meneruskan ke RealDocumentStorage.
 *
 * Check 1: Autentikasi  — mahasiswa harus sudah login (isAuthenticated = true)
 * Check 2: Ekstensi     — file harus berekstensi .pdf
 * Check 3: Ukuran file  — file harus <= 5.0 MB
 *
 * Jika semua lolos → diteruskan ke RealDocumentStorage.
 * Jika satu pun gagal → ditolak di sini, RealStorage tidak pernah dipanggil.
 */
public class DocumentStorageProxy implements DocumentStorage {

    private static final double MAX_FILE_SIZE_MB   = 5.0;
    private static final String REQUIRED_EXTENSION = ".pdf";

    private final DocumentStorage realStorage;

    public DocumentStorageProxy(DocumentStorage realStorage) {
        this.realStorage = realStorage;
    }

    @Override
    public boolean store(Proposal proposal, String fileName, double fileSizeMB) {
        System.out.printf("[PROXY]    Memeriksa file: %s (%.1f MB)%n", fileName, fileSizeMB);

        // Check 1: Autentikasi
        if (!proposal.isAuthenticated()) {
            System.out.println("[PROXY]    ✗ Autentikasi gagal: mahasiswa belum login");
            System.out.println("[PROXY]    Upload DITOLAK — file tidak diteruskan ke storage");
            return false;
        }
        System.out.println("[PROXY]    ✓ Autentikasi: mahasiswa terverifikasi");

        // Check 2: Ekstensi file
        if (!fileName.toLowerCase().endsWith(REQUIRED_EXTENSION)) {
            String ext = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf('.'))
                    : "(tidak ada ekstensi)";
            System.out.println("[PROXY]    ✗ Ekstensi tidak valid: " + ext + " (hanya .pdf yang diterima)");
            System.out.println("[PROXY]    Upload DITOLAK — file tidak diteruskan ke storage");
            return false;
        }
        System.out.println("[PROXY]    ✓ Ekstensi valid: .pdf");

        // Check 3: Ukuran file
        if (fileSizeMB > MAX_FILE_SIZE_MB) {
            System.out.printf("[PROXY]    ✗ Ukuran tidak valid: %.1f MB (batas: %.1f MB)%n",
                    fileSizeMB, MAX_FILE_SIZE_MB);
            System.out.println("[PROXY]    Upload DITOLAK — file tidak diteruskan ke storage");
            return false;
        }
        System.out.printf("[PROXY]    ✓ Ukuran valid: %.1f MB (batas: %.1f MB)%n",
                fileSizeMB, MAX_FILE_SIZE_MB);

        System.out.println("[PROXY]    File lolos pemeriksaan → diteruskan ke storage");
        return realStorage.store(proposal, fileName, fileSizeMB);
    }

    @Override
    public String retrieve(String proposalId) {
        return realStorage.retrieve(proposalId);
    }
}
