# Script untuk mengecek status XAMPP dan koneksi
# Jalankan dengan: PowerShell -ExecutionPolicy Bypass -File check_xampp.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  XAMPP & Koneksi Checker" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Fungsi untuk test URL
function Test-Url {
    param($url, $description)
    
    try {
        $response = Invoke-WebRequest -Uri $url -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $description" -ForegroundColor Green
            Write-Host "   URL: $url" -ForegroundColor Gray
            return $true
        }
    } catch {
        Write-Host "❌ $description" -ForegroundColor Red
        Write-Host "   URL: $url" -ForegroundColor Gray
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Yellow
        return $false
    }
}

# Check 1: Apache Process
Write-Host "🔍 Checking Apache Process..." -ForegroundColor Cyan
$apacheProcess = Get-Process -Name "httpd" -ErrorAction SilentlyContinue
if ($apacheProcess) {
    Write-Host "✅ Apache is running (PID: $($apacheProcess[0].Id))" -ForegroundColor Green
} else {
    Write-Host "❌ Apache is NOT running!" -ForegroundColor Red
    Write-Host "   Solusi: Buka XAMPP Control Panel dan Start Apache" -ForegroundColor Yellow
}
Write-Host ""

# Check 2: MySQL Process
Write-Host "🔍 Checking MySQL Process..." -ForegroundColor Cyan
$mysqlProcess = Get-Process -Name "mysqld" -ErrorAction SilentlyContinue
if ($mysqlProcess) {
    Write-Host "✅ MySQL is running (PID: $($mysqlProcess[0].Id))" -ForegroundColor Green
} else {
    Write-Host "❌ MySQL is NOT running!" -ForegroundColor Red
    Write-Host "   Solusi: Buka XAMPP Control Panel dan Start MySQL" -ForegroundColor Yellow
}
Write-Host ""

# Check 3: Port 80 (Apache)
Write-Host "🔍 Checking Port 80..." -ForegroundColor Cyan
$port80 = Get-NetTCPConnection -LocalPort 80 -State Listen -ErrorAction SilentlyContinue
if ($port80) {
    $processId = $port80[0].OwningProcess
    $processName = (Get-Process -Id $processId).ProcessName
    Write-Host "✅ Port 80 is open (Process: $processName)" -ForegroundColor Green
} else {
    Write-Host "❌ Port 80 is NOT listening!" -ForegroundColor Red
    Write-Host "   Solusi: Start Apache atau ganti port ke 8080" -ForegroundColor Yellow
}
Write-Host ""

# Check 4: htdocs folder
Write-Host "🔍 Checking htdocs folder..." -ForegroundColor Cyan
$htdocsPath = "C:\xampp\htdocs\IkanKu"
if (Test-Path $htdocsPath) {
    $phpFiles = (Get-ChildItem -Path $htdocsPath -Filter "*.php" | Measure-Object).Count
    Write-Host "✅ Folder exists: $htdocsPath" -ForegroundColor Green
    Write-Host "   PHP files: $phpFiles" -ForegroundColor Gray
    
    # Check important files
    $importantFiles = @("koneksi.php", "login.php", "register.php", "test_koneksi.php")
    foreach ($file in $importantFiles) {
        $filePath = Join-Path $htdocsPath $file
        if (Test-Path $filePath) {
            Write-Host "   ✅ $file" -ForegroundColor Green
        } else {
            Write-Host "   ❌ $file - NOT FOUND!" -ForegroundColor Red
        }
    }
} else {
    Write-Host "❌ Folder NOT exists: $htdocsPath" -ForegroundColor Red
    Write-Host "   Solusi: Jalankan copy_to_htdocs.ps1" -ForegroundColor Yellow
}
Write-Host ""

# Check 5: Test URLs
Write-Host "🔍 Testing URLs..." -ForegroundColor Cyan

$test1 = Test-Url "http://localhost/" "XAMPP Dashboard"
$test2 = Test-Url "http://localhost/IkanKu/" "IkanKu Folder"
$test3 = Test-Url "http://localhost/IkanKu/test_koneksi.php" "Test Koneksi API"

Write-Host ""

# Check 6: Firewall
Write-Host "🔍 Checking Firewall..." -ForegroundColor Cyan
$firewallRules = Get-NetFirewallRule -DisplayName "*Apache*" -ErrorAction SilentlyContinue
if ($firewallRules) {
    Write-Host "✅ Firewall rules for Apache found" -ForegroundColor Green
    foreach ($rule in $firewallRules) {
        Write-Host "   - $($rule.DisplayName) [$($rule.Enabled)]" -ForegroundColor Gray
    }
} else {
    Write-Host "⚠️  No firewall rules for Apache found" -ForegroundColor Yellow
    Write-Host "   Jika koneksi gagal, tambahkan firewall exception" -ForegroundColor Yellow
}
Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$allGood = $apacheProcess -and $mysqlProcess -and $port80 -and (Test-Path $htdocsPath) -and $test3

if ($allGood) {
    Write-Host "✅ Semua OK! Server siap digunakan." -ForegroundColor Green
    Write-Host ""
    Write-Host "🚀 Langkah selanjutnya:" -ForegroundColor Cyan
    Write-Host "   1. Buka aplikasi Android" -ForegroundColor White
    Write-Host "   2. Test Register/Login" -ForegroundColor White
    Write-Host "   3. Jika masih error, cek PERBAIKAN_ERROR_KONEKSI.md" -ForegroundColor White
} else {
    Write-Host "❌ Ada masalah yang perlu diperbaiki!" -ForegroundColor Red
    Write-Host ""
    Write-Host "📋 Action Items:" -ForegroundColor Cyan
    
    if (-not $apacheProcess) {
        Write-Host "   [ ] Start Apache di XAMPP Control Panel" -ForegroundColor Yellow
    }
    if (-not $mysqlProcess) {
        Write-Host "   [ ] Start MySQL di XAMPP Control Panel" -ForegroundColor Yellow
    }
    if (-not (Test-Path $htdocsPath)) {
        Write-Host "   [ ] Jalankan copy_to_htdocs.ps1" -ForegroundColor Yellow
    }
    if (-not $test3) {
        Write-Host "   [ ] Setup database dengan setup_database.sql" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "📖 Baca panduan lengkap: PERBAIKAN_ERROR_KONEKSI.md" -ForegroundColor Cyan
}

Write-Host ""
Read-Host "Tekan Enter untuk keluar"
