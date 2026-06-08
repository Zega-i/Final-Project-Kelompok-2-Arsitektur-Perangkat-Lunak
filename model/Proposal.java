package model;

import state.ProposalState;
import state.DraftState;
import observer.ProposalObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain model proposal PKM.
 * Berperan sebagai Context dalam State Pattern dan Subject dalam Observer Pattern.
 */
public class Proposal {

    // ── Identitas ─────────────────────────────────────────────────────────
    private final String id;
    private final String mahasiswaName;
    private final String mahasiswaNIM;
    private final String pkmType;
    private final String fileName;
    private final double fileSizeMB;
    private boolean isAuthenticated;

    // ── Atribut validasi per jenis PKM (diisi via builder sebelum processUpload) ──
    private boolean hasAnggaranKas;       // PKM-K: wajib ada anggaran kas
    private boolean susunanTimLengkap;    // PKM-K: susunan tim lengkap
    private boolean hasIzinLab;           // PKM-RE: surat izin laboratorium
    private boolean hasPembimbing;        // PKM-RE: pembimbing terdaftar
    private boolean hasReferensiJurnal;   // PKM-RE: referensi jurnal ilmiah
    private boolean hasSuratIzinMitra;    // PKM-PM: surat izin mitra komunitas
    private boolean hasLokasiKegiatan;    // PKM-PM: lokasi kegiatan terdefinisi

    // ── State & Observer ──────────────────────────────────────────────────
    private ProposalState currentState;
    private String filePath;
    private final List<ProposalObserver> observers = new ArrayList<>();

    public Proposal(String id, String mahasiswaName, String mahasiswaNIM,
                    String pkmType, String fileName, double fileSizeMB) {
        this.id              = id;
        this.mahasiswaName   = mahasiswaName;
        this.mahasiswaNIM    = mahasiswaNIM;
        this.pkmType         = pkmType;
        this.fileName        = fileName;
        this.fileSizeMB      = fileSizeMB;
        this.isAuthenticated = true;
        this.currentState    = new DraftState();
    }

    // ── Fluent builder API untuk atribut validasi ─────────────────────────

    /** PKM-K: menandai bahwa anggaran kas tersedia dalam proposal. */
    public Proposal withAnggaranKas(boolean v)     { this.hasAnggaranKas    = v; return this; }

    /** PKM-K: menandai bahwa susunan tim sudah lengkap. */
    public Proposal withSusunanTim(boolean v)      { this.susunanTimLengkap = v; return this; }

    /** PKM-RE: menandai bahwa surat izin laboratorium tersedia. */
    public Proposal withIzinLab(boolean v)         { this.hasIzinLab        = v; return this; }

    /** PKM-RE: menandai bahwa pembimbing sudah terdaftar. */
    public Proposal withPembimbing(boolean v)      { this.hasPembimbing     = v; return this; }

    /** PKM-RE: menandai bahwa referensi jurnal ilmiah tersedia. */
    public Proposal withReferensiJurnal(boolean v) { this.hasReferensiJurnal = v; return this; }

    /** PKM-PM: menandai bahwa surat izin mitra komunitas tersedia. */
    public Proposal withSuratIzinMitra(boolean v)  { this.hasSuratIzinMitra = v; return this; }

    /** PKM-PM: menandai bahwa lokasi kegiatan sudah terdefinisi. */
    public Proposal withLokasiKegiatan(boolean v)  { this.hasLokasiKegiatan = v; return this; }

    /** Override status autentikasi (default: true). */
    public Proposal withAuthenticated(boolean v)   { this.isAuthenticated   = v; return this; }

    // ── Delegasi aksi ke state aktif (State Pattern) ──────────────────────
    public void upload()              { currentState.upload(this); }
    public void verify()              { currentState.verify(this); }
    public void submit()              { currentState.submit(this); }
    public void reject(String reason) { currentState.reject(this, reason); }
    public void edit()                { currentState.edit(this); }

    /**
     * Transisi ke state baru sekaligus notifikasi otomatis semua observer.
     * Dipanggil oleh implementasi State — bukan oleh klien secara langsung.
     */
    public void setState(ProposalState newState) {
        String oldStatus = this.currentState.getStateName();
        this.currentState = newState;
        String newStatus  = newState.getStateName();
        for (ProposalObserver obs : observers) {
            obs.onStatusChanged(this, oldStatus, newStatus);
        }
    }

    // ── Observer management ───────────────────────────────────────────────
    public void addObserver(ProposalObserver observer) { observers.add(observer); }

    // ── Getters ───────────────────────────────────────────────────────────
    public String  getId()                 { return id; }
    public String  getMahasiswaName()      { return mahasiswaName; }
    public String  getMahasiswaNIM()       { return mahasiswaNIM; }
    public String  getPkmType()            { return pkmType; }
    public String  getFileName()           { return fileName; }
    public double  getFileSizeMB()         { return fileSizeMB; }
    public boolean isAuthenticated()       { return isAuthenticated; }
    public boolean hasAnggaranKas()        { return hasAnggaranKas; }
    public boolean isSusunanTimLengkap()   { return susunanTimLengkap; }
    public boolean hasIzinLab()            { return hasIzinLab; }
    public boolean hasPembimbing()         { return hasPembimbing; }
    public boolean hasReferensiJurnal()    { return hasReferensiJurnal; }
    public boolean hasSuratIzinMitra()     { return hasSuratIzinMitra; }
    public boolean hasLokasiKegiatan()     { return hasLokasiKegiatan; }
    public String  getStateName()          { return currentState.getStateName(); }
    public String  getFilePath()           { return filePath; }
    public void    setFilePath(String p)   { this.filePath = p; }
}
