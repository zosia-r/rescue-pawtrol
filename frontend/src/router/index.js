import { createRouter, createWebHistory } from 'vue-router'
import keycloak from '../keycloak' 

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            component: () => import('../views/Dashboard.vue'),
            children: [
                {
                    path: '',
                    name: 'registry',
                    component: () => import('../views/Registry.vue'),
                    meta: { roles: ['CARETAKER', 'MANAGER'] }
                },
                {
                    path: 'map',
                    name: 'map',
                    component: () => import('../views/Map.vue'),
                    meta: { roles: ['DISPATCHER', 'MANAGER'] }
                },
                {
                    path: 'floor-plan',
                    name: 'floor-plan',
                    component: () => import('../views/FloorPlan.vue'),
                    meta: { roles: ['CARETAKER', 'MANAGER'] }
                },
                {
                    path: 'reports',
                    name: 'reports',
                    component: () => import('../views/Reports.vue'),
                    meta: { roles: ['MANAGER'] }
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    if (to.meta.roles) {
        const hasAccess = to.meta.roles.some(role =>
            keycloak.realmAccess && keycloak.realmAccess.roles.includes(role)
        );

        if (!hasAccess) {
            alert("Brak uprawnień do przeglądania tej strony!");
            return next('/'); // Wyrzucamy go na stronę główną
        }
    }
    next();
})

export default router