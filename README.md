# 📱 ScanMate

> Aplikasi mobile berbasis QR Code untuk monitoring jadwal dan status ruangan kampus secara real-time.

![Java](https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![ZXing](https://img.shields.io/badge/ZXING-663399?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAMAAAC6sB3iAAAAG1BMVEUAAAD/AAAAAAD///8AAP8A/wD//wAA/wAA//+P/WdhAAAAAXRSTlMAQObYZgAAABdJREFUCNdjYGBgYGRgYGBgAAIwAxFQBwAALh4D/TdLbNoAAAAASUVORK5CYII=)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

---

## 🧠 Tentang ScanMate

**ScanMate** adalah aplikasi mobile berbasis Android yang memanfaatkan teknologi **QR Code** untuk menampilkan **jadwal dan status ruangan kampus** secara real-time. Dirancang untuk mencegah bentrok ruangan, meningkatkan efisiensi manajemen ruang, dan mendukung perkuliahan yang tertib serta transparan.

---

## 🚀 Fitur Utama

✅ **Scan QR Ruangan**  
📅 **Lihat Jadwal Mingguan Ruangan**  
📝 **Booking Ruangan (Role-based)**  
🔔 **Notifikasi Bentrok Jadwal**  
📌 **Favoritkan Ruangan**  
📍 **Denah Kampus Interaktif**  
👥 **Delegasi Akses Ketua Kelas**  
🛠 **Panel Admin via Firebase Console**  

---

## 🛠 Teknologi yang Digunakan

- **Java + Kotlin** – Bahasa pemrograman utama
- **Firebase Authentication** – Sistem login & register
- **Firebase Realtime Database / Firestore** – Manajemen data pengguna, jadwal, dan ruangan
- **ZXing (Zebra Crossing)** – Library open-source untuk pemindaian QR Code
- **Android Studio** – IDE untuk pengembangan dan debugging
- **Figma** – Desain UI/UX
- **GitHub** – Versi kontrol proyek

---

## 🖥️ Struktur Folder

```bash
Fix-ScanMate/
└── app/
    └── src/
        └── java/
            └── com/
                └── scanmate/
                    ├── activities/
                    ├── adapters/
                    ├── models/
                    └── utils/
    └── res/
        ├── layout/
        ├── drawable/
        └── values/
├── build.gradle
├── google-services.json
└── README.md
```


## 🧩 Role & Hak Akses
|--------------------------------------------------------------|
| Role              | Akses                                    |
|-------------------|------------------------------------------|
| Mahasiswa Biasa   | Melihat jadwal & status ruangan          |
| Ketua/Wakil Kelas | Booking ruangan & delegasi akses         |
| Delegasi          | Akses booking sementara                  |
| Admin             | Mengelola jadwal, user, validasi booking |
|--------------------------------------------------------------|

---

## 📸 Preview Aplikasi

> ✨ (Tambahkan screenshot aplikasi jika tersedia di folder `assets/`)

---

## ⚙️ Cara Menjalankan Proyek

1. Clone repository:
```bash
git clone https://github.com/your-username/scanmate-app.git
cd scanmate-app
```
2. Buka di Android Studio dan sync Gradle
3. Tambahkan file google-services.json ke folder app/.
4. Jalankan di emulator atau perangkat Android fisik


🏁 Roadmap Pengembangan
 > Scan QR & tampilkan info ruangan
 > Role-based booking
 > Delegasi akses
 > Notifikasi bentrok
 > Peta denah kampus interaktif
 > Mode offline cache
 > Web admin panel

## 🔍 Tampilan Fitur ScanMate

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Danthemen51/assets/main/11.png" width="170"/></td>
    <td><img src="https://raw.githubusercontent.com/Danthemen51/assets/main/4.jpg" width="170"/></td>
    <td><img src="https://raw.githubusercontent.com/Danthemen51/assets/main/10.jpg" width="170"/></td>
      <td><img src="https://raw.githubusercontent.com/Danthemen51/assets/main/2.jpg" width="170"/></td>
      <td><img src="https://raw.githubusercontent.com/Danthemen51/assets/main/3.jpg" width="170"/></td>
  </tr>
  <tr>
    <td align="center">Scan QR</td>
    <td align="center">Dahboard</td>
    <td align="center">Booking</td>
    <td align="center">Login</td>
    <td align="center">Register</td>
  </tr>
</table>


👨‍💻 Kontributor
Kelompok TIF RP 23I - Universitas Teknologi Bandung
> Dandi Mulyana (232101182)


📄 Lisensi
Proyek ini dibuat sebagai bagian dari tugas mata kuliah Pemrograman Mobile 1 dan bebas digunakan untuk keperluan akademik. Lisensi akan ditambahkan di rilis final.

🔥 Spesial Thanks
Dosen Pengampu: Muhammad Ikhwan Fathulloh, S.Kom.
Untuk bimbingan dan dukungannya dalam menyelesaikan proyek ini.


---

📌 **Catatan:**
- Jika kamu ingin menambahkan **GIF atau screenshot**, buat folder `assets/` lalu tautkan: `![preview](assets/screenshot1.png)`
- Link badge bisa dikustomisasi atau diperluas sesuai tools tambahan (misalnya Kotlin, Android Jetpack, dll.)

Perlu bantuan membuat **README bilingual (Indonesia + Inggris)?** atau versi **Markdown dengan HTML mix** untuk layout lebih kompleks? Saya siap bantu.
