document.addEventListener('DOMContentLoaded', () => {
    const errorAlert = document.querySelector('.alert-error');
    const form = document.querySelector('.login-form');
    const inputs = form.querySelectorAll('input');

    // If there's an error, add fade-in animation
    if (errorAlert) {
        errorAlert.style.opacity = '0';
        errorAlert.style.transition = 'opacity 300ms ease-in-out';
        setTimeout(() => {
            errorAlert.style.opacity = '1';
        }, 100);
    }

    // Hide error message when user starts typing
    inputs.forEach(input => {
        input.addEventListener('input', () => {
            if (errorAlert) {
                errorAlert.style.opacity = '0';
                setTimeout(() => {
                    errorAlert.style.display = 'none';
                }, 300); // Match transition duration
            }
        });
    });
});