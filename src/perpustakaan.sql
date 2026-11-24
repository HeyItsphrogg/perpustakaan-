-- Skema sesuai diagram. Jalankan di phpMyAdmin (tab SQL).
DROP DATABASE IF EXISTS perpustakaan;
CREATE DATABASE perpustakaan CHARACTER SET utf8mb4;
USE perpustakaan;

-- ===== Login & peran =====
CREATE TABLE users (
  id_user  INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50)  NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nama     VARCHAR(100) NOT NULL,
  peran    ENUM('Admin','Petugas') NOT NULL
);

-- ===== Master data =====
CREATE TABLE kategori (
  id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
  nama_kategori VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE buku (
  id_buku     INT AUTO_INCREMENT PRIMARY KEY,
  judul       VARCHAR(200) NOT NULL,
  pengarang   VARCHAR(150) NOT NULL,
  id_kategori INT NOT NULL,
  stok        INT NOT NULL DEFAULT 0,
  CONSTRAINT chk_stok CHECK (stok >= 0),
  CONSTRAINT fk_buku_kategori FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori)
);

CREATE TABLE anggota (
  id_anggota INT AUTO_INCREMENT PRIMARY KEY,
  nama       VARCHAR(150) NOT NULL,
  jenis      ENUM('Mahasiswa','Dosen','Umum') NOT NULL,
  alamat     VARCHAR(255) NOT NULL,
  telepon    VARCHAR(20)  NOT NULL
);

-- ===== Master - Detail =====
CREATE TABLE peminjaman (                     -- header
  id_peminjaman    INT AUTO_INCREMENT PRIMARY KEY,
  id_anggota       INT  NOT NULL,
  id_user          INT  NOT NULL,             -- petugas yang melayani
  tgl_pinjam       DATE NOT NULL,
  tgl_jatuh_tempo  DATE NOT NULL,
  CONSTRAINT fk_pinjam_anggota FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota),
  CONSTRAINT fk_pinjam_user    FOREIGN KEY (id_user)    REFERENCES users(id_user)
);

CREATE TABLE detail_peminjaman (              -- detail
  id_detail     INT AUTO_INCREMENT PRIMARY KEY,
  id_peminjaman INT NOT NULL,
  id_buku       INT NOT NULL,
  tgl_kembali   DATE NULL,                    -- NULL = belum dikembalikan
  denda         INT  NOT NULL DEFAULT 0,
  CONSTRAINT fk_detail_pinjam FOREIGN KEY (id_peminjaman) REFERENCES peminjaman(id_peminjaman) ON DELETE CASCADE,
  CONSTRAINT fk_detail_buku   FOREIGN KEY (id_buku)       REFERENCES buku(id_buku)
);

-- ===== Tambahan =====
CREATE TABLE laporan (
  id_laporan       INT AUTO_INCREMENT PRIMARY KEY,
  id_user          INT  NOT NULL,
  tgl_awal         DATE NOT NULL,
  tgl_akhir        DATE NOT NULL,
  total_transaksi  INT  NOT NULL DEFAULT 0,
  total_buku       INT  NOT NULL DEFAULT 0,
  total_denda      INT  NOT NULL DEFAULT 0,
  tgl_dibuat       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_laporan_user FOREIGN KEY (id_user) REFERENCES users(id_user)
);

-- ===== Data awal =====
INSERT INTO users (username,password,nama,peran) VALUES
 ('admin','admin123','Administrator','Admin'),
 ('petugas01','petugas123','Petugas Satu','Petugas');

INSERT INTO kategori (nama_kategori) VALUES ('Teknologi'),('Pendidikan'),('Manajemen'),('Sastra');

INSERT INTO buku (judul,pengarang,id_kategori,stok) VALUES
 ('Pemrograman Java','Abdul Kadir',1,10),
 ('Basis Data','Rosa A.S.',1,8),
 ('Matematika Diskrit','Rinaldi Munir',2,5),
 ('Algoritma & Pemrograman','Adi Nugroho',1,12),
 ('Sistem Informasi','Jogiyanto',3,7);

INSERT INTO anggota (nama,jenis,alamat,telepon) VALUES
 ('Budi Santoso','Mahasiswa','Denpasar','081234567890'),
 ('Siti Aminah','Dosen','Badung','081298765432'),
 ('Agus Pratama','Umum','Gianyar','085612345678'),
 ('Made Putri','Mahasiswa','Tabanan','087812345678');
