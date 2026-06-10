package com.rescuepawtrol.backend.config;

import com.rescuepawtrol.backend.model.*;
import com.rescuepawtrol.backend.model.enums.*;
import com.rescuepawtrol.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            RoutePointRepository routePointRepository) { // Usunięto PasswordEncoder

        return args -> {

            // ─── 1. EMPLOYEES (Synchronizacja z Keycloak) ───────────────────
            Employee admin = null;
            Employee driver = null;

            if (employeeRepository.count() == 0) {
                // Pracownik zsynchronizowany z użytkownikiem 'admin' w Keycloak
                admin = new Employee();
                admin.setFirstName("Alicja");
                admin.setEmail("admin@rescuepawtrol.pl");
                // UWAGA: Nie ustawiamy hasła. Keycloak to obsługuje.
                admin.setRole(EmployeeRole.values()[0]); // Zależnie od struktury enuma (np. MANAGER)

                // Pracownik zsynchronizowany z użytkownikiem 'driver' w Keycloak
                driver = new Employee();
                driver.setFirstName("John");
                driver.setEmail("driver@rescuepawtrol.pl");
                driver.setRole(EmployeeRole.values()[0]); // Zależnie od struktury enuma (np. DISPATCHER)

                employeeRepository.saveAll(List.of(admin, driver));
                System.out.println("✅ Seeded: Employees (Keycloak synced emails)");
            } else {
                admin  = employeeRepository.findByEmail("admin@rescuepawtrol.pl").orElse(null);
                driver = employeeRepository.findByEmail("driver@rescuepawtrol.pl").orElse(null);
            }

            // ─── 2. KENNELS — 4 per type ─────────────────────────────────────
            if (kennelRepository.count() == 0) {

                // Dogs
                Kennel d1 = kennel("D1", KennelType.DOGS, 4);
                Kennel d2 = kennel("D2", KennelType.DOGS, 4);
                Kennel d3 = kennel("D3", KennelType.DOGS, 3);
                Kennel d4 = kennel("D4", KennelType.DOGS, 3);

                // Cats
                Kennel c1 = kennel("C1", KennelType.CATS, 4);
                Kennel c2 = kennel("C2", KennelType.CATS, 4);
                Kennel c3 = kennel("C3", KennelType.CATS, 3);
                Kennel c4 = kennel("C4", KennelType.CATS, 3);

                // Other
                Kennel o1 = kennel("O1", KennelType.OTHER, 3);
                Kennel o2 = kennel("O2", KennelType.OTHER, 3);
                Kennel o3 = kennel("O3", KennelType.OTHER, 2);
                Kennel o4 = kennel("O4", KennelType.OTHER, 2);

                // Isolation Ward
                Kennel i1 = kennel("I1", KennelType.ISOLATION_WARD, 2);
                Kennel i2 = kennel("I2", KennelType.ISOLATION_WARD, 2);
                Kennel i3 = kennel("I3", KennelType.ISOLATION_WARD, 1);
                Kennel i4 = kennel("I4", KennelType.ISOLATION_WARD, 1);

                // Quarantine
                Kennel q1 = kennel("Q1", KennelType.QUARANTINE, 2);
                Kennel q2 = kennel("Q2", KennelType.QUARANTINE, 2);
                Kennel q3 = kennel("Q3", KennelType.QUARANTINE, 1);
                Kennel q4 = kennel("Q4", KennelType.QUARANTINE, 1);

                kennelRepository.saveAll(List.of(
                        d1, d2, d3, d4,
                        c1, c2, c3, c4,
                        o1, o2, o3, o4,
                        i1, i2, i3, i4,
                        q1, q2, q3, q4
                ));
                System.out.println("✅ Seeded: Kennels (20 — 4 per type)");
            }

            // ─── 3. ANIMALS — 20, none assigned to a kennel ──────────────────
            if (animalRepository.count() == 0) {

                // Dogs (7)
                Animal a1  = animal("Buddy",    "Dog",    LocalDate.now().minusYears(3), "Available",  false);
                Animal a2  = animal("Max",      "Dog",    LocalDate.now().minusYears(5), "Available",  false);
                Animal a3  = animal("Rocky",    "Dog",    LocalDate.now().minusYears(2), "Pending",    false);
                Animal a4  = animal("Charlie",  "Dog",    LocalDate.now().minusYears(4), "Available",  false);
                Animal a5  = animal("Duke",     "Dog",    LocalDate.now().minusYears(6), "On Hold",    false);
                Animal a6  = animal("Bella",    "Dog",    LocalDate.now().minusYears(1), "Available",  false);
                Animal a7  = animal("Rex",      "Dog",    LocalDate.now().minusYears(7), "Available",  false);

// Cats (7)
                Animal a8  = animal("Whiskers", "Cat",    LocalDate.now().minusYears(2), "Available",  false);
                Animal a9  = animal("Luna",     "Cat",    LocalDate.now().minusYears(1), "Pending",    false);
                Animal a10 = animal("Mittens",  "Cat",    LocalDate.now().minusYears(4), "Available",  false);
                Animal a11 = animal("Shadow",   "Cat",    LocalDate.now().minusYears(3), "On Hold",    false);
                Animal a12 = animal("Cleo",     "Cat",    LocalDate.now().minusYears(5), "Available",  false);
                Animal a13 = animal("Nala",     "Cat",    LocalDate.now().minusYears(2), "Available",  false);
                Animal a14 = animal("Simba",    "Cat",    LocalDate.now().minusYears(6), "Available",  false);

// Other (6)
                Animal a15 = animal("Thumper",  "Rabbit", LocalDate.now().minusYears(2), "Available",  false);
                Animal a16 = animal("Hazel",    "Rabbit", LocalDate.now().minusYears(1), "Pending",    false);
                Animal a17 = animal("Tweety",   "Bird",   LocalDate.now().minusYears(3), "Available",  false);
                Animal a18 = animal("Polly",    "Bird",   LocalDate.now().minusYears(4), "On Hold",    false);
                Animal a19 = animal("Nibbles",  "Hamster",LocalDate.now().minusYears(1), "Available",  false);
                Animal a20 = animal("Patches",  "Rabbit", LocalDate.now().minusYears(2), "Available",  false);

                animalRepository.saveAll(List.of(
                        a1,  a2,  a3,  a4,  a5,  a6,  a7,
                        a8,  a9,  a10, a11, a12, a13, a14,
                        a15, a16, a17, a18, a19, a20
                ));
                System.out.println("✅ Seeded: Animals (20 — no kennel assigned)");
            }

            // ─── 4. MEDICAL RECORDS ───────────────────────────────────────────
            if (medicalRecordRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                MedicalRecord m1 = new MedicalRecord();
                m1.setRecordDate(LocalDate.now().minusDays(10));
                m1.setRecordType(MedicalRecordType.values()[0]);
                m1.setDescription("FVRCP booster administered. General health is good.");
                m1.setDoctor("Dr. Sarah Chen");
                m1.setAnimal(animals.get(0));

                MedicalRecord m2 = new MedicalRecord();
                m2.setRecordDate(LocalDate.now().minusDays(2));
                m2.setRecordType(MedicalRecordType.values()[0]);
                m2.setDescription("Initial health screening and deworming completed.");
                m2.setDoctor("Dr. Michael Torres");
                m2.setAnimal(animals.get(7));

                medicalRecordRepository.saveAll(List.of(m1, m2));
                System.out.println("✅ Seeded: Medical Records");
            }

            // ─── 5. ADOPTION CARDS ────────────────────────────────────────────
            if (adoptionCardRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                AdoptionCard card1 = new AdoptionCard();
                card1.setStatus(AdoptionStatus.values()[0]);
                card1.setCandidateDetails("Emily Johnson, 123 Maple Street, loves cats.");
                card1.setSubmissionDate(LocalDate.now().minusDays(5));
                card1.setFinalizationDate(null);
                card1.setAnimal(animals.get(2)); // Rocky (Pending)

                adoptionCardRepository.save(card1);
                System.out.println("✅ Seeded: Adoption Cards");
            }

            // ─── 6. INTERVENTIONS & ROUTE POINTS ─────────────────────────────
            if (interventionRepository.count() == 0 && driver != null) {
                Intervention inv = new Intervention();
                inv.setReportTime(LocalDateTime.now().minusHours(2));
                inv.setDescription("Cat on a tree");
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

            System.out.println("🚀 Baza danych jest w pełni gotowa do testów z Keycloak!");
        };
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Kennel kennel(String code, KennelType type, int capacity) {
        Kennel k = new Kennel();
        k.setCode(code);
        k.setType(type);
        k.setCapacity(capacity);
        return k;
    }

    private Animal animal(String name, String species, LocalDate birthDate,
                          String adoptionStatus, boolean isQuarantined) {
        Animal a = new Animal();
        a.setName(name);
        a.setSpecies(species);
        a.setBirthDate(birthDate); // o tutaj
        a.setAdoptionStatus(adoptionStatus);
        a.setIsQuarantined(isQuarantined);
        // kennel celowo null — żadne zwierzę nie jest przypisane na start
        return a;
    }
}