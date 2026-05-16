package com.rescuepawtrol.backend.config;

import com.rescuepawtrol.backend.model.*;
import com.rescuepawtrol.backend.model.enums.*;
import com.rescuepawtrol.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(
            EmployeeRepository employeeRepository,
            KennelRepository kennelRepository,
            AnimalRepository animalRepository,
            MedicalRecordRepository medicalRecordRepository,
            AdoptionCardRepository adoptionCardRepository,
            InterventionRepository interventionRepository,
            RoutePointRepository routePointRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            Employee admin = null;
            Employee driver = null;
            if (employeeRepository.count() == 0) {
                admin = new Employee();
                admin.setFirstName("Alicja");
                admin.setEmail("admin@rescuepawtrol.pl");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(EmployeeRole.values()[0]);

                driver = new Employee();
                driver.setFirstName("John");
                driver.setEmail("driver@rescuepawtrol.pl");
                driver.setPassword(passwordEncoder.encode("driver123"));
                driver.setRole(EmployeeRole.values()[0]); // Możesz to zmienić na odpowiednią rolę kierowcy z Enuma

                employeeRepository.saveAll(List.of(admin, driver));
                System.out.println("✅ Seeded: Employees (admin@rescuepawtrol.pl & driver@rescuepawtrol.pl)");
            } else {
                // Pobieramy istniejących, aby użyć ich do powiązań poniżej
                admin = employeeRepository.findByEmail("admin@rescuepawtrol.pl").orElse(null);
                driver = employeeRepository.findByEmail("driver@rescuepawtrol.pl").orElse(null);
            }

            Kennel k1 = null, k2 = null, k3 = null, k4 = null;
            if (kennelRepository.count() == 0) {
                k1 = new Kennel(); k1.setCode("A1"); k1.setZone("Cat Ward"); k1.setMaxCapacity(2);
                k2 = new Kennel(); k2.setCode("B2"); k2.setZone("Dog Ward"); k2.setMaxCapacity(1);
                k3 = new Kennel(); k3.setCode("A3"); k3.setZone("Cat Ward"); k3.setMaxCapacity(3);
                k4 = new Kennel(); k4.setCode("B4"); k4.setZone("Dog Ward"); k4.setMaxCapacity(2);

                kennelRepository.saveAll(List.of(k1, k2, k3, k4));
                System.out.println("✅ Seeded: Kennels");
            }

            Animal a1 = null, a2 = null, a3 = null, a4 = null;
            if (animalRepository.count() == 0 && kennelRepository.count() > 0) {
                // Pobieramy klatki z bazy, by powiązać je ze zwierzętami
                List<Kennel> kennels = kennelRepository.findAll();

                a1 = new Animal();
                a1.setName("Whiskers");
                a1.setSpecies("Cat");
                a1.setAdoptionStatus("Available");
                a1.setIsQuarantined(false);
                a1.setKennel(kennels.get(0)); // A1

                a2 = new Animal();
                a2.setName("Buddy");
                a2.setSpecies("Dog");
                a2.setAdoptionStatus("Pending");
                a2.setIsQuarantined(false);
                a2.setKennel(kennels.get(1)); // B2

                a3 = new Animal();
                a3.setName("Luna");
                a3.setSpecies("Cat");
                a3.setAdoptionStatus("Available");
                a3.setIsQuarantined(true); // Na kwarantannie
                a3.setKennel(kennels.get(2)); // A3

                a4 = new Animal();
                a4.setName("Max");
                a4.setSpecies("Dog");
                a4.setAdoptionStatus("On Hold");
                a4.setIsQuarantined(false);
                a4.setKennel(kennels.get(3)); // B4

                animalRepository.saveAll(List.of(a1, a2, a3, a4));
                System.out.println("✅ Seeded: Animals");
            }

            // --- 4. MEDICAL RECORDS ---
            if (medicalRecordRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                MedicalRecord m1 = new MedicalRecord();
                m1.setRecordDate(LocalDate.now().minusDays(10));
                m1.setRecordType(MedicalRecordType.values()[0]);
                m1.setDescription("FVRCP booster administered. General health is good.");
                m1.setDoctor("Dr. Sarah Chen");
                m1.setAnimal(animals.get(0)); // Powiązanie z Whiskers

                MedicalRecord m2 = new MedicalRecord();
                m2.setRecordDate(LocalDate.now().minusDays(2));
                m2.setRecordType(MedicalRecordType.values()[0]);
                m2.setDescription("Initial health screening and deworming completed.");
                m2.setDoctor("Dr. Michael Torres");
                m2.setAnimal(animals.get(1)); // Powiązanie z Buddy

                medicalRecordRepository.saveAll(List.of(m1, m2));
                System.out.println("✅ Seeded: Medical Records");
            }

            // --- 5. ADOPTION CARDS ---
            if (adoptionCardRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                AdoptionCard card1 = new AdoptionCard();
                card1.setStatus(AdoptionStatus.values()[0]);
                card1.setCandidateDetails("Emily Johnson, 123 Maple Street, loves cats.");
                card1.setSubmissionDate(LocalDate.now().minusDays(5));
                card1.setFinalizationDate(null); // Jeszcze nie sfinalizowano
                card1.setAnimal(animals.get(1)); // Powiązanie z Buddy (który ma status "Pending")

                adoptionCardRepository.save(card1);
                System.out.println("✅ Seeded: Adoption Cards");
            }

            // --- 6. INTERVENTIONS & ROUTE POINTS ---
            if (interventionRepository.count() == 0 && driver != null) {
                Intervention inv = new Intervention();
                inv.setReportTime(LocalDateTime.now().minusHours(2));
                inv.setLatitude(51.107883);
                inv.setLongitude(17.038538);
                inv.setStatus(InterventionStatus.values()[0]);
                inv.setDriver(driver);

                interventionRepository.save(inv);

                RoutePoint rp1 = new RoutePoint();
                rp1.setStepOrder(1);
                rp1.setAddress("Shelter Base, Main St 1");
                rp1.setLatitude(51.110000);
                rp1.setLongitude(17.030000);
                rp1.setPointType(RoutePointType.values()[0]);
                rp1.setIntervention(inv);

                RoutePoint rp2 = new RoutePoint();
                rp2.setStepOrder(2);
                rp2.setAddress("City Park, Reported Stray Dog");
                rp2.setLatitude(51.107883);
                rp2.setLongitude(17.038538);
                rp2.setPointType(RoutePointType.values()[0]);
                rp2.setIntervention(inv);

                routePointRepository.saveAll(List.of(rp1, rp2));
                System.out.println("✅ Seeded: Interventions & Route Points");
            }

            System.out.println("🚀 Baza danych jest w pełni gotowa do testów!");
        };
    }
}