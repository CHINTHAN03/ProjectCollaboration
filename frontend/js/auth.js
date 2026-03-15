const API_URL = 'http://localhost:7070/api';

function toggleAuth() {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    if (loginForm.classList.contains('active-form')) {
        loginForm.classList.remove('active-form');
        loginForm.classList.add('hidden-form');
        registerForm.classList.remove('hidden-form');
        registerForm.classList.add('active-form');
    } else {
        registerForm.classList.remove('active-form');
        registerForm.classList.add('hidden-form');
        loginForm.classList.remove('hidden-form');
        loginForm.classList.add('active-form');
    }
}

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const messageDiv = document.getElementById('loginMessage');

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            messageDiv.textContent = "Success! Redirecting...";
            messageDiv.className = "message success-msg";
            localStorage.setItem('currentUser', username);
            localStorage.setItem('userRole', data.role);
            setTimeout(() => { window.location.href = 'dashboard.html'; }, 1000);
        } else {
            messageDiv.textContent = data.error || "Login failed.";
            messageDiv.className = "message error-msg";
        }
    } catch (error) {
        messageDiv.textContent = "Cannot connect to server.";
        messageDiv.className = "message error-msg";
    }
});

document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;
    const messageDiv = document.getElementById('regMessage');

    try {
        const response = await fetch(`${API_URL}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, role })
        });

        const data = await response.json();

        if (response.ok) {
            messageDiv.textContent = "Account created! Please log in.";
            messageDiv.className = "message success-msg";
            setTimeout(toggleAuth, 1500);
        } else {
            messageDiv.textContent = data.error || "Registration failed.";
            messageDiv.className = "message error-msg";
        }
    } catch (error) {
        messageDiv.textContent = "Cannot connect to server.";
        messageDiv.className = "message error-msg";
    }
});