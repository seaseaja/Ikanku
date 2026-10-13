# Script untuk copy file PHP ke htdocs XAMPP
# Jalankan dengan: PowerShell -ExecutionPolicy Bypass -File copy_to_htdocs.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Copy File PHP ke htdocs XAMPP" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Path folder
$sourceFolder = $PSScriptRoot  # Folder saat ini (AndroidStudioProjects\IkanKu)
$targetFolder = "C:\xampp\htdocs\IkanKu"

# Cek apakah folder htdocs ada
if (-not (Test-Path "C:\xampp\htdocs")) {
    Write-Host "❌ ERROR: Folder C:\xampp\htdocs tidak ditemukan!" -ForegroundColor Red
    Write-Host "   Pastikan XAMPP sudah terinstall dengan benar." -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Tekan Enter untuk keluar"
    exit
}

# Buat folder IkanKu di htdocs jika belum ada
if (-not (Test-Path $targetFolder)) {
    Write-Host "📁 Membuat folder: $targetFolder" -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $targetFolder -Force | Out-Null
}

Write-Host "📂 Source: $sourceFolder" -ForegroundColor Gray
Write-Host "📂 Target: $targetFolder" -ForegroundColor Gray
Write-Host ""

# Daftar file yang akan di-copy
$filesToCopy = @(
    "koneksi.php",
    "login.php",
    "register.php",
    "validator.php",
    "setup_database.sql",
    "test_koneksi.php",
    "debug_users.php",
    "delete_user.php",
    "admin_header.php",
    "admin_dashboard.php",
    "admin_verifikasi.php",
    "admin_moderasi.php",
    "admin_keuangan.php",
    "admin_komplain.php",
    "admin_kurir.php",
    "create_pesanan.php",
    "get_pesanan.php",
    "send_message.php",
    "update_profile.php"
)

$copiedCount = 0
$skippedCount = 0

Write-Host "🔄 Memulai copy file..." -ForegroundColor Cyan
Write-Host ""

foreach ($file in $filesToCopy) {
    $sourcePath = Join-Path $sourceFolder $file
    $targetPath = Join-Path $targetFolder $file
    
    if (Test-Path $sourcePath) {
        try {
            Copy-Item -Path $sourcePath -Destination $targetPath -Force
            Write-Host "✅ $file" -ForegroundColor Green
            $copiedCount++
        } catch {
            Write-Host "❌ $file - ERROR: $_" -ForegroundColor Red
        }
    } else {
        Write-Host "⏭️  $file - File tidak ditemukan, skip" -ForegroundColor Gray
        $skippedCount++
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Selesai!" -ForegroundColor Green
Write-Host "   File berhasil di-copy: $copiedCount" -ForegroundColor Green
Write-Host "   File di-skip: $skippedCount" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verifikasi
Write-Host "🔍 Verifikasi file di htdocs..." -ForegroundColor Cyan
$phpFiles = Get-ChildItem -Path $targetFolder -Filter "*.php" | Measure-Object
$sqlFiles = Get-ChildItem -Path $targetFolder -Filter "*.sql" | Measure-Object

Write-Host "   File PHP: $($phpFiles.Count)" -ForegroundColor White
Write-Host "   File SQL: $($sqlFiles.Count)" -ForegroundColor White
Write-Host ""

# Test koneksi
Write-Host "🧪 Langkah selanjutnya:" -ForegroundColor Cyan
Write-Host "   1. Pastikan XAMPP Apache & MySQL running" -ForegroundColor White
Write-Host "   2. Buka: http://localhost/phpmyadmin" -ForegroundColor White
Write-Host "   3. Jalankan file setup_database.sql" -ForegroundColor White
Write-Host "   4. Test koneksi: http://localhost/IkanKu/test_koneksi.php" -ForegroundColor White
Write-Host ""

Read-Host "Tekan Enter untuk keluar"
