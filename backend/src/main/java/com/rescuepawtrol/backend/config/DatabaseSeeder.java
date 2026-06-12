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
            RoutePointRepository routePointRepository,
            AdoptionStatusHistoryRepository adoptionStatusHistoryRepository) {

        return args -> {

            // ─── 1. EMPLOYEES (Synchronizacja z Keycloak) ───────────────────
            Employee admin = null;
            Employee driver = null;

            if (employeeRepository.count() == 0) {
                admin = new Employee();
                admin.setFirstName("Alicja");
                admin.setEmail("admin@rescuepawtrol.pl");
                admin.setRole(EmployeeRole.values()[0]);

                driver = new Employee();
                driver.setFirstName("John");
                driver.setEmail("driver@rescuepawtrol.pl");
                driver.setRole(EmployeeRole.values()[0]);

                employeeRepository.saveAll(List.of(admin, driver));
                System.out.println("✅ Seeded: Employees");
            } else {
                admin  = employeeRepository.findByEmail("admin@rescuepawtrol.pl").orElse(null);
                driver = employeeRepository.findByEmail("driver@rescuepawtrol.pl").orElse(null);
            }

            // ─── 2. KENNELS — 4 per type ─────────────────────────────────────
            Kennel d1=null, d2=null, c1=null, c2=null, o1=null, q1=null, q2=null, q3=null;

            if (kennelRepository.count() == 0) {
                d1 = kennel("D1", KennelType.DOGS, 4);
                d2 = kennel("D2", KennelType.DOGS, 4);
                Kennel d3 = kennel("D3", KennelType.DOGS, 3);
                Kennel d4 = kennel("D4", KennelType.DOGS, 3);

                c1 = kennel("C1", KennelType.CATS, 4);
                c2 = kennel("C2", KennelType.CATS, 4);
                Kennel c3 = kennel("C3", KennelType.CATS, 3);
                Kennel c4 = kennel("C4", KennelType.CATS, 3);

                o1 = kennel("O1", KennelType.OTHER, 3);
                Kennel o2 = kennel("O2", KennelType.OTHER, 3);
                Kennel o3 = kennel("O3", KennelType.OTHER, 2);
                Kennel o4 = kennel("O4", KennelType.OTHER, 2);

                Kennel i1 = kennel("I1", KennelType.ISOLATION_WARD, 2);
                Kennel i2 = kennel("I2", KennelType.ISOLATION_WARD, 2);

                q1 = kennel("Q1", KennelType.QUARANTINE, 2);
                q2 = kennel("Q2", KennelType.QUARANTINE, 2);
                q3 = kennel("Q3", KennelType.QUARANTINE, 1);
                Kennel q4 = kennel("Q4", KennelType.QUARANTINE, 1);

                kennelRepository.saveAll(List.of(
                        d1, d2, d3, d4,
                        c1, c2, c3, c4,
                        o1, o2, o3, o4,
                        i1, i2,
                        q1, q2, q3, q4
                ));
                System.out.println("✅ Seeded: Kennels");
            } else {
                List<Kennel> kennels = kennelRepository.findAll();
                if(!kennels.isEmpty()) {
                    d1 = kennels.stream().filter(k -> k.getCode().equals("D1")).findFirst().orElse(null);
                    c1 = kennels.stream().filter(k -> k.getCode().equals("C1")).findFirst().orElse(null);
                    o1 = kennels.stream().filter(k -> k.getCode().equals("O1")).findFirst().orElse(null);
                    q1 = kennels.stream().filter(k -> k.getCode().equals("Q1")).findFirst().orElse(null);
                    q2 = kennels.stream().filter(k -> k.getCode().equals("Q2")).findFirst().orElse(null);
                    q3 = kennels.stream().filter(k -> k.getCode().equals("Q3")).findFirst().orElse(null);
                }
            }

            // ─── 3. ANIMALS (Zaktualizowane statusy + przypisanie boksów) ────
            if (animalRepository.count() == 0) {
                // Zwierzęta COMPLETED zawsze mają kennel = null
                // Dogs
                Animal a1  = animal("Buddy",    "Dog",    LocalDate.now().minusYears(3), "Available", false, d1);
                Animal a2  = animal("Max",      "Dog",    LocalDate.now().minusYears(5), "Completed", false, null);
                Animal a3  = animal("Rocky",    "Dog",    LocalDate.now().minusYears(2), "Pending",   false, d1);
                Animal a4  = animal("Charlie",  "Dog",    LocalDate.now().minusYears(4), "Completed", false, null);
                Animal a5  = animal("Duke",     "Dog",    LocalDate.now().minusYears(6), "Available", false, d1);
                Animal a6  = animal("Bella",    "Dog",    LocalDate.now().minusYears(1), "Available", false, d2);
                Animal a7  = animal("Rex",      "Dog",    LocalDate.now().minusYears(7), "Available", true,  q1); // Kwarantanna -> Q1

                // Cats
                Animal a8  = animal("Whiskers", "Cat",    LocalDate.now().minusYears(2), "Available", false, c1);
                Animal a9  = animal("Luna",     "Cat",    LocalDate.now().minusYears(1), "Pending",   false, null);
                Animal a10 = animal("Mittens",  "Cat",    LocalDate.now().minusYears(4), "Completed", false, null);
                Animal a11 = animal("Shadow",   "Cat",    LocalDate.now().minusYears(3), "Pending",   false, c2);
                Animal a12 = animal("Cleo",     "Cat",    LocalDate.now().minusYears(5), "Completed", false, null);
                Animal a13 = animal("Nala",     "Cat",    LocalDate.now().minusYears(2), "Available", true,  q2); // Kwarantanna -> Q2
                Animal a14 = animal("Simba",    "Cat",    LocalDate.now().minusYears(6), "Available", false, c2);

                // Other
                Animal a15 = animal("Thumper",  "Rabbit", LocalDate.now().minusYears(2), "Available", false, o1);
                Animal a16 = animal("Hazel",    "Rabbit", LocalDate.now().minusYears(1), "Pending",   false, o1);
                Animal a17 = animal("Tweety",   "Bird",   LocalDate.now().minusYears(3), "Available", false, o1);
                Animal a18 = animal("Polly",    "Bird",   LocalDate.now().minusYears(4), "Available", false, null);
                Animal a19 = animal("Nibbles",  "Hamster",LocalDate.now().minusYears(1), "Completed", false, null);
                Animal a20 = animal("Patches",  "Rabbit", LocalDate.now().minusYears(2), "Available", true,  q3); // Kwarantanna -> Q3

                animalRepository.saveAll(List.of(
                        a1,  a2,  a3,  a4,  a5,  a6,  a7,
                        a8,  a9,  a10, a11, a12, a13, a14,
                        a15, a16, a17, a18, a19, a20
                ));
                System.out.println("✅ Seeded: Animals (Unified statuses & kennels updated)");
            }

            // ─── 4. ADOPTION STATUS HISTORY ──────────────────────────────────
            if (adoptionStatusHistoryRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                // Historia dla Maxa (Completed)
                AdoptionStatusHistory h1 = createStatusHistory(animals.get(1), LocalDate.of(2026, 5, 10), "Pending", "Kowalski", "Wizyta zapoznawcza.");
                AdoptionStatusHistory h2 = createStatusHistory(animals.get(1), LocalDate.of(2026, 5, 20), "Completed", "Kowalski", "Podpisano umowę i wydano zwierzę.");

                // Historia dla Rocky'ego (Pending)
                AdoptionStatusHistory h3 = createStatusHistory(animals.get(2), LocalDate.of(2026, 5, 28), "Pending", "Anna Nowak", "Czekamy na weryfikację warunków.");

                // Historia dla Mittens (Completed)
                AdoptionStatusHistory h4 = createStatusHistory(animals.get(9), LocalDate.of(2026, 5, 15), "Pending", "Tomasz Lis", "Rezerwacja w toku.");
                AdoptionStatusHistory h5 = createStatusHistory(animals.get(9), LocalDate.of(2026, 6, 2), "Completed", "Tomasz Lis", "Zwierzę wydane nowemu właścicielowi.");

                // Historia dla Luny (Pending)
                AdoptionStatusHistory h6 = createStatusHistory(animals.get(8), LocalDate.of(2026, 6, 5), "Pending", "Marek Mostowiak", "Złożono wniosek.");

                adoptionStatusHistoryRepository.saveAll(List.of(h1, h2, h3, h4, h5, h6));
                System.out.println("✅ Seeded: Adoption Status History");
            }

            // ─── 5. MEDICAL RECORDS ──────────────────────────────────────────
            if (medicalRecordRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                MedicalRecord m1 = createMedicalRecord(animals.get(0), LocalDate.of(2026, 5, 12), "Standardowe odrobaczanie.");
                MedicalRecord m2 = createMedicalRecord(animals.get(6), LocalDate.of(2026, 5, 18), "Rozpoczęcie kwarantanny - Rex.");
                MedicalRecord m3 = createMedicalRecord(animals.get(2), LocalDate.of(2026, 5, 25), "Leczenie infekcji ucha.");
                MedicalRecord m4 = createMedicalRecord(animals.get(12), LocalDate.of(2026, 6, 1), "Szczepienie na wściekliznę - Nala.");
                MedicalRecord m5 = createMedicalRecord(animals.get(19), LocalDate.of(2026, 6, 8), "Przegląd weterynaryjny na kwarantannie - Patches.");

                medicalRecordRepository.saveAll(List.of(m1, m2, m3, m4, m5));
                System.out.println("✅ Seeded: Medical Records");
            }

            // ─── 6. ADOPTION CARDS ───────────────────────────────────────────
            if (adoptionCardRepository.count() == 0 && animalRepository.count() > 0) {
                List<Animal> animals = animalRepository.findAll();

                AdoptionCard ac1 = createAdoptionCard(animals.get(2), LocalDate.of(2026, 5, 28), AdoptionStatus.PENDING); // Rocky
                AdoptionCard ac2 = createAdoptionCard(animals.get(8), LocalDate.of(2026, 6, 5), AdoptionStatus.PENDING);  // Luna
                AdoptionCard ac3 = createAdoptionCard(animals.get(9), LocalDate.of(2026, 5, 15), AdoptionStatus.COMPLETED); // Mittens
                AdoptionCard ac4 = createAdoptionCard(animals.get(1), LocalDate.of(2026, 5, 10), AdoptionStatus.COMPLETED); // Max
                AdoptionCard ac5 = createAdoptionCard(animals.get(15), LocalDate.of(2026, 5, 22), AdoptionStatus.PENDING);// Hazel

                adoptionCardRepository.saveAll(List.of(ac1, ac2, ac3, ac4, ac5));
                System.out.println("✅ Seeded: Adoption Cards");
            }

            // ─── 7. INTERVENTIONS & ROUTE POINTS ─────────────────────────────
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

            System.out.println("🚀 Baza danych jest w pełni gotowa do pracy z ujednoliconymi statusami!");
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

    private Animal animal(String name, String species, LocalDate birthDate, String adoptionStatus, boolean isQuarantined, Kennel kennel) {
        Animal a = new Animal();
        a.setName(name);
        a.setSpecies(species);
        a.setBirthDate(birthDate);
        a.setAdoptionStatus(adoptionStatus);
        a.setIsQuarantined(isQuarantined);
        a.setKennel(kennel);
        return a;
    }

    private MedicalRecord createMedicalRecord(Animal animal, LocalDate date, String description) {
        MedicalRecord m = new MedicalRecord();
        m.setRecordDate(date);
        m.setRecordType(MedicalRecordType.values()[0]);
        m.setDescription(description);
        m.setDoctor("Dr. Sarah Chen");
        m.setAnimal(animal);
        return m;
    }

    private AdoptionCard createAdoptionCard(Animal animal, LocalDate date, AdoptionStatus status) {
        AdoptionCard card = new AdoptionCard();
        card.setStatus(status);
        card.setCandidateDetails("System Generated Candidate");
        card.setSubmissionDate(date);
        card.setFinalizationDate(status == AdoptionStatus.COMPLETED ? date.plusDays(10) : null);
        card.setAnimal(animal);
        return card;
    }

    private AdoptionStatusHistory createStatusHistory(Animal animal, LocalDate date, String status, String owner, String notes) {
        AdoptionStatusHistory ash = new AdoptionStatusHistory();
        ash.setAnimal(animal);
        ash.setDate(date);
        ash.setStatus(status);
        ash.setOwner(owner);
        ash.setNotes(notes);
        return ash;
    }
}