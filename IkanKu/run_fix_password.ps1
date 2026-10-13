# Script untuk fix password hash
# Jalankan dengan: PowerShell -ExecutionPolicy Bypass -File run_fix_password.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fix Password Hash - IkanKu" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$sourceFile = Join-Path $PSScriptRoot "fix_password_hash.php"
$targetFile = "C:\xampp\htdocs\IkanKu\fix_password_hash.php"

# Step 1: Copy file
Write-Host "📂 Step 1: Copy file ke htdocs..." -ForegroundColor Cyan
if (Test-Path $sourceFile) {
    try {
        Copy-Item -Path $sourceFile -Destination $targetFile -Force
        Write-Host "✅ File berhasil di-copy!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Gagal copy file: $_" -ForegroundColor Red
        Read-Host "Tekan Enter untuk keluar"
        exit
    }
} else {
    Write-Host "❌ File fix_password_hash.php tidak ditemukan!" -ForegroundColor Red
    Read-Host "Tekan Enter untuk keluar"
    exit
}

Write-Host ""

# Step 2: Run script
Write-Host "🚀 Step 2: Menjalankan script..." -ForegroundColor Cyan
Write-Host "   Membuka browser..." -ForegroundColor Gray

Start-Sleep -Seconds 1
Start-Process "http://localhost/IkanKu/fix_password_hash.php"

Write-Host ""
Write-Host "✅ Browser terbuka!" -ForegroundColor Green
Write-Host ""

# Wait for user confirmation
Write-Host "📋 Instruksi:" -ForegroundColor Yellow
Write-Host "   1. Lihat hasil di browser" -ForegroundColor White
Write-Host "   2. Pastikan status: success" -ForegroundColor White
Write-Host "   3. Catat jumlah password yang di-update" -ForegroundColor White
Write-Host ""

$continue = Read-Host "Apakah script berhasil dijalankan? (y/n)"

if ($continue -eq "y" -or $continue -eq "Y") {
    # Step 3: Verify
    Write-Host ""
    Write-Host "🔍 Step 3: Verifikasi..." -ForegroundColor Cyan
    Write-Host "   Membuka debug_users.php..." -ForegroundColor Gray
    
    Start-Sleep -Seconds 1
    Start-Process "http://localhost/IkanKu/debug_users.php"
    
    Write-Host ""
    Write-Host "✅ Verifikasi terbuka!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📋 Cek di browser:" -ForegroundColor Yellow
    Write-Host "   - Semua password_preview harus dimulai dengan `$2y`$10`$..." -ForegroundColor White
    Write-Host ""
    
    $verified = Read-Host "Apakah semua password sudah di-hash? (y/n)"
    
    if ($verified -eq "y" -or $verified -eq "Y") {
        # Step 4: Delete script
        Write-Host ""
        Write-Host "🗑️  Step 4: Hapus file script..." -ForegroundColor Cyan
        
        try {
            Remove-Item -Path $targetFile -Force
            Write-Host "✅ File fix_password_hash.php berhasil dihapus!" -ForegroundColor Green
        } catch {
            Write-Host "⚠️  Gagal hapus file otomatis: $_" -ForegroundColor Yellow
            Write-Host "   Silakan hapus manual: $targetFile" -ForegroundColor Yellow
        }
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "  ✅ SELESAI!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "🎉 Password berhasil di-hash!" -ForegroundColor Green
        Write-Host "   Sekarang semua password tersimpan dengan aman." -ForegroundColor White
        Write-Host ""
        Write-Host "🧪 Test login:" -ForegroundColor Cyan
        Write-Host "   - Buka aplikasi Android" -ForegroundColor White
        Write-Host "   - Login dengan user yang ada" -ForegroundColor White
        Write-Host "   - Seharusnya tetap bisa login!" -ForegroundColor White
        
    } else {
        Write-Host ""
        Write-Host "⚠️  Ada masalah dengan hashing!" -ForegroundColor Yellow
        Write-Host "   Cek error di browser atau hubungi support." -ForegroundColor Yellow
    }
    
} else {
    Write-Host ""
    Write-Host "⚠️  Script dibatalkan!" -ForegroundColor Yellow
    Write-Host "   File fix_password_hash.php masih ada di htdocs." -ForegroundColor Yellow
    Write-Host "   Hapus manual jika tidak digunakan: $targetFile" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Tekan Enter untuk keluar"
