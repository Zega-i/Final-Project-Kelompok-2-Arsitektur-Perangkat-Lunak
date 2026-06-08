# 🎓 Simulasi Sistem PKM Upload - PoC Design Patterns

Selamat datang di repositori **Final Project Kelompok 2 - Arsitektur Perangkat Lunak**! 
Proyek ini adalah implementasi *Proof of Concept* (PoC) untuk **Sistem Unggah Proposal PKM (The PKM Upload Bottleneck)** menggunakan bahasa pemrograman Java.

---

## 🎯 Deskripsi Proyek

Proyek ini mensimulasikan alur unggah proposal PKM (Program Kreativitas Mahasiswa) mulai dari tahap draft hingga disetujui atau ditolak. Untuk mengatasi berbagai masalah desain perangkat lunak (seperti bottleneck, validasi yang kompleks, dan status yang rumit), kami menerapkan **5 Design Patterns** utama dari Gang of Four (GoF).

---

## 🛠️ Design Patterns yang Digunakan

| Pattern | Penjelasan Implementasi dalam Sistem |
| :--- | :--- |
| **State** | Mengatur siklus hidup status proposal: `DRAFT` ➜ `UPLOADING` ➜ `VERIFYING` ➜ `SUBMITTED` / `REJECTED`. |
| **Strategy** | Menangani logika validasi yang berbeda untuk setiap jenis PKM (PKM-K, PKM-RE, PKM-PM). |
| **Proxy** | Bertindak sebagai gerbang *pre-check* (mengecek tipe file & ukuran) sebelum file diteruskan dan disimpan ke *Object Storage*. |
| **Observer** | Mengirimkan notifikasi otomatis (Email) & mencatat log sistem secara otomatis saat status proposal berubah. |
| **Facade** | Menyembunyikan kompleksitas pipeline proses upload di balik satu method sederhana: `SubmitFacade.processUpload()`. |

---

## 🚀 Skenario Simulasi

Program ini dilengkapi dengan 6 skenario pengujian utama untuk mendemonstrasikan kelima design patterns di atas:

1. ✅ **Skenario 1:** Upload Sukses (PKM-K) ➜ Status Akhir: `SUBMITTED`
2. 🚫 **Skenario 2:** Ditolak Proxy (File Bukan PDF) ➜ Status Akhir: `DITOLAK`
3. 🚫 **Skenario 3:** Ditolak Proxy (Ukuran File > 5MB) ➜ Status Akhir: `DITOLAK`
4. ❌ **Skenario 4:** Validasi Gagal (PKM-RE tanpa izin lab) ➜ Status Akhir: `REJECTED`
5. ⛔ **Skenario 5:** Upload ulang saat status sedang VERIFYING ➜ Status Akhir: `DIBLOKIR` (State Block)
6. ✅ **Skenario 6:** Upload Sukses (PKM-PM) ➜ Status Akhir: `SUBMITTED`

---

## 💻 Cara Menjalankan Program

Pastikan Anda telah menginstal **Java Development Kit (JDK)** di perangkat Anda.

### 1. Kompilasi (Compile)
Buka terminal / PowerShell di direktori proyek, lalu jalankan perintah berikut:
```powershell
# Untuk pengguna Windows PowerShell
$files = Get-ChildItem -Recurse -Filter "*.java" | % { $_.FullName }
javac -encoding UTF-8 -cp . $files
```

### 2. Menjalankan (Run) Simulasi Terminal
Setelah dikompilasi, Anda dapat melihat skenario berjalan dengan perintah:
```powershell
java -cp . "-Dfile.encoding=UTF-8" Main
```

### 3. Menjalankan Mode Server (Opsional)
Jika Anda ingin menjalankan backend server sederhana dari simulasi ini:
```powershell
java -cp . "-Dfile.encoding=UTF-8" Main --server
```

---

## 👥 Tim Pengembang - Kelompok 2
*Proyek Akhir Mata Kuliah Arsitektur Perangkat Lunak*

