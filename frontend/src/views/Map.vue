<template>
  <div class="map-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-box">
          <svg viewBox="0 0 24 24" fill="currentColor" class="heart-icon"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
        </div>
        <h2 class="text-primary">Rescue Pawtrol</h2>
      </div>

      <div class="mode-toggle-container">
        <div class="mode-toggle">
          <button :class="{ active: appMode === 'DISPATCHER' }" @click="setMode('DISPATCHER')">🎧 Dispatcher</button>
          <button :class="{ active: appMode === 'DRIVER' }" @click="setMode('DRIVER')">🚓 Driver</button>
        </div>
      </div>

      <div class="reports-list">
        <div class="instruction-box" :class="appMode.toLowerCase()">
          <p v-if="appMode === 'DISPATCHER'">
            🎧 <strong>Dispatcher:</strong> Click anywhere on the map to dispatch a unit.
          </p>
          <p v-else>
            🚓 <strong>Driver:</strong> Live GPS tracking is active. Mark incidents as completed.
          </p>
        </div>

        <div v-for="report in interventions" :key="report.id" class="report-card" @click="focusMap(report.latitude, report.longitude)">
          <div class="card-header">
            <h3>Intervention #{{ report.id }}</h3>
            <span class="priority-badge">{{ report.status || 'NEW' }}</span>
          </div>
          <p class="location-text">📍 Lat: {{ report.latitude.toFixed(4) }}, Lng: {{ report.longitude.toFixed(4) }}</p>

          <div class="card-footer">
            <span>Animals Affected: <strong>{{ report.animalsCount || 1 }}</strong></span>

            <button v-if="appMode === 'DRIVER'" @click.stop="completeIntervention(report.id)" class="complete-btn">
              ✓ Complete
            </button>
          </div>
        </div>

        <div v-if="interventions.length === 0" class="empty-state">
          No active interventions. Great job! 🐾
        </div>
      </div>
    </aside>

    <main class="map-container">
      <div id="leaflet-map"></div>

      <div class="routing-controls">
        <button v-if="!isRouteGenerated" @click="generateRoute(true)" class="route-btn generate">
          🗺️ Generate Route
        </button>
        <button v-else @click="clearRoute" class="route-btn clear">
          ❌ Clear Route
        </button>
      </div>
    </main>

    <div v-if="showAddModal" class="modal-overlay">
      <div class="modal-card">
        <h3>🚨 Dispatch Unit</h3>
        <p class="coords-info">Coordinates: {{ newReportData.latitude.toFixed(5) }}, {{ newReportData.longitude.toFixed(5) }}</p>
        <p style="margin-bottom: 2rem; color: #4B5563; font-size: 0.95rem;">
          Do you want to create a new intervention report at this location?
        </p>
        <div class="modal-actions">
          <button class="cancel-btn" @click="cancelNewReport">Cancel</button>
          <button class="primary-btn" @click="submitNewReport">Confirm & Dispatch</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const appMode = ref('DISPATCHER')
const map = ref(null)
const markers = ref([])
const routeLine = ref(null)
const isRouteGenerated = ref(false)
const interventions = ref([])

const showAddModal = ref(false)
let tempMarker = null
const newReportData = ref({ latitude: 0, longitude: 0 })

const driverLocation = ref({ lat: 51.105000, lng: 17.035000 })
let driverMarker = null

let trackingInterval = null
let animationInterval = null
const routeCoordsArray = ref([])

// Kluczowa technika: className: '' usuwa wszystkie domyślne klasy Leafleta
// (leaflet-marker-icon, leaflet-zoom-animated itp.), które mogą interferować
// z pozycjonowaniem. Pozycjonowanie realizowane jest przez CSS transform
// bezpośrednio na elemencie SVG — jest to niezależne od iconAnchor i marginesów
// Leafleta, przez co działa identycznie dla markerów dodanych w dowolnym momencie.
const getCustomIcon = (type) => {
  const color = type === 'Base' ? '#111827' : '#EF4444';
  // transform: translate(-50%, -100%) przesuwa element tak, żeby
  // dolny środek SVG (czubek pinezki) był dokładnie na współrzędnych markera.
  const svgHtml = `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      width="36"
      height="36"
      fill="${color}"
      style="display:block; position:absolute; transform:translate(-50%, -100%); top:0; left:0; overflow:visible;"
    >
      <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
      <circle cx="12" cy="9" r="2.5" fill="white"/>
    </svg>`;
  return L.divIcon({
    className: '',
    html: svgHtml,
    iconSize: [0, 0],
    iconAnchor: [0, 0],
    popupAnchor: [0, -36]
  });
}

const getDriverIcon = () => {
  // Ta sama technika dla ikony kierowcy: transform centruje element względem punktu markera
  return L.divIcon({
    className: '',
    html: `<div style="font-size:32px; background:white; border-radius:50%; width:44px; height:44px; display:flex; align-items:center; justify-content:center; box-shadow:0 4px 10px rgba(0,0,0,0.3); border:2px solid #D41B65; position:absolute; transform:translate(-50%,-50%); top:0; left:0;">🚓</div>`,
    iconSize: [0, 0],
    iconAnchor: [0, 0]
  })
}

const shelterLocation = { lat: 51.110000, lng: 17.030000 }

const initMap = () => {
  map.value = L.map('leaflet-map', { zoomControl: false }).setView([51.107883, 17.038538], 13)

  L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; OpenStreetMap contributors',
    maxZoom: 20
  }).addTo(map.value)

  L.control.zoom({ position: 'topright' }).addTo(map.value)

  L.marker([shelterLocation.lat, shelterLocation.lng], { icon: getCustomIcon('Base') })
      .bindPopup('<b>🏠 Shelter Base</b>')
      .addTo(map.value)

  driverMarker = L.marker([driverLocation.value.lat, driverLocation.value.lng], {
    icon: getDriverIcon(),
    draggable: false,
    zIndexOffset: 1000
  }).addTo(map.value)

  driverMarker.bindPopup('<b>🚓 Driver Unit 1</b><br>Live GPS Tracking Active')

  map.value.on('click', handleMapClick)
}

const startLiveTracking = () => {
  if (trackingInterval) return;

  trackingInterval = setInterval(async () => {
    try {
      const token = localStorage.getItem('jwt_token');
      const config = { headers: { Authorization: `Bearer ${token}` } };
      const response = await axios.get('http://localhost:8080/api/tracking/unit1', config);

      driverLocation.value.lat = response.data.latitude;
      driverLocation.value.lng = response.data.longitude;

      if (driverMarker) {
        driverMarker.setLatLng([driverLocation.value.lat, driverLocation.value.lng]);
      }
    } catch (error) {
      console.error("Błąd pobierania pozycji API:", error);
    }
  }, 3000);
}

const stopLiveTracking = () => {
  if (trackingInterval) {
    clearInterval(trackingInterval);
    trackingInterval = null;
  }
}

const setMode = (mode) => {
  appMode.value = mode;
  document.querySelector('.map-container').style.cursor = mode === 'DISPATCHER' ? 'crosshair' : 'default';
}

const handleMapClick = (e) => {
  if (appMode.value === 'DRIVER') return;

  newReportData.value.latitude = e.latlng.lat
  newReportData.value.longitude = e.latlng.lng

  if (tempMarker) map.value.removeLayer(tempMarker)

  tempMarker = L.marker([e.latlng.lat, e.latlng.lng], {
    icon: getCustomIcon('Intervention'),
    opacity: 0.5
  }).addTo(map.value)

  showAddModal.value = true
}

const cancelNewReport = () => {
  showAddModal.value = false
  if (tempMarker) map.value.removeLayer(tempMarker)
}

const submitNewReport = async () => {
  try {
    const payload = {
      latitude: newReportData.value.latitude,
      longitude: newReportData.value.longitude
    };

    const token = localStorage.getItem('jwt_token');
    const config = { headers: { Authorization: `Bearer ${token}` } };

    const response = await axios.post('http://localhost:8080/api/interventions', payload, config);
    const savedReport = response.data;

    interventions.value.unshift(savedReport);

    if (tempMarker) map.value.removeLayer(tempMarker);
    const marker = L.marker([savedReport.latitude, savedReport.longitude], { icon: getCustomIcon('Intervention') })
        .bindPopup(`<b>Intervention #${savedReport.id}</b>`)
        .addTo(map.value);

    markers.value.push({ id: savedReport.id, marker: marker });

    if (isRouteGenerated.value) generateRoute(true);
    showAddModal.value = false;

  } catch (error) {
    console.error("Error saving intervention:", error);
    alert("Błąd połączenia z serwerem. Zgłoszenie odrzucone (403).");
  }
}

const fetchInterventions = async () => {
  try {
    const token = localStorage.getItem('jwt_token');
    const config = { headers: { Authorization: `Bearer ${token}` } };

    const response = await axios.get('http://localhost:8080/api/interventions', config)
    interventions.value = response.data

    interventions.value.forEach(report => {
      if(report.latitude && report.longitude) {
        const marker = L.marker([report.latitude, report.longitude], { icon: getCustomIcon('Intervention') })
            .bindPopup(`<b>Intervention #${report.id}</b>`)
            .addTo(map.value)
        markers.value.push({ id: report.id, marker: marker })
      }
    })
  } catch (error) {
    console.error("Failed to fetch interventions", error)
  }
}

const focusMap = (lat, lng) => {
  if (lat && lng) map.value.flyTo([lat, lng], 16, { duration: 1.5 })
}

const generateRoute = async (fitBounds = true) => {
  if (routeLine.value) map.value.removeLayer(routeLine.value);

  stopLiveTracking();
  if (animationInterval) clearInterval(animationInterval);

  const allPoints = [
    [driverLocation.value.lat, driverLocation.value.lng],
    ...interventions.value.filter(inv => inv.latitude && inv.longitude).map(inv => [inv.latitude, inv.longitude])
  ];

  if (allPoints.length > 1) {
    try {
      const coordsString = allPoints.map(p => `${p[1]},${p[0]}`).join(';');
      const url = `https://router.project-osrm.org/trip/v1/driving/${coordsString}?source=first&roundtrip=false&overview=full&geometries=geojson`;

      const response = await fetch(url);
      const data = await response.json();

      if (data && data.trips && data.trips.length > 0) {
        const osrmCoords = data.trips[0].geometry.coordinates.map(coord => [coord[1], coord[0]]);

        routeLine.value = L.polyline(osrmCoords, { color: '#D41B65', weight: 4, opacity: 0.8 }).addTo(map.value);

        if (fitBounds) map.value.fitBounds(routeLine.value.getBounds(), { padding: [50, 50] });

        isRouteGenerated.value = true;
        routeCoordsArray.value = osrmCoords;
        animateCarAlongRoute();
      }
    } catch (error) {
      console.warn("Błąd serwera drogowego OSRM. Rysuję trasę awaryjną w linii prostej!", error);

      routeLine.value = L.polyline(allPoints, { color: '#D41B65', weight: 4, dashArray: '10, 10' }).addTo(map.value);
      if (fitBounds) map.value.fitBounds(routeLine.value.getBounds(), { padding: [50, 50] });

      isRouteGenerated.value = true;
      routeCoordsArray.value = allPoints;
      animateCarAlongRoute();
    }
  } else {
    startLiveTracking();
  }
};

const animateCarAlongRoute = () => {
  if (routeCoordsArray.value.length < 2) return;

  let currentTargetIndex = 1;
  const SPEED = 0.000015;

  animationInterval = setInterval(() => {
    const targetLat = routeCoordsArray.value[currentTargetIndex][0];
    const targetLng = routeCoordsArray.value[currentTargetIndex][1];

    const dLat = targetLat - driverLocation.value.lat;
    const dLng = targetLng - driverLocation.value.lng;
    const distance = Math.sqrt(dLat * dLat + dLng * dLng);

    if (distance < SPEED) {
      driverLocation.value.lat = targetLat;
      driverLocation.value.lng = targetLng;
      currentTargetIndex++;

      if (currentTargetIndex >= routeCoordsArray.value.length) {
        clearInterval(animationInterval);
        clearRoute();
        return;
      }
    } else {
      const ratio = SPEED / distance;
      driverLocation.value.lat += dLat * ratio;
      driverLocation.value.lng += dLng * ratio;
    }

    if (driverMarker) {
      driverMarker.setLatLng([driverLocation.value.lat, driverLocation.value.lng]);
    }
  }, 50);
};

const clearRoute = () => {
  if (routeLine.value) map.value.removeLayer(routeLine.value)
  if (animationInterval) {
    clearInterval(animationInterval)
    animationInterval = null;
  }
  isRouteGenerated.value = false

  startLiveTracking();
}

onMounted(() => {
  initMap()
  fetchInterventions()
  startLiveTracking()
})

onBeforeUnmount(() => {
  if (animationInterval) clearInterval(animationInterval)
  if (trackingInterval) clearInterval(trackingInterval)
})

const completeIntervention = async (id) => {
  try {
    const token = localStorage.getItem('jwt_token');
    const config = { headers: { Authorization: `Bearer ${token}` } };
    await axios.delete(`http://localhost:8080/api/interventions/${id}`, config);
    interventions.value = interventions.value.filter(i => i.id !== id);
    const markerObj = markers.value.find(m => m.id === id);
    if (markerObj) {
      map.value.removeLayer(markerObj.marker);
      markers.value = markers.value.filter(m => m.id !== id);
    }
    if (isRouteGenerated.value) {
      generateRoute(false);
    }
  } catch (error) {
    console.error("Error completing intervention:", error);
    alert("Błąd podczas usuwania interwencji.");
  }
};
</script>

<style scoped>
.map-layout { display: flex; height: calc(100vh - 75px); margin: -2rem; background-color: #F8F9FB; }
.sidebar { width: 380px; background-color: white; border-right: 1px solid #E5E7EB; display: flex; flex-direction: column; box-shadow: 2px 0 10px rgba(0,0,0,0.02); z-index: 10; }
.sidebar-header { padding: 1.5rem; display: flex; align-items: center; gap: 12px; border-bottom: 1px solid #E5E7EB; }
.logo-box { background-color: #D41B65; width: 40px; height: 40px; border-radius: 10px; display: flex; justify-content: center; align-items: center; }
.heart-icon { width: 20px; height: 20px; color: white; }
.text-primary { margin: 0; color: #D41B65; font-size: 1.25rem; font-weight: 600; }

.mode-toggle-container { padding: 1.5rem 1.5rem 0 1.5rem; }
.mode-toggle { display: flex; background-color: #F3F4F6; border-radius: 8px; padding: 4px; }
.mode-toggle button { flex: 1; padding: 0.5rem; border: none; background: transparent; border-radius: 6px; font-weight: 600; font-size: 0.9rem; color: #6B7280; cursor: pointer; transition: all 0.2s; }
.mode-toggle button.active { background-color: white; color: #111827; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }

.reports-list { padding: 1.5rem; overflow-y: auto; display: flex; flex-direction: column; gap: 1rem; }
.instruction-box { padding: 1rem; border-radius: 8px; font-size: 0.85rem; line-height: 1.4; transition: all 0.3s; }
.instruction-box.dispatcher { background-color: #EFF6FF; border: 1px solid #BFDBFE; color: #1E3A8A; }
.instruction-box.driver { background-color: #F0FDF4; border: 1px solid #BBF7D0; color: #166534; }

.report-card { background-color: white; border: 1px solid #E5E7EB; border-radius: 12px; padding: 1.25rem; cursor: pointer; transition: all 0.2s ease; }
.report-card:hover { border-color: #D41B65; box-shadow: 0 4px 12px rgba(212, 27, 101, 0.1); }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
.card-header h3 { margin: 0; font-size: 1rem; color: #111827; }
.priority-badge { padding: 0.25rem 0.75rem; border-radius: 20px; font-size: 0.75rem; font-weight: 600; background-color: #FEE2E2; color: #EF4444; }
.location-text { margin: 0 0 1rem 0; font-size: 0.85rem; color: #6B7280; }
.card-footer { display: flex; justify-content: space-between; align-items: center; font-size: 0.85rem; color: #9CA3AF; border-top: 1px dashed #E5E7EB; padding-top: 0.75rem; }

.complete-btn { background-color: #10B981; color: white; border: none; padding: 0.4rem 0.8rem; border-radius: 6px; font-weight: 600; cursor: pointer; transition: 0.2s; }
.complete-btn:hover { background-color: #059669; transform: scale(1.05); }

.empty-state { text-align: center; color: #9CA3AF; font-size: 0.9rem; padding: 2rem 0; }
.map-container { flex-grow: 1; position: relative; cursor: crosshair; }
#leaflet-map { width: 100%; height: 100%; z-index: 1; }

.routing-controls { position: absolute; bottom: 2rem; left: 2rem; z-index: 1000; }
.route-btn { padding: 0.75rem 1.5rem; border-radius: 8px; font-weight: 600; font-size: 0.95rem; cursor: pointer; border: none; box-shadow: 0 4px 15px rgba(0,0,0,0.15); transition: all 0.2s; display: flex; align-items: center; gap: 8px; }
.route-btn.generate { background-color: #111827; color: white; }
.route-btn.generate:hover { background-color: #374151; transform: scale(1.02); }
.route-btn.clear { background-color: white; color: #EF4444; border: 1px solid #EF4444; }

/* Ikony markerów używają className: '' — brak dodatkowych override CSS potrzebnych */

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 2000; }
.modal-card { background-color: white; padding: 2rem; border-radius: 12px; width: 400px; box-shadow: 0 10px 25px rgba(0,0,0,0.1); }
.modal-card h3 { margin: 0 0 0.5rem 0; font-size: 1.25rem; color: #111827; }
.coords-info { margin: 0 0 1.5rem 0; font-size: 0.8rem; color: #6B7280; font-family: monospace; }
.modal-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1rem; }
.primary-btn { background-color: #D41B65; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 8px; font-weight: 500; cursor: pointer; }
.cancel-btn { background-color: transparent; color: #6B7280; border: none; font-weight: 500; cursor: pointer; padding: 0.6rem 1.2rem; }
</style>