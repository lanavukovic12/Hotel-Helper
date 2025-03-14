package testiranje;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import csv.CSVHandler;
import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import uloge.Administrator;
import uloge.Gost;
import uloge.Osoba;
import uloge.Recepcioner;
import uloge.Sobarica;
import usluge.Soba;
import usluge.TimeBoundPriceList;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;

import interfejsi.OsobljeZaCiscenje;

public class Test {

	public static void main(String[] args) {
		
		RacunHotela hotelAccount = new RacunHotela(500000.0);
		
		CSVHandler csvHandler = new CSVHandler();
		
		csvHandler.initializeCSVData();
		
		System.out.println("\nTestiranje load sobe:");
		
		System.out.println(csvHandler.loadSobeFromCSV());
		
		
		System.out.println("\nTestiranje load TimeBoundPriceLists:");
		
		System.out.println(csvHandler.loadTimeBoundPriceListsFromCSV());
		
		System.out.println("\nTestiranje load Goste:");
		
		System.out.println(csvHandler.loadGosteFromCSV());
		
		System.out.println("\nTestiranje load Rezervacije:");
		
		System.out.println(csvHandler.loadRezervacijeFromCSV(csvHandler.loadGosteFromCSV(),
															 csvHandler.loadSobeFromCSV(),
															 csvHandler.loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now())
															));
		
		System.out.println("\nTestiranje load OsobljeZaCiscenje:");
		
		System.out.println(csvHandler.loadOsobljeZaCiscenjeFromCSV(csvHandler.loadSobeFromCSV(),
						   										   csvHandler.loadTimeBoundPriceListsFromCSV())
						   										  );
		System.out.println("\nTestiranje load Employees:");
		
		System.out.println(csvHandler.loadEmployeesFromCSV(csvHandler.loadTimeBoundPriceListsFromCSV()));	
		
		System.out.println("\nTestiranje load Osobe:");
		
		System.out.println(csvHandler.loadOsobeFromCSV());
		
		// Kreiranje TimeBoundPriceList'a
		
        LocalDate datumVazenja = LocalDate.now();
        double baseSalary = 30000.0;
        double experienceBonus = 3000.0;
        Map<Obrazovanje, Double> obrazovanjeKoeficijenti = Map.of(
            Obrazovanje.PRIMARY_EDUCATION, 1.4,
            Obrazovanje.LOWER_SECONDARY_EDUCATION, 1.6,
            Obrazovanje.UPPER_SECONDARY_EDUCATION, 2.0,
            Obrazovanje.BACHELORS_DEGREE, 2.6,
            Obrazovanje.MASTERS_DEGREE, 3.0,
            Obrazovanje.DOCTORAL_DEGREE, 4.2
        );
        Map<TipSobe, Double> roomPrices = Map.of(
            TipSobe.JEDNOKREVETNA, 1200.0,
            TipSobe.DVOKREVETNA_BRACNI, 1400.0,
            TipSobe.DVOKREVETNA_ODVOJENI, 1700.0,
            TipSobe.TROKREVETNA_BRACNI, 1800.0,
            TipSobe.TROKREVETNA_ODVOJENI, 2100.0
        );
        Map<Amenities, Double> amenityPrices = Map.of(
            Amenities.WIFI, 150.0,
            Amenities.BREAKFAST, 300.0,
            Amenities.PARKING, 150.0,
            Amenities.SPAPOOL, 400.0,
            Amenities.AIRCONDITIONING, 200.0,
            Amenities.TV, 400.0,
            Amenities.MINIBAR, 300.0,
            Amenities.SMOKING, 0.0,
            Amenities.BALCONY, 50.0
        );
        
        double loyaltyThreshold = 80000.0;
        double loyaltyDiscount = 15.0;
        Cenovnik cenovnik = new Cenovnik(
            baseSalary,
            experienceBonus,
            obrazovanjeKoeficijenti,
            roomPrices,
            amenityPrices,
            loyaltyThreshold,
            loyaltyDiscount
        );
        
        NavigableMap<LocalDate, Cenovnik> tbpLists = new TreeMap<>();
        
        tbpLists.put(datumVazenja, cenovnik);
        
        TimeBoundPriceList timeBoundPriceList = new TimeBoundPriceList(tbpLists);
        
		
		
		// Kreiranje osoblja
		String ime = "Admin";
        String prezime = "Sobarica";
        Pol pol = Pol.MUSKI; // Assuming you have an enum for Pol
        LocalDate datumRodjenja = LocalDate.of(1985, 5, 20);
        String brojTelefona = "+1234567890";
        String adresa = "Novi Sad, Liman";
        String username = "admin_sobarica";
        String password = "adminpass";
        LicniRacun racun = new LicniRacun(10000.0); // Assuming an initial balance of 10000.0
        Obrazovanje obrazovanje = Obrazovanje.BACHELORS_DEGREE; // Assuming you have an enum for Obrazovanje
        String background = "Background text";
        double godineIskustva = 10.0; // Years of experience
		
		
		// =========================================================
		// Testiranje sobarica i obaveza
		
		System.out.println("\n\n Testiranje sobarica i obaveza:");
		
		Administrator admin_sobarica = new Administrator(ime,
														 prezime,
														 pol,
														 datumRodjenja,
														 brojTelefona,
														 adresa,
														 username,
														 password,
														 racun,
														 obrazovanje,
														 background,
														 godineIskustva,
														 timeBoundPriceList
														);
		
		admin_sobarica.setHotelAccount(hotelAccount);
		
		
		Administrator admin_recepcioner = new Administrator("Admin Recepcioner");
        Sobarica sobarica1 = new Sobarica("Sobarica 1");
        Sobarica sobarica2 = new Sobarica("Sobarica 2");
        Recepcioner recepcioner = new Recepcioner("Recepcioner 1");

        admin_sobarica.getSobeObaveze().add(new Soba (TipSobe.JEDNOKREVETNA));
        admin_sobarica.getSobeObaveze().add(new Soba(TipSobe.DVOKREVETNA_BRACNI));

        sobarica1.getSobeObaveze().add(new Soba(TipSobe.TROKREVETNA_BRACNI));

        sobarica2.getSobeObaveze().add(new Soba(TipSobe.DVOKREVETNA_BRACNI));
        sobarica2.getSobeObaveze().add(new Soba(TipSobe.JEDNOKREVETNA));

        recepcioner.getOsobljeZaCiscenje().add(admin_sobarica);
        recepcioner.getOsobljeZaCiscenje().add(sobarica1);
        recepcioner.getOsobljeZaCiscenje().add(sobarica2);
        
        OsobljeZaCiscenje najmanjeZauzeta = recepcioner.pronadjiNajmanjeZauzetuSobaricu();

        // Assign a new cleaning duty
        recepcioner.dodeliSobu(new Soba(TipSobe.JEDNOKREVETNA), najmanjeZauzeta);

        // Find and print the least busy cleaning staff member
        
        System.out.println("The least busy person is: " + ((Osoba)najmanjeZauzeta).getIme() + " , class: " + najmanjeZauzeta.getClass().getSimpleName() + ", obaveze:" + najmanjeZauzeta.getSobeObaveze());
        
        System.out.println(recepcioner.getOsobljeZaCiscenje());
        
        
        // Add cleaning staff to the admin's (recepcioner) list
        System.out.println("\n\n\nTesting admin (glumi recepcionera)");
        admin_recepcioner.getOsobljeZaCiscenje().add(admin_sobarica);
        admin_recepcioner.getOsobljeZaCiscenje().add(sobarica1);
        admin_recepcioner.getOsobljeZaCiscenje().add(sobarica2);
        
        OsobljeZaCiscenje najmanjeZauzeta2 = admin_recepcioner.pronadjiNajmanjeZauzetuSobaricu();

        // Assign a new cleaning duty
        admin_recepcioner.dodeliSobu(new Soba(TipSobe.JEDNOKREVETNA), najmanjeZauzeta2);

        // Find and print the least busy cleaning staff member
        
        System.out.println("The least busy person is: " + ((Osoba)najmanjeZauzeta2).getIme() + " , class: " + najmanjeZauzeta2.getClass().getSimpleName());
        
        System.out.println(admin_recepcioner.getOsobljeZaCiscenje());
        
        // ===========================================
        //Testiranje rezervacija
        
        System.out.println("\n\n Testiranje rezervacija:");
        
        // lista amenities
        List<Amenities> amenities = Arrays.asList(
                Amenities.WIFI,
                Amenities.BREAKFAST,
                Amenities.TV,
                Amenities.SPAPOOL,
                Amenities.AIRCONDITIONING,
                Amenities.BALCONY,
                Amenities.MINIBAR,
                Amenities.SMOKING,
                Amenities.PARKING
        );
        
        List<Soba> sobe = new ArrayList<>();
        Soba soba1 = new Soba(101, TipSobe.JEDNOKREVETNA, StanjeSobe.AVAILABLE);
        Soba soba2 = new Soba(102, TipSobe.DVOKREVETNA_ODVOJENI, StanjeSobe.AVAILABLE);
        Soba soba3 = new Soba(103, TipSobe.TROKREVETNA_BRACNI, StanjeSobe.OCCUPIED);
        Soba soba4 = new Soba(104, TipSobe.JEDNOKREVETNA, StanjeSobe.CLEANING);
        
        // Testiranje Time-bound cenovnika:
        System.out.println("Tekuci vazeci cenovnik za " + LocalDate.now() + " je: " + timeBoundPriceList.getPriceList(LocalDate.now()));
        
        /*
        //kreiranje rezervacije gostom, potvrdjivanje recepcionerom
        System.out.println("\n\n Testiranje rezervacija 1");
        Gost gost = new Gost("Boris");
        LocalDate checkInDate = LocalDate.of(2024, 7, 12);
        LocalDate checkOutDate = LocalDate.of(2024, 7, 30);
        Rezervacija nova_rezervacija = gost.napraviRezervacijuSebi(new Soba(TipSobe.JEDNOKREVETNA), checkInDate, checkOutDate);
        System.out.println(nova_rezervacija);
        recepcioner.promeniStanjeRezervacije(nova_rezervacija, StanjeRezervacije.CONFIRMED);
        System.out.println(nova_rezervacija.getStanjeRezervacije());
        recepcioner.promeniStanjeRezervacije(nova_rezervacija, StanjeRezervacije.CHECKED_IN);
        System.out.println(nova_rezervacija.getStanjeRezervacije());
        System.out.println("\nRezervacije recepcionera: " + recepcioner.getRezervacije());
        System.out.println("\nRezervacije gosta: " + gost.getRezervacije());
        
        
        //kreiranje i potvrdjivanje rezervacije recepcionerom
        System.out.println("\n\n Testiranje rezervacija 2");
        Gost gost2 = new Gost("Melinda");
        Rezervacija nova_rezervacija_2 = recepcioner.napraviRezervacijuNekome(gost2, new Soba(TipSobe.DVOKREVETNA_ODVOJENI), checkInDate, checkOutDate);
        System.out.println(nova_rezervacija_2);
        recepcioner.promeniStanjeRezervacije(nova_rezervacija_2, StanjeRezervacija.CANCELLED);
        System.out.println("Rezervacije recepcionera: " + recepcioner.getRezervacije());
        System.out.println("Rezervacije gosta: " + gost2.getRezervacije());
        
        
        //kreiranje rezervacije gostom, potvrdjivanje administratorom
        System.out.println("\n\n Testiranje rezervacija 3");
        Gost gost3 = new Gost("Uros");
        Rezervacija nova_rezervacija_3 = gost3.napraviRezervacijuSebi(new Soba(TipSobe.TROKREVETNA_ODVOJENI), checkInDate, checkOutDate);
        System.out.println(nova_rezervacija_3);
        admin_recepcioner.promeniStanjeRezervacije(nova_rezervacija_3, StanjeRezervacija.CHECKED_IN);
        System.out.println("\nRezervacije administratora: " + admin_recepcioner.getRezervacije());
        System.out.println("\nRezervacije gosta: " + gost3.getRezervacije());
        
        
        //kreiranje i potvrdjivanje rezervacije administratorom
        System.out.println("\n\n Testiranje rezervacija 4");
        Gost gost4 = new Gost("Anja");
        Rezervacija nova_rezervacija_4 = admin_recepcioner.napraviRezervacijuNekome(gost4, new Soba(TipSobe.JEDNOKREVETNA), checkInDate, checkOutDate);
        System.out.println(nova_rezervacija_4);
        admin_recepcioner.promeniStanjeRezervacije(nova_rezervacija_4, StanjeRezervacija.CANCELLED);
        System.out.println("\nRezervacije administratora: " + admin_recepcioner.getRezervacije());
        System.out.println("Rezervacije gosta: " + gost4.getRezervacije());
        */

	}

}
