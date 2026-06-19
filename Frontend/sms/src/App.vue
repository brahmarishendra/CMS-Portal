<script setup lang="ts">
import { ref } from 'vue'
import RegisterForm from './components/RegisterForm.vue'
import Signin from './components/Signin.vue'
import Home from './components/home.vue'

// Try to recover logged in user from localStorage on initialization
const storedUser = localStorage.getItem('sms_user')
const initialUser = storedUser ? JSON.parse(storedUser) : null

const currentView = ref<'register' | 'signin' | 'home'>(initialUser ? 'home' : 'signin')
const loggedInUser = ref<any>(initialUser)

const handleLoginSuccess = (user: any) => {
  loggedInUser.value = user || { firstName: 'Demo', lastName: 'User' }
  localStorage.setItem('sms_user', JSON.stringify(loggedInUser.value))
  currentView.value = 'home'
}

const handleLogout = () => {
  loggedInUser.value = null
  localStorage.removeItem('sms_user')
  currentView.value = 'signin'
}
</script>

<template>
  <RegisterForm v-if="currentView === 'register'" @switch-view="currentView = 'signin'" @home="handleLoginSuccess" />
  <Signin v-else-if="currentView === 'signin'" @switch-view="currentView = 'register'" @home="handleLoginSuccess" />
  <Home v-else :user="loggedInUser" @home="handleLogout" />
</template>

<style scoped></style>
