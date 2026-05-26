const TOKEN_KEY = 'pedidos_token';
const USER_KEY  = 'pedidos_user';

function saveAuth(token, username, role) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify({ username, role }));
}

function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function getUser() {
    const user = localStorage.getItem(USER_KEY);
    return user ? JSON.parse(user) : null;
}

function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '../index.html';
}

function requireAuth() {
    if (!getToken()) {
        window.location.href = '../index.html';
    }
}

function isAdmin() {
    const user = getUser();
    return user && user.role === 'ADMIN';
}