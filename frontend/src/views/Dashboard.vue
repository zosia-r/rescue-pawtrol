<template>
  <div class="dashboard-layout">
    <header class="navbar">
      <div class="navbar-left">
        <div class="logo-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="heart-icon">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </div>
        <div class="brand-text">
          <h1>Rescue Pawtrol</h1>
          <p>{{ currentModuleName }}</p>
        </div>
      </div>

      <div class="navbar-center">
        <div class="search-bar">
          <span class="search-icon">🔍</span>
          <input type="text" placeholder="Search..." v-model="searchQuery">
        </div>
      </div>

      <div class="navbar-right">
        <button v-if="hasRole('DISPATCHER') || hasRole('MANAGER')"
                class="nav-btn" :class="{ active: route.path === '/map' }" @click="router.push('/map')">Map</button>

        <button v-if="hasRole('CARETAKER') || hasRole('MANAGER')"
                class="nav-btn" :class="{ active: route.path === '/floor-plan' }" @click="router.push('/floor-plan')">Floor Plan</button>

        <button v-if="hasRole('CARETAKER') || hasRole('MANAGER')"
                class="nav-btn" :class="{ active: route.path === '/' }" @click="router.push('/')">Registry</button>

        <button v-if="hasRole('MANAGER')"
                class="nav-btn" :class="{ active: route.path === '/reports' }" @click="router.push('/reports')">Reports</button>

        <button @click="handleLogout" class="logout-btn">Log out</button>
      </div>
    </header>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import keycloak from '../keycloak'

const router = useRouter()
const route = useRoute()
const searchQuery = ref('')

const hasRole = (roleName) => {
  return keycloak.realmAccess && keycloak.realmAccess.roles.includes(roleName);
}

const handleLogout = () => {
  localStorage.removeItem('jwt_token')
  keycloak.logout({ redirectUri: 'http://localhost:5173' })
}

const currentModuleName = computed(() => {
  if (route.path === '/map') return 'Map Module'
  if (route.path === '/floor-plan') return 'Facility Module'
  if (route.path === '/reports') return 'Analytics & Reporting Module'
  return 'Animal Registry Module'
})
</script>

<style scoped>
/* GŁÓWNY LAYOUT */
.dashboard-layout {
  min-height: 100vh;
  background-color: #F8F9FB;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #FFFFFF;
  border-bottom: 1px solid #E5E7EB;
}

.navbar-left { display: flex; align-items: center; gap: 12px; }
.logo-box { background-color: #D41B65; width: 40px; height: 40px; border-radius: 10px; display: flex; justify-content: center; align-items: center; }
.heart-icon { width: 20px; height: 20px; color: white; }
.brand-text h1 { margin: 0; font-size: 1.1rem; font-weight: 600; color: #111827; }
.brand-text p { margin: 0; font-size: 0.8rem; color: #6B7280; }
.navbar-center { flex: 1; display: flex; justify-content: center; }
.search-bar { display: flex; align-items: center; background-color: #F3F4F6; border-radius: 8px; padding: 0.5rem 1rem; width: 100%; max-width: 400px; }
.search-icon { color: #9CA3AF; margin-right: 8px; }
.search-bar input { border: none; background: transparent; outline: none; width: 100%; font-size: 0.9rem; color: #374151; }
.navbar-right { display: flex; gap: 0.5rem; align-items: center; }
.nav-btn { padding: 0.6rem 1.2rem; background-color: #F3F4F6; color: #4B5563; border: none; border-radius: 8px; font-weight: 500; font-size: 0.9rem; cursor: pointer; transition: all 0.2s; }
.nav-btn:hover { background-color: #E5E7EB; }
.nav-btn.active { background-color: #D41B65; color: white; }
.logout-btn { background-color: transparent; color: #DC2626; margin-left: 1rem; padding: 0.6rem; border: 1px solid #FEE2E2; }
.logout-btn:hover { background-color: #FEE2E2; }

.main-content { padding: 2rem; max-width: 1400px; margin: 0 auto; }
</style>