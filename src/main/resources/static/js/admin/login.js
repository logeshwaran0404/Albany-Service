// Add this script to your admin-login.html file

document.addEventListener('DOMContentLoaded', function () {
    // Password visibility toggle
    const togglePassword = document.getElementById('togglePassword');
    const password = document.getElementById('password');
    const eyeIcon = document.getElementById('eyeIcon');

    if (togglePassword && password && eyeIcon) {
        togglePassword.addEventListener('click', function () {
            const type =
                password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);

            eyeIcon.classList.toggle('fa-eye');
            eyeIcon.classList.toggle('fa-eye-slash');
        });
    }

    // Form submission handling
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const rememberMe = document.getElementById('rememberMe')?.checked || false;

            // Show loading state
            const loginBtn = document.querySelector('.btn-login');
            const originalText = loginBtn.innerHTML;
            loginBtn.disabled = true;
            loginBtn.innerHTML =
                '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Signing in...';

            // Call the API
            fetch('/api/auth/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                    rememberMe: rememberMe
                })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Store token if provided
                        if (data.data) {
                            localStorage.setItem('authToken', data.data);
                        }

                        // Redirect to dashboard
                        window.location.href = '/';
                    } else {
                        // Show error
                        showError(data.message || 'Login failed. Please try again.');

                        // Reset button state
                        loginBtn.disabled = false;
                        loginBtn.innerHTML = originalText;
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showError('An error occurred. Please try again.');

                    // Reset button state
                    loginBtn.disabled = false;
                    loginBtn.innerHTML = originalText;
                });
        });
    }

    // Helper function to show error
    function showError(message) {
        // Remove any existing error message
        const existingError = document.querySelector('.login-error');
        if (existingError) {
            existingError.remove();
        }

        // Create error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'login-error alert alert-danger mt-3';
        errorDiv.textContent = message;

        // Insert after form
        loginForm.insertAdjacentElement('afterend', errorDiv);

        // Auto remove after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }
});