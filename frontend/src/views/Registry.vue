<template>
  <div class="registry-container">
    <div class="table-header">
      <h2>{{ activeView === 'registry' ? 'Shelter Animals' : 'Archive' }}</h2>
      <div class="header-right">
        <div class="view-tabs">
          <button 
            :class="['tab-btn', { 'active': activeView === 'registry' }]" 
            @click="handleViewChange('registry')">
            Registry
          </button>
          <button 
            :class="['tab-btn', { 'active': activeView === 'archive' }]" 
            @click="handleViewChange('archive')">
            Archive
          </button>
        </div>
        <button class="primary-btn" v-if="activeView === 'registry'" @click="showAddModal = true">+ Add Animal</button>
      </div>
    </div>

    <div class="filters-card">
      <div class="filters-row">
        <div class="filter-group search-group">
          <label>Search by name</label>
          <input type="text" v-model="filters.name" placeholder="Type animal name..." class="input-field">
        </div>

        <div class="filter-group">
          <label>Species</label>
          <select v-model="filters.species" class="input-field">
            <option value="">All Species</option>
            <option value="Dog">Dog</option>
            <option value="Cat">Cat</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Max Age (years)</label>
          <input type="number" v-model.number="filters.maxAge" placeholder="e.g. 5" min="0" class="input-field">
        </div>

        <div v-if="activeView === 'registry'" class="filter-group">
          <label>Adoption Status</label>
          <select v-model="filters.status" class="input-field">
            <option value="">All Statuses</option>
            <option value="Available">Available</option>
            <option value="Pending">Pending</option>
            <option value="On Hold">On Hold</option>
            <option value="Adopted">Adopted</option>
          </select>
        </div>

        <button class="secondary-btn reset-btn" @click="resetFilters">Clear Filters</button>
      </div>
    </div>

    <div class="registry-card">
      <table class="registry-table">
        <thead>
        <tr>
          <th style="width: 40px;"></th>
          <th>Photo</th>
          <th>Name</th>
          <th>Species</th>
          <th>Age</th>
          <th>Kennel</th>
          <th>Adoption Status</th>
          <th>Admission Date</th>
        </tr>
        </thead>
        <tbody>
        <template v-for="animal in filteredAnimals" :key="animal.id">

          <tr class="animal-row" @click="toggleRow(animal.id)">
            <td class="expand-cell">
              <span class="expand-arrow" :class="{ 'expanded': expandedRows.includes(animal.id) }">›</span>
            </td>
            <td><div class="avatar">{{ animal.photo }}</div></td>
            <td class="font-medium">{{ animal.name }}</td>
            <td class="text-gray">{{ animal.species }}</td>
            <td>{{ animal.age }} {{ animal.age === 1 ? 'year' : 'years' }}</td>
            <td><span class="kennel-badge">{{ animal.kennel }}</span></td>
            <td>
                <span :class="['status-badge', getStatusClass(animal.status)]">
                  {{ animal.status }}
                </span>
            </td>
            <td class="text-gray">{{ animal.date }}</td>
          </tr>

          <tr v-if="expandedRows.includes(animal.id)" class="details-row">
            <td colspan="8" class="details-cell">
              
              <div class="medical-history-container">
                <div class="history-header">
                  <span class="history-icon">🩺</span> <h3>Medical History ({{ animal.name }})</h3>
                </div>

                <table class="medical-table">
                  <thead>
                  <tr>
                    <th>Date</th>
                    <th>Event Type</th>
                    <th>Description</th>
                    <th>Veterinarian</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="(record, index) in animal.medicalHistory" :key="index">
                    <td>{{ record.date }}</td>
                    <td><span class="event-badge">{{ record.type }}</span></td>
                    <td class="text-gray">{{ record.description }}</td>
                    <td>{{ record.vet }}</td>
                  </tr>
                  <tr v-if="!animal.medicalHistory || animal.medicalHistory.length === 0">
                    <td colspan="4" class="text-gray" style="text-align: center; padding: 2rem;">No medical records found for this animal.</td>
                  </tr>
                  </tbody>
                </table>

                <div v-if="activeView === 'registry'" class="add-record-form">
                  <h4>+ Add New Record</h4>
                  <div class="form-row">
                    <select v-model="newMedical[animal.id].type" class="input-field">
                      <option value="" disabled>Select type</option>
                      <option v-for="type in medicalRecordTypes" :key="type" :value="type">{{ type }}</option>
                    </select>
                    <input type="text" v-model="newMedical[animal.id].description" placeholder="Procedure description..." class="input-field flex-grow">
                    <input type="text" v-model="newMedical[animal.id].vet" placeholder="Veterinarian" class="input-field">
                    <button class="secondary-btn" @click="submitMedicalRecord(animal.id)">Save</button>
                  </div>
                </div>
              </div>

              <div class="adoption-history-container">
                <div class="history-header">
                  <span class="history-icon">📋</span> <h3>Adoption Status History ({{ animal.name }})</h3>
                </div>

                <table class="adoption-table">
                  <thead>
                  <tr>
                    <th>Date</th>
                    <th>Status</th>
                    <th>Owner</th>
                    <th>Description</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="(record, index) in animal.adoptionStatusHistory" :key="index">
                    <td>{{ record.date }}</td>
                    <td><span :class="['status-badge', getStatusClass(record.status)]">{{ record.status }}</span></td>
                    <td class="text-gray">{{ record.owner || '-' }}</td>
                    <td class="text-gray">{{ record.notes || '-' }}</td>
                  </tr>
                  <tr v-if="!animal.adoptionStatusHistory || animal.adoptionStatusHistory.length === 0">
                    <td colspan="4" class="text-gray" style="text-align: center; padding: 2rem;">No adoption status changes recorded.</td>
                  </tr>
                  </tbody>
                </table>

                <div v-if="activeView === 'registry'" class="add-record-form">
                  <h4>+ Add Adoption Status Change</h4>
                  <div class="form-row">
                    <input type="date" v-model="newAdoptionStatus[animal.id].date" class="input-field">
                    <select v-model="newAdoptionStatus[animal.id].status" class="input-field">
                      <option value="" disabled>Select status</option>
                      <option value="Available">Available</option>
                      <option value="Pending">Pending</option>
                      <option value="On Hold">On Hold</option>
                      <option value="Adopted">Adopted</option>
                      <option value="Finalized">Finalized</option>
                    </select>
                  </div>
                  <div class="form-row">
                    <input type="text" v-model="newAdoptionStatus[animal.id].owner" placeholder="Owner" class="input-field flex-grow">
                    <input type="text" v-model="newAdoptionStatus[animal.id].notes" placeholder="Description (optional)" class="input-field flex-grow">
                    <button class="secondary-btn" @click="submitAdoptionStatusChange(animal.id)">Save</button>
                  </div>
                </div>
              </div>

              <div v-if="activeView === 'archive'" class="restore-button-container">
                <button class="restore-btn-large" @click="restoreAnimal(animal.id)">↩️ Restore from Archive</button>
              </div>

            </td>
          </tr>
        </template>
        <tr v-if="filteredAnimals.length === 0">
          <td colspan="8" class="text-gray" style="text-align: center; padding: 3rem;">No animals match the selected filters.</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-card">
        <h3>New Animal Registration</h3>

        <div class="form-group">
          <label>Animal Name</label>
          <input v-model="newAnimal.name" type="text" placeholder="e.g., Buddy" class="input-field w-full">
        </div>

        <div class="form-group">
          <label>Species</label>
          <select v-model="newAnimal.species" class="input-field w-full">
            <option value="Dog">Dog</option>
            <option value="Cat">Cat</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div class="form-group">
          <label>Age (years)</label>
          <input v-model.number="newAnimal.age" type="number" min="0" placeholder="e.g., 2" class="input-field w-full">
        </div>

        <div class="modal-actions">
          <button class="cancel-btn" @click="showAddModal = false">Cancel</button>
          <button class="primary-btn" @click="submitNewAnimal">Add Animal</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, computed } from 'vue'
import axios from 'axios'

const expandedRows = ref([])
const animals = ref([])
const medicalRecordTypes = ref([])
const activeView = ref('registry')
const showAddModal = ref(false)

const newAnimal = ref({ name: '', species: 'Dog', age: 0, status: 'Available' })
const newMedical = ref({})
const newAdoptionStatus = ref({})

const filters = ref({
  name: '',
  species: '',
  maxAge: null,
  status: ''
})

const filteredAnimals = computed(() => {
  return animals.value.filter(animal => {
    // Widok podstawowy (Registry vs Archive)
    const matchesView = activeView.value === 'registry' 
      ? animal.status !== 'Finalized' 
      : animal.status === 'Finalized'

    if (!matchesView) return false

    // Dynamiczne wyszukiwanie po imieniu (ignoruje wielkość liter)
    const matchesName = !filters.value.name || animal.name.toLowerCase().includes(filters.value.name.toLowerCase())

    // Reaktywna filtracja po gatunku
    let matchesSpecies = true
    if (filters.value.species === 'Other') {
      matchesSpecies = animal.species !== 'Dog' && animal.species !== 'Cat'
    } else if (filters.value.species) {
      matchesSpecies = animal.species === filters.value.species
    }

    // Filtracja po wieku
    const matchesAge = filters.value.maxAge === null || filters.value.maxAge === '' || animal.age <= filters.value.maxAge

    // Filtracja po statusie adopcji
    const matchesStatus = activeView.value === 'archive' || !filters.value.status || animal.status === filters.value.status

    return matchesName && matchesSpecies && matchesAge && matchesStatus
  })
})

const handleViewChange = (view) => {
  activeView.value = view
  resetFilters()
}

const resetFilters = () => {
  filters.value = { name: '', species: '', maxAge: null, status: '' }
}

const fetchMedicalRecordTypes = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/medical-records/types')
    medicalRecordTypes.value = response.data

    animals.value.forEach(animal => {
      if (newMedical.value[animal.id] && !newMedical.value[animal.id].type) {
        newMedical.value[animal.id].type = response.data[0] ?? ''
      }
    })
  } catch (error) {
    console.error('Error fetching medical record types:', error)
  }
}

const fetchAnimals = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/animals')

    animals.value = response.data.map(animal => {
      if (!newMedical.value[animal.id]) {
        newMedical.value[animal.id] = { type: medicalRecordTypes.value[0] ?? '', description: '', vet: '' }
      }
      if (!newAdoptionStatus.value[animal.id]) {
        newAdoptionStatus.value[animal.id] = { date: new Date().toISOString().split('T')[0], status: '', owner: '', notes: '' }
      }

      return {
        id: animal.id,
        photo: animal.species === 'Cat' ? '🐱' : (animal.species === 'Dog' ? '🐕' : '🐾'),
        name: animal.name,
        species: animal.species,
        age: animal.age ?? 0,
        kennel: animal.kennel ? animal.kennel.code : '-',
        status: animal.adoptionStatus,
        date: new Date().toISOString().split('T')[0],
        medicalHistory: [],
        adoptionStatusHistory: [
          {
            date: new Date().toISOString().split('T')[0],
            status: animal.adoptionStatus,
            owner: null,
            notes: 'Initial status'
          }
        ]
      }
    })
  } catch (error) {
    console.error("Error fetching animals:", error)
  }
}

const toggleRow = async (id) => {
  const index = expandedRows.value.indexOf(id)
  if (index === -1) {
    expandedRows.value.push(id)
    const animal = animals.value.find(a => a.id === id);

    if (animal && animal.medicalHistory.length === 0) {
      try {
        const response = await axios.get(`http://localhost:8080/api/medical-records/animal/${id}`)
        animal.medicalHistory = response.data.map(record => ({
          date: record.recordDate,
          type: record.recordType,
          description: record.description,
          vet: record.doctor
        }))
      } catch (error) {
        console.error("Error fetching medical records:", error)
      }
    }

    if (animal && animal.adoptionStatusHistory.length === 1) {
      try {
        const response = await axios.get(`http://localhost:8080/api/adoption-status-history/animal/${id}`)
        animal.adoptionStatusHistory = response.data.map(record => ({
          date: record.date,
          status: record.status,
          owner: record.owner || record.potentialOwner,
          notes: record.notes
        }))
      } catch (error) {
        console.error("Error fetching adoption status history:", error)
      }
    }
  } else {
    expandedRows.value.splice(index, 1)
  }
}

const submitNewAnimal = async () => {
  if (!newAnimal.value.name) return alert("Please provide the animal's name!");
  if (newAnimal.value.age < 0) return alert("Age cannot be negative!");

  try {
    await axios.post('http://localhost:8080/api/animals', {
      name: newAnimal.value.name,
      species: newAnimal.value.species,
      age: newAnimal.value.age,
      adoptionStatus: newAnimal.value.status,
      isQuarantined: false
    })

    showAddModal.value = false;
    newAnimal.value = { name: '', species: 'Dog', age: 0, status: 'Available' };
    await fetchAnimals();
  } catch (error) {
    console.error("Error adding animal:", error)
    alert("An error occurred while adding the animal.");
  }
}

const submitMedicalRecord = async (animalId) => {
  const recordData = newMedical.value[animalId];
  if (!recordData.description || !recordData.type) return alert("Please fill in the record type and description!");

  try {
    const payload = {
      recordDate: new Date().toISOString().split('T')[0],
      recordType: recordData.type,
      description: recordData.description,
      doctor: recordData.vet,
      animal: { id: animalId }
    };

    await axios.post('http://localhost:8080/api/medical-records', payload);

    const animal = animals.value.find(a => a.id === animalId);
    animal.medicalHistory.push({
      date: payload.recordDate,
      type: payload.recordType,
      description: payload.description,
      vet: payload.doctor
    });

    newMedical.value[animalId] = { type: '', description: '', vet: '' };
  } catch (error) {
    console.error("Error saving medical record:", error)
    alert("Error saving record. Make sure the event type matches your Java Enum.");
  }
}

const submitAdoptionStatusChange = async (animalId) => {
  const recordData = newAdoptionStatus.value[animalId];
  if (!recordData.date || !recordData.status || !recordData.owner) return alert("Please fill in the date, status, and owner!");

  try {
    await axios.patch(`http://localhost:8080/api/animals/${animalId}/adoption-status`, {
      status: recordData.status
    })

    await axios.post(`http://localhost:8080/api/adoption-status-history`, {
      date: recordData.date,
      status: recordData.status,
      owner: recordData.owner,
      notes: recordData.notes || null,
      animal: { id: animalId }
    })

    const animal = animals.value.find(a => a.id === animalId);
    if (animal) {
      animal.status = recordData.status
      if (!animal.adoptionStatusHistory) {
        animal.adoptionStatusHistory = []
      }
      animal.adoptionStatusHistory.unshift({
        date: recordData.date,
        status: recordData.status,
        owner: recordData.owner,
        notes: recordData.notes || null
      })
    }

    newAdoptionStatus.value[animalId] = { date: new Date().toISOString().split('T')[0], status: '', owner: '', notes: '' };
  } catch (error) {
    console.error("Error saving adoption status change:", error)
    alert("An error occurred while saving the adoption status change.");
  }
}

const restoreAnimal = async (animalId) => {
  if (!confirm("Restore this animal to the registry?")) return;

  try {
    await axios.patch(`http://localhost:8080/api/animals/${animalId}/adoption-status`, {
      status: 'Available'
    })

    const animal = animals.value.find(a => a.id === animalId);
    if (animal) {
      animal.status = 'Available'
      if (!animal.adoptionStatusHistory) {
        animal.adoptionStatusHistory = []
      }
      animal.adoptionStatusHistory.unshift({
        date: new Date().toISOString().split('T')[0],
        status: 'Available',
        owner: null,
        notes: 'Restored from archive'
      })
    }
  } catch (error) {
    console.error("Error restoring animal:", error)
    alert("An error occurred while restoring the animal.");
  }
}

const getStatusClass = (status) => {
  switch(status) {
    case 'Available': return 'status-available';
    case 'Pending': return 'status-pending';
    case 'On Hold': return 'status-on-hold';
    case 'Adopted': return 'status-adopted';
    case 'Finalized': return 'status-finalized';
    default: return '';
  }
}

onMounted(() => {
  fetchMedicalRecordTypes().then(fetchAnimals)
})
onActivated(() => {
  fetchMedicalRecordTypes().then(fetchAnimals)
})
</script>

<style scoped>
.registry-card { background-color: #FFFFFF; border-radius: 12px; border: 1px solid #E5E7EB; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02); overflow: hidden; }
.registry-table { width: 100%; border-collapse: collapse; text-align: left; }
.registry-table th { padding: 1.25rem 1rem; font-size: 0.85rem; font-weight: 600; color: #374151; border-bottom: 1px solid #E5E7EB; }
.registry-table td { padding: 1rem; font-size: 0.95rem; border-bottom: 1px solid #F3F4F6; vertical-align: middle; }
.animal-row { transition: background-color 0.2s; cursor: pointer; }
.animal-row:hover { background-color: #F9FAFB; }
.expand-cell { text-align: center; color: #9CA3AF; width: 40px; }
.expand-arrow { display: inline-block; font-size: 1.5rem; line-height: 1; transition: transform 0.2s ease; }
.expand-arrow.expanded { transform: rotate(90deg); }
.avatar { width: 36px; height: 36px; background-color: #FEE2E2; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-size: 1.2rem; }
.font-medium { font-weight: 500; color: #111827; }
.text-gray { color: #6B7280; }
.kennel-badge { background-color: #F3F4F6; padding: 0.25rem 0.75rem; border-radius: 6px; font-size: 0.85rem; color: #4B5563; font-weight: 500; }
.status-badge { padding: 0.35rem 0.75rem; border-radius: 20px; font-size: 0.85rem; font-weight: 500; display: inline-block; }
.status-available { background-color: #D1FAE5; color: #065F46; }
.status-pending { background-color: #FEF3C7; color: #92400E; }
.status-on-hold { background-color: #F3F4F6; color: #4B5563; }
.status-adopted { background-color: #DBEAFE; color: #1E40AF; }
.status-finalized { background-color: #DDD6FE; color: #4F46E5; }
.details-row td { padding: 0; border-bottom: 1px solid #E5E7EB; background-color: #FAFAFA; }
.details-cell { padding-bottom: 1.5rem !important; }
.medical-history-container { margin: 0 1rem 1rem 1rem; background-color: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.history-header { display: flex; align-items: center; gap: 8px; padding: 1rem 1.5rem; border-bottom: 1px solid #F3F4F6; }
.history-icon { color: #D41B65; font-size: 1.1rem; }
.history-header h3 { margin: 0; font-size: 0.95rem; font-weight: 600; color: #374151; }
.medical-table { width: 100%; border-collapse: collapse; text-align: left; }
.medical-table th { padding: 0.75rem 1.5rem; font-size: 0.8rem; font-weight: 600; color: #4B5563; background-color: #FFFFFF; border-bottom: 1px solid #F3F4F6; }
.medical-table td { padding: 1rem 1.5rem; font-size: 0.9rem; color: #111827; border-bottom: 1px solid #F3F4F6; }
.medical-table tr:last-child td { border-bottom: none; }
.event-badge { background-color: #FDF2F8; color: #D41B65; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }
.adoption-history-container { margin: 1rem 1rem 0 1rem; background-color: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.adoption-table { width: 100%; border-collapse: collapse; text-align: left; }
.adoption-table th { padding: 0.75rem 1.5rem; font-size: 0.8rem; font-weight: 600; color: #4B5563; background-color: #FFFFFF; border-bottom: 1px solid #F3F4F6; }
.adoption-table td { padding: 1rem 1.5rem; font-size: 0.9rem; color: #111827; border-bottom: 1px solid #F3F4F6; }
.adoption-table tr:last-child td { border-bottom: none; }

.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.table-header h2 { margin: 0; font-size: 1.25rem; font-weight: 600; color: #111827; }
.header-right { display: flex; gap: 1rem; align-items: center; }
.view-tabs { display: flex; gap: 0.5rem; }
.tab-btn { background-color: transparent; border: 1px solid #D1D5DB; padding: 0.5rem 1rem; border-radius: 6px; font-weight: 500; cursor: pointer; color: #6B7280; transition: 0.2s; }
.tab-btn.active { background-color: #D41B65; color: white; border-color: #D41B65; }
.tab-btn:hover { border-color: #D41B65; }
.primary-btn { background-color: #D41B65; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 8px; font-weight: 500; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { background-color: #B01552; }
.secondary-btn { background-color: #F3F4F6; color: #4B5563; border: 1px solid #E5E7EB; padding: 0.5rem 1rem; border-radius: 6px; font-weight: 500; cursor: pointer; }
.secondary-btn:hover { background-color: #E5E7EB; }
.cancel-btn { background-color: transparent; color: #6B7280; border: none; font-weight: 500; cursor: pointer; padding: 0.6rem 1.2rem; }

.restore-button-container { margin: 1rem; padding: 1.5rem; background-color: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 8px; text-align: center; }
.restore-btn-large { background-color: #D1FAE5; color: #065F46; border: none; padding: 0.75rem 1.5rem; border-radius: 6px; font-weight: 500; cursor: pointer; font-size: 1rem; }
.restore-btn-large:hover { background-color: #A7F3D0; }
.input-field { padding: 0.5rem 0.75rem; border: 1px solid #D1D5DB; border-radius: 6px; font-size: 0.9rem; outline: none; }
.input-field:focus { border-color: #D41B65; }
.w-full { width: 100%; box-sizing: border-box; }
.flex-grow { flex-grow: 1; }
.add-record-form { padding: 1.5rem; background-color: #F9FAFB; border-top: 1px solid #E5E7EB; }
.add-record-form h4 { margin: 0 0 1rem 0; font-size: 0.9rem; color: #374151; }
.form-row { display: flex; gap: 0.75rem; align-items: center; margin-bottom: 0.75rem; }
.form-row:last-child { margin-bottom: 0; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-card { background-color: white; padding: 2rem; border-radius: 12px; width: 400px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
.modal-card h3 { margin-top: 0; margin-bottom: 1.5rem; font-size: 1.25rem; color: #111827; }
.form-group { margin-bottom: 1.25rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-size: 0.85rem; font-weight: 500; color: #4B5563; }
.modal-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem; }

/* Style Filtrowania */
.filters-card { background-color: #FFFFFF; border-radius: 12px; border: 1px solid #E5E7EB; padding: 1.25rem; margin-bottom: 1.5rem; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.01); }
.filters-row { display: flex; gap: 1.5rem; align-items: flex-end; flex-wrap: wrap; }
.filter-group { display: flex; flex-direction: column; gap: 0.5rem; flex-grow: 1; max-width: 220px; }
.search-group { max-width: 280px; } /* Szerszy kontener dla wyszukiwarki */
.filter-group label { font-size: 0.8rem; font-weight: 600; color: #4B5563; text-transform: uppercase; letter-spacing: 0.05em; }
.filter-group .input-field { width: 100%; box-sizing: border-box; height: 38px; }
.reset-btn { height: 38px; padding: 0 1.25rem; display: flex; align-items: center; justify-content: center; background-color: #F3F4F6; border-color: #D1D5DB; }
.reset-btn:hover { background-color: #E5E7EB; }
</style>