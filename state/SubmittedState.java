package state;

import model.Proposal;

/** Status final positif — proposal telah berhasil disubmit dan diterima sistem. */
public class SubmittedState implements ProposalState {

    @Override
    public void upload(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Proposal sudah SUBMITTED (status final). Upload tidak diizinkan.");
    }

    @Override
    public void verify(Proposal proposal) {
        System.out.println("[STATE]    ✗ Proposal sudah SUBMITTED dan terverifikasi (status final).");
    }

    @Override
    public void submit(Proposal proposal) {
        System.out.println("[STATE]    ✗ Proposal sudah SUBMITTED (status final).");
    }

    @Override
    public void reject(Proposal proposal, String reason) {
        System.out.println("[STATE]    ✗ Proposal sudah SUBMITTED, tidak bisa ditolak kembali (status final).");
    }

    @Override
    public void edit(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Proposal sudah SUBMITTED (status final). Edit tidak diizinkan.");
    }

    @Override
    public String getStateName() { return "SUBMITTED"; }
}
