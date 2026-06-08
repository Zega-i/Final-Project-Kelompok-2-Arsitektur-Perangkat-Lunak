package state;

import model.Proposal;

/**
 * State Pattern — Interface.
 * Mendefinisikan kontrak aksi yang tersedia di setiap status proposal.
 * Setiap implementasi menentukan sendiri apakah aksi diizinkan atau diblokir.
 */
public interface ProposalState {
    void upload(Proposal proposal);
    void verify(Proposal proposal);
    void submit(Proposal proposal);
    void reject(Proposal proposal, String reason);
    void edit(Proposal proposal);
    String getStateName();
}
