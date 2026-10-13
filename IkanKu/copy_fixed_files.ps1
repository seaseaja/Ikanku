# Script untuk copy file yang sudah diperbaiki ke htdocs
# Jalankan dengan: PowerShell -ExecutionPolicy Bypass -File copy_fixed_files.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Copy Fixed Files ke htdocs" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$sourceFolder = $PSScriptRoot
$targetFolder = "C:\xampp\htdocs\IkanKu"

# Cek folder htdocs
if (-not (Test-Path "C:\xampp\htdocs")) {
    Write-Host "❌ ERROR: Folder C:\xampp\htdocs tidak ditemukan!" -ForegroundColor Red
    Write-Host "   Pastikan XAMPP sudah terinstall." -ForegroundColor Yellow
    Read-Host "Tekan Enter untuk keluar"
    exit
}

# Buat folder IkanKu jika belum ada
if (-not (Test-Path $targetFolder)) {
    Write-Host "📁 Membuat folder: $targetFolder" -ForegroundColor Yellow
    New-Item -ItemType Directory -Path $targetFolder -Force | Out-Null
}

Write-Host "📂 Source: $sourceFolder" -ForegroundColor Gray
Write-Host "📂 Target: $targetFolder" -ForegroundColor Gray
Write-Host ""

# File yang akan di-copy
$filesToCopy = @(
    @{name="register.php"; desc="Register API (FIXED)"},
    @{name="login.php"; desc="Login API (FIXED)"},
    @{name="koneksi.php"; desc="Database Connection"},
    @{name="validator.php"; desc="Password Validator"},
    @{name="test_koneksi.php"; desc="Test Connection"},
    @{name="debug_users.php"; desc="Debug Users"},
    @{name="delete_user.php"; desc="Delete User"},
    @{name="fix_password_hash.php"; desc="Fix Password Hash"}
)

$copied = 0
$failed = 0

Write-Host "🔄 Copying files..." -ForegroundColor Cyan
Write-Host ""

foreach ($file in $filesToCopy) {
    $sourcePath = Join-Path $sourceFolder $file.name
    $targetPath = Join-Path $targetFolder $file.name
    
    if (Test-Path $sourcePath) {
        try {
            Copy-Item -Path $sourcePath -Destination $targetPath -Force
            Write-Host "✅ $($file.name) - $($file.desc)" -ForegroundColor Green
            $copied++
        } catch {
            Write-Host "❌ $($file.name) - ERROR: $_" -ForegroundColor Red
            $failed++
        }
    } else {
        Write-Host "⏭️  $($file.name) - File tidak ditemukan, skip" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Selesai!" -ForegroundColor Green
Write-Host "   Berhasil: $copied file" -ForegroundColor Green
if ($failed -gt 0) {
    Write-Host "   Gagal: $failed file" -ForegroundColor Red
}
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verifikasi file penting
Write-Host "🔍 Verifikasi file penting..." -ForegroundColor Cyan

$importantFiles = @("register.php", "login.php", "koneksi.php")
$allGood = $true

foreach ($file in $importantFiles) {
    $path = Join-Path $targetFolder $file
    if (Test-Path $path) {
        Write-Host "   ✅ $file" -ForegroundColor Green
    } else {
        Write-Host "   ❌ $file - MISSING!" -ForegroundColor Red
        $allGood = $false
    }
}

Write-Host ""

if ($allGood) {
    Write-Host "🎉 Semua file penting sudah ada!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🧪 Test sekarang:" -ForegroundColor Cyan
    Write-Host "   1. Browser: http://localhost/IkanKu/test_koneksi.php" -ForegroundColor White
    Write-Host "   2. Aplikasi Android: Test Register/Login" -ForegroundColor White
    Write-Host ""
    
    $openBrowser = Read-Host "Buka test_koneksi.php di browser? (y/n)"
    if ($openBrowser -eq "y" -or $openBrowser -eq "Y") {
        Start-Process "http://localhost/IkanKu/test_koneksi.php"
        Write-Host "✅ Browser dibuka!" -ForegroundColor Green
    }
} else {
    Write-Host "⚠️  Ada file yang hilang!" -ForegroundColor Yellow
    Write-Host "   Cek folder source: $sourceFolder" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Tekan Enter untuk keluar"
