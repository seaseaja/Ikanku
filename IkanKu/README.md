# Panduan Login Akun IkanKu

Aplikasi ini menggunakan **Role-based Simulation** (Simulasi Peran) yang dapat dipilih langsung melalui antarmuka Login.

Tidak diperlukan email/password khusus yang terdaftar di sistem backend, Anda bebas menggunakan kombinasi valid apa pun untuk masuk ke akun yang diinginkan.

---

### 1. Akun Pelanggan (User Biasa)
- **Role Tab**: Pastikan tab **"Pelanggan"** aktif (terpilih biru) di halaman login.
- **Email**: `customer@ikanku.id` *(atau format email valid apa saja)*
- **Password**: Bebas (minimal 8 karakter, contoh: `12345abc`)

### 2. Akun Jastiper (Partner Jastip)
- **Role Tab**: Pilih tab **"Jastiper"** di halaman login.
- **Email**: `jastiper@ikanku.id` *(atau format email valid apa saja)*
- **Password**: Bebas (minimal 8 karakter, contoh: `12345abc`)
>[!NOTE]
> Setelah login sebagai Jastiper, Anda berkesempatan mengelola tawaran antar, mengatur komisi jastip layaknya penjual (seller dashboard).

### 3. Akun Ekspedisi (Kurir)
- **Role Tab**: Pilih tab **"Kurir"** pada opsi role selection (berada di ujung kanan).
- **Email**: `kurir@ikanku.id` *(atau format email valid apa saja)*
- **Password**: Bebas (minimal 8 karakter, contoh: `12345abc`)
>[!NOTE]
> Layar ini khusus didesain untuk kurir melacak detail pengantaran alamat (Drop-Off/Pick-Up), rute map simulasi, serta pelaporan keluhan/kendala jalan raya.

---

**Catatan Umum**: 
1. Demi mempermudah pengujian (testing), simulasi *Log out* akan diarahkan kembali ke layar Login ini untuk mereset seluruh data sesi *role*.
2. Semua akun *(termasuk akun baru dari proses pendaftaran Jastip multi-step)* bersumber dari penunjukan role lokal perangkat (UI layer).
