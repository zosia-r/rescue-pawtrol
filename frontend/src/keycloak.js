import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: 'http://localhost:8081',
    realm: 'rescuepawtrol',
    clientId: 'vue-frontend'
});

export default keycloak;