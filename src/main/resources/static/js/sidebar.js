const toggleButton = document.getElementById('toggle-btn');
const sidebar = document.getElementById('sidebar');

function toggleSidebar() {
    document.documentElement.classList.toggle('sidebar-closed');
    toggleButton.classList.toggle('rotate');
    closeAllSubMenus();
    localStorage.setItem('sidebarClosed', document.documentElement.classList.contains('sidebar-closed'));
}

function toggleSubMenu(button) {
    const subMenu = button.parentElement.querySelector('.sub-menu');

    if (!subMenu.classList.contains('show')) {
        closeAllSubMenus();
    }

    subMenu.classList.toggle('show');
    button.classList.toggle('rotate');

    if (document.documentElement.classList.contains('sidebar-closed')) {
        document.documentElement.classList.remove('sidebar-closed');
        toggleButton.classList.remove('rotate');
    }
}

function closeAllSubMenus() {
    Array.from(sidebar.getElementsByClassName('show')).forEach(subMenu => {
        subMenu.classList.remove('show');
        const button = subMenu.closest('.dropdown-container, li').querySelector('.dropdown-btn');
        if (button) {
            button.classList.remove('rotate');
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    // Remove no-transitions class after load
    setTimeout(() => {
        document.documentElement.classList.remove('no-transitions');
    }, 0);

    const navLinks = document.querySelectorAll('#sidebar ul li a');
    const currentPath = window.location.pathname || '/';

    navLinks.forEach(link => {
        const linkPath = link.getAttribute('href');
        if (linkPath === currentPath) {
            link.parentElement.classList.add('active');
        } else {
            link.parentElement.classList.remove('active');
        }
    });
});

document.documentElement.classList.add('no-transitions');
if (localStorage.getItem('sidebarClosed') === 'true') {
    document.documentElement.classList.add('sidebar-closed');
}