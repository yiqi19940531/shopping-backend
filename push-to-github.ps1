# PowerShell script to push all files to GitHub
# This script will help you push all project files once Git is installed

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Shopping Backend - GitHub Push Script" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Check if Git is installed
try {
    $gitVersion = git --version
    Write-Host "✓ Git is installed: $gitVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Git is not installed" -ForegroundColor Red
    Write-Host "`nPlease install Git first:" -ForegroundColor Yellow
    Write-Host "  1. Download from: https://git-scm.com/download/win" -ForegroundColor Yellow
    Write-Host "  2. Or use: https://registry.npmmirror.com/binary.html?path=git-for-windows/" -ForegroundColor Yellow
    Write-Host "`nAfter installation, restart PowerShell and run this script again." -ForegroundColor Yellow
    exit 1
}

Write-Host "`n" 
Write-Host "Repository: https://github.com/yiqi19940531/shopping-backend" -ForegroundColor Cyan
Write-Host "`n"

# Initialize Git repository if needed
if (-not (Test-Path ".git")) {
    Write-Host "Initializing Git repository..." -ForegroundColor Yellow
    git init
    Write-Host "✓ Git repository initialized" -ForegroundColor Green
} else {
    Write-Host "✓ Git repository already exists" -ForegroundColor Green
}

# Configure Git (optional, uncomment and fill in your details if needed)
# git config user.name "Your Name"
# git config user.email "your.email@example.com"

# Add all files
Write-Host "`nAdding files to Git..." -ForegroundColor Yellow
git add .
Write-Host "✓ Files added" -ForegroundColor Green

# Commit
Write-Host "`nCommitting changes..." -ForegroundColor Yellow
git commit -m "Initial commit: Shopping Backend System with Spring Boot 3.2.5"
Write-Host "✓ Changes committed" -ForegroundColor Green

# Set branch to main
Write-Host "`nSetting branch to main..." -ForegroundColor Yellow
git branch -M main
Write-Host "✓ Branch set to main" -ForegroundColor Green

# Add remote origin
Write-Host "`nConfiguring remote repository..." -ForegroundColor Yellow
git remote remove origin 2>$null
git remote add origin https://github.com/yiqi19940531/shopping-backend.git
Write-Host "✓ Remote repository configured" -ForegroundColor Green

# Push to GitHub
Write-Host "`nPushing to GitHub..." -ForegroundColor Yellow
Write-Host "(You may need to enter your GitHub credentials)" -ForegroundColor Cyan
git push -u origin main --force

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "✓ Successfully pushed to GitHub!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "`nView your repository at:" -ForegroundColor Cyan
    Write-Host "https://github.com/yiqi19940531/shopping-backend" -ForegroundColor Cyan
} else {
    Write-Host "`n✗ Push failed. Please check the error messages above." -ForegroundColor Red
}
