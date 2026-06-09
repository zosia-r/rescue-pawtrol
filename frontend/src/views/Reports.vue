<template>
  <div class="reports-layout">
    <main class="dashboard-container" ref="reportArea">

      <section class="filter-bar">
        <div class="date-filters">
          <span class="filter-label">📅 Date Range:</span>
          <div class="date-input-group">
            <label>Start Date</label>
            <input type="date" v-model="startDate" @change="fetchData" />
          </div>
          <span class="separator">—</span>
          <div class="date-input-group">
            <label>End Date</label>
            <input type="date" v-model="endDate" @change="fetchData" />
          </div>
        </div>

        <div class="export-dropdown" @mouseleave="showExportMenu = false">
          <button class="export-btn" @click="showExportMenu = !showExportMenu" :disabled="isExporting">
            {{ isExporting ? '⏳ Generating...' : '📥 Export Report ▼' }}
          </button>

          <div v-if="showExportMenu && !isExporting" class="dropdown-menu">
            <button @click="exportReport('pdf')">📄 Export as PDF</button>
            <button @click="exportReport('png')">🖼️ Export as PNG</button>
          </div>
        </div>
      </section>

      <section class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-header">
            <span>Total Animals</span>
            <div class="icon-wrapper blue">🐾</div>
          </div>
          <h2>{{ kpiData.totalAnimals }}</h2>
          <p class="trend positive">{{ kpiData.trendAnimals }}% from last period</p>
        </div>

        <div class="kpi-card">
          <div class="kpi-header">
            <span>Total Adoptions</span>
            <div class="icon-wrapper pink">📈</div>
          </div>
          <h2>{{ kpiData.totalAdoptions }}</h2>
          <p class="trend positive">{{ kpiData.trendAdoptions }}% from last period</p>
        </div>

        <div class="kpi-card">
          <div class="kpi-header">
            <span>Medical Interventions</span>
            <div class="icon-wrapper orange">💉</div>
          </div>
          <h2>{{ kpiData.interventions }}</h2>
          <p class="trend positive">{{ kpiData.trendInterventions }}% from last period</p>
        </div>

        <div class="kpi-card">
          <div class="kpi-header">
            <span>Avg. Stay Duration</span>
            <div class="icon-wrapper green">⏱️</div>
          </div>
          <h2>{{ kpiData.avgStay }}</h2>
          <p class="trend neutral">days</p>
        </div>
      </section>

      <section class="charts-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3>Pie Chart Distribution</h3>
            <p>Current shelter population by species</p>
          </div>
          <div class="chart-wrapper doughnut-wrapper">
            <Doughnut v-if="chartReady" :data="speciesData" :options="doughnutOptions" />
          </div>
        </div>

        <div class="chart-card span-2">
          <div class="chart-header">
            <h3>Adoption & Intervention Trends</h3>
            <p>Monthly adoption and medical intervention volume</p>
          </div>
          <div class="chart-wrapper line-wrapper">
            <Line v-if="chartReady" :data="trendData" :options="lineOptions" />
          </div>
        </div>
      </section>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement, LineElement, ArcElement, Filler } from 'chart.js'
import { Line, Doughnut } from 'vue-chartjs'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, ArcElement, Title, Tooltip, Legend, Filler)

const reportArea = ref(null)
const isExporting = ref(false)
const chartReady = ref(false)
const showExportMenu = ref(false)

// Domyślny zakres dat ustawiany na starcie
const startDate = ref('2025-10-01')
const endDate = ref('2026-04-30')

// Struktury danych przygotowane na przyjęcie obiektów z Twojego DTO z backendu
const kpiData = ref({
  totalAnimals: 0, trendAnimals: '0',
  totalAdoptions: 0, trendAdoptions: '0',
  interventions: 0, trendInterventions: '0',
  avgStay: 0
})

const speciesData = ref({
  labels: ['Dogs', 'Cats', 'Rabbits', 'Birds'],
  datasets: [{
    data: [0, 0, 0, 0], // Domyślnie puste, bazy danych uzupełnią te pola
    backgroundColor: ['#3b82f6', '#10b981', '#f59e0b', '#8b5cf6'],
    borderWidth: 0,
    hoverOffset: 4
  }]
})

const trendData = ref({
  labels: ['Nov', 'Dec', 'Jan', 'Feb', 'Mar', 'Apr'],
  datasets: [
    {
      label: 'Adoptions',
      borderColor: '#D41B65',
      backgroundColor: 'rgba(212, 27, 101, 0.1)',
      data: [0, 0, 0, 0, 0, 0],
      fill: true,
      tension: 0.4
    },
    {
      label: 'Medical Interventions',
      borderColor: '#f59e0b',
      backgroundColor: 'rgba(245, 158, 11, 0.1)',
      data: [0, 0, 0, 0, 0, 0],
      fill: true,
      tension: 0.4
    }
  ]
})

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'bottom' } }
}

const lineOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'top' } },
  scales: {
    y: { beginAtZero: true, grid: { borderDash: [5, 5] } },
    x: { grid: { display: false } }
  }
}

// 🚨 PRAWDZIWA FUNKCJA: Pobiera dane z bazy za pomocą stworzonego API w Spring Boocie
const fetchData = async () => {
  console.log(`Pobieranie prawdziwych danych dla zakresu: ${startDate.value} do ${endDate.value}`);

  try {
    const token = localStorage.getItem('jwt_token');
    const config = { headers: { Authorization: `Bearer ${token}` } };

    // Strzał do Twojego nowego endpointu z parametrami dat
    const response = await axios.get(`http://localhost:8080/api/reports?start=${startDate.value}&end=${endDate.value}`, config);

    // Mapowanie odebranego JSON-a (zgodnego z ReportResponseDTO) na reaktywne zmienne we Vue
    kpiData.value = response.data.kpi;
    speciesData.value.datasets[0].data = response.data.speciesDistribution;
    trendData.value.datasets[0].data = response.data.adoptionsArray;
    trendData.value.datasets[1].data = response.data.interventionsArray;

  } catch (error) {
    console.error("Błąd podczas pobierania danych raportowych z bazy:", error);
  }

  // Odświeżenie widoku wykresów po wstrzyknięciu nowych danych
  chartReady.value = false;
  setTimeout(() => { chartReady.value = true; }, 50);
}

const exportReport = async (format) => {
  if (!reportArea.value) return;

  showExportMenu.value = false;

  try {
    isExporting.value = true;

    const canvas = await html2canvas(reportArea.value, {
      scale: 2,
      backgroundColor: '#F8F9FB'
    });

    const imgData = canvas.toDataURL('image/png');

    if (format === 'pdf') {
      const pdf = new jsPDF('l', 'mm', 'a4');
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width;

      pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
      pdf.save(`Rescue_Pawtrol_Report_${startDate.value}.pdf`);

    } else if (format === 'png') {
      const link = document.createElement('a');
      link.href = imgData;
      link.download = `Rescue_Pawtrol_Report_${startDate.value}.png`;
      link.click();
    }

  } catch (error) {
    console.error("Błąd podczas generowania raportu:", error);
    alert("Wystąpił problem z eksportem raportu.");
  } finally {
    isExporting.value = false;
  }
}

// 🚨 ZMIANA: Wywołujemy pobieranie danych z bazy OD RAZU przy wejściu na stronę
onMounted(() => {
  fetchData();
})
</script>

<style scoped>
.reports-layout {
  min-height: calc(100vh - 80px);
  background-color: #F8F9FB;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #374151;
}

.dashboard-container {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  margin-bottom: 2rem;
}
.date-filters { display: flex; align-items: center; gap: 1.5rem; }
.filter-label { font-weight: 600; color: #4B5563; display: flex; align-items: center; gap: 8px; }
.date-input-group { display: flex; flex-direction: column; }
.date-input-group label { font-size: 0.75rem; color: #9CA3AF; margin-bottom: 4px; }
.date-input-group input { padding: 0.5rem; border: 1px solid #E5E7EB; border-radius: 6px; outline: none; color: #374151; font-family: inherit; }
.separator { color: #9CA3AF; }

.export-dropdown {
  position: relative;
  display: inline-block;
}

.export-btn {
  background-color: #D41B65; color: white; border: none; padding: 0.75rem 1.5rem;
  border-radius: 8px; font-weight: 600; cursor: pointer; transition: 0.2s;
}
.export-btn:hover { background-color: #b01553; }
.export-btn:disabled { opacity: 0.7; cursor: not-allowed; }

.dropdown-menu {
  position: absolute;
  top: 110%;
  right: 0;
  background-color: white;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 100;
  min-width: 160px;
}

.dropdown-menu button {
  padding: 0.75rem 1rem;
  border: none;
  background: transparent;
  text-align: left;
  cursor: pointer;
  font-family: inherit;
  font-size: 0.9rem;
  font-weight: 500;
  color: #374151;
  transition: background-color 0.2s, color 0.2s;
}

.dropdown-menu button:hover {
  background-color: #FDF2F8;
  color: #D41B65;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}
.kpi-card {
  background: white; padding: 1.5rem; border-radius: 12px; border: 1px solid #E5E7EB;
}
.kpi-header { display: flex; justify-content: space-between; align-items: center; color: #6B7280; font-size: 0.9rem; margin-bottom: 1rem; }
.kpi-card h2 { margin: 0 0 0.5rem 0; font-size: 2rem; color: #111827; }
.trend { margin: 0; font-size: 0.85rem; font-weight: 500; }
.trend.positive { color: #10B981; }
.trend.neutral { color: #6B7280; }

.icon-wrapper { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem; }
.icon-wrapper.blue { background-color: #EFF6FF; color: #3B82F6; }
.icon-wrapper.pink { background-color: #FDF2F8; color: #DB2777; }
.icon-wrapper.orange { background-color: #FFF7ED; color: #F59E0B; }
.icon-wrapper.green { background-color: #F0FDF4; color: #10B981; }

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1.5rem;
}
.chart-card {
  background: white; padding: 1.5rem; border-radius: 12px; border: 1px solid #E5E7EB;
  display: flex; flex-direction: column;
}
.chart-header { margin-bottom: 1.5rem; }
.chart-header h3 { margin: 0 0 0.25rem 0; font-size: 1.1rem; color: #111827; display: flex; align-items: center; gap: 8px; }
.chart-header p { margin: 0; font-size: 0.85rem; color: #6B7280; }

.chart-wrapper {
  position: relative;
  width: 100%;
  flex-grow: 1;
}
.doughnut-wrapper { min-height: 300px; }
.line-wrapper { min-height: 350px; }
</style>