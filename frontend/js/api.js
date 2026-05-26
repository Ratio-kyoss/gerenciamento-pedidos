const BASE_URL = 'http://localhost:8080/api';

async function request(method, endpoint, body = null) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const config = { method, headers };
    if (body) config.body = JSON.stringify(body);

    const response = await fetch(`${BASE_URL}${endpoint}`, config);
    const text = await response.text();

    try {
        return { ok: response.ok, data: JSON.parse(text), status: response.status };
    } catch {
        return { ok: response.ok, data: text, status: response.status };
    }
}

// Auth
const api = {
    login: (username, password) =>
        request('POST', '/auth/login', { username, password }),

    register: (data) =>
        request('POST', '/auth/register', data),

    // Produtos
    getProducts: () => request('GET', '/products'),
    createProduct: (data) => request('POST', '/products', data),
    updateProduct: (id, data) => request('PUT', `/products/${id}`, data),
    deleteProduct: (id) => request('DELETE', `/products/${id}`),
    updateStock: (id, quantity) =>
        request('PATCH', `/products/${id}/stock?quantity=${quantity}`),

    // Pedidos
    getOrders: () => request('GET', '/orders'),
    getMyOrders: () => request('GET', '/orders/my'),
    createOrder: (data) => request('POST', '/orders', data),
    updateOrderStatus: (id, status) =>
        request('PATCH', `/orders/${id}/status?status=${status}`),
    cancelOrder: (id) => request('DELETE', `/orders/${id}`),

    // Histórico
    getHistory: () => request('GET', '/history'),
};