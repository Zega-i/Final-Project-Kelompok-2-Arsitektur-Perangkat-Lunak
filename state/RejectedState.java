package state;

import model.Proposal;

/**
 * Status final negatif — proposal ditolak oleh Proxy atau Document Processing Worker.
 * Tidak ada aksi yang diizinkan dari status ini (status final, per spesifikasi ADR).
 */
public class RejectedState implements ProposalState {

    @Override
    public void upload(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Proposal sudah REJECTED (status final). Tidak ada aksi yang diizinkan.");
    }

    @Override
    public void verify(Proposal proposal) {
        System.out.println("[STATE]    ✗ Proposal sudah REJECTED (status final).");
    }

    @Override
    public void submit(Proposal proposal) {
        System.out.println("[STATE]    ✗ Proposal sudah REJECTED (status final).");
    }

    @Override
    public void reject(Proposal proposal, String reason) {
        System.out.println("[STATE]    ✗ Proposal sudah REJECTED (status final).");
    }

    @Override
    public void edit(Proposal proposal) {
        System.out.println("[STATE]    ✗ DIBLOKIR — Proposal sudah REJECTED (status final). Edit tidak diizinkan.");
    }

    @Override
    public String getStateName() { return "REJECTED"; }
}
