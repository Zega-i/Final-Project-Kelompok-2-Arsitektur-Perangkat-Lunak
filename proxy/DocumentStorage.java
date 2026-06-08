package proxy;

import model.Proposal;

/**
 * Proxy Pattern — Interface Object Storage.
 * Digunakan oleh SubmitFacade tanpa mengetahui apakah ia berinteraksi dengan
 * Proxy atau langsung ke RealDocumentStorage (Dependency Inversion Principle).
 */
public interface DocumentStorage {
    boolean store(Proposal proposal, String fileName, double fileSizeMB);
    String retrieve(String proposalId);
}
