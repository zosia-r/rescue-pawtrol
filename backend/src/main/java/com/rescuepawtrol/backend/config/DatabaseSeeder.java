package com.rescuepawtrol.backend.config;

import com.rescuepawtrol.backend.model.*;
import com.rescuepawtrol.backend.model.enums.*;
import com.rescuepawtrol.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            Kennel d1=null, d2=null, d3=null, d4=null;
            Kennel c1=null, c2=null, c3=null, c4=null;
            Kennel o1=null, o2=null, o3=null, o4=null;
            Kennel q1=null, q2=null, q3=null, q4=null;

            if (kennelRepository.count() == 0) {
                d1 = kennel("D1", KennelType.DOGS, 4); d2 = kennel("D2", KennelType.DOGS, 4);
                d3 = kennel("D3", KennelType.DOGS, 4); d4 = kennel("D4", KennelType.DOGS, 3);

                c1 = kennel("C1", KennelType.CATS, 4); c2 = kennel("C2", KennelType.CATS, 4);
                c3 = kennel("C3", KennelType.CATS, 3); c4 = kennel("C4", KennelType.CATS, 3);

                o1 = kennel("O1", KennelType.OTHER, 3); o2 = kennel("O2", KennelType.OTHER, 3);
                o3 = kennel("O3", KennelType.OTHER, 2); o4 = kennel("O4", KennelType.OTHER, 2);

                Kennel i1 = kennel("I1", KennelType.ISOLATION_WARD, 2);
                Kennel i2 = kennel("I2", KennelType.ISOLATION_WARD, 2);

                q1 = kennel("Q1", KennelType.QUARANTINE, 2); q2 = kennel("Q2", KennelType.QUARANTINE, 2);
                q3 = kennel("Q3", KennelType.QUARANTINE, 1); q4 = kennel("Q4", KennelType.QUARANTINE, 1);

                kennelRepository.saveAll(List.of(
                        d1, d2, d3, d4, c1, c2, c3, c4, o1, o2, o3, o4, i1, i2, q1, q2, q3, q4
                ));
                System.out.println("✅ Seeded: Kennels");
            } else {
                List<Kennel> kennels = kennelRepository.findAll();
                if(!kennels.isEmpty()) {
                    d1 = kennels.stream().filter(k -> k.getCode().equals("D1")).findFirst().orElse(null);
                    d2 = kennels.stream().filter(k -> k.getCode().equals("D2")).findFirst().orElse(null);
                    d3 = kennels.stream().filter(k -> k.getCode().equals("D3")).findFirst().orElse(null);
                    d4 = kennels.stream().filter(k -> k.getCode().equals("D4")).findFirst().orElse(null);
                    c1 = kennels.stream().filter(k -> k.getCode().equals("C1")).findFirst().orElse(null);
                    c2 = kennels.stream().filter(k -> k.getCode().equals("C2")).findFirst().orElse(null);
                    c3 = kennels.stream().filter(k -> k.getCode().equals("C3")).findFirst().orElse(null);
                    c4 = kennels.stream().filter(k -> k.getCode().equals("C4")).findFirst().orElse(null);
                    o1 = kennels.stream().filter(k -> k.getCode().equals("O1")).findFirst().orElse(null);
                    o2 = kennels.stream().filter(k -> k.getCode().equals("O2")).findFirst().orElse(null);
                    o3 = kennels.stream().filter(k -> k.getCode().equals("O3")).findFirst().orElse(null);
                    o4 = kennels.stream().filter(k -> k.getCode().equals("O4")).findFirst().orElse(null);
                    q1 = kennels.stream().filter(k -> k.getCode().equals("Q1")).findFirst().orElse(null);
                    q2 = kennels.stream().filter(k -> k.getCode().equals("Q2")).findFirst().orElse(null);
                    q3 = kennels.stream().filter(k -> k.getCode().equals("Q3")).findFirst().orElse(null);
                    q4 = kennels.stream().filter(k -> k.getCode().equals("Q4")).findFirst().orElse(null);
                }
            }

            // ─── 3, 4, 5, 6. ANIMALS, RECORDS, CARDS & HISTORIES ──────────────
            if (animalRepository.count() == 0) {
                List<Animal> animals = new ArrayList<>();
                List<AdoptionCard> cards = new ArrayList<>();
                List<MedicalRecord> records = new ArrayList<>();
                List<AdoptionStatusHistory> histories = new ArrayList<>();

                Random rand = new Random(42); 
                LocalDate endDate = LocalDate.of(2026, 6, 18);
                LocalDate startDate = endDate.minusMonths(6);

                List<Kennel> dKennels = List.of(d1, d2, d3, d4);
                List<Kennel> cKennels = List.of(c1, c2, c3, c4);
                List<Kennel> oKennels = List.of(o1, o2, o3, o4);
                List<Kennel> qKennels = List.of(q1, q2, q3, q4);

                String[] dogNames = {"Buddy", "Max", "Rex", "Bella", "Luna", "Charlie", "Duke", "Rocky", "Zoe", "Milo", "Bailey", "Rover", "Buster", "Lucy", "Daisy"};
                String[] catNames = {"Whiskers", "Mittens", "Shadow", "Cleo", "Nala", "Simba", "Leo", "Chloe", "Kitty", "Loki", "Oreo", "Jasper", "Smokey", "George", "Lily"};
                String[] rabbitNames = {"Thumper", "Hazel", "Patches", "Bugs", "Snowball", "Oreo", "Clover", "Bunny"};
                String[] birdNames = {"Tweety", "Polly", "Rio", "Sunny", "Kiwi", "Mango", "Sky", "Blue"};

                // Generowanie 60 zakończonych adopcji w ciągu ostatnich 6 miesięcy (dane dla wykresów liniowych)
                for(int i=0; i<25; i++) generateAnimalData(animals, cards, records, histories, dogNames[i%dogNames.length] + " " + i, "Dog", "Completed", false, null, rand, startDate, endDate);
                for(int i=0; i<20; i++) generateAnimalData(animals, cards, records, histories, catNames[i%catNames.length] + " " + i, "Cat", "Completed", false, null, rand, startDate, endDate);
                for(int i=0; i<8; i++) generateAnimalData(animals, cards, records, histories, rabbitNames[i%rabbitNames.length] + " " + i, "Rabbit", "Completed", false, null, rand, startDate, endDate);
                for(int i=0; i<7; i++) generateAnimalData(animals, cards, records, histories, birdNames[i%birdNames.length] + " " + i, "Bird", "Completed", false, null, rand, startDate, endDate);

                // Generowanie 34 zwierząt aktualnie w schronisku (dane dla wykresu kołowego)
                for(int i=0; i<15; i++) generateAnimalData(animals, cards, records, histories, dogNames[(i+5)%dogNames.length] + " " + (i+25), "Dog", i%4==0?"Pending":"Available", false, dKennels.get(i%dKennels.size()), rand, startDate, endDate);
                for(int i=0; i<11; i++) generateAnimalData(animals, cards, records, histories, catNames[(i+3)%catNames.length] + " " + (i+20), "Cat", i%4==0?"Pending":"Available", false, cKennels.get(i%cKennels.size()), rand, startDate, endDate);
                for(int i=0; i<4; i++) generateAnimalData(animals, cards, records, histories, rabbitNames[(i+2)%rabbitNames.length] + " " + (i+8), "Rabbit", "Available", false, oKennels.get(i%oKennels.size()), rand, startDate, endDate);
                for(int i=0; i<4; i++) generateAnimalData(animals, cards, records, histories, birdNames[(i+1)%birdNames.length] + " " + (i+7), "Bird", "Available", false, oKennels.get((i+2)%oKennels.size()), rand, startDate, endDate);

                // Generowanie 6 zwierząt na kwarantannie (dla kafelka KPI)
                for(int i=0; i<3; i++) generateAnimalData(animals, cards, records, histories, dogNames[(i+1)%dogNames.length] + " Q" + i, "Dog", "Available", true, qKennels.get(i%qKennels.size()), rand, startDate, endDate);
                for(int i=0; i<2; i++) generateAnimalData(animals, cards, records, histories, catNames[(i+4)%catNames.length] + " Q" + i, "Cat", "Available", true, qKennels.get((i+2)%qKennels.size()), rand, startDate, endDate);
                for(int i=0; i<1; i++) generateAnimalData(animals, cards, records, histories, rabbitNames[(i+5)%rabbitNames.length] + " Q" + i, "Rabbit", "Available", true, qKennels.get(3), rand, startDate, endDate);

                animalRepository.saveAll(animals);
                adoptionCardRepository.saveAll(cards);
                adoptionStatusHistoryRepository.saveAll(histories);
                medicalRecordRepository.saveAll(records);

                System.out.println("✅ Seeded: 100 Animals + Adoptions + Medical Records (optimized for 6-months reports)");
            }

            // ─── 7. INTERVENTIONS & ROUTE POINTS (Bez zmian) ─────────────────
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

            System.out.println("🚀 Baza danych jest w pełni gotowa do pracy z ujednoliconymi statusami i rozbudowanymi danymi raportowymi!");
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

    private void generateAnimalData(
            List<Animal> animals, List<AdoptionCard> cards, List<MedicalRecord> records, List<AdoptionStatusHistory> histories,
            String name, String species, String status, boolean isQuarantined, Kennel kennel,
            Random rand, LocalDate start, LocalDate end) {

        Animal a = new Animal();
        a.setName(name);
        a.setSpecies(species);
        a.setBirthDate(LocalDate.now().minusYears(rand.nextInt(7) + 1).minusMonths(rand.nextInt(12)));
        a.setAdoptionStatus(status);
        a.setIsQuarantined(isQuarantined);
        a.setKennel(kennel);
        animals.add(a);

        long startDay = start.toEpochDay();
        long endDay = end.toEpochDay();
        LocalDate randomDate = LocalDate.ofEpochDay(startDay + (long)(rand.nextDouble() * (endDay - startDay)));

        if ("Completed".equals(status)) {
            AdoptionCard card = new AdoptionCard();
            card.setStatus(AdoptionStatus.COMPLETED);
            card.setCandidateDetails("Jan Kowalski " + rand.nextInt(1000));
            card.setSubmissionDate(randomDate.minusDays(10));
            card.setFinalizationDate(randomDate);
            card.setAnimal(a);
            cards.add(card);

            AdoptionStatusHistory ash = new AdoptionStatusHistory();
            ash.setAnimal(a);
            ash.setDate(randomDate);
            ash.setStatus("Completed");
            ash.setOwner(card.getCandidateDetails());
            ash.setNotes("Zwierzę adoptowane pomyślnie.");
            histories.add(ash);

            MedicalRecord m = new MedicalRecord();
            m.setRecordDate(randomDate.minusDays(14));
            m.setRecordType(MedicalRecordType.values()[0]);
            m.setDescription("Szczepienie i odrobaczenie przed adopcją.");
            m.setDoctor("Dr. Sarah Chen");
            m.setAnimal(a);
            records.add(m);

        } else if ("Pending".equals(status)) {
            AdoptionCard card = new AdoptionCard();
            card.setStatus(AdoptionStatus.PENDING);
            card.setCandidateDetails("Anna Nowak " + rand.nextInt(1000));
            card.setSubmissionDate(randomDate);
            card.setFinalizationDate(null);
            card.setAnimal(a);
            cards.add(card);

            AdoptionStatusHistory ash = new AdoptionStatusHistory();
            ash.setAnimal(a);
            ash.setDate(randomDate);
            ash.setStatus("Pending");
            ash.setOwner(card.getCandidateDetails());
            ash.setNotes("Oczekiwanie na finalizację dokumentów.");
            histories.add(ash);
        }

        if (!"Completed".equals(status)) {
            MedicalRecord m = new MedicalRecord();
            m.setRecordDate(randomDate);
            m.setRecordType(MedicalRecordType.values()[0]);
            m.setDescription(isQuarantined ? "Wstępne badanie kwarantannowe." : "Standardowa kontrola weterynaryjna w schronisku.");
            m.setDoctor("Dr. Anna Kowal");
            m.setAnimal(a);
            records.add(m);
        }
    }
}