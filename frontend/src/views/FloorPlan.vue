<template>
  <div class="floorplan-container">

    <div class="floorplan-main">

      <div class="sectors-grid">
        <div
          v-for="sector in filteredSectors"
          :key="sector.id"
          class="sector-card"
        >
          <div class="sector-header">
            <span class="sector-dot" :style="{ backgroundColor: sector.color }"></span>
            <h2 class="sector-title">{{ sector.name }}</h2>
          </div>

          <div class="kennels-grid">

            <div
              v-for="kennel in sector.kennels"
              :key="kennel.id"
              class="kennel-wrapper"
            >

              <div
                class="kennel-card"
                :class="getKennelClass(kennel)"
                @dragover.prevent
                @drop="onDrop($event, kennel)"
                @click="openKennelDetail(kennel)"
              >

                <div
                  class="kennel-full-badge"
                  v-if="occupancyPercent(kennel) >= 100"
                >
                  Full
                </div>

                <div class="kennel-top-row">
                  <span class="kennel-code">{{ kennel.code }}</span>
                  <span class="kennel-species-icon">{{ sector.icon }}</span>
                </div>

                <div class="kennel-occupancy-row">
                  <span class="occ-label">Occupancy</span>

                  <span
                    class="occ-percent"
                    :class="getPercentClass(kennel)"
                  >
                    {{ occupancyPercent(kennel) }}%
                  </span>
                </div>

                <div class="kennel-bar-bg">
                  <div
                    class="kennel-bar-fill"
                    :class="getBarClass(kennel)"
                    :style="{ width: occupancyPercent(kennel) + '%' }"
                  ></div>
                </div>

                <div class="kennel-count">
                  {{ kennel.animals.length }} / {{ kennel.capacity }}
                </div>

              </div>

              <transition name="expand">
                <div
                  v-if="selectedKennel?.id === kennel.id"
                  class="kennel-expanded"
                >

                  <div
                    v-if="kennel.animals.length === 0"
                    class="empty-kennel-msg"
                  >
                    No animals in this kennel.
                  </div>

                  <div
                    v-for="animal in kennel.animals"
                    :key="animal.id"
                    class="animal-card-in-kennel"
                  >
                    <div class="animal-avatar">
                      {{ speciesIcon(animal.species) }}
                    </div>

                    <div class="animal-info">
                      <span class="animal-name">{{ animal.name }}</span>

                      <span class="animal-age text-gray">
                        {{ animal.age }}
                      </span>
                    </div>

                    <button
                      class="remove-btn"
                      @click.stop="removeFromKennel(animal, kennel)"
                      title="Remove from kennel"
                    >
                      ✕
                    </button>
                  </div>

                </div>
              </transition>

            </div>

          </div>
        </div>
      </div>

      <div class="legend-card">
        <span class="legend-title">Occupancy Status</span>

        <div class="legend-items">
          <div class="legend-item">
            <span class="legend-dot low"></span>
            Low (&lt;70%)
          </div>

          <div class="legend-item">
            <span class="legend-dot medium"></span>
            Medium (70–90%)
          </div>

          <div class="legend-item">
            <span class="legend-dot high"></span>
            High (≥90%)
          </div>
        </div>
      </div>
    </div>

    <div class="side-panel">

      <div class="panel-section">

        <div class="panel-section-header">
          <h3 class="panel-title">Available Animals</h3>

          <p class="panel-subtitle">
            Drag to assign to a kennel
          </p>
        </div>

        <div
          v-if="unassignedAnimals.length === 0"
          class="empty-kennel-msg"
        >
          All animals are assigned.
        </div>

        <div
          v-for="animal in unassignedAnimals"
          :key="animal.id"
          class="animal-card draggable"
          draggable="true"
          @dragstart="onDragStart($event, animal)"
        >
          <div class="animal-avatar">
            {{ speciesIcon(animal.species) }}
          </div>

          <div class="animal-info">
            <span class="animal-name">{{ animal.name }}</span>

            <span class="animal-age text-gray">
              {{ animal.age }}
            </span>
          </div>

          <span
            :class="[
              'status-badge',
              getStatusClass(animal.adoptionStatus)
            ]"
          >
            {{ animal.adoptionStatus }}
          </span>
        </div>
      </div>

      <div class="stats-card">

        <div class="stat-row">
          <span class="stat-label">Total Capacity</span>
          <span class="stat-value">{{ totalCapacity }}</span>
        </div>

        <div class="stat-row">
          <span class="stat-label">Current Occupancy</span>
          <span class="stat-value">{{ totalOccupancy }}</span>
        </div>

        <div class="stat-row">
          <span class="stat-label">Available Spaces</span>

          <span class="stat-value available-spaces">
            {{ totalCapacity - totalOccupancy }}
          </span>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const searchQuery = ref('')
const selectedKennel = ref(null)
const draggedAnimal = ref(null)

const sectors = ref([])
const allAnimals = ref([])

// AUTH

const authHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem('jwt_token')}`
})

// FETCH

const fetchData = async () => {
  try {
    const [kennelsRes, animalsRes] = await Promise.all([
      axios.get(
        'http://localhost:8080/api/kennels',
        { headers: authHeaders() }
      ),

      axios.get(
        'http://localhost:8080/api/animals',
        { headers: authHeaders() }
      )
    ])

    // Filtrujemy zwierzęta z "Completed" zanim trafią do widoku mapy
    allAnimals.value = animalsRes.data
      .filter(a => a.adoptionStatus !== 'Completed')
      .map(a => ({
        id: a.id,
        name: a.name,
        species: a.species,

        age: a.age
          ? `${a.age} ${a.age === 1 ? 'year' : 'years'}`
          : '',

        adoptionStatus: a.adoptionStatus,
        kennelId: a.kennel?.id ?? null
      }))

    const sectorMap = {}

    kennelsRes.data.forEach(k => {
      const key = k.sectorName || 'Other'

      if (!sectorMap[key]) {
        sectorMap[key] = []
      }

      sectorMap[key].push({
        id: k.id,
        code: k.code,
        capacity: k.capacity,
        type: k.type,
        animals: allAnimals.value.filter(a => a.kennelId === k.id)
      })
    })

    sectors.value = Object.entries(sectorMap).map(
      ([name, kennels], i) => ({
        id: i,
        name,
        kennels,
        color: sectorColor(name),
        icon: sectorIcon(name)
      })
    )

  } catch (err) {
    console.error('Error fetching floor plan data:', err)
  }
}

// HELPERS

const speciesIcon = (species) => {
  if (species === 'Cat') return '🐱'
  if (species === 'Dog') return '🐕'
  return '🐾'
}

const sectorColor = (name) => {
  const n = name.toLowerCase()

  if (n.includes('dog')) return '#3B82F6'
  if (n.includes('cat')) return '#8B5CF6'
  if (n.includes('quarantine')) return '#F97316'
  if (n.includes('isolation')) return '#EF4444'

  return '#6B7280'
}

const sectorIcon = (name) => {
  const n = name.toLowerCase()

  if (n.includes('dog')) return '🐕'
  if (n.includes('cat')) return '🐱'
  if (n.includes('quarantine')) return '⚠️'
  if (n.includes('isolation')) return '🏥'

  return '🐾'
}

const occupancyPercent = (kennel) =>
  kennel.capacity
    ? Math.round((kennel.animals.length / kennel.capacity) * 100)
    : 0

const getKennelClass = (kennel) => {
  const pct = occupancyPercent(kennel)

  if (pct >= 100) return 'kennel-full'
  if (pct >= 90) return 'kennel-high'
  if (pct >= 70) return 'kennel-medium'

  return 'kennel-low'
}

const getBarClass = (kennel) => {
  const pct = occupancyPercent(kennel)

  if (pct >= 90) return 'bar-red'
  if (pct >= 70) return 'bar-yellow'

  return 'bar-green'
}

const getPercentClass = (kennel) => {
  const pct = occupancyPercent(kennel)

  if (pct >= 90) return 'pct-red'
  if (pct >= 70) return 'pct-yellow'

  return 'pct-green'
}

const getStatusClass = (status) => {
  const map = {
    Available: 'status-available',
    Pending: 'status-pending',
    Completed: 'status-completed'
  }

  return map[status] ?? ''
}

// COMPUTED

const unassignedAnimals = computed(() =>
  allAnimals.value.filter(a => !a.kennelId)
)

const filteredSectors = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()

  if (!q) return sectors.value

  return sectors.value
    .map(s => ({
      ...s,
      kennels: s.kennels.filter(k =>
        k.code.toLowerCase().includes(q)
      )
    }))
    .filter(s => s.kennels.length > 0)
})

const allKennels = computed(() =>
  sectors.value.flatMap(s => s.kennels)
)

const totalCapacity = computed(() =>
  allKennels.value.reduce(
    (sum, k) => sum + (k.capacity || 0),
    0
  )
)

const totalOccupancy = computed(() =>
  allKennels.value.reduce(
    (sum, k) => sum + k.animals.length,
    0
  )
)

// DRAG & DROP

const onDragStart = (event, animal) => {
  draggedAnimal.value = animal
  event.dataTransfer.effectAllowed = 'move'
}

const onDrop = async (event, kennel) => {
  event.preventDefault()

  const animal = draggedAnimal.value
  draggedAnimal.value = null

  if (!animal) return

  if (kennel.animals.length >= kennel.capacity) {
    alert(`Kennel ${kennel.code} is full!`)
    return
  }

  try {

    await axios.patch(
      `http://localhost:8080/api/animals/${animal.id}/kennel`,
      { kennelId: kennel.id },
      { headers: authHeaders() }
    )

    animal.kennelId = kennel.id
    kennel.animals.push(animal)

  } catch (err) {

    const msg =
      err.response?.data?.message ??
      `Cannot place ${animal.species} in kennel ${kennel.code}.`

    alert(msg)
  }
}

const removeFromKennel = async (animal, kennel) => {
  try {

    await axios.patch(
      `http://localhost:8080/api/animals/${animal.id}/kennel`,
      { kennelId: null },
      { headers: authHeaders() }
    )

    kennel.animals.splice(
      kennel.animals.findIndex(a => a.id === animal.id),
      1
    )

    const global = allAnimals.value.find(
      a => a.id === animal.id
    )

    if (global) {
      global.kennelId = null
    }

    if (
      kennel.animals.length === 0 &&
      selectedKennel.value?.id === kennel.id
    ) {
      selectedKennel.value = null
    }

  } catch (err) {

    alert(
      err.response?.data?.message ??
      'Failed to remove animal from kennel.'
    )
  }
}

// OPEN / CLOSE

const openKennelDetail = (kennel) => {
  selectedKennel.value =
    selectedKennel.value?.id === kennel.id
      ? null
      : kennel
}

onMounted(fetchData)
</script>

<style scoped>
.floorplan-container {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
}

.floorplan-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

/* SEARCH */

.search-bar-row {
  display: flex;
}

.search-wrapper {
  position: relative;
  width: 280px;
}

.search-icon {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.85rem;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 0.55rem 0.75rem 0.55rem 2.25rem;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  font-size: 0.9rem;
  outline: none;
  background: #fff;
  box-sizing: border-box;
  color: #374151;
}

.search-input:focus {
  border-color: #D41B65;
}

.search-input::placeholder {
  color: #9CA3AF;
}

/* SECTORS */

.sectors-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

.sector-card {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

.sector-header {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  margin-bottom: 1rem;
}

.sector-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.sector-title {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: #111827;
}

/* KENNELS */

.kennels-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.kennel-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.kennel-card {
  position: relative;
  border-radius: 8px;
  padding: 0.85rem 1rem;
  cursor: pointer;
  transition: box-shadow 0.15s, transform 0.15s;
  border: 1px solid transparent;
}

.kennel-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.kennel-low {
  background: #F0FDF4;
  border-color: #BBF7D0;
}

.kennel-medium {
  background: #FEFCE8;
  border-color: #FDE68A;
}

.kennel-high,
.kennel-full {
  background: #FFF1F2;
  border-color: #FECDD3;
}

.kennel-full-badge {
  position: absolute;
  top: -8px;
  right: 8px;
  background: #EF4444;
  color: #fff;
  font-size: 0.7rem;
  font-weight: 700;
  padding: 0.15rem 0.5rem;
  border-radius: 10px;
}

.kennel-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.kennel-code {
  font-weight: 600;
  font-size: 0.95rem;
  color: #1F2937;
}

.kennel-species-icon {
  font-size: 1rem;
  opacity: 0.6;
}

.kennel-occupancy-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.4rem;
}

.occ-label {
  font-size: 0.8rem;
  color: #6B7280;
}

.occ-percent {
  font-size: 0.85rem;
  font-weight: 700;
}

.pct-green {
  color: #16A34A;
}

.pct-yellow {
  color: #D97706;
}

.pct-red {
  color: #DC2626;
}

.kennel-bar-bg {
  height: 5px;
  background: #E5E7EB;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 0.45rem;
}

.kennel-bar-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.4s ease;
}

.bar-green {
  background: #22C55E;
}

.bar-yellow {
  background: #EAB308;
}

.bar-red {
  background: #EF4444;
}

.kennel-count {
  font-size: 0.8rem;
  color: #6B7280;
}

/* EXPANDED */

.kennel-expanded {
  border: 1px solid #F3F4F6;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
  animation: fadeIn 0.2s ease;
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.2s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 500px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-4px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* LEGEND */

.legend-card {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  padding: 1rem 1.25rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

.legend-title {
  font-size: 0.85rem;
  font-weight: 600;
  color: #374151;
}

.legend-items {
  display: flex;
  gap: 1.25rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.82rem;
  color: #6B7280;
}

.legend-dot {
  width: 14px;
  height: 14px;
  border-radius: 3px;
  flex-shrink: 0;
}

.legend-dot.low {
  background: #F0FDF4;
  border: 1px solid #BBF7D0;
}

.legend-dot.medium {
  background: #FEFCE8;
  border: 1px solid #FDE68A;
}

.legend-dot.high {
  background: #FFF1F2;
  border: 1px solid #FECDD3;
}

/* SIDE PANEL */

.side-panel {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.panel-section {
  background: #fff;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

.panel-section-header {
  padding: 1rem 1.25rem 0.75rem;
  border-bottom: 1px solid #F3F4F6;
}

.panel-title {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #111827;
}

.panel-subtitle {
  margin: 0.2rem 0 0;
  font-size: 0.8rem;
  color: #9CA3AF;
}

/* ANIMALS */

.animal-card,
.animal-card-in-kennel {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.25rem;
  border-bottom: 1px solid #F3F4F6;
  transition: background 0.15s;
}

.animal-card:last-child,
.animal-card-in-kennel:last-child {
  border-bottom: none;
}

.animal-card:hover {
  background: #F9FAFB;
}

.animal-card.draggable {
  cursor: grab;
}

.animal-card.draggable:active {
  cursor: grabbing;
  opacity: 0.7;
}

.animal-avatar {
  width: 36px;
  height: 36px;
  background: #FEE2E2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  flex-shrink: 0;
}

.animal-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.1rem;
  min-width: 0;
}

.animal-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.animal-age {
  font-size: 0.78rem;
}

.text-gray {
  color: #6B7280;
}

.remove-btn {
  background: none;
  border: none;
  color: #9CA3AF;
  font-size: 0.75rem;
  cursor: pointer;
  padding: 0.3rem 0.5rem;
  border-radius: 4px;
  flex-shrink: 0;
  transition: background 0.15s, color 0.15s;
}

.remove-btn:hover {
  background: #FEE2E2;
  color: #DC2626;
}

.empty-kennel-msg {
  padding: 1.5rem 1.25rem;
  font-size: 0.85rem;
  color: #9CA3AF;
  text-align: center;
}

/* STATUS */

.status-badge {
  padding: 0.25rem 0.6rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
  flex-shrink: 0;
}

.status-available {
  background: #D1FAE5;
  color: #065F46;
}

.status-pending {
  background: #FEF3C7;
  color: #92400E;
}

.status-completed {
  background: #DBEAFE;
  color: #1E40AF;
}

/* STATS */

.stats-card {
  background: #FFF0F6;
  border: 1px solid #FBCFE8;
  border-radius: 12px;
  padding: 1rem 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.65rem;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 0.85rem;
  color: #6B7280;
}

.stat-value {
  font-size: 0.95rem;
  font-weight: 700;
  color: #111827;
}

.available-spaces {
  color: #D41B65;
}
</style>