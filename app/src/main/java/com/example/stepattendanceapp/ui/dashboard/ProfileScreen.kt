package com.example.stepattendanceapp.ui.dashboard

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.stepattendanceapp.R
import com.example.stepattendanceapp.data.model.UserDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState = profileViewModel.uiState
    val context = LocalContext.current

    // Fetch the profile when the screen is first composed
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    // --- Listen for Profile Update messages ---
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            profileViewModel.clearUpdateSuccess()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
            profileViewModel.clearErrorMessage()
        }
    }

    // --- Listen for Password Update messages ---
    LaunchedEffect(uiState.passwordSuccessMessage) {
        if (uiState.passwordSuccessMessage != null) {
            Toast.makeText(context, uiState.passwordSuccessMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.clearPasswordSuccessMessage()
        }
    }
    LaunchedEffect(uiState.passwordErrorMessage) {
        if (uiState.passwordErrorMessage != null) {
            Toast.makeText(context, uiState.passwordErrorMessage, Toast.LENGTH_LONG).show()
            profileViewModel.clearPasswordErrorMessage()
        }
    }

    // --- Listen for 2FA messages ---
    LaunchedEffect(uiState.twoFaSuccessMessage) {
        if (uiState.twoFaSuccessMessage != null) {
            Toast.makeText(context, uiState.twoFaSuccessMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.clearTwoFaSuccessMessage()
        }
    }
    LaunchedEffect(uiState.twoFaErrorMessage) {
        if (uiState.twoFaErrorMessage != null) {
            Toast.makeText(context, uiState.twoFaErrorMessage, Toast.LENGTH_LONG).show()
            profileViewModel.clearTwoFaErrorMessage()
        }
    }

    // --- Main Screen Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            // Show a full-screen loader if the VM is busy (initial fetch)
            uiState.isLoading && uiState.user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Show error if the initial fetch failed
            uiState.errorMessage != null && uiState.user == null -> {
                Text(text = "Error: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
            }
            // Show profile content
            uiState.user != null -> {
                UserProfileLayout(
                    user = uiState.user,
                    onLogoutClick = onLogoutClick,
                    viewModel = profileViewModel
                )
            }
        }
    }
}

@Composable
private fun UserProfileLayout(
    user: UserDetail,
    onLogoutClick: () -> Unit,
    viewModel: ProfileViewModel
) {
    // --- Top Section: Profile Picture ---
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_github_logo), // Placeholder
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user.fullName ?: "N/A",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // --- Editable Information Card ---
    EditableInfoCard(user = user, viewModel = viewModel)

    Spacer(modifier = Modifier.height(24.dp))

    // --- Change Password Card ---
    ChangePasswordCard(
        isLoading = viewModel.uiState.isPasswordLoading,
        onChangePassword = { current, new, confirm ->
            viewModel.changePassword(current, new, confirm)
        }
    )

    Spacer(modifier = Modifier.height(24.dp))

    // --- 2FA Card ---
    TwoFactorAuthCard(viewModel = viewModel)

    Spacer(modifier = Modifier.height(24.dp))

    // --- Logout Card ---
    LogoutCard(onLogoutClick = onLogoutClick)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableInfoCard(user: UserDetail, viewModel: ProfileViewModel) {
    var isEditMode by remember { mutableStateOf(false) }

    // State for editable fields
    var fullName by remember { mutableStateOf(user.fullName ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }
    var gender by remember { mutableStateOf(user.gender ?: "") }
    var dateOfBirth by remember { mutableStateOf(user.dateOfBirth ?: "") }

    // State for error messages
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // State for Gender dropdown
    val genderOptions = listOf("MALE", "FEMALE")
    var genderMenuExpanded by remember { mutableStateOf(false) }

    // State for Date Picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Resets local state if the user data from ViewModel changes (e.g., after a save)
    LaunchedEffect(user) {
        if (!isEditMode) {
            fullName = user.fullName ?: ""
            email = user.email ?: ""
            phoneNumber = user.phoneNumber ?: ""
            gender = user.gender ?: ""
            dateOfBirth = user.dateOfBirth ?: ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Read-Only Fields ---
            OutlinedTextField(value = user.id ?: "Not Provided", onValueChange = {}, label = { Text("User ID") }, leadingIcon = { Icon(Icons.Default.Badge, null) }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = user.username ?: "Not Provided", onValueChange = {}, label = { Text("Username") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = user.role ?: "Not Provided", onValueChange = {}, label = { Text("Role") }, readOnly = true, modifier = Modifier.fillMaxWidth())

            HorizontalDivider()

            // --- Editable Fields with Validation ---
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; fullNameError = null },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                readOnly = !isEditMode,
                modifier = Modifier.fillMaxWidth(),
                isError = fullNameError != null,
                supportingText = { if (fullNameError != null) Text(fullNameError!!) }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                readOnly = !isEditMode,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = { if (emailError != null) Text(emailError!!) }
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it; phoneError = null },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                readOnly = !isEditMode,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError != null,
                supportingText = { if (phoneError != null) Text(phoneError!!) }
            )

            ExposedDropdownMenuBox(
                expanded = genderMenuExpanded && isEditMode,
                onExpandedChange = { if (isEditMode) genderMenuExpanded = !genderMenuExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    readOnly = true,
                    trailingIcon = { if (isEditMode) ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderMenuExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = genderMenuExpanded,
                    onDismissRequest = { genderMenuExpanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                genderMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {},
                label = { Text("Date of Birth") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable(enabled = isEditMode) { showDatePicker = true },
                trailingIcon = { if (isEditMode) Icon(Icons.Default.CalendarToday, contentDescription = "Open Date Picker") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Action Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isEditMode) Arrangement.SpaceBetween else Arrangement.End
            ) {
                if (isEditMode) {
                    OutlinedButton(onClick = {
                        // Reset fields to original values and exit edit mode
                        fullName = user.fullName ?: ""
                        email = user.email ?: ""
                        phoneNumber = user.phoneNumber ?: ""
                        gender = user.gender ?: ""
                        dateOfBirth = user.dateOfBirth ?: ""
                        isEditMode = false
                    }) { Text("Cancel") }

                    Button(onClick = {
                        // --- Run Validation ---
                        fullNameError = if (fullName.length < 3 || fullName.length > 50) "Must be 3-50 chars." else if (!fullName.matches("^[a-zA-Z ]+$".toRegex())) "Letters and spaces only." else null
                        emailError = if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Invalid email format." else null
                        phoneError = if (phoneNumber.isNotBlank() && phoneNumber.length < 8) "Must be at least 8 digits." else null

                        if (fullNameError == null && emailError == null && phoneError == null) {
                            // Call ViewModel to save data
                            viewModel.updateProfile(
                                fullName = fullName,
                                email = email,
                                phoneNumber = phoneNumber,
                                gender = gender,
                                dateOfBirth = dateOfBirth
                            )
                            isEditMode = false
                        }
                    }) {
                        Text("Save Changes")
                    }
                } else {
                    Button(onClick = { isEditMode = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }
        }
    }

    // --- Date Picker Dialog ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateOfBirth = formatter.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordCard(
    isLoading: Boolean,
    onChangePassword: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Client-side validation state
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Clear fields on success
    val context = LocalContext.current
    LaunchedEffect(key1 = isLoading) {
        // If it was loading and now it's not (and there's no error), it was a success
        if (!isLoading && passwordError == null) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Change Password", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (currentPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isLoading
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; passwordError = null },
                label = { Text("New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isLoading,
                isError = passwordError != null
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; passwordError = null },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(image, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isLoading,
                isError = passwordError != null,
                supportingText = { if (passwordError != null) Text(passwordError!!) }
            )
            Button(
                onClick = {
                    if (newPassword.length < 6) {
                        passwordError = "New password must be at least 6 characters."
                    } else if (newPassword != confirmPassword) {
                        passwordError = "Passwords do not match."
                    } else {
                        passwordError = null
                        onChangePassword(currentPassword, newPassword, confirmPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Update Password")
                }
            }
        }
    }
}

// --- 2FA Setup Card ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TwoFactorAuthCard(viewModel: ProfileViewModel) {
    val uiState = viewModel.uiState
    var verificationCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Two-Factor Authentication", style = MaterialTheme.typography.titleMedium)

            // If 2FA setup isn't started, show the "Enable" button
            if (uiState.twoFaSetupInfo == null) {
                Text(
                    "Enable 2FA to add an extra layer of security to your account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.setupTwoFa() },
                    enabled = !uiState.isTwoFaLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isTwoFaLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Enable 2FA")
                    }
                }
            } else {
                // 2FA setup has started, show the QR code and verification
                Text(
                    "Scan the QR code with your authenticator app, or enter the key manually.",
                    style = MaterialTheme.typography.bodyMedium
                )

                AsyncImage(
                    model = uiState.twoFaSetupInfo.qrCodeUrl,
                    contentDescription = "2FA QR Code",
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = uiState.twoFaSetupInfo.secretKey,
                    onValueChange = {},
                    label = { Text("Secret Key") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) verificationCode = it },
                    label = { Text("6-Digit Verification Code") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = uiState.isTwoFaLoading
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = { viewModel.cancelTwoFaSetup() }, enabled = !uiState.isTwoFaLoading) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { viewModel.verifyTwoFa(verificationCode) },
                        enabled = !uiState.isTwoFaLoading && verificationCode.length == 6
                    ) {
                        if (uiState.isTwoFaLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Verify")
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun LogoutCard(onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Log Out")
        }
    }
}