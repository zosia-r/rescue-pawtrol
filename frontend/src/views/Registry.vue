<template>
  <div class="registry-container">
    <div class="table-header">
      <h2>Shelter Animals</h2>
      <button class="primary-btn" @click="showAddModal = true">+ Add Animal</button>
    </div>

    <div class="registry-card">
      <table class="registry-table">
        <thead>
        <tr>
          <th style="width: 40px;"></th>
          <th>Photo</th>
          <th>Name</th>
          <th>Species</th>
          <th>Kennel</th>
          <th>Adoption Status</th>
          <th>Admission Date</th>
        </tr>
        </thead>
        <tbody>
        <template v-for="animal in animals" :key="animal.id">

          <tr class="animal-row" @click="toggleRow(animal.id)">
            <td class="expand-cell">
              <span class="expand-arrow" :class="{ 'expanded': expandedRows.includes(animal.id) }">›</span>
            </td>
            <td><div class="avatar">{{ animal.photo }}</div></td>
            <td class="font-medium">{{ animal.name }}</td>
            <td class="text-gray">{{ animal.species }}</td>
            <td><span class="kennel-badge">{{ animal.kennel }}</span></td>
            <td>
                <span :class="['status-badge', getStatusClass(animal.status)]">
                  {{ animal.status }}
                </span>
            </td>
            <td class="text-gray">{{ animal.date }}</td>
          </tr>

          <tr v-if="expandedRows.includes(animal.id)" class="details-row">
            <td colspan="7" class="details-cell">
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

                <div class="add-record-form">
                  <h4>+ Add New Record</h4>
                  <div class="form-row">
                    <input type="text" v-model="newMedical[animal.id].type" placeholder="Type (e.g., VACCINATION)" class="input-field">
                    <input type="text" v-model="newMedical[animal.id].description" placeholder="Procedure description..." class="input-field flex-grow">
                    <input type="text" v-model="newMedical[animal.id].vet" placeholder="Veterinarian" class="input-field">
                    <button class="secondary-btn" @click="submitMedicalRecord(animal.id)">Save</button>
                  </div>
                </div>

              </div>
            </td>
          </tr>
        </template>
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
          <label>Adoption Status</label>
          <select v-model="newAnimal.status" class="input-field w-full">
            <option value="Available">Available</option>
            <option value="Pending">Pending</option>
            <option value="On Hold">On Hold</option>
          </select>
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
import { ref, onMounted, onActivated } from 'vue'
import axios from 'axios'

const expandedRows = ref([])
const animals = ref([])

const showAddModal = ref(false)
const newAnimal = ref({ name: '', species: 'Dog', status: 'Available' })
const newMedical = ref({})

const fetchAnimals = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const response = await axios.get('http://localhost:8080/api/animals', {
      headers: { Authorization: `Bearer ${token}` }
    })

    animals.value = response.data.map(animal => {
      if (!newMedical.value[animal.id]) {
        newMedical.value[animal.id] = { type: '', description: '', vet: '' }
      }

      return {
        id: animal.id,
        photo: animal.species === 'Cat' ? '🐱' : (animal.species === 'Dog' ? '🐕' : '🐾'),
        name: animal.name,
        species: animal.species,
        kennel: animal.kennel ? animal.kennel.code : '-',
        status: animal.adoptionStatus,
        date: new Date().toISOString().split('T')[0],
        medicalHistory: []
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
        const token = localStorage.getItem('jwt_token')
        const response = await axios.get(`http://localhost:8080/api/medical-records/animal/${id}`, {
          headers: { Authorization: `Bearer ${token}` }
        })

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
  } else {
    expandedRows.value.splice(index, 1)
  }
}

const submitNewAnimal = async () => {
  if (!newAnimal.value.name) return alert("Please provide the animal's name!");

  try {
    const token = localStorage.getItem('jwt_token')

    await axios.post('http://localhost:8080/api/animals', {
      name: newAnimal.value.name,
      species: newAnimal.value.species,
      adoptionStatus: newAnimal.value.status,
      isQuarantined: false
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })

    showAddModal.value = false;
    newAnimal.value = { name: '', species: 'Dog', status: 'Available' };
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
    const token = localStorage.getItem('jwt_token')

    const payload = {
      recordDate: new Date().toISOString().split('T')[0],
      recordType: recordData.type,
      description: recordData.description,
      doctor: recordData.vet,
      animal: { id: animalId }
    };

    const response = await axios.post('http://localhost:8080/api/medical-records', payload, {
      headers: { Authorization: `Bearer ${token}` }
    });

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

onMounted(() => {
  fetchAnimals()
})
onActivated(() => {
  fetchAnimals()
})

const getStatusClass = (status) => {
  switch(status) {
    case 'Available': return 'status-available';
    case 'Pending': return 'status-pending';
    case 'On Hold': return 'status-on-hold';
    case 'Adopted': return 'status-adopted';
    default: return '';
  }
}
</script>

<style scoped>
/* --- STYLE BEZ ZMIAN --- */
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
.details-row td { padding: 0; border-bottom: 1px solid #E5E7EB; background-color: #FAFAFA; }
.details-cell { padding-bottom: 1.5rem !important; }
.medical-history-container { margin: 0 1rem 1rem 5rem; background-color: #FFFFFF; border: 1px solid #E5E7EB; border-radius: 8px; overflow: hidden; }
.history-header { display: flex; align-items: center; gap: 8px; padding: 1rem 1.5rem; border-bottom: 1px solid #F3F4F6; }
.history-icon { color: #D41B65; font-size: 1.1rem; }
.history-header h3 { margin: 0; font-size: 0.95rem; font-weight: 600; color: #374151; }
.medical-table { width: 100%; border-collapse: collapse; text-align: left; }
.medical-table th { padding: 0.75rem 1.5rem; font-size: 0.8rem; font-weight: 600; color: #4B5563; background-color: #FFFFFF; border-bottom: 1px solid #F3F4F6; }
.medical-table td { padding: 1rem 1.5rem; font-size: 0.9rem; color: #111827; border-bottom: 1px solid #F3F4F6; }
.medical-table tr:last-child td { border-bottom: none; }
.event-badge { background-color: #FDF2F8; color: #D41B65; padding: 0.25rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 500; }

.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.table-header h2 { margin: 0; font-size: 1.25rem; font-weight: 600; color: #111827; }
.primary-btn { background-color: #D41B65; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 8px; font-weight: 500; cursor: pointer; transition: 0.2s; }
.primary-btn:hover { background-color: #B01552; }
.secondary-btn { background-color: #F3F4F6; color: #4B5563; border: 1px solid #E5E7EB; padding: 0.5rem 1rem; border-radius: 6px; font-weight: 500; cursor: pointer; }
.secondary-btn:hover { background-color: #E5E7EB; }
.cancel-btn { background-color: transparent; color: #6B7280; border: none; font-weight: 500; cursor: pointer; padding: 0.6rem 1.2rem; }
.input-field { padding: 0.5rem 0.75rem; border: 1px solid #D1D5DB; border-radius: 6px; font-size: 0.9rem; outline: none; }
.input-field:focus { border-color: #D41B65; }
.w-full { width: 100%; box-sizing: border-box; }
.flex-grow { flex-grow: 1; }
.add-record-form { padding: 1.5rem; background-color: #F9FAFB; border-top: 1px solid #E5E7EB; }
.add-record-form h4 { margin: 0 0 1rem 0; font-size: 0.9rem; color: #374151; }
.form-row { display: flex; gap: 0.75rem; align-items: center; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-card { background-color: white; padding: 2rem; border-radius: 12px; width: 400px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
.modal-card h3 { margin-top: 0; margin-bottom: 1.5rem; font-size: 1.25rem; color: #111827; }
.form-group { margin-bottom: 1.25rem; }
.form-group label { display: block; margin-bottom: 0.5rem; font-size: 0.85rem; font-weight: 500; color: #4B5563; }
.modal-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem; }
</style>