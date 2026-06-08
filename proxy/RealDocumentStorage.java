package proxy;

import model.Proposal;

/** Proxy Pattern — Simulasi Object Storage (S3-compatible). Target penyimpanan setelah lolos Proxy. */
public class RealDocumentStorage implements DocumentStorage {

    @Override
    public boolean store(Proposal proposal, String fileName, double fileSizeMB) {
        String path = "s3://pkm-nasional-bucket/" + proposal.getMahasiswaNIM()
                + "/" + proposal.getId() + ".pdf";
        System.out.printf("[STORAGE]  File %s berhasil disimpan (simulasi)%n", fileName);
        System.out.printf("[STORAGE]  Path  : %s%n", path);
        System.out.printf("[STORAGE]  Ukuran: %.1f MB%n", fileSizeMB);
        proposal.setFilePath(path);
        return true;
    }

    @Override
    public String retrieve(String proposalId) {
        return "s3://pkm-nasional-bucket/*/" + proposalId + ".pdf";
    }
}
