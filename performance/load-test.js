import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Métricas customizadas
const errorRate = new Rate('error_rate');
const loginTrend = new Trend('login_duration');
const productsTrend = new Trend('products_duration');
const ordersTrend = new Trend('orders_duration');

// Configuração dos cenários de teste
export const options = {
    stages: [
        { duration: '30s', target: 10 },  // Subindo para 10 usuários
        { duration: '60s', target: 20 },  // Mantendo 20 usuários
        { duration: '30s', target: 0  },  // Descendo para 0
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'], // 95% das requisições < 2s
        error_rate: ['rate<0.1'],          // Menos de 10% de erros
    },
};

const BASE_URL = 'http://localhost:8080/api';

// Função para fazer login e obter token
function login() {
    const payload = JSON.stringify({
        username: 'admin',
        password: '123456'
    });

    const params = {
        headers: { 'Content-Type': 'application/json' }
    };

    const res = http.post(`${BASE_URL}/auth/login`, payload, params);
    loginTrend.add(res.timings.duration);

    check(res, {
        'login status 200': (r) => r.status === 200,
        'token recebido': (r) => JSON.parse(r.body).token !== undefined,
    });

    if (res.status === 200) {
        return JSON.parse(res.body).token;
    }
    return null;
}

export default function () {
    // Cenário 1 — Login
    let token;
    group('01_autenticacao', () => {
        token = login();
        errorRate.add(!token);
        sleep(1);
    });

    if (!token) return;

    const authHeaders = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    // Cenário 2 — Listar produtos
    group('02_listar_produtos', () => {
        const res = http.get(`${BASE_URL}/products`, authHeaders);
        productsTrend.add(res.timings.duration);

        check(res, {
            'produtos status 200': (r) => r.status === 200,
            'resposta em menos de 1s': (r) => r.timings.duration < 1000,
        });
        sleep(1);
    });

    // Cenário 3 — Criar produto
    group('03_criar_produto', () => {
        const payload = JSON.stringify({
            name: `Produto Teste ${Date.now()}`,
            description: 'Produto criado pelo teste de performance',
            price: 99.90,
            stock: 100
        });

        const res = http.post(`${BASE_URL}/products`, payload, authHeaders);

        check(res, {
            'produto criado status 200': (r) => r.status === 200,
        });
        sleep(1);
    });

    // Cenário 4 — Listar pedidos
    group('04_listar_pedidos', () => {
        const res = http.get(`${BASE_URL}/orders`, authHeaders);
        ordersTrend.add(res.timings.duration);

        check(res, {
            'pedidos status 200': (r) => r.status === 200,
            'resposta em menos de 2s': (r) => r.timings.duration < 2000,
        });
        sleep(1);
    });

    // Cenário 5 — Listar histórico
    group('05_historico', () => {
        const res = http.get(`${BASE_URL}/history`, authHeaders);

        check(res, {
            'historico status 200': (r) => r.status === 200,
        });
        sleep(1);
    });
}