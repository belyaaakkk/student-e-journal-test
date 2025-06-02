// Utility functions for cookies
function setCookie(name, value, days) {
    const expires = new Date();
    expires.setTime(expires.getTime() + (days * 24 * 60 * 60 * 1000));
    document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`;
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

function toggleSubMenu(button) {
    const subMenu = button.parentElement.querySelector('.sub-menu');
    if (!subMenu.classList.contains('show')) {
        closeAllSubMenus();
    }
    subMenu.classList.toggle('show');
    button.classList.toggle('rotate');
}

function closeAllSubMenus() {
    Array.from(document.getElementsByClassName('sub-menu')).forEach(subMenu => {
        subMenu.classList.remove('show');
        const button = subMenu.closest('.dropdown-container').querySelector('.dropdown-btn');
        if (button) {
            button.classList.remove('rotate');
        }
    });
}

document.querySelectorAll('.lang-link').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault(); // Prevent default navigation
        const lang = link.getAttribute('data-lang');
        // Remove active class from all links
        document.querySelectorAll('.lang-link').forEach(l => l.classList.remove('active'));
        // Add active class to clicked link
        link.classList.add('active');
        // Store the language in a cookie
        setCookie('lang', lang, 30); // Cookie lasts 30 days
        // Reload the page to apply the language (or use an AJAX call to update content)
        window.location.reload();
    });
});

// Set default active language based on cookie or default to 'ua'
const currentLang = getCookie('lang') || 'ua';
document.querySelector(`.lang-link[data-lang="${currentLang}"]`).classList.add('active');