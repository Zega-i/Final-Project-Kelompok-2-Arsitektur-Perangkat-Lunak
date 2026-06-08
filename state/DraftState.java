package state;

import model.Proposal;

/** Status awal — proposal baru dibuat, belum ada file yang diupload. */
public class DraftState implements ProposalState {

    @Override
    public void upload(Proposal proposal) {
        System.out.println("[STATE]    Status berubah: DRAFT → UPLOADING");
        proposal.setState(new UploadingState());
    }

    @Override
    public void verify(Proposal proposal) {
        System.out.println("[STATE]    ✗ Tidak bisa diverifikasi — proposal masih berstatus DRAFT.");
    }

    @Override
    public void submit(Proposal proposal) {
        System.out.println("[STATE]    ✗ Tidak bisa disubmit — proposal masih berstatus DRAFT.");
    }

    @Override
    public void reject(Proposal proposal, String reason) {
        System.out.println("[STATE]    ✗ Tidak ada yang ditolak — proposal masih berstatus DRAFT.");
    }

    @Override
    public void edit(Proposal proposal) {
        System.out.println("[STATE]    ✓ Edit diizinkan di status DRAFT.");
    }

    @Override
    public String getStateName() { return "DRAFT"; }
}
