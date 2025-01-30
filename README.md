# Define the new password
$newPassword = "YourSecurePassword123!"

# Update the password for the Administrator account
$adminAccount = [ADSI]("WinNT://./Administrator,User")
$adminAccount.SetPassword($newPassword)

# Confirm the password change
if ($?) {
    Write-Host "Password updated successfully for Administrator account."
} else {
    Write-Host "Failed to update the Administrator password." -ForegroundColor Red
}
