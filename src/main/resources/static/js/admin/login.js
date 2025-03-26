/**
 * Admin Login JavaScript
 * Handles login form submission and UI interactions
 */
document.addEventListener('DOMContentLoaded', function () {
    // Get form and form elements
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const rememberMeCheckbox = document.getElementById('rememberMe');
    const loginButton = document.querySelector('.btn-login');

    // Password visibility toggle
    const togglePassword = document.getElementById('togglePassword');
    const eyeIcon = document.getElementById('eyeIcon');

    if (togglePassword && passwordInput && eyeIcon) {
        togglePassword.addEventListener('click', function () {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            eyeIcon.classList.toggle('fa-eye');
            eyeIcon.classList.toggle('fa-eye-slash');
        });
    }

    // Create background particles
    createParticles();

    // Form validation and submission
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            e.preventDefault();

            // Basic validation
            if (!validateForm()) {
                return;
            }

            // Show loading state
            setLoadingState(true);

            // Get form data
            const loginData = {
                email: emailInput.value.trim(),
                password: passwordInput.value,
                rememberMe: rememberMeCheckbox.checked
            };

            // Send login request
            fetch('/auth/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    handleLoginResponse(data);
                })
                .catch(error => {
                    console.error('Error:', error);
                    showError('An error occurred. Please try again later.');
                    setLoadingState(false);
                });
        });
    }

    /**
     * Validate form inputs
     * @returns {boolean} True if form is valid
     */
    function validateForm() {
        let isValid = true;

        // Clear previous error messages
        clearErrors();

        // Validate email
        if (!emailInput.value.trim()) {
            showError('Please enter your email address.');
            isValid = false;
        } else if (!isValidEmail(emailInput.value.trim())) {
            showError('Please enter a valid email address.');
            isValid = false;
        }

        // Validate password
        if (!passwordInput.value) {
            showError('Please enter your password.');
            isValid = false;
        }

        return isValid;
    }

    /**
     * Check if email is valid
     * @param {string} email Email address to validate
     * @returns {boolean} True if email is valid
     */
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Set loading state for login button
     * @param {boolean} isLoading True to show loading state
     */
    function setLoadingState(isLoading) {
        if (isLoading) {
            loginButton.disabled = true;
            loginButton.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Signing in...';
        } else {
            loginButton.disabled = false;
            loginButton.innerHTML = '<span>Sign In to Dashboard</span>';
        }
    }

    /**
     * Handle login API response
     * @param {Object} data Response data
     */
    function handleLoginResponse(data) {
        if (data.success) {
            // Redirect to dashboard
            window.location.href = data.redirectUrl;
        } else {
            // Show error message
            showError(data.message || 'Invalid email or password.');
            setLoadingState(false);
        }
    }

    /**
     * Show error message
     * @param {string} message Error message to display
     */
    function showError(message) {
        const loginHeader = document.querySelector('.login-header');

        // Create error alert if it doesn't exist
        let errorAlert = document.querySelector('.alert-danger');
        if (!errorAlert) {
            errorAlert = document.createElement('div');
            errorAlert.className = 'alert alert-danger';
            errorAlert.innerHTML = `<i class="fas fa-exclamation-circle me-2"></i><span class="error-message"></span>`;

            // Insert after login header
            loginHeader.after(errorAlert);
        }

        // Update error message
        const errorMessage = errorAlert.querySelector('.error-message');
        if (errorMessage) {
            errorMessage.textContent = message;
        } else {
            errorAlert.innerHTML = `<i class="fas fa-exclamation-circle me-2"></i>${message}`;
        }

        // Scroll to error message
        errorAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    /**
     * Clear all error messages
     */
    function clearErrors() {
        const errorAlert = document.querySelector('.alert-danger');
        if (errorAlert) {
            errorAlert.remove();
        }
    }

    /**
     * Create decorative particles in the background
     */
    function createParticles() {
        const particlesContainer = document.getElementById('particles');
        if (!particlesContainer) return;

        const particlesCount = 30;

        for (let i = 0; i < particlesCount; i++) {
            const particle = document.createElement('div');
            particle.classList.add('particle');

            // Random size between 5px and 20px
            const size = Math.random() * 15 + 5;
            particle.style.width = `${size}px`;
            particle.style.height = `${size}px`;

            // Random position
            const posX = Math.random() * 100;
            const posY = Math.random() * 100;
            particle.style.left = `${posX}%`;
            particle.style.top = `${posY}%`;

            // Random opacity
            particle.style.opacity = Math.random() * 0.1 + 0.05;

            // Random animation delay
            particle.style.animation = `float ${Math.random() * 10 + 10}s ease-in-out infinite`;
            particle.style.animationDelay = `${Math.random() * 5}s`;

            particlesContainer.appendChild(particle);
        }
    }
});