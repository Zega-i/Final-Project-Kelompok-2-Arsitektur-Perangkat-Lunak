package state;

import model.Proposal;

/** File sedang diunggah ke Object Storage melalui Pre-signed URL. */
public class UploadingState implements ProposalState {

    @Override
    public void upload(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — File sedang dalam proses upload ke storage.");
    }

    @Override
    public void verify(Proposal proposal) {
        System.out.println("[STATE]    Status berubah: UPLOADING → VERIFYING");
        proposal.setState(new VerifyingState());
    }

    @Override
    public void submit(Proposal proposal) {
        System.out.println("[STATE]    ✗ Belum bisa disubmit — file masih dalam proses upload.");
    }

    @Override
    public void reject(Proposal proposal, String reason) {
        System.out.println("[STATE]    Status berubah: UPLOADING → REJECTED");
        System.out.println("[STATE]    Alasan: " + reason);
        proposal.setState(new RejectedState());
    }

    @Override
    public void edit(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Edit tidak diizinkan saat file sedang diupload.");
    }

    @Override
    public String getStateName() { return "UPLOADING"; }
}
