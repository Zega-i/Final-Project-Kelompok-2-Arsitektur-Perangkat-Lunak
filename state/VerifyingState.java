package state;

import model.Proposal;

/**
 * File sudah tersimpan; Document Processing Worker sedang memvalidasi di background.
 * Status kritis dalam demonstrasi State Pattern: upload ulang DIBLOKIR otomatis
 * untuk mencegah antrean Message Broker membengkak akibat kepanikan mahasiswa.
 */
public class VerifyingState implements ProposalState {

    @Override
    public void upload(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Tidak dapat diupload: proposal sedang dalam status VERIFYING");
        System.out.println("[STATE]    Upload ulang tidak diizinkan selama validasi background berjalan.");
    }

    @Override
    public void verify(Proposal proposal) {
        System.out.println("[STATE]    ✗ Proposal sudah berstatus VERIFYING.");
    }

    @Override
    public void submit(Proposal proposal) {
        System.out.println("[STATE]    Status berubah: VERIFYING → SUBMITTED");
        proposal.setState(new SubmittedState());
    }

    @Override
    public void reject(Proposal proposal, String reason) {
        System.out.println("[STATE]    Status berubah: VERIFYING → REJECTED");
        System.out.println("[STATE]    Alasan: " + reason);
        proposal.setState(new RejectedState());
    }

    @Override
    public void edit(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Edit tidak diizinkan saat proposal sedang diverifikasi.");
    }

    @Override
    public String getStateName() { return "VERIFYING"; }
}
