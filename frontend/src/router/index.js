import { createRouter, createWebHistory } from 'vue-router'

const isTokenExpired = (token) => {
    if (!token) return true;
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const { exp } = JSON.parse(jsonPayload);

        return (Date.now() / 1000) >= exp;
    } catch (e) {
        return true;
    }
};

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/',
            component: () => import('../views/Dashboard.vue'),
            meta: { requiresAuth: true },
            children: [
                {
                    path: '',
                    name: 'registry',
                    component: () => import('../views/Registry.vue')
                },
                {
                    path: 'map',
                    name: 'map',
                    component: () => import('../views/Map.vue')
                },
                {
                    path: 'floor-plan',
                    name: 'floor-plan',
                    component: () => import('../views/FloorPlan.vue')
                },
                {
                    path: 'reports',
                    name: 'reports',
                    component: () => import('../views/Reports.vue')
                }
            ]
        }
    ]
})

// token sprawdzany przed wejściem na każdą podstronę
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('jwt_token')

    if (to.matched.some(record => record.meta.requiresAuth)) {

        if (!token || isTokenExpired(token)) {
            localStorage.removeItem('jwt_token')
            next('/login')
        } else {
            next()
        }
    } else {
        if (to.path === '/login' && token && !isTokenExpired(token)) {
            next('/')
        } else {
            next()
        }
    }
})

export default router