package observer;

import model.Proposal;

/** Observer Pattern — Simulasi pengiriman email ke mahasiswa saat status proposal berubah. */
public class EmailNotificationObserver implements ProposalObserver {

    @Override
    public void onStatusChanged(Proposal proposal, String oldStatus, String newStatus) {
        if (newStatus.equals("SUBMITTED")) {
            System.out.printf("[OBSERVER] EmailNotifier : \"Selamat! Proposal %s %s DITERIMA — ID: %s\"%n",
                    proposal.getPkmType(), proposal.getMahasiswaName(), proposal.getId());
        } else if (newStatus.equals("REJECTED")) {
            System.out.printf("[OBSERVER] EmailNotifier : \"Proposal %s %s DITOLAK. Periksa kelengkapan dokumen.\"%n",
                    proposal.getPkmType(), proposal.getMahasiswaName());
        }
        // Transisi UPLOADING & VERIFYING tidak memerlukan email — hanya status final
    }
}
