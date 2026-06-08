package observer;

import model.Proposal;

/**
 * Observer Pattern — Interface.
 * Dipanggil otomatis setiap kali status proposal berubah.
 * Menerima status lama dan status baru sehingga observer bisa bereaksi selektif.
 */
public interface ProposalObserver {
    void onStatusChanged(Proposal proposal, String oldStatus, String newStatus);
}
