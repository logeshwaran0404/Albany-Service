document.addEventListener('DOMContentLoaded', function() {
    // Tab switching functionality
    const loginTab = document.getElementById('login-tab');
    const registerTab = document.getElementById('register-tab');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const otpForm = document.getElementById('otp-form');
    const switchToRegister = document.getElementById('switch-to-register');
    const switchToLogin = document.getElementById('switch-to-login');

    // Form elements
    const loginFormEl = document.getElementById('loginForm');
    const registerFormEl = document.getElementById('registerForm');
    const otpFormEl = document.getElementById('otpForm');
    const displayEmail = document.getElementById('display-email');
    const changeEmail = document.getElementById('change-email');
    const resendOtpBtn = document.getElementById('resend-otp');
    const countdownEl = document.getElementById('countdown');

    // OTP input fields
    const otpInputs = [
        document.getElementById('otp-1'),
        document.getElementById('otp-2'),
        document.getElementById('otp-3'),
        document.getElementById('otp-4')
    ];

    // Timer variables
    let countdownInterval;
    let seconds = 30; // 30 seconds
    let isLoginFlow = true; // Track if we're in login or registration flow

    // Function to switch to login tab
    function showLoginForm() {
        loginTab.classList.add('active');
        registerTab.classList.remove('active');
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        otpForm.classList.add('hidden');
    }

    // Function to switch to register tab
    function showRegisterForm() {
        registerTab.classList.add('active');
        loginTab.classList.remove('active');
        registerForm.classList.remove('hidden');
        loginForm.classList.add('hidden');
        otpForm.classList.add('hidden');
    }

    // Function to show OTP form
    function showOtpForm(isLogin = false) {
        isLoginFlow = isLogin;

        if (isLogin) {
            loginTab.classList.add('active');
            registerTab.classList.remove('active');
        } else {
            loginTab.classList.remove('active');
            registerTab.classList.add('active');
        }

        otpForm.classList.remove('hidden');
        loginForm.classList.add('hidden');
        registerForm.classList.add('hidden');

        // Set email in the OTP form
        const email = isLogin ?
            document.getElementById('login-email').value :
            document.getElementById('register-email').value;

        displayEmail.textContent = email;

        // Focus the first OTP input
        otpInputs[0].focus();

        // Start the countdown
        startCountdown();
    }

    // Function to start the countdown timer
    function startCountdown() {
        // Reset timer
        seconds = 30;
        updateCountdownDisplay();

        // Clear any existing intervals
        if (countdownInterval) clearInterval(countdownInterval);

        // Disable resend button
        resendOtpBtn.disabled = true;

        // Start the interval
        countdownInterval = setInterval(() => {
            seconds--;

            if (seconds <= 0) {
                clearInterval(countdownInterval);
                resendOtpBtn.disabled = false;
            }

            updateCountdownDisplay();
        }, 1000);
    }

    // Function to update the countdown display
    function updateCountdownDisplay() {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        countdownEl.textContent = `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
    }

    // Tab event listeners
    loginTab.addEventListener('click', showLoginForm);
    registerTab.addEventListener('click', showRegisterForm);

    // Ensure "Register now" and "Sign in" links work properly
    if (switchToRegister) {
        switchToRegister.addEventListener('click', (e) => {
            e.preventDefault();
            showRegisterForm();
        });
    }

    if (switchToLogin) {
        switchToLogin.addEventListener('click', (e) => {
            e.preventDefault();
            showLoginForm();
        });
    }

    // OTP input handling
    otpInputs.forEach((input, index) => {
        // Auto focus next input
        input.addEventListener('input', (e) => {
            if (e.target.value && index < otpInputs.length - 1) {
                otpInputs[index + 1].focus();
            }
        });

        // Handle backspace
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                otpInputs[index - 1].focus();
            }
        });

        // Validate numeric input
        input.addEventListener('input', (e) => {
            e.target.value = e.target.value.replace(/[^0-9]/g, '');
        });
    });

    // Login form submission - UPDATED TO CALL API
    loginFormEl.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;

        // Validate email format
        if (!validateEmail(email)) {
            showError(document.getElementById('login-email'), 'Please enter a valid email address');
            return;
        }

        // Call the login API
        fetch('/api/auth/login/otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Show OTP form
                    showOtpForm(true);
                } else {
                    showFormError(loginFormEl, data.message || 'Failed to send OTP. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFormError(loginFormEl, 'An error occurred. Please try again.');
            });
    });

    // Registration form submission - UPDATED TO CALL API
    // Update the registration form submission code in your auth.js file:

// Registration form submission
    registerFormEl.addEventListener('submit', (e) => {
        e.preventDefault();
        const name = document.getElementById('register-name').value;
        const email = document.getElementById('register-email').value;

        // Get mobile number if it exists, otherwise set to empty string
        // This makes it optional in the UI
        const mobileInput = document.getElementById('register-mobile');
        const mobileNumber = mobileInput ? mobileInput.value : "";

        // Validate email format
        if (!validateEmail(email)) {
            showError(document.getElementById('register-email'), 'Please enter a valid email address');
            return;
        }

        // Call the registration API
        fetch('/api/auth/register/otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: name,
                email: email,
                mobileNumber: mobileNumber  // Send whatever value we have, empty is fine
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Show OTP form
                    showOtpForm(false);
                } else {
                    showFormError(registerFormEl, data.message || 'Registration failed. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFormError(registerFormEl, 'An error occurred. Please try again.');
            });
    });

    // OTP verification form submission - UPDATED TO CALL API
    otpFormEl.addEventListener('submit', (e) => {
        e.preventDefault();

        // Get OTP values
        const otp = otpInputs.map(input => input.value).join('');

        // Validate OTP length
        if (otp.length !== 4) {
            // Show error
            showFormError(otpFormEl, 'Please enter a valid 4-digit code');
            return;
        }

        // Get email
        const email = displayEmail.textContent;

        // Call appropriate API based on flow
        const url = isLoginFlow ? '/api/auth/login/verify' : '/api/auth/register/verify';

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                otp: otp
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    if (isLoginFlow) {
                        // Store token in localStorage if present
                        if (data.data) {
                            localStorage.setItem('authToken', data.data);
                        }
                        showLoginSuccess();
                    } else {
                        showRegistrationSuccess();
                    }
                } else {
                    showFormError(otpFormEl, data.message || 'Verification failed. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFormError(otpFormEl, 'An error occurred. Please try again.');
            });
    });

    // Resend OTP - UPDATED TO CALL API
    resendOtpBtn.addEventListener('click', () => {
        const email = displayEmail.textContent;

        fetch('/api/auth/otp/resend', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Restart countdown
                    startCountdown();

                    // Show success message
                    showFormSuccess(otpFormEl, 'A new verification code has been sent');
                } else {
                    showFormError(otpFormEl, data.message || 'Failed to resend OTP. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showFormError(otpFormEl, 'An error occurred. Please try again.');
            });
    });

    // Change email event listeners
    changeEmail.addEventListener('click', goBackToEmailForm);
    document.getElementById('edit-email').addEventListener('click', goBackToEmailForm);

    // Function to go back to email form
    function goBackToEmailForm(e) {
        e.preventDefault();

        // Determine if we should go back to login or register
        if (isLoginFlow) {
            showLoginForm();
        } else {
            showRegisterForm();
        }

        // Focus on email field
        setTimeout(() => {
            const emailField = isLoginFlow ?
                document.getElementById('login-email') :
                document.getElementById('register-email');
            emailField.focus();
        }, 100);
    }

    // Helper function to validate email
    function validateEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    // Helper function to show error
    function showError(inputElement, message) {
        // Remove any existing error
        const existingError = inputElement.parentElement.querySelector('.error-text');
        if (existingError) existingError.remove();

        // Add error class to input
        inputElement.classList.add('is-invalid');

        // Create and append error message
        const errorElement = document.createElement('span');
        errorElement.classList.add('error-text');
        errorElement.textContent = message;
        inputElement.parentElement.appendChild(errorElement);

        // Remove error after input changes
        inputElement.addEventListener('input', function removeError() {
            inputElement.classList.remove('is-invalid');
            const error = inputElement.parentElement.querySelector('.error-text');
            if (error) error.remove();
            inputElement.removeEventListener('input', removeError);
        });
    }

    // Helper function to show form error
    function showFormError(formElement, message) {
        // Remove any existing messages
        const existingMessages = formElement.querySelectorAll('.error-text, .success-text');
        existingMessages.forEach(el => el.remove());

        // Create and append error message
        const errorElement = document.createElement('div');
        errorElement.classList.add('error-text', 'text-center', 'mb-3');
        errorElement.textContent = message;
        formElement.prepend(errorElement);

        // Auto remove after 5 seconds
        setTimeout(() => {
            errorElement.remove();
        }, 5000);
    }

    // Helper function to show form success
    function showFormSuccess(formElement, message) {
        // Remove any existing messages
        const existingMessages = formElement.querySelectorAll('.error-text, .success-text');
        existingMessages.forEach(el => el.remove());

        // Create and append success message
        const successElement = document.createElement('div');
        successElement.classList.add('success-text', 'text-center', 'mb-3');
        successElement.textContent = message;
        formElement.prepend(successElement);

        // Auto remove after 5 seconds
        setTimeout(() => {
            successElement.remove();
        }, 5000);
    }

    // Show login success
    function showLoginSuccess() {
        showFormSuccess(otpFormEl, 'Login successful! Redirecting to dashboard...');

        // Redirect to dashboard
        setTimeout(() => {
            window.location.href = '/dashboard';
        }, 2000);
    }

    // Show registration success
    function showRegistrationSuccess() {
        // For demo purposes - replace with redirect
        const successMessage = document.createElement('div');
        successMessage.innerHTML = `
            <div class="text-center mb-4">
                <i class="fas fa-check-circle" style="font-size: 60px; color: var(--success);"></i>
                <h3 class="mt-3">Registration Successful!</h3>
                <p>Your account has been created successfully. Redirecting to dashboard...</p>
            </div>
        `;

        otpForm.innerHTML = '';
        otpForm.appendChild(successMessage);

        // Redirect to dashboard
        setTimeout(() => {
            window.location.href = '/dashboard';
        }, 3000);
    }
});