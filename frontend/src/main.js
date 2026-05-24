import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import keycloak from './keycloak'
import axios from 'axios'
keycloak.init({
    onLoad: 'login-required',
    checkLoginIframe: false
}).then((authenticated) => {
    if (!authenticated) {
        window.location.reload();
    } else {

        axios.interceptors.request.use(config => {
            if (keycloak.token) {
                config.headers.Authorization = `Bearer ${keycloak.token}`;
            }
            return config;
        });

        setInterval(() => {
            keycloak.updateToken(70).catch(() => {
                keycloak.logout();
            });
        }, 60000);

        createApp(App).use(router).mount('#app')
    }
}).catch(() => {
    console.error("Błąd połączenia z serwerem Keycloak!");
});