<template>
  <div class="login-wrapper">
    <div class="login-card">

      <div class="brand-header">
        <div class="logo-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="heart-icon">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </div>
        <div class="brand-text">
          <h1>Rescue Pawtrol</h1>
          <p>Authentication Module</p>
        </div>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Login</label>
          <input
              type="text"
              id="username"
              v-model="username"
              required
              placeholder="Wpisz swój login"
          >
        </div>

        <div class="form-group">
          <label for="password">Hasło</label>
          <input
              type="password"
              id="password"
              v-model="password"
              required
              placeholder="Wpisz hasło"
          >
        </div>
        <form @submit.prevent="handleLogin" class="login-form">
          <button type="submit" class="primary-btn">Zaloguj się</button>

          <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
        </form>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    errorMessage.value = ''

    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    const realToken = response.data.token

    localStorage.setItem('jwt_token', realToken)

    router.push('/')

  } catch (error) {
    console.error("Błąd autoryzacji:", error)
    errorMessage.value = 'Błędny adres e-mail lub hasło!'
  }
}
</script>

<style scoped>
/* Główne tło i wyśrodkowanie */
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #F8F9FB; /* Kolor tła z makiety */
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
}

/* Karta logowania */
.login-card {
  background: #FFFFFF;
  padding: 3rem 2.5rem;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  width: 100%;
  max-width: 420px;
}

/* Nagłówek (Logo + Tytuł) */
.brand-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 2.5rem;
}

.logo-box {
  background-color: #D41B65; /* Różowy brand color */
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.heart-icon {
  width: 24px;
  height: 24px;
  color: white;
}

.brand-text h1 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #111827;
}

.brand-text p {
  margin: 0;
  font-size: 0.85rem;
  color: #6B7280;
}

/* Formularz */
.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #374151;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #D1D5DB;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 0.95rem;
  color: #111827;
  background-color: #F9FAFB;
  transition: all 0.2s ease;
}

input:focus {
  outline: none;
  border-color: #D41B65;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(212, 27, 101, 0.1);
}

input::placeholder {
  color: #9CA3AF;
}

.primary-btn {
  width: 100%;
  padding: 0.85rem;
  background-color: #D41B65;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
  margin-top: 1rem;
}

.primary-btn:hover {
  background-color: #B81555;
}

.primary-btn:active {
  transform: scale(0.98);
}

.error-msg {
  color: #DC2626;
  background-color: #FEE2E2;
  padding: 0.75rem;
  border-radius: 6px;
  margin-top: 1rem;
  font-size: 0.85rem;
  text-align: center;
  font-weight: 500;
}
</style>