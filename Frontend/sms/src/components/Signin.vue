<script setup lang="ts">
import { ref, computed } from 'vue'

const emit = defineEmits(['switch-view' , 'home'])

const role = ref<'STUDENT' | 'STAFF'>('STUDENT') // Defaults to STUDENT
const email = ref('')
const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

const isEmailValid = computed(() => {
  if (!email.value) return true // Don't show error while empty
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email.value)
})

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const setRole = (newRole: 'STUDENT' | 'STAFF') => {
  role.value = newRole
  // Clear error/success messages on toggle
  successMessage.value = ''
  errorMessage.value = ''
}

const LoginHandling = async () => {  
  successMessage.value = ''
  errorMessage.value = ''

  if (!email.value.trim() || !password.value.trim()) {
    errorMessage.value = 'Please fill out all required fields.'
    return
  }
  
  if (!isEmailValid.value) {
    errorMessage.value = 'Please enter a valid email address.'
    return
  }
  
  if (password.value.length < 4) {
    errorMessage.value = 'Password must be at least 4 characters long.'
    return
  }

  isLoading.value = true
  
  const payload = {
    email: email.value.trim(),
    password: password.value,
    role: role.value
  }

  try {
    // Pointed to /api/all-users/signin as defined in UsersController.java
    const response = await fetch('/api/all-users/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(payload)
    })
    
    const responseText = await response.text()
    
    if (response.ok) {
      successMessage.value = `Successfully signed in as a ${role.value.toLowerCase()}!`
      try {
        const user = JSON.parse(responseText)
        // Reset form fields
        email.value = ''
        password.value = ''
        // Navigate to home with user object
        emit('home', user)
      } catch (e) {
        // Fallback if response is plain text instead of JSON
        email.value = ''
        password.value = ''
        emit('home', { firstName: 'Authenticated', lastName: role.value })
      }
    } else {
      errorMessage.value = responseText || 'Sign in failed. Please check your credentials.'
    }
  } catch (error) {
    errorMessage.value = 'Network error: Unable to connect to the server. Make sure the backend is running.'
    console.error('Sign in error:', error)
  } finally {
    isLoading.value = false
  }

  if (role=='staff' && !email.value.includes('@teachers.com')) {
    errorMessage.value = 'Staff email must end with @teachers.com'
  }
}
</script>

<template>
  <div class="register-container">
    <!-- Left Decorative Side Panel -->
    <div class="brand-panel">
      <!-- Floating Background Glow Orbs -->
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      
      <div class="brand-content">
        <!-- Star Logo SVG -->
        <div class="brand-logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L13.8 8.2H20L15 12L16.8 18.2L12 14.4L7.2 18.2L9 12L4 8.2H10.2L12 2Z" fill="currentColor"/>
          </svg>
        </div>
        
        <div class="brand-text">
          <span class="badge">Academic Portal</span>
          <h1>Welcome Back</h1>
          <p>
            Access your student or staff records, view classes, and manage your school profile dashboard.
          </p>
        </div>
        
        <div class="brand-footer">
          <p>© 2026 SMS Studio. All rights reserved.</p>
        </div>
      </div>
    </div>

    <!-- Right Form Panel -->
    <div class="form-panel">
      <div class="form-card">
        
        <!-- Header -->
        <div class="form-header">
          <div class="mobile-logo">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L13.8 8.2H20L15 12L16.8 18.2L12 14.4L7.2 18.2L9 12L4 8.2H10.2L12 2Z" fill="currentColor"/>
            </svg>
          </div>
          <h2>Sign in to account</h2>
          <p>Welcome back! Select your role and sign in with your email.</p>
        </div>

        <!-- Success & Error Banners -->
        <transition name="slide-fade">
          <div v-if="successMessage" class="alert alert-success" role="alert">
            <div class="alert-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                <polyline points="22 4 12 14.01 9 11.01" />
              </svg>
            </div>
            <div class="alert-content">{{ successMessage }}</div>
          </div>
          <div v-else-if="errorMessage" class="alert alert-error" role="alert">
            <div class="alert-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
            </div>
            <div class="alert-content">{{ errorMessage }}</div>
          </div>
        </transition>

        <!-- Role Selector (Slide Toggle) -->
        <div class="role-selector-container">
          <label class="section-label">Select Your Role</label>
          <div class="role-selector-pill">
            <div 
              class="role-bg-slide" 
              :class="{ 'slide-right': role === 'STAFF' }"
            ></div>
            <button 
              type="button" 
              class="role-btn" 
              :class="{ 'active': role === 'STUDENT' }"
              @click="setRole('STUDENT')"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="role-icon">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
              </svg>
              Student
            </button>
            <button 
              type="button" 
              class="role-btn" 
              :class="{ 'active': role === 'STAFF' }"
              @click="setRole('STAFF')"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="role-icon">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
              Staff Member
            </button>
          </div>
        </div>

        <!-- Form fields -->
        <form @submit.prevent="LoginHandling" class="signup-form">
          <!-- Email address -->
          <div class="form-group">
            <label for="email">Email address</label>
            <div class="input-wrapper">
              <input 
                id="email" 
                v-model="email" 
                type="email" 
                placeholder="name@school.com"
                required
                :class="{ 'input-invalid': !isEmailValid && email }"
                :disabled="isLoading"
                autocomplete="username"
              />
            </div>
            <span v-if="!isEmailValid && email" class="field-error-text">
              Please enter a valid email address format.
            </span>
          </div>

          <!-- Password field -->
          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-wrapper">
              <input 
                id="password" 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                placeholder="••••••••••••"
                required
                :disabled="isLoading"
                autocomplete="current-password"
              />
              <button 
                type="button" 
                class="password-toggle-btn" 
                @click="togglePassword"
                title="Toggle password visibility"
              >
                <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </button>
            </div>
          </div>

          <!-- Submit Button -->
          <button 
            type="submit" 
            class="submit-btn" 
            :disabled="isLoading || !isEmailValid"
          >
            <span v-if="isLoading" class="btn-spinner" @click.prevent="emit('home')"></span>
            <span v-else>Log In</span>
          </button>
        </form>

        <div class="form-footer">
          Don't have an account? <a href="#" class="login-link" @click.prevent="emit('switch-view')">Sign up</a>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
/* Container styling to frame the split panels */
.register-container {
  display: grid;
  grid-template-columns: 1fr;
  width: 100%;
  max-width: 1100px;
  min-height: 720px;
  background-color: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  animation: slide-down var(--transition-slow);
}

@media (min-width: 1024px) {
  .register-container {
    grid-template-columns: 1fr 1fr;
  }
}

/* Left Brand Panel Styles */
.brand-panel {
  display: none;
  position: relative;
  background: linear-gradient(135deg, hsl(235, 80%, 48%), hsl(280, 85%, 45%));
  padding: 60px;
  color: white;
  overflow: hidden;
  flex-direction: column;
  justify-content: space-between;
}

@media (min-width: 1024px) {
  .brand-panel {
    display: flex;
  }
}

/* Glowing background animations */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.65;
}
.orb-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, hsl(210, 100%, 75%) 0%, transparent 70%);
  top: -50px;
  left: -50px;
  animation: float-slow 10s infinite ease-in-out;
}
.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, hsl(280, 100%, 75%) 0%, transparent 70%);
  bottom: -50px;
  right: -50px;
  animation: float-reverse 12s infinite ease-in-out;
}

.brand-content {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  color: white;
}
.brand-logo svg {
  width: 24px;
  height: 24px;
}

.badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 100px;
  background-color: rgba(255, 255, 255, 0.15);
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
  margin-bottom: 20px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  align-self: flex-start;
}

.brand-text h1 {
  font-size: 40px;
  font-weight: 700;
  line-height: 1.25;
  margin-bottom: 20px;
  letter-spacing: -1px;
}

.brand-text p {
  font-size: 16px;
  line-height: 1.6;
  opacity: 0.9;
  font-weight: 400;
}

.brand-footer {
  font-size: 13px;
  opacity: 0.7;
}

/* Right Form Panel Styles */
.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
  background-color: var(--bg-card);
}

@media (min-width: 640px) {
  .form-panel {
    padding: 60px 50px;
  }
}

.form-card {
  width: 100%;
  max-width: 440px;
}

/* Form Header */
.form-header {
  margin-bottom: 32px;
}

.mobile-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-sm);
  background-color: var(--primary-light);
  color: var(--primary);
  margin-bottom: 20px;
}

@media (min-width: 1024px) {
  .mobile-logo {
    display: none;
  }
}

.mobile-logo svg {
  width: 20px;
  height: 20px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: var(--dark);
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.form-header p {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* Alerts and banners */
.alert {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 16px;
  border-radius: var(--radius-md);
  margin-bottom: 24px;
  font-size: 14px;
  line-height: 1.4;
  border: 1px solid transparent;
}

.alert-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.alert-icon svg {
  width: 100%;
  height: 100%;
}

.alert-success {
  background-color: var(--success-bg);
  border-color: hsl(142, 72%, 90%);
  color: var(--success);
}

.alert-error {
  background-color: var(--error-bg);
  border-color: hsl(346, 84%, 93%);
  color: var(--error);
}

/* Role Toggle Pill Switch */
.role-selector-container {
  margin-bottom: 28px;
}

.section-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.role-selector-pill {
  position: relative;
  display: flex;
  background-color: hsl(220, 12%, 95%);
  padding: 4px;
  border-radius: 100px;
  border: 1px solid var(--border);
}

.role-bg-slide {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background-color: var(--bg-card);
  border-radius: 100px;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-normal);
  z-index: 1;
}

.role-bg-slide.slide-right {
  transform: translateX(100%);
}

.role-btn {
  position: relative;
  z-index: 2;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  border: none;
  background: none;
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 100px;
  transition: color var(--transition-fast);
}

.role-btn.active {
  color: var(--primary);
}

.role-icon {
  width: 16px;
  height: 16px;
}

/* Sign up form & inputs */
.signup-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

input {
  width: 100%;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  border: 1.5px solid var(--border);
  font-family: inherit;
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-card);
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
}

input::placeholder {
  color: hsl(220, 10%, 75%);
}

input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-focus);
}

input.input-invalid {
  border-color: var(--error);
  box-shadow: 0 0 0 4px hsl(346, 84%, 94%);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.password-toggle-btn {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color var(--transition-fast);
}

.password-toggle-btn:hover {
  color: var(--primary);
}

.password-toggle-btn svg {
  width: 20px;
  height: 20px;
}

.field-error-text {
  font-size: 12px;
  color: var(--error);
  font-weight: 500;
  margin-top: 2px;
}

/* Submit button styling */
.submit-btn {
  margin-top: 8px;
  padding: 14px 20px;
  border-radius: var(--radius-md);
  border: none;
  background-color: var(--primary);
  color: white;
  font-family: inherit;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: background-color var(--transition-fast), transform var(--transition-fast), box-shadow var(--transition-fast);
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2.5px solid rgba(255, 255, 255, 0.3);
  border-radius: 10%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

.form-footer {
  margin-top: 28px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.login-link {
  color: var(--primary);
  font-weight: 600;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

/* Vue Animations */
.slide-fade-enter-active {
  animation: slide-down 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.slide-fade-leave-active {
  animation: slide-down 0.2s cubic-bezier(0.34, 1.56, 0.64, 1) reverse;
}
</style>