<script setup lang="ts">
import { Phone } from 'lucide-react'
import { ref, computed, watch } from 'vue'

const props = defineProps({
  user: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['switch-view', 'home'])

// Compute role (STUDENT or STAFF)
const role = computed(() => {
  if (props.user?.role) {
    return props.user.role.toUpperCase()
  }
  // Smart fallback for cached user sessions
  if (props.user?.phoneNumber) {
    return 'STAFF'
  }
  return 'STUDENT'
})


// Tab navigation state: dashboard, results, fees, or salary
const currentTab = ref<'dashboard' | 'results' | 'fees' | 'salary'>('dashboard')

// Compute username supporting both central Users (username) and legacy Student/Staff (firstName + lastName)
const username = computed(() => {
  if (!props.user) return ''
  if (props.user.username) {
    return props.user.username.trim()
  }
  const first = (props.user.firstName || '').trim()
  const last = (props.user.lastName || '').trim()
  return `${first} ${last}`.trim()
})

const message = computed(() => {
  return username.value ? `Welcome back, ${username.value}!` : "Welcome!"
})


// Results Management State
const resultsList = ref<any[]>([])
const isResultsLoading = ref(false)
const resultsFeedback = ref('')
const feedbackType = ref<'success' | 'error'>('success')

const feedbackClass = computed(() => {
  return feedbackType.value === 'success' ? 'feedback-success' : 'feedback-error'
})

// Modal / Form state for Staff CRUD
const showResultsModal = ref(false)
const editingRecordId = ref<number | null>(null)
const formStudentId = ref<number | undefined>(undefined)
const formSubject = ref('')
const formMarks = ref<number | undefined>(undefined)
const formResult = ref<'Pass' | 'Fail'>('Pass')
const modalError = ref('')
const isSubmittingResult = ref(false)


// Fetch results from backend
const fetchResults = async () => {
  if (!props.user) return

  isResultsLoading.value = true
  resultsFeedback.value = ''

  try {
    let url = '/api/results/get' // Staff retrieves all grades
    if (role.value === 'STUDENT') {
      // Retrieve only this student's grades (e.g. 61620261000). When a staff member
      // assigns marks to this ID, those marks will show up on this student's Results page.
      const studentId = props.user.domainId || props.user.userId || props.user.id || 1
      url = `/api/results/student/${studentId}`
      
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })

    if (response.ok) {
      const data = await response.json()
      resultsList.value = data
    } else {
      resultsFeedback.value = 'Failed to load academic reports.'
      feedbackType.value = 'error'
    }
  } catch (err) {
    resultsFeedback.value = 'Connection error: Unable to load grades.'
    feedbackType.value = 'error'
    console.error('Fetch results error:', err)
  } finally {
    isResultsLoading.value = false
  }
}

// Open modal for Adding a new result
const openAddModal = async () => {
  editingRecordId.value = null
  
  // Fetch a random student ID from the backend endpoint, fallback to the computed local random ID
  try {
    const response = await fetch('/api/students/random-id')
    if (response.ok) {
      const data = await response.text()
      formStudentId.value = parseInt(data.trim(), 10)
    } else {
      formStudentId.value = props.user?.id
    }
  } catch (err) {
    console.error('Error fetching random ID:', err)
    formStudentId.value = props.user?.id
  }

  formSubject.value = ''
  formMarks.value = undefined
  formResult.value = 'Pass'
  modalError.value = ''
  showResultsModal.value = true
}

// Open modal for Editing an existing result
const openEditModal = (record: any) => {
  editingRecordId.value = record.id
  formStudentId.value = record.studentId
  formSubject.value = record.subject
  formMarks.value = record.marks
  formResult.value = record.result === 'Fail' ? 'Fail' : 'Pass'
  modalError.value = ''
  showResultsModal.value = true
}

// Close the modal
const closeModal = () => {
  showResultsModal.value = false
}

// Submit the Add/Edit form
const submitResultsForm = async () => {
  modalError.value = ''

  if (formStudentId.value === undefined || !formSubject.value.trim() || formMarks.value === undefined) {
    modalError.value = 'Please fill out all required fields.'
    return
  }

  isSubmittingResult.value = true

  const payload = {
    id: editingRecordId.value || undefined,
    studentId: formStudentId.value,
    subject: formSubject.value.trim(),
    marks: formMarks.value,
    result: formResult.value
  }

  // Determine endpoint
  const url = editingRecordId.value ? '/api/results/Results' : '/api/results/add'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    if (response.ok) {
      resultsFeedback.value = editingRecordId.value
        ? 'Report record updated successfully!'
        : 'New report record published successfully!'
      feedbackType.value = 'success'
      closeModal()
      fetchResults()
    } else {
      const errText = await response.text()
      modalError.value = errText || 'Failed to save record.'
    }
  } catch (err) {
    modalError.value = 'Connection error: Unable to save grade.'
    console.error('Submit results error:', err)
  } finally {
    isSubmittingResult.value = false
  }
}

// Delete result record (Staff only)
const deleteRecord = async (record: any) => {
  if (!confirm(`Are you sure you want to delete the result record for Student #${record.studentId} in ${record.subject}?`)) {
    return
  }

  resultsFeedback.value = ''

  try {
    const response = await fetch('/api/results/Results', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(record)
    })

    if (response.ok) {
      resultsFeedback.value = 'Record successfully deleted.'
      feedbackType.value = 'success'
      fetchResults()
    } else {
      resultsFeedback.value = 'Failed to delete record.'
      feedbackType.value = 'error'
    }
  } catch (err) {
    resultsFeedback.value = 'Connection error: Unable to delete grade.'
    feedbackType.value = 'error'
    console.error('Delete results error:', err)
  }
}

// Fees Management State
const showFeesModal = ref(false)
const feesList = ref<any[]>([])
const isFeesLoading = ref(false)
const feesFeedback = ref('')
const feesFeedbackType = ref<'success' | 'error'>('success')

const feesFeedbackClass = computed(() => {
  return feesFeedbackType.value === 'success' ? 'feedback-success' : 'feedback-error'
})

// Fees Form Fields
const feeFormStudentId = ref<number | undefined>(undefined)
const feeFormType = ref('Tuition')
const feeFormTerm = ref('Term 1')
const feeFormTotalAmount = ref<number | undefined>(undefined)
const feeFormPaidAmount = ref<number>(0)
const feeFormDueDate = ref(new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0])
const feeFormStatus = ref('Unpaid')
const isSubmittingFee = ref(false)
const feesModalError = ref('')

// Fetch fees from backend
const fetchFees = async () => {
  if (!props.user) return

  isFeesLoading.value = true
  feesFeedback.value = ''

  try {
    let url = '/api/fees/get' // Staff retrieves all fees
    if (role.value === 'STUDENT') {
      // Retrieve only this student's fees
      const studentId = props.user.domainId || props.user.userId || props.user.id || 1
      url = `/api/fees/student/${studentId}`
    }

    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })

    if (response.ok) {
      const data = await response.json()
      feesList.value = data
    } else {
      feesFeedback.value = 'Failed to load billing statements.'
      feesFeedbackType.value = 'error'
    }
  } catch (err) {
    feesFeedback.value = 'Connection error: Unable to load billing details.'
    feesFeedbackType.value = 'error'
    console.error('Fetch fees error:', err)
  } finally {
    isFeesLoading.value = false
  }
}

// Open fees popup modal
const openFeesModal = () => {
  feeFormStudentId.value = undefined
  feeFormType.value = 'Tuition'
  feeFormTerm.value = 'Term 1'
  feeFormTotalAmount.value = undefined
  feeFormPaidAmount.value = 0
  feeFormDueDate.value = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
  feeFormStatus.value = 'Unpaid'
  feesModalError.value = ''
  showFeesModal.value = true
}

// Close fees popup modal
const closeFeesModal = () => {
  showFeesModal.value = false
}

// Submit fees form to backend
const submitFeesForm = async () => {
  feesModalError.value = ''

  if (feeFormStudentId.value === undefined || feeFormTotalAmount.value === undefined || !feeFormDueDate.value) {
    feesModalError.value = 'Please fill out all required fields.'
    return
  }

  isSubmittingFee.value = true

  const payload = {
    studentId: feeFormStudentId.value,
    feeType: feeFormType.value,
    termName: feeFormTerm.value,
    totalAmount: feeFormTotalAmount.value,
    paidAmount: feeFormPaidAmount.value,
    dueDate: feeFormDueDate.value,
    paymentStatus: feeFormStatus.value
  }

  try {
    const response = await fetch('/api/fees/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    if (response.ok) {
      feesFeedback.value = 'New billing statement published successfully!'
      feesFeedbackType.value = 'success'
      closeFeesModal()
      fetchFees()
    } else {
      const errText = await response.text()
      feesModalError.value = errText || 'Failed to save billing record.'
    }
  } catch (err) {
    feesModalError.value = 'Connection error: Unable to save fee record.'
    console.error('Submit fees error:', err)
  } finally {
    isSubmittingFee.value = false
  }
}

// Salary Management State
const salaryList = ref<any[]>([])
const isSalaryLoading = ref(false)
const salaryFeedback = ref('')
const salaryFeedbackType = ref<'success' | 'error'>('success')
const selectedStaffPhone = ref<string>('')

const salaryFeedbackClass = computed(() => {
  return salaryFeedbackType.value === 'success' ? 'feedback-success' : 'feedback-error'
})

// Fetch salary from backend
const fetchSalary = async () => {
  if (!props.user) return

  isSalaryLoading.value = true
  salaryFeedback.value = ''

  try {
    let url = '/api/salary/get'
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    })

    if (response.ok) {
      const data = await response.json()
      salaryList.value = data
      
      // Select the first one or matching one
      if (data.length > 0 && !selectedStaffPhone.value) {
        const myPhone = props.user?.phoneNumber || props.user?.phoneNo || ''
        const found = data.find((s: any) => s.phoneNo === myPhone)
        selectedStaffPhone.value = found ? found.phoneNo : data[0].phoneNo
      }
    } else {
      salaryFeedback.value = 'Failed to load salary statements.'
      salaryFeedbackType.value = 'error'
    }
  } catch (err) {
    salaryFeedback.value = 'Connection error: Unable to load salary details.'
    salaryFeedbackType.value = 'error'
    console.error('Fetch salary error:', err)
  } finally {
    isSalaryLoading.value = false
  }
}

// ==========================================
// SALARY PORTAL FUNCTIONALITIES & COMPUTED STATE
// ==========================================

/**
 * Computed property to find the logged-in staff member's salary statement in the database.
 * It dynamically extracts the phone number from the logged-in user object (props.user)
 * and matches it against the phone number column in the database-derived salary list.
 */
const userSalaryRecord = computed(() => {
  const phone = (props.user?.phoneNumber || props.user?.phoneNo || '').toString().trim()
  if (!phone) return null
  return salaryList.value.find((s: any) => s.phoneNo.toString().trim() === phone) || null
})

/**
 * Watcher: Triggers data fetches when switching between navigation tabs.
 * Dynamically fetches Results, Fees, or Salary statement from database APIs.
 */
watch(currentTab, (newTab) => {
  if (newTab === 'results') {
    fetchResults()
  } else if (newTab === 'fees') {
    fetchFees()
  } else if (newTab === 'salary') {
    fetchSalary()
  }
})

/**
 * Watcher: Ensures that the user's salary details are requested immediately upon sign-in/mount.
 * This runs after refs are initialized to safely populate database-matched salary records,
 * allowing the 'Salary' navigation tab button to show or hide conditionally based on presence.
 */
watch(() => props.user, (newUser) => {
  if (newUser) {
    fetchSalary()
  }
}, { immediate: true })
</script>

<template>
  <div class="main">
    
    <!-- Top Premium Navigation Menu Bar -->
    <nav class="main-nav">
      <div class="nav-left">
        <span class="brand">SMS Portal</span>
      </div>
      
      <!-- Nav Pill Tabs -->
      <div class="nav-links">
        <button 
          class="nav-tab-btn" 
          :class="{ active: currentTab === 'dashboard' }" 
          @click="currentTab = 'dashboard'"
        >
          Dashboard
        </button>
        <button 
          id="Result"
          class="nav-tab-btn" 
          :class="{ active: currentTab === 'results' }" 
          @click="currentTab = 'results'"
        >
          Results
        </button>
        <button 
          class="nav-tab-btn" 
          :class="{ active: currentTab === 'fees' }" 
          @click="currentTab = 'fees'"
        >
          Fees
        </button>
        <button 
          v-if="userSalaryRecord"
          class="nav-tab-btn" 
          :class="{ active: currentTab === 'salary' }" 
          @click="currentTab = 'salary'"
        >
          Salary
        </button>
      </div>
      
      <!-- User profile menu -->
      <div class="nav-right">
        <div class="user-pill">
          <div class="user-avatar">
            {{ username?.charAt(0).toUpperCase() }}
          </div>
          <div class="user-meta">
            <span class="user-fullname">{{ username }}</span>
            <span class="user-role-badge" :class="role?.toLowerCase()">{{ role }}</span>
          </div>
        </div>
        
        <button class="logout-btn" @click="emit('home')">
          <svg class="logout-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          Log Out
        </button>
      </div>
    </nav>

    <!-- Main Workspace Content -->
    <div class="workspace-area">

      <!-- TAB 1: WELCOME DASHBOARD -->
      <main v-if="currentTab === 'dashboard'" class="dashboard-content">
        <h1 class="welcome-message">{{ message }}</h1>
        <p class="welcome-subtitle" style="margin-bottom: 30px;">
          Welcome to your School Management System dashboard portal.<b v-if="role === 'STUDENT'"> Your Id is {{ props.user?.id }} </b>. Start by choosing a section from the navigation bar above to manage your academic profile records.
        </p>


      </main>

      <!-- TAB 2: RESULTS SECTION -->
      <main v-else-if="currentTab === 'results'" class="tab-pane">
        <div class="results-card">
          <div class="results-card-header">
            <div>
              <h2>Academic Report Cards</h2>
              <p v-if="role === 'STUDENT'">Review your grades, subjects, and pass/fail statuses below.</p>
              <p v-else>Manage and update exam grades, report records, and passing statuses for all student accounts.</p>
            </div>
            
            <button v-if="role === 'STAFF'" class="btn-primary" @click="openAddModal">
              <svg class="btn-icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
              </svg>
              Add New Report Card
            </button>
          </div>

          <!-- Alert Notifications -->
          <transition name="slide-fade">
            <div v-if="resultsFeedback" class="results-alert" :class="feedbackClass">
              {{ resultsFeedback }}
            </div>
          </transition>

          <!-- Loading State Spinner -->
          <div v-if="isResultsLoading" class="center-state">
            <div class="spinner"></div>
            <p class="state-text">Fetching database records...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="resultsList.length === 0" class="center-state empty-state">
            <div class="empty-icon-wrapper">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path>
                <line x1="12" y1="17" x2="12.01" y2="17"></line>
              </svg>
            </div>
            <p class="state-text">No results records found on file.</p>
          </div>

          <!-- STUDENT READ-ONLY VIEW -->
          <div v-else-if="role === 'STUDENT'" class="table-wrapper">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>Subject</th>
                  <th>Marks</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="res in resultsList" :key="res.id">
                  <td class="bold-td">{{ res.subject }}</td>
                  <td>{{ res.marks }} / 100</td>
                  <td>
                    <span class="status-badge" :class="res.result?.toLowerCase() === 'pass' ? 'pass' : 'fail'">
                      {{ res.result }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- STAFF CRUD VIEW -->
          <div v-else-if="role === 'STAFF'" class="table-wrapper">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>Record ID</th>
                  <th>Student ID</th>
                  <th>Subject</th>
                  <th>Marks</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="res in resultsList" :key="res.id">
                  <td>#{{ res.id }}</td>
                  <td class="bold-td">Student #{{ res.studentId }}</td>
                  <td>{{ res.subject }}</td>
                  <td>{{ res.marks }} / 100</td>
                  <td>
                    <span class="status-badge" :class="res.result?.toLowerCase() === 'pass' ? 'pass' : 'fail'">
                      {{ res.result }}
                    </span>
                  </td>
                  <td>
                    <div class="actions-group">
                      <button class="action-btn edit" @click="openEditModal(res)">Edit</button>
                      <button class="action-btn delete" @click="deleteRecord(res)">Delete</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Add/Edit Modal (Overlay Popup) -->
        <transition name="modal">
          <div v-if="showResultsModal" class="modal-backdrop">
            <div class="modal-content">
              <div class="modal-header-bar">
                <h3>{{ editingRecordId ? 'Edit Student Grade' : 'Publish Student Grade' }}</h3>
                <button class="close-modal-btn" @click="closeModal">&times;</button>
              </div>
              
              <form @submit.prevent="submitResultsForm" class="modal-form">
                <div v-if="modalError" class="modal-error-banner">{{ modalError }}</div>
                
                <div class="form-field">
                  <label for="formStudentId">Student Database ID</label>
                  <input 
                    id="formStudentId" 
                    v-model.number="formStudentId" 
                    type="number" 
                    placeholder="e.g. 1" 
                    required
                    :disabled="isSubmittingResult"
                  />
                </div>

                <div class="form-row-2">
                  <div class="form-field">
                    <label for="formSubject">Subject Name</label>
                    <input 
                      id="formSubject" 
                      v-model="formSubject" 
                      type="text" 
                      placeholder="e.g. Science" 
                      required
                      :disabled="isSubmittingResult"
                    />
                  </div>
                  <div class="form-field">
                    <label for="formMarks">Marks Obtained (0-100)</label>
                    <input 
                      id="formMarks" 
                      v-model.number="formMarks" 
                      type="number" 
                      min="0" 
                      max="100" 
                      placeholder="e.g. 90" 
                      required
                      :disabled="isSubmittingResult"
                    />
                  </div>
                </div>

                <div class="form-field">
                  <label>Passing Status Result</label>
                  <div class="radio-pill-group">
                    <label class="radio-pill">
                      <input 
                        type="radio" 
                        v-model="formResult" 
                        value="Pass" 
                        :disabled="isSubmittingResult" 
                      />
                      <span class="pill-span pass">Pass</span>
                    </label>
                    <label class="radio-pill">
                      <input 
                        type="radio" 
                        v-model="formResult" 
                        value="Fail" 
                        :disabled="isSubmittingResult" 
                      />
                      <span class="pill-span fail">Fail</span>
                    </label>
                  </div>
                </div>

                <div class="modal-action-bar">
                  <button type="button" class="btn-secondary-action" @click="closeModal" :disabled="isSubmittingResult">Cancel</button>
                  <button type="submit" class="btn-primary-action" :disabled="isSubmittingResult">
                    <span v-if="isSubmittingResult" class="form-spinner"></span>
                    <span v-else>{{ editingRecordId ? 'Save Changes' : 'Publish Report' }}</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </transition>
      </main>

      <!-- TAB 3: FEES SECTION -->
      <main v-else-if="currentTab === 'fees'" class="tab-pane">
        <div class="results-card">
          <div class="results-card-header">
            <div>
              <h2>Billing & Fees Statement Portal</h2>
              <p v-if="role === 'STUDENT'">Review your billing history, outstanding balances, and payment status details.</p>
              <p v-else>Manage student billing statements, publish outstanding balances, and track payment collection status.</p>
            </div>
            
            <button v-if="role === 'STAFF'" class="btn-primary" @click="openFeesModal">
              <svg class="btn-icon-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <line x1="12" y1="5" x2="12" y2="19"></line>
                <line x1="5" y1="12" x2="19" y2="12"></line>
              </svg>
              Add Fee Record
            </button>
          </div>

          <!-- Alert Notifications -->
          <transition name="slide-fade">
            <div v-if="feesFeedback" class="results-alert" :class="feesFeedbackClass">
              {{ feesFeedback }}
            </div>
          </transition>

          <!-- Loading State Spinner -->
          <div v-if="isFeesLoading" class="center-state">
            <div class="spinner"></div>
            <p class="state-text">Fetching database records...</p>
          </div>

          <!-- Empty State -->
          <div v-else-if="feesList.length === 0" class="center-state empty-state">
            <div class="empty-icon-wrapper fees-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="2" y="4" width="20" height="16" rx="2" ry="2"></rect>
                <line x1="12" y1="18" x2="12.01" y2="18"></line>
                <path d="M12 13.5a2.5 2.5 0 0 1 0-5 2.5 2.5 0 0 1 0 5z"></path>
              </svg>
            </div>
            <p class="state-text">No fee statements found on file.</p>
          </div>

          <!-- STUDENT READ-ONLY VIEW -->
          <div v-else-if="role === 'STUDENT'" class="table-wrapper">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>Fee Type</th>
                  <th>Term Name</th>
                  <th>Total Amount</th>
                  <th>Paid Amount</th>
                  <th>Outstanding Amount</th>
                  <th>Due Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="fee in feesList" :key="fee.id">
                  <td class="bold-td">{{ fee.feeType }}</td>
                  <td>{{ fee.termName }}</td>
                  <td>${{ fee.totalAmount?.toFixed(2) }}</td>
                  <td>${{ fee.paidAmount?.toFixed(2) }}</td>
                  <td>
                    ${{ (fee.dueAmount !== null && fee.dueAmount !== undefined ? fee.dueAmount : (fee.totalAmount - fee.paidAmount))?.toFixed(2) }}
                  </td>
                  <td>{{ fee.dueDate }}</td>
                  <td>
                    <span class="status-badge" :class="fee.paymentStatus?.toLowerCase()">
                      {{ fee.paymentStatus }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- STAFF VIEW -->
          <div v-else-if="role === 'STAFF'" class="table-wrapper">
            <table class="premium-table">
              <thead>
                <tr>
                  <th>Statement ID</th>
                  <th>Student ID</th>
                  <th>Fee Type</th>
                  <th>Term Name</th>
                  <th>Total Amount</th>
                  <th>Paid Amount</th>
                  <th>Outstanding Amount</th>
                  <th>Due Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="fee in feesList" :key="fee.id">
                  <td>#{{ fee.id }}</td>
                  <td class="bold-td">Student #{{ fee.studentId }}</td>
                  <td>{{ fee.feeType }}</td>
                  <td>{{ fee.termName }}</td>
                  <td>${{ fee.totalAmount?.toFixed(2) }}</td>
                  <td>${{ fee.paidAmount?.toFixed(2) }}</td>
                  <td>
                    ${{ (fee.dueAmount !== null && fee.dueAmount !== undefined ? fee.dueAmount : (fee.totalAmount - fee.paidAmount))?.toFixed(2) }}
                  </td>
                  <td>{{ fee.dueDate }}</td>
                  <td>
                    <span class="status-badge" :class="fee.paymentStatus?.toLowerCase()">
                      {{ fee.paymentStatus }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Add Fees Modal (Overlay Popup) -->
        <transition name="modal">
          <div v-if="showFeesModal" class="modal-backdrop">
            <div class="modal-content">
              <div class="modal-header-bar">
                <h3>Publish Student Fee Statement</h3>
                <button class="close-modal-btn" @click="closeFeesModal">&times;</button>
              </div>
              
              <form @submit.prevent="submitFeesForm" class="modal-form">
                <div v-if="feesModalError" class="modal-error-banner">{{ feesModalError }}</div>
                
                <div class="form-field">
                  <label for="feeFormStudentId">Student Database ID</label>
                  <input 
                    id="feeFormStudentId" 
                    v-model.number="feeFormStudentId" 
                    type="number" 
                    placeholder="e.g. 1" 
                    required
                    :disabled="isSubmittingFee"
                  />
                </div>

                <div class="form-row-2">
                  <div class="form-field">
                    <label for="feeFormType">Fee Type</label>
                    <select 
                      id="feeFormType" 
                      v-model="feeFormType" 
                      required
                      :disabled="isSubmittingFee"
                      style="width: 100%; padding: 10px 14px; border-radius: var(--radius-md); border: 1.5px solid var(--border); font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: var(--bg-card);"
                    >
                      <option value="Tuition">Tuition</option>
                      <option value="Exam">Exam</option>
                      <option value="Hostel">Hostel</option>
                      <option value="Sports">Sports</option>
                      <option value="Library">Library</option>
                    </select>
                  </div>
                  <div class="form-field">
                    <label for="feeFormTerm">Term Name</label>
                    <select 
                      id="feeFormTerm" 
                      v-model="feeFormTerm" 
                      required
                      :disabled="isSubmittingFee"
                      style="width: 100%; padding: 10px 14px; border-radius: var(--radius-md); border: 1.5px solid var(--border); font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: var(--bg-card);"
                    >
                      <option value="Term 1">Term 1</option>
                      <option value="Term 2">Term 2</option>
                      <option value="Term 3">Term 3</option>
                    </select>
                  </div>
                </div>

                <div class="form-row-2">
                  <div class="form-field">
                    <label for="feeFormTotalAmount">Total Amount ($)</label>
                    <input 
                      id="feeFormTotalAmount" 
                      v-model.number="feeFormTotalAmount" 
                      type="number" 
                      min="0" 
                      step="0.01"
                      placeholder="e.g. 1500" 
                      required
                      :disabled="isSubmittingFee"
                    />
                  </div>
                  <div class="form-field">
                    <label for="feeFormPaidAmount">Paid Amount ($)</label>
                    <input 
                      id="feeFormPaidAmount" 
                      v-model.number="feeFormPaidAmount" 
                      type="number" 
                      min="0" 
                      step="0.01"
                      placeholder="e.g. 0" 
                      required
                      :disabled="isSubmittingFee"
                    />
                  </div>
                </div>

                <div class="form-field">
                  <label for="feeFormDueDate">Due Date</label>
                  <input 
                    id="feeFormDueDate" 
                    v-model="feeFormDueDate" 
                    type="date" 
                    required
                    :disabled="isSubmittingFee"
                    style="width: 100%; padding: 10px 14px; border-radius: var(--radius-md); border: 1.5px solid var(--border); font-family: inherit; font-size: 14px; color: var(--text-primary); background-color: var(--bg-card);"
                  />
                </div>

                <div class="form-field">
                  <label>Payment Status</label>
                  <div class="radio-pill-group">
                    <label class="radio-pill">
                      <input 
                        type="radio" 
                        v-model="feeFormStatus" 
                        value="Unpaid" 
                        :disabled="isSubmittingFee" 
                      />
                      <span class="pill-span fail">Unpaid</span>
                    </label>
                    <label class="radio-pill">
                      <input 
                        type="radio" 
                        v-model="feeFormStatus" 
                        value="Partial" 
                        :disabled="isSubmittingFee" 
                      />
                      <span class="pill-span partial">Partial</span>
                    </label>
                    <label class="radio-pill">
                      <input 
                        type="radio" 
                        v-model="feeFormStatus" 
                        value="Paid" 
                        :disabled="isSubmittingFee" 
                      />
                      <span class="pill-span pass">Paid</span>
                    </label>
                  </div>
                </div>

                <div class="modal-action-bar">
                  <button type="button" class="btn-secondary-action" @click="closeFeesModal" :disabled="isSubmittingFee">Cancel</button>
                  <button type="submit" class="btn-primary-action" :disabled="isSubmittingFee">
                    <span v-if="isSubmittingFee" class="form-spinner"></span>
                    <span v-else>Publish Statement</span>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </transition>
      </main>

      <!-- TAB 4: SALARY SECTION (Pay Slip Bill Format) -->
      <main v-else-if="currentTab === 'salary'" class="tab-pane">
        <!-- Alert Notifications -->
        <transition name="slide-fade">
          <div v-if="salaryFeedback" class="results-alert" :class="salaryFeedbackClass">
            {{ salaryFeedback }}
          </div>
        </transition>

        <!-- Loading State Spinner -->
        <div v-if="isSalaryLoading" class="center-state">
          <div class="spinner"></div>
          <p class="state-text">Fetching salary statement from database...</p>
        </div>

        <!-- Fallback if no matching record (should not happen since tab is hidden otherwise) -->
        <div v-else-if="!userSalaryRecord" class="center-state empty-state">
          <div class="empty-icon-wrapper fees-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="4" width="18" height="16" rx="2" ry="2"></rect>
              <line x1="12" y1="17" x2="12.01" y2="17"></line>
            </svg>
          </div>
          <p class="state-text" style="font-size: 16px; font-weight: 600;">No salary statement found on file.</p>
        </div>

        <!-- Salary Statement Bill Card -->
        <div v-else class="salary-bill-container">
          <div class="salary-bill-card">
            <!-- Bill Header -->
            <div class="bill-header">
              <div class="bill-logo-info">
                <span class="bill-brand">SMS School Portal</span>
                <span class="bill-title">Official Pay Slip & Salary Receipt</span>
              </div>
              <div class="bill-meta-right">
                <div class="bill-meta-item">
                  <span class="meta-label">STATEMENT ID</span>
                  <span class="meta-value">#PAY-{{ userSalaryRecord.phoneNo.slice(-4) }}</span>
                </div>
                <div class="bill-meta-item">
                  <span class="meta-label">DATE OF ISSUE</span>
                  <span class="meta-value">{{ new Date().toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' }) }}</span>
                </div>
              </div>
            </div>

            <hr class="bill-divider" />

            <!-- Employee & Payment info -->
            <div class="bill-details-grid">
              <div class="details-column">
                <span class="details-header">EMPLOYEE DETAILS</span>
                <span class="details-name">{{ userSalaryRecord.staffName }}</span>
                <span class="details-text">Phone: {{ userSalaryRecord.phoneNo }}</span>
                <span class="details-text">Role: {{ role }}</span>
              </div>
              <div class="details-column">
                <span class="details-header">PAYMENT METHOD</span>
                <span class="details-name">Direct Bank Deposit</span>
                <span class="details-text">Status: Disbursed & Settled</span>
                <span class="details-text">Currency: Indian Rupee (INR)</span>
              </div>
            </div>

            <!-- Bill Table -->
            <div class="bill-table-wrapper">
              <table class="bill-table">
                <thead>
                  <tr>
                    <th>DESCRIPTION</th>
                    <th class="text-right">AMOUNT</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>
                      <div class="item-desc-title">Basic Salary</div>
                      <div class="item-desc-subtitle">Base compensation for academic term</div>
                    </td>
                    <td class="text-right bold-amount">₹{{ userSalaryRecord.salaryAmount?.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</td>
                  </tr>
                  <tr>
                    <td>
                      <div class="item-desc-title">Taxes & Deductions</div>
                      <div class="item-desc-subtitle">Professional tax and withholding deductions</div>
                    </td>
                    <td class="text-right bold-amount text-red">-₹{{ userSalaryRecord.taxes?.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Total Invoice Bar -->
            <div class="bill-summary-bar">
              <span class="summary-lbl">NET TAKE-HOME PAY (TOTAL)</span>
              <span class="summary-val">₹{{ userSalaryRecord.total?.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }}</span>
            </div>

            <!-- Footer terms -->
            <div class="bill-footer">
              <p class="footer-terms-text">This is a system-generated document and does not require a physical signature. If you have any inquiries regarding your salary breakdown, please contact the finance audit department.</p>
              <div class="bill-stamp-signature">
                <div class="stamp-seal">APPROVED</div>
                <div class="signature-line">
                  <span class="sig-title">Finance Officer</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="bill-actions">
            <button class="btn-primary" onclick="window.print()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="btn-icon-svg" style="width:16px; height:16px;">
                <polyline points="6 9 6 2 18 2 18 9"></polyline>
                <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                <rect x="6" y="14" width="12" height="8"></rect>
              </svg>
              Print Pay Slip
            </button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
/* Main layouts */
.main {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-app);
}

.main-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  height: 72px;
  background-color: var(--bg-card);
  border-bottom: 1px solid var(--border);
  box-shadow: var(--shadow-sm);
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
}

.brand {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: -0.5px;
}

/* Nav Button Tab Styles matching solution pill styling */
.nav-links {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-tab-btn {
  background: none;
  border: none;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  border-radius: 2px;
  cursor: pointer;
  font-family: inherit;
  transition: all var(--transition-fast);
}

.nav-tab-btn.active {
  background-color: hsl(74, 75%, 85%);
  color: hsl(74, 95%, 12%);
}

.nav-tab-btn:hover:not(.active) {
  background-color: var(--primary-light);
  color: var(--primary);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 16px;
  background-color: var(--primary-light);
  border-radius: 100px;
  border: 1px solid var(--primary-focus);
}

.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--primary);
  color: white;
  font-weight: 700;
  font-size: 14px;
}

.user-meta {
  display: flex;
  flex-direction: column;
}

.user-fullname {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role-badge {
  font-size: 10px;
  font-weight: 700;
  text-transform: uppercase;
  padding: 1px 6px;
  border-radius: 4px;
  letter-spacing: 0.5px;
  align-self: flex-start;
}
.user-role-badge.student {
  background-color: hsl(200, 85%, 90%);
  color: hsl(200, 85%, 35%);
}
.user-role-badge.staff {
  background-color: hsl(280, 85%, 90%);
  color: hsl(280, 85%, 35%);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border-radius: var(--radius-md);
  border: 1.5px solid var(--border);
  background: transparent;
  color: var(--text-secondary);
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.logout-icon {
  width: 16px;
  height: 16px;
}

.logout-btn:hover {
  background-color: var(--error-bg);
  color: var(--error);
  border-color: hsl(346, 84%, 90%);
}

/* Workspace area */
.workspace-area {
  flex: 1;
  overflow-y: auto;
  padding: 32px;
  display: flex;
  flex-direction: column;
}

.dashboard-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  animation: slide-down var(--transition-slow);
}

.welcome-message {
  font-size: 36px;
  font-weight: 800;
  color: var(--dark);
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.welcome-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  max-width: 480px;
  line-height: 1.6;
}

/* Results section styles */
.tab-pane {
  animation: slide-down 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}


.results-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 2px;
  padding: 32px;
}

.results-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
  gap: 20px;
}

.results-card-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--dark);
  margin-bottom: 6px;
}

.results-card-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

.feesbtn {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 28px;
  gap: 20px;
  font-weight: 600;
}

/* Premium Buttons */
.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: var(--primary);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
}

.btn-primary:hover {
  background-color: var(--primary-hover);
  transform: translateY(-1px);
}

.btn-icon-svg {
  width: 14px;
  height: 14px;
}

/* Banners / Notifications */
.results-alert {
  padding: 12px 16px;
  border-radius: var(--radius-md);
  margin-bottom: 24px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid transparent;
}

.feedback-success {
  background-color: var(--success-bg);
  color: var(--success);
  border-color: hsl(142, 72%, 90%);
}

.feedback-error {
  background-color: var(--error-bg);
  color: var(--error);
  border-color: hsl(346, 84%, 90%);
}

/* Spinner States */
.center-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 16px;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3.5px solid rgba(13, 110, 253, 0.1);
  border-radius: 50%;
  border-top-color: var(--primary);
  animation: spin 0.8s linear infinite;
}

.state-text {
  font-size: 14px;
  color: var(--text-secondary);
}

/* Empty State */
.empty-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: hsl(220, 10%, 94%);
  color: var(--text-secondary);
}
.empty-icon-wrapper svg {
  width: 28px;
  height: 28px;
}

.empty-icon-wrapper.fees-icon {
  background-color: hsl(45, 100%, 94%);
  color: hsl(45, 100%, 35%);
}

/* Premium Table styles */
.table-wrapper {
  width: 100%;
  overflow-x: auto;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-lg);
}

.premium-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 14px;
}

.premium-table th {
  background-color: hsl(220, 12%, 97%);
  color: var(--text-secondary);
  font-weight: 600;
  padding: 14px 20px;
  border-bottom: 1.5px solid var(--border);
}

.premium-table td {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  color: var(--text-primary);
}

.premium-table tbody tr:last-child td {
  border-bottom: none;
}

.premium-table tbody tr:hover {
  background-color: hsl(220, 10%, 98.5%);
}

.bold-td {
  font-weight: 600;
  color: var(--dark) !important;
}

/* Badges */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 100px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-badge.pass,
.status-badge.paid {
  background-color: var(--success-bg);
  color: var(--success);
}

.status-badge.fail,
.status-badge.unpaid {
  background-color: var(--error-bg);
  color: var(--error);
}

.status-badge.partial {
  background-color: hsl(45, 100%, 94%);
  color: hsl(45, 100%, 35%);
}

/* Action buttons for Staff */
.actions-group {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: transparent;
  border: 1.5px solid var(--border);
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: all var(--transition-fast);
}

.action-btn.edit {
  color: var(--primary);
}
.action-btn.edit:hover {
  background-color: var(--primary-light);
  border-color: var(--primary-focus);
}

.action-btn.delete {
  color: var(--error);
}
.action-btn.delete:hover {
  background-color: var(--error-bg);
  border-color: hsl(346, 84%, 88%);
}

/* Modal overlays and backdrops */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fade-in 0.25s ease-out;
}

.modal-content {
  background-color: var(--bg-card);
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-md);
  width: 100%;
  max-width: 500px;
  padding: 32px;
  animation: zoom-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.modal-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header-bar h3 {
  font-size: 18px;
  font-weight: 700;
  color: var(--dark);
}

.close-modal-btn {
  background: none;
  border: none;
  font-size: 28px;
  color: var(--text-secondary);
  cursor: pointer;
  line-height: 1;
}

.close-modal-btn:hover {
  color: var(--error);
}

/* Modal forms */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.modal-error-banner {
  padding: 10px 14px;
  background-color: var(--error-bg);
  color: var(--error);
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 500;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-field label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.form-row-2 {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
}

.form-field input[type="number"],
.form-field input[type="text"] {
  width: 100%;
  padding: 10px 14px;
  border-radius: var(--radius-md);
  border: 1.5px solid var(--border);
  font-family: inherit;
  font-size: 14px;
  color: var(--text-primary);
  background-color: var(--bg-card);
  transition: all var(--transition-fast);
}

.form-field input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-focus);
}

/* Radio buttons styled like nice pills */
.radio-pill-group {
  display: flex;
  gap: 12px;
}

.radio-pill {
  flex: 1;
  position: relative;
  cursor: pointer;
}

.radio-pill input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.pill-span {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 14px;
  border-radius: var(--radius-md);
  border: 1.5px solid var(--border);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.radio-pill input:checked + .pill-span.pass {
  background-color: var(--success-bg);
  color: var(--success);
  border-color: var(--success);
}

.radio-pill input:checked + .pill-span.fail {
  background-color: var(--error-bg);
  color: var(--error);
  border-color: var(--error);
}

.radio-pill input:checked + .pill-span.partial {
  background-color: hsl(45, 100%, 94%);
  color: hsl(45, 100%, 35%);
  border-color: hsl(45, 100%, 35%);
}

/* Modal actions */
.modal-action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.btn-secondary-action {
  background-color: transparent;
  border: 1.5px solid var(--border);
  color: var(--text-primary);
  padding: 10px 18px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  transition: all var(--transition-fast);
}

.btn-secondary-action:hover {
  background-color: hsl(220, 10%, 95%);
}

.btn-primary-action {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: var(--primary);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
}

.btn-primary-action:hover {
  background-color: var(--primary-hover);
}

.form-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes zoom-in {
  from { transform: scale(0.95); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Modal transitions */
.modal-enter-active {
  animation: fade-in 0.2s ease-out;
}
.modal-leave-active {
  animation: fade-in 0.15s ease-in reverse;
}

/* Salary Bill Styles */
.salary-bill-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.salary-bill-card {
  background-color: white;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  box-shadow: none; /* No shadow */
  padding: 40px;
  width: 100%;
  color: var(--dark);
  font-family: inherit;
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.bill-logo-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.bill-brand {
  font-size: 24px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: -0.5px;
}

.bill-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
}

.bill-meta-right {
  display: flex;
  gap: 24px;
}

.bill-meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: right;
}

.meta-label {
  font-size: 11px;
  font-weight: 700;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
}

.meta-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--dark);
}

.bill-divider {
  border: none;
  border-top: 1.5px dashed var(--border);
  margin: 28px 0;
}

.bill-details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-bottom: 32px;
}

.details-column {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.details-header {
  font-size: 11px;
  font-weight: 800;
  color: var(--text-secondary);
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.details-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--dark);
}

.details-text {
  font-size: 13px;
  color: var(--text-primary);
}

.bill-table-wrapper {
  margin-bottom: 32px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.bill-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 14px;
}

.bill-table th {
  background-color: hsl(220, 10%, 97%);
  color: var(--text-secondary);
  font-weight: 700;
  padding: 12px 20px;
  border-bottom: 1.5px solid var(--border);
}

.bill-table td {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.bill-table tr:last-child td {
  border-bottom: none;
}

.item-desc-title {
  font-weight: 700;
  color: var(--dark);
  margin-bottom: 2px;
}

.item-desc-subtitle {
  font-size: 12px;
  color: var(--text-secondary);
}

.bold-amount {
  font-weight: 700;
  font-size: 15px;
  color: var(--dark);
}

.bold-amount.text-red {
  color: var(--error);
}

.text-right {
  text-align: right;
}

.bill-summary-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--primary-light);
  border: 1.5px solid var(--primary-focus);
  padding: 18px 24px;
  border-radius: var(--radius-md);
  margin-bottom: 32px;
}

.summary-lbl {
  font-size: 14px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: 0.5px;
}

.summary-val {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary);
}

.bill-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 40px;
}

.footer-terms-text {
  font-size: 11px;
  color: var(--text-secondary);
  line-height: 1.5;
  flex: 1;
}

.bill-stamp-signature {
  display: flex;
  align-items: center;
  gap: 24px;
}

.stamp-seal {
  border: 3px double hsl(142, 72%, 40%);
  color: hsl(142, 72%, 35%);
  background-color: hsl(142, 72%, 96%);
  font-size: 12px;
  font-weight: 800;
  padding: 6px 14px;
  border-radius: 4px;
  transform: rotate(-8deg);
  letter-spacing: 1px;
}

.signature-line {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-top: 1.5px solid var(--text-secondary);
  padding-top: 6px;
  width: 120px;
}

.sig-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-secondary);
}

.bill-actions {
  display: flex;
  justify-content: center;
  width: 100%;
}
</style>