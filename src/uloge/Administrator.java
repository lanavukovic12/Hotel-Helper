package uloge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import csvServices.EmployeeCSVService;
import csvServices.GostCSVService;
import csvServices.RezervacijaCSVService;
import csvServices.SobaCSVService;
import csvServices.SobaricaCSVService;
import csvServices.TimeBoundPriceListCSVService;
import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import enumeratori.Uloga;
import interfejsi.GlumiGosta;
import interfejsi.GlumiRecepcionera;
import interfejsi.OsobljeZaCiscenje;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public class Administrator extends Employee implements OsobljeZaCiscenje, GlumiRecepcionera, GlumiGosta {
	private List<Soba> sobe_obaveze;
	private List<OsobljeZaCiscenje> osobljeZaCiscenje;
	private List<Rezervacija> ukupneRezervacije;
	private List<Rezervacija> licneRezervacije;
	private List<GlumiGosta> goste;
	private List<Employee> zaposlene;
	private List<LocalDate> cleaningDates;
	private List<Soba> sobe;
	private int totalCleanedRoomsCounter;
	private RacunHotela hotelAccount;

	// Add admin to CSV files
    private EmployeeCSVService employeeCSVService;
    private GostCSVService gostCSVService;
    private SobaricaCSVService sobaricaCSVService;
    private RezervacijaCSVService rezervacijaCSVService;
    private SobaCSVService sobaCSVService;
    private TimeBoundPriceListCSVService timeBoundPriceListCSVService;
	
	public Administrator()
	{
		employeeCSVService = new EmployeeCSVService();
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
	}
	
	public Administrator(String ime)
	{
		super(ime);
		employeeCSVService = new EmployeeCSVService();
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
	}
	
	// konstruktor Gosta: (GlumiGosta)
		public Administrator(String ime, // opsti deo employee
							 String prezime,
							 Pol pol,
							 LocalDate datum_rodjenja,
							 String broj_telefona,
							 String adresa,
							 String username,
							 String password,
							 LicniRacun racun
							 )
		{
			this.ime = ime;
			this.prezime = prezime;
			this.pol = pol;
			this.datum_rodjenja = datum_rodjenja;
			this.broj_telefona = broj_telefona;
			this.adresa = adresa;
			this.username = username;
			this.password = password;
			this.racun = racun;
			this.uloga = Uloga.ADMINISTRATOR;
			this.osobljeZaCiscenje = new ArrayList<>();
			if (!this.osobljeZaCiscenje.contains(this)) {
	            this.osobljeZaCiscenje.add(this);
	        }
			this.ukupneRezervacije = new ArrayList<>();
			this.licneRezervacije = new ArrayList<>();
			this.goste = new ArrayList<>();
			if (!this.goste.contains(this)) {
	            this.goste.add(this);
	        }
			this.sobe_obaveze = new ArrayList<>();
			this.zaposlene = new ArrayList<>();
			if (!this.zaposlene.contains(this)) {
	            this.zaposlene.add(this);
	        }
			this.cleaningDates = new ArrayList<>();
			this.sobe = new ArrayList<>();
			this.totalCleanedRoomsCounter = 0;
			this.hotelAccount = new RacunHotela(0.0);
			
			employeeCSVService = new EmployeeCSVService();
		    gostCSVService = new GostCSVService();
		    sobaricaCSVService = new SobaricaCSVService();
		    rezervacijaCSVService = new RezervacijaCSVService();
		    sobaCSVService = new SobaCSVService();
		    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
		}
	
	
	// konstruktor Sobarice: (OsobljeZaCiscenje)
	public Administrator(String ime, // opsti deo employee
						 String prezime,
						 Pol pol,
						 LocalDate datum_rodjenja,
						 String broj_telefona,
						 String adresa,
						 String username,
						 String password,
						 LicniRacun racun,
						 Obrazovanje obrazovanje,
						 String background,
						 double godine_iskustva,
						 TimeBoundPriceList timeBoundPriceList,
						 
						 int totalCleanedRoomsCounter, // deo sobarice
						 List<LocalDate> cleaningDates,
						 List<Soba> sobe_obaveze
						 )
	{
		super(ime,
			  prezime,
			  pol,
			  datum_rodjenja,
			  broj_telefona,
			  adresa,
			  username,
			  password,
			  racun,
			  Uloga.ADMINISTRATOR,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.osobljeZaCiscenje = new ArrayList<>();
		if (!this.osobljeZaCiscenje.contains(this)) {
            this.osobljeZaCiscenje.add(this);
        }
		this.ukupneRezervacije = new ArrayList<>();
		this.licneRezervacije = new ArrayList<>();
		this.goste = new ArrayList<>();
		if (!this.goste.contains(this)) {
            this.goste.add(this);
        }
		this.sobe_obaveze = sobe_obaveze;
		this.zaposlene = new ArrayList<>();
		if (!this.zaposlene.contains(this)) {
            this.zaposlene.add(this);
        }
		this.cleaningDates = cleaningDates;
		this.sobe = new ArrayList<>();
		this.totalCleanedRoomsCounter = totalCleanedRoomsCounter;
		this.hotelAccount = new RacunHotela(0.0);
		
		employeeCSVService = new EmployeeCSVService();
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
	}
	
	// "prazan" konstruktor, od nule
	public Administrator(String ime,
						 String prezime,
						 Pol pol,
						 LocalDate datum_rodjenja,
						 String broj_telefona,
						 String adresa,
						 String username,
						 String password,
						 LicniRacun racun,
						 Obrazovanje obrazovanje,
						 String background,
						 double godine_iskustva,
						 TimeBoundPriceList timeBoundPriceList
						 )
	{
		super(ime,
			  prezime,
			  pol,
			  datum_rodjenja,
			  broj_telefona,
			  adresa,
			  username,
			  password,
			  racun,
			  Uloga.ADMINISTRATOR,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.osobljeZaCiscenje = new ArrayList<>();
		if (!this.osobljeZaCiscenje.contains(this)) {
            this.osobljeZaCiscenje.add(this);
        }
		this.ukupneRezervacije = new ArrayList<>();
		this.licneRezervacije = new ArrayList<>();
		this.goste = new ArrayList<>();
		if (!this.goste.contains(this)) {
            this.goste.add(this);
        }
		this.sobe_obaveze = new ArrayList<>();
		this.zaposlene = new ArrayList<>();
		if (!this.zaposlene.contains(this)) {
            this.zaposlene.add(this);
        }
		this.cleaningDates = new ArrayList<>();
		this.sobe = new ArrayList<>();
		this.totalCleanedRoomsCounter = 0;
		this.hotelAccount = new RacunHotela(0.0);
		
		employeeCSVService = new EmployeeCSVService();
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
		
	}
	
	//konstruktor od postojecih podataka
	public Administrator(String ime, // opsti deo employee
						 String prezime,
						 Pol pol,
						 LocalDate datum_rodjenja,
						 String broj_telefona,
						 String adresa,
						 String username,
						 String password,
						 LicniRacun racun,
						 Obrazovanje obrazovanje,
						 String background,
						 double godine_iskustva,
						 TimeBoundPriceList timeBoundPriceList,
						 
						 List<Rezervacija> licneRezervacije, // deo gosta
						 
						 int totalCleanedRoomsCounter, // deo sobarice
						 List<LocalDate> cleaningDates,
						 List<Soba> sobe_obaveze,
						 
						 List<OsobljeZaCiscenje> osobljeZaCiscenje, // deo recepcionera
						 List<Rezervacija> ukupneRezervacije,
						 List<GlumiGosta> goste,
						 List<Soba> sobe,
						 
						 List<Employee> zaposlene, // deo administratora
						 RacunHotela hotelAccount,
						 
						 EmployeeCSVService employeeCSVService,
    					 GostCSVService gostCSVService,
					     SobaricaCSVService sobaricaCSVService,
					     RezervacijaCSVService rezervacijaCSVService,
					     SobaCSVService sobaCSVService,
					     TimeBoundPriceListCSVService timeBoundPriceListCSVService
						 )
	{
		super(ime,
			  prezime,
			  pol,
			  datum_rodjenja,
			  broj_telefona,
			  adresa,
			  username,
			  password,
			  racun,
			  Uloga.ADMINISTRATOR,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.licneRezervacije = licneRezervacije;
		
		this.totalCleanedRoomsCounter = totalCleanedRoomsCounter;
		this.cleaningDates = cleaningDates;
		this.sobe_obaveze = sobe_obaveze;
		
		this.osobljeZaCiscenje = osobljeZaCiscenje;
		if (!this.osobljeZaCiscenje.contains(this)) {
            this.osobljeZaCiscenje.add(this);
        }
		this.ukupneRezervacije = ukupneRezervacije;
		this.goste = goste;
		if (!this.goste.contains(this)) {
            this.goste.add(this);
        }
		this.sobe = sobe;
		
		this.zaposlene = zaposlene;
		if (!this.zaposlene.contains(this)) {
            this.zaposlene.add(this);
        }
		this.hotelAccount = hotelAccount;
		
		employeeCSVService = new EmployeeCSVService();
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	    timeBoundPriceListCSVService = new TimeBoundPriceListCSVService();
	}



	
	// funkcije GlumiRecepcionera:
	// funkcije za dodavanje sobarica/obaveza
	public List<OsobljeZaCiscenje> getOsobljeZaCiscenje() {
		return osobljeZaCiscenje;
	}
	
	public void setOsobljeZaCiscenje(List<OsobljeZaCiscenje> osobljeZaCiscenje) {
		this.osobljeZaCiscenje = osobljeZaCiscenje;
	}
	
	public OsobljeZaCiscenje pronadjiNajmanjeZauzetuSobaricu() {
        if (osobljeZaCiscenje.isEmpty()) {
            throw new IllegalArgumentException("The list of cleaning staff is empty.");
        }
        OsobljeZaCiscenje najmanjeZauzeta = osobljeZaCiscenje.get(0);
        for (OsobljeZaCiscenje osoblje : osobljeZaCiscenje) {
            if (osoblje.getSobeObaveze().size() < najmanjeZauzeta.getSobeObaveze().size()) {
                najmanjeZauzeta = osoblje;
            }
        }
        return najmanjeZauzeta;
    }

	public void dodeliSobu (Soba soba, OsobljeZaCiscenje najmanjeZauzeta)
	{
		najmanjeZauzeta.getSobeObaveze().add(soba);
		
		sobaricaCSVService.updateSobarica(najmanjeZauzeta);
	}
	
	
	// funkcije za kreiranje/promenu rezervacija
	public Rezervacija napraviRezervacijuNekome(GlumiGosta gost,
												Soba soba,
												LocalDate checkInDate,
												LocalDate checkOutDate,
												List<Amenities> amenities,
												Cenovnik cenovnik,
												RacunHotela hotelAccount
												) 
	{
		// Check if the room is available for the selected date range
        if (!rezervacijaCSVService.isRoomAvailable(soba, checkInDate, checkOutDate)) {
            throw new IllegalStateException("Soba nije dostupna za odabrani period!");
        }
        
		if (soba.getStanjeSobe() != StanjeSobe.AVAILABLE) {
	        throw new IllegalStateException("Soba nije dostupna za rezervaciju!");
	    }
		
		Rezervacija rezervacija = new Rezervacija(gost,
												  soba,
												  checkInDate,
												  checkOutDate,
												  StanjeRezervacije.PENDING,
												  amenities,
												  cenovnik
												  );
		
		// Add to CSV
        rezervacijaCSVService.addRezervacija(rezervacija);
        
        ukupneRezervacije.add(rezervacija);
        
        gost.getLicneRezervacije().add(rezervacija);
        
        //soba.setStanjeSobe(StanjeSobe.OCCUPIED); // soba postaje OCCUPIED samo posle check-in'a
        return rezervacija;
    }
	
	public void promeniStanjeRezervacije(Rezervacija rezervacija,
										 StanjeRezervacije novoStanje,
										 RacunHotela hotelAccount
										 ) 
	{
		if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.PAYED_FOR) {
            if (novoStanje == StanjeRezervacije.CANCELLED) {
            	double refundAmount = rezervacija.getTotalPrice();
            	hotelAccount.processRefund(refundAmount);
                rezervacija.getGost().getRacun().deposit(refundAmount);
            }
            rezervacija.setStanjeRezervacije(novoStanje);
            
            // Update in CSV
            rezervacijaCSVService.updateRezervacija(rezervacija);
            
        } else {
            throw new IllegalStateException("Rezervacija mora biti uplacena pre promene");
        }
    }
	
	public void checkIn(Rezervacija rezervacija) {
        LocalDate today = LocalDate.now();
        if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.CONFIRMED) {
            if (!today.isBefore(rezervacija.getCheckInDate()) && !today.isAfter(rezervacija.getCheckOutDate())) {
                rezervacija.setStanjeRezervacije(StanjeRezervacije.CHECKED_IN);
                rezervacija.getSoba().setStanjeSobe(StanjeSobe.OCCUPIED);
                
                // Update in CSV
                sobaCSVService.updateSoba(rezervacija.getSoba());
                rezervacijaCSVService.updateRezervacija(rezervacija);
                
                System.out.println("Uspesno ste zavrsili check-on gosta.");
            } else {
                throw new IllegalStateException("Check-in moze biti izvrsen samo na dan dolaska ili tokom boravka.");
            }
        } else {
            throw new IllegalStateException("Samo potvrdjene rezervacije mogu biti pretvorene u check-in.");
        }
    }

    public void checkOut(Rezervacija rezervacija) {
        LocalDate today = LocalDate.now();
        if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.CHECKED_IN) {
            if (!today.isBefore(rezervacija.getCheckInDate())) {
                rezervacija.setStanjeRezervacije(StanjeRezervacije.CHECKED_OUT);
                rezervacija.getSoba().setStanjeSobe(StanjeSobe.CLEANING);
                
                // Update in CSV
                sobaCSVService.updateSoba(rezervacija.getSoba());
                rezervacijaCSVService.updateRezervacija(rezervacija);
                
                OsobljeZaCiscenje najmanjeZauzeta = pronadjiNajmanjeZauzetuSobaricu();
                dodeliSobu(rezervacija.getSoba(), najmanjeZauzeta);
                
                System.out.println("Uspesno ste zavrsili check-out za gosta i dodelili sobu najmanje zauzetoj sobarici");
            } else {
                throw new IllegalStateException("Check-out ne moze biti izvrsen pre pocetka datuma check-in'a");
            }
        } else {
            throw new IllegalStateException("Check-out ne moze biti izvrsen pre check-in'a");
        }
    }
    
    public void checkForExpiredReservations() {
        LocalDate today = LocalDate.now();
        for (Rezervacija rezervacija : ukupneRezervacije) {
            if (rezervacija.getStanjeRezervacije() != StanjeRezervacije.CHECKED_OUT &&
                rezervacija.getStanjeRezervacije() != StanjeRezervacije.CANCELLED &&
                rezervacija.getStanjeRezervacije() != StanjeRezervacije.EXPIRED) {
                if (today.isAfter(rezervacija.getCheckOutDate())) {
                    rezervacija.setStanjeRezervacije(StanjeRezervacije.EXPIRED);
                    
                    rezervacijaCSVService.updateRezervacija(rezervacija);  // Update in CSV
                    
                    System.out.println("Rezervacija za " + rezervacija.getGost().getIme() + " je istekla.");
                }
            }
        }
    }
	
    public List<Rezervacija> getUkupneRezervacije()
	{
		return this.ukupneRezervacije;
	}
	
	public void setUkupneRezervacije(List<Rezervacija> ukupneRezervacije)
	{
		this.ukupneRezervacije = ukupneRezervacije;
	}
	
	public List<Rezervacija> filterAndSortAllReservations(LocalDate startDate,
											           LocalDate endDate,
											           List<Amenities> amenities,
											           TipSobe roomType,
											           StanjeRezervacije reservationStatus,
											           boolean sortByTotalPrice
											           )
    {
		List<Rezervacija> filteredReservations = new ArrayList<>();
		
		for (Rezervacija rezervacija : ukupneRezervacije) {
			boolean matchesTime = (startDate == null || !rezervacija.getCheckOutDate().isBefore(startDate)) &&
					(endDate == null || !rezervacija.getCheckInDate().isAfter(endDate));
			boolean matchesAmenities = (amenities == null || rezervacija.getAmenities().containsAll(amenities));
			boolean matchesRoomType = (roomType == null || rezervacija.getSoba().getTipSobe() == roomType);
			boolean matchesReservationStatus = (reservationStatus == null || rezervacija.getStanjeRezervacije() == reservationStatus);
		
			if (matchesTime && matchesAmenities && matchesRoomType && matchesReservationStatus) {
				filteredReservations.add(rezervacija);
			}
		}
		
		// sortiranje po ukupnoj ceni rezervacije
		if (sortByTotalPrice) {
			for (int i = 0; i < filteredReservations.size() - 1; i++) {
                for (int j = 0; j < filteredReservations.size() - i - 1; j++) {
                    if (filteredReservations.get(j).getTotalPrice() > filteredReservations.get(j + 1).getTotalPrice()) {
                        Rezervacija temp = filteredReservations.get(j);
                        filteredReservations.set(j, filteredReservations.get(j + 1));
                        filteredReservations.set(j + 1, temp);
                    }
                }
            }
		}

		return filteredReservations;
    }
	
	//funkcije za dodavanje gostija
	public List<GlumiGosta> getGoste() {
		return goste;
	}

	public void setGoste(List<GlumiGosta> goste) {
		this.goste = goste;
	}
	
	public void dodajGosta(GlumiGosta gost)
	{
		this.goste.add(gost);
		
		gostCSVService.addGost(gost);
	}
	
	//funkcije za sobe
  	public List<Soba> getSobe() {
  		return sobe;
  	}

  	public void setSobe(List<Soba> sobe) {
  		this.sobe = sobe;
  	}

  	public List<Soba> filterRooms(List<Soba> sobe,
    							  StanjeSobe status,
  								  TipSobe roomType,
  								  Double maxPricePerNight,
  								  Cenovnik cenovnik
  								  )
  	{
          List<Soba> filteredRooms = new ArrayList<>();

          for (Soba soba : sobe) {
              boolean matchesStatus = (status == null || soba.getStanjeSobe() == status);
              boolean matchesRoomType = (roomType == null || soba.getTipSobe() == roomType);
              double roomPrice = cenovnik.getRoomPrice(soba.getTipSobe());
              boolean matchesPrice = (maxPricePerNight == null || roomPrice <= maxPricePerNight);

              if (matchesStatus && matchesRoomType && matchesPrice) {
                  filteredRooms.add(soba);
              }
          }

          return filteredRooms;
    }
	
	
    
  	
	// funkcije OsobljeZaCiscenje:
  	public int getTotalCleanedRoomsCounter() {
		return this.totalCleanedRoomsCounter;
	}
  	
  	public void setTotalCleanedRoomsCounter(int totalCleanedRoomsCounter) {
		this.totalCleanedRoomsCounter = totalCleanedRoomsCounter;
	}
  	
  	//public int getTotalCleanedRoomsCounterForDateRange(LocalDate startDate, LocalDate endDate);
	
  	public void addCleaningEvent() { // razmatramo svako sredjiavnje kao "cleaning event" sa tekucim datumom
        this.cleaningDates.add(LocalDate.now());
        this.totalCleanedRoomsCounter++;
    }
  	
  	public List<LocalDate> getCleaningDates() {
		return this.cleaningDates;
	}

	public void setCleaningDates(List<LocalDate> cleaningDates) {
		this.cleaningDates = cleaningDates;
	}
  	
  	public List<Soba> getSobeObaveze() {
		return this.sobe_obaveze;
	}
	
  	public void setSobeObaveze(List<Soba> sobeObaveze) {
  	    if (sobeObaveze != null && !sobeObaveze.isEmpty()) {
  	        this.sobe_obaveze = new ArrayList<>(sobeObaveze);
  	    } else {
  	        System.err.println("Warning: Attempted to set an empty or null sobe_obaveze list.");
  	    }
  	}

    public void zavrsiSredjivanjeSobe(Soba soba) {
        if (soba.getStanjeSobe() == StanjeSobe.CLEANING) {
            soba.setStanjeSobe(StanjeSobe.AVAILABLE);
            this.sobe_obaveze.remove(soba);
            addCleaningEvent();
            
         // Update the room status in the CSV
            sobaCSVService.updateSoba(soba);

            // Update the administrator (acting as Sobarica) in the CSV
            sobaricaCSVService.updateSobarica(this);
            
            System.out.println("Soba je sredjena i sada je dostupna za rezervaciju.");
        } else {
            throw new IllegalStateException("Samo sobe u toku sredjivanja mogu biti obelezene kao slobodne.");
        }
    }
    
    
    
    
    // funkcije GlumiGosta:
	public List<Rezervacija> getLicneRezervacije()
	{
		return this.licneRezervacije;
	}
	
	public void setLicneRezervacije(List<Rezervacija> licneRezervacije) {
	    if (licneRezervacije != null) {
	        this.licneRezervacije = new ArrayList<>(licneRezervacije);
	    } else {
	        System.err.println("Warning: Attempted to set an empty or null licneRezervacije list.");
	    }
	}
	
	public double calculateTotalOwnSpendings()
    {
    	double totalOwnSpendings = 0.0;
    	for (Rezervacija rezervacija: licneRezervacije)
    	{
    		if (rezervacija.getStanjeRezervacije() != StanjeRezervacije.CANCELLED) {
                totalOwnSpendings += rezervacija.calculateTotalPrice();
            }
    	}
    	return totalOwnSpendings;
    }
	
	public void uplatiRezervaciju(Rezervacija rezervacija, RacunHotela hotelAccount) {
	    double discountedPrice = this.calculateDiscountedPrice(rezervacija);
	    
	    if (this.getRacun().getBalance() >= discountedPrice) {
	        racun.withdraw(discountedPrice);
	        hotelAccount.getHotelAccount().deposit(discountedPrice);
	        if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.PENDING) {
	            rezervacija.setStanjeRezervacije(StanjeRezervacije.PAYED_FOR);
	            rezervacija.setPaymentDate(LocalDate.now());
	            rezervacija.setTotalPrice(discountedPrice); // set nova cena sa popustom
	            // Update the reservation in the CSV using RezervacijaCSVService
                rezervacijaCSVService.updateRezervacija(rezervacija);
	        } else {
	            throw new IllegalStateException("Rezervacija ne moze biti obelezena kao uplacena");
	        }
	    } else {
	        throw new IllegalArgumentException("Nemate dovoljno novca na racunu za rezervaciju!");
	    }
	}
	
    public Rezervacija napraviRezervacijuSebi(Soba soba,
    									      LocalDate checkInDate,
    									      LocalDate checkOutDate,
    									      List<Amenities> amenities,
    									      Cenovnik cenovnik,
    									      List<Rezervacija> ukupneRezervacije
    									      ) 
    {
    	// Check if the room is available for the selected date range
        if (!rezervacijaCSVService.isRoomAvailable(soba, checkInDate, checkOutDate)) {
            throw new IllegalStateException("Soba nije dostupna za odabrani period!");
        }
    	
    	if (soba.getStanjeSobe() != StanjeSobe.AVAILABLE) {
            throw new IllegalStateException("Soba nije dostupna za rezervaciju!");
        }

    	Rezervacija rezervacija = new Rezervacija(this,
    											  soba,
    											  checkInDate,
    											  checkOutDate,
    											  StanjeRezervacije.PENDING,
    											  amenities,
    											  cenovnik
    											  );
    	
    	// Save the reservation in the CSV using RezervacijaService
        rezervacijaCSVService.addRezervacija(rezervacija);

        licneRezervacije.add(rezervacija);
        
        ukupneRezervacije.add(rezervacija); // ????? resiti kasnije

        //soba.setStanjeSobe(StanjeSobe.OCCUPIED); // soba postaje OCCUPIED samo posle check-in'a
        return rezervacija;
    }
    
	public boolean isEligibleForLoyaltyDiscount(Rezervacija rezervacija) {
        double totalSpendingsPastYear = 0.0;
        LocalDate oneYearAgo = LocalDate.now().minusYears(1); // ukupni troskovi zadnjih 365 dana

        for (Rezervacija pastRezervacija : licneRezervacije) {
            if (pastRezervacija.getStanjeRezervacije() != StanjeRezervacije.CANCELLED &&
                !pastRezervacija.getCheckOutDate().isBefore(oneYearAgo)) 
            {
                totalSpendingsPastYear += pastRezervacija.calculateTotalPrice();
            }
        }

        Cenovnik applicableCenovnik = rezervacija.getCenovnik();
        return totalSpendingsPastYear >= applicableCenovnik.getLoyaltyThreshold(); // vraca se true ukoliko su troskovi vise od threshold
    }
    
    public double calculateDiscountedPrice(Rezervacija rezervacija) {
        Cenovnik applicableCenovnik = rezervacija.getCenovnik();

        double originalPrice = rezervacija.calculateTotalPrice(); // racunamo originalnu cenu rezervacije

        if (isEligibleForLoyaltyDiscount(rezervacija)) {
            double discount = applicableCenovnik.getLoyaltyDiscount();
            return originalPrice * (1 - discount / 100);
        }
        return originalPrice;
    }
	
    public List<Rezervacija> filterAndSortPersonalReservations(LocalDate startDate,
											           LocalDate endDate,
											           List<Amenities> amenities,
											           TipSobe roomType,
											           StanjeRezervacije reservationStatus,
											           boolean sortByTotalPrice
											           ) 
    {
		List<Rezervacija> filteredReservations = new ArrayList<>();
		
		for (Rezervacija licnaRezervacija : licneRezervacije) {
			boolean matchesTime = (startDate == null || !licnaRezervacija.getCheckOutDate().isBefore(startDate)) &&
					(endDate == null || !licnaRezervacija.getCheckInDate().isAfter(endDate));
			boolean matchesAmenities = (amenities == null || licnaRezervacija.getAmenities().containsAll(amenities));
			boolean matchesRoomType = (roomType == null || licnaRezervacija.getSoba().getTipSobe() == roomType);
			boolean matchesReservationStatus = (reservationStatus == null || licnaRezervacija.getStanjeRezervacije() == reservationStatus);
		
			if (matchesTime && matchesAmenities && matchesRoomType && matchesReservationStatus) {
				filteredReservations.add(licnaRezervacija);
			}
		}
		
		// sortiranje po ukupnoj ceni rezervacije
		if (sortByTotalPrice) {
			for (int i = 0; i < filteredReservations.size() - 1; i++) {
                for (int j = 0; j < filteredReservations.size() - i - 1; j++) {
                    if (filteredReservations.get(j).getTotalPrice() > filteredReservations.get(j + 1).getTotalPrice()) {
                        Rezervacija temp = filteredReservations.get(j);
                        filteredReservations.set(j, filteredReservations.get(j + 1));
                        filteredReservations.set(j + 1, temp);
                    }
                }
            }
		}

		return filteredReservations;
    }
    
    
    
    
    // funkcije administratora:
 	// funkcija za kreiranje novog admina
 	public Administrator createNewAdmin(String ime, // opsti deo employee
 										String prezime,
 										Pol pol,
 										LocalDate datum_rodjenja,
 										String broj_telefona,
 									    String adresa,
 										String username,
 										String password,
 									    LicniRacun racun,
 										Obrazovanje obrazovanje,
 										String background,
 										double godine_iskustva,
 										TimeBoundPriceList cenovnik,
 										 
 										List<Rezervacija> licneRezervacije, // deo gosta
 										 
 										int totalCleanedRoomsCounter, // deo sobarice
 										List<LocalDate> cleaningDates,
 										List<Soba> sobe_obaveze,
 										 
 										List<OsobljeZaCiscenje> osobljeZaCiscenje, // deo recepcionera
 										List<Rezervacija> ukupneRezervacije,
 										List<GlumiGosta> goste,
 										List<Soba> sobe,
 										 
 										List<Employee> zaposlene, // deo administratora
 										RacunHotela hotelAccount,
 										
 										EmployeeCSVService employeeCSVService,
 				    					GostCSVService gostCSVService,
 									    SobaricaCSVService sobaricaCSVService,
 									    RezervacijaCSVService rezervacijaCSVService,
 									    SobaCSVService sobaCSVService,
 									    TimeBoundPriceListCSVService timeBoundPriceListCSVService
 	                                    ) 
 	{
 	    Administrator newAdmin = new Administrator(ime,
 	    										   prezime,
 	    										   pol,
 	    										   datum_rodjenja,
 	    										   broj_telefona,
 	    										   adresa,
 	    										   username,
 	    										   password,
 	    										   racun,
 	    										   obrazovanje,
 	    										   background,
 	    										   godine_iskustva,
 	    										   cenovnik,
 	    										   
 	    										   licneRezervacije,
 	    										   
 	    										   totalCleanedRoomsCounter,
 	    										   cleaningDates,
 	    										   sobe_obaveze, 
 	                                               
 	    										   osobljeZaCiscenje,
 	    										   ukupneRezervacije,
 	    										   goste,
 	                                               sobe,
 	                                               
 	                                               zaposlene,
 	                                               hotelAccount,
 	                                               
 	                                               employeeCSVService,
 	                        					   gostCSVService,
 	                    					       sobaricaCSVService,
 	                    					       rezervacijaCSVService,
 	                    					       sobaCSVService,
 	                    					       timeBoundPriceListCSVService
 	                                               
 	                                               );
 	    return newAdmin;
 	}

 	public List<Employee> getZaposlene() {
 		return zaposlene;
 	}

 	public void setZaposlene(List<Employee> zaposlene) {
 		this.zaposlene = zaposlene;
 	}
 	
 	public void dodajZaposlenog(Employee employee)
 	{
 		this.zaposlene.add(employee);
 		
 		employeeCSVService.addEmployee(employee);
 	}
    
    // monetarne funkcije
	public RacunHotela getHotelAccount() {
		return hotelAccount;
	}

	public void setHotelAccount(RacunHotela hotelAccount) {
		this.hotelAccount = hotelAccount;
	}
	
	public void payAllWages() {
        for (Employee employee : zaposlene) {
            double wage = employee.izracunajZaradu();
            hotelAccount.processWagePayment(wage);
            employee.getRacun().deposit(wage);
            
            // Reflect the change in the employee's account CSV
            employeeCSVService.updateEmployee(employee);
            
            System.out.println("Zarada " + wage + " je uplacena radniku " + employee.getIme());
        }
    }
	
	public double calculateRefundsForDateRange(LocalDate startDate, LocalDate endDate) {
        return hotelAccount.getTotalRefunds(startDate, endDate);
    }
	
	public double calculateTotalWagesForDateRange(LocalDate startDate, LocalDate endDate) {
        return hotelAccount.getTotalWages(startDate, endDate);
    }
	
	public double calculateTotalExpensesForDateRange(LocalDate startDate, LocalDate endDate) {
        double totalRefunds = hotelAccount.getTotalRefunds(startDate, endDate);
        double totalWages = calculateTotalWagesForDateRange(startDate, endDate);
        return totalRefunds + totalWages;
    }

    public void updateTimeBoundPriceList(LocalDate startDate,
		    							 double baseSalary,
		    							 double experienceBonus,
		                                 Map<Obrazovanje, Double> obrazovanjeKoeficijenti,
		                                 Map<TipSobe, Double> roomPrices,
		                                 Map<Amenities, Double> amenityPrices,
		                                 double loyaltyThreshold,
		                                 double loyaltyDiscount
		                                 ) 
    {
    	// Check if a Cenovnik already exists for the given start date
        Cenovnik existingCenovnik = timeBoundPriceList.getPriceList(startDate);

        if (existingCenovnik != null) {
            existingCenovnik.updateCenovnik(baseSalary,
            								experienceBonus,
            								obrazovanjeKoeficijenti,
            								roomPrices,
            								amenityPrices,
            								loyaltyThreshold,
            								loyaltyDiscount
            								);
            timeBoundPriceListCSVService.updateTimeBoundPriceList(startDate, existingCenovnik);

        } else {
            // If no Cenovnik exists for the start date, create and add a new one
            Cenovnik newCenovnik = new Cenovnik(baseSalary,
                                                experienceBonus,
                                                obrazovanjeKoeficijenti,
                                                roomPrices,
                                                amenityPrices,
                                                loyaltyThreshold,
                                                loyaltyDiscount);

            timeBoundPriceList.addPriceList(startDate, newCenovnik);

            timeBoundPriceListCSVService.addTimeBoundPriceList(startDate, newCenovnik);
        }
    }
    
    public double calculateIncomeForSateRange(LocalDate startDate, LocalDate endDate) {
        double totalIncome = 0.0;

        for (Rezervacija rezervacija : ukupneRezervacije) {
            if (rezervacija.getStanjeRezervacije() == StanjeRezervacije.PAYED_FOR &&
                !rezervacija.getPaymentDate().isBefore(startDate) &&
                !rezervacija.getPaymentDate().isAfter(endDate)) {
                totalIncome += rezervacija.getTotalPrice();
            }
        }

        return totalIncome;
    }
    
    @Override
    protected double izracunajZaradu() {
    	LocalDate today = LocalDate.now();
        double baseSalary = timeBoundPriceList.getTimeBoundBaseSalary(today);
        double experienceBonus = timeBoundPriceList.getTimeBoundExperienceBonus(today) * this.getGodineIskustva();
        double koeficijentObrazovanja = timeBoundPriceList.getTimeBoundObrazovanjeKoeficijenti(this.getObrazovanje(), today);
        return (baseSalary + experienceBonus) * koeficijentObrazovanja;
    }
 
 	// metode za statistiku
 	// 1. prikazi prihod po tipu sobe u opsegu datuma
 	public Map<TipSobe, Double> calculateIncomeByRoomTypeForDateRange(LocalDate startDate, LocalDate endDate) {
        Map<TipSobe, Double> incomeByRoomType = new HashMap<>();

        for (Rezervacija rezervacija : ukupneRezervacije) {
            // filtracija po datumu
            boolean isWithinDateRange = (startDate == null || !rezervacija.getCheckOutDate().isBefore(startDate)) &&
                                        (endDate == null || !rezervacija.getCheckInDate().isAfter(endDate));

            if (isWithinDateRange && rezervacija.getStanjeRezervacije() == StanjeRezervacije.CONFIRMED) {
                TipSobe roomType = rezervacija.getSoba().getTipSobe();
                double totalPrice = rezervacija.getTotalPrice();

                // zbir prihoda po tipu sobe
                incomeByRoomType.put(roomType, incomeByRoomType.getOrDefault(roomType, 0.0) + totalPrice);
            }
        }

        return incomeByRoomType;
    }
 	
 	// 2. prikazi odnos confirmed/cancelled rezervacija u opsegu datuma
 	public Map<StanjeRezervacije, Integer> countReservationsByStatusForDateRange(LocalDate startDate, LocalDate endDate) {
         Map<StanjeRezervacije, Integer> reservationCount = new HashMap<>();

         for (Rezervacija rezervacija : ukupneRezervacije) {
             boolean isWithinDateRange = (startDate == null || !rezervacija.getCheckOutDate().isBefore(startDate)) &&
                                         (endDate == null || !rezervacija.getCheckInDate().isAfter(endDate));

             if (isWithinDateRange) {
                 StanjeRezervacije stanje = rezervacija.getStanjeRezervacije();
                 reservationCount.put(stanje, reservationCount.getOrDefault(stanje, 0) + 1);
             }
         }

         return reservationCount;
    }
 	
 	//3. prikazi Opterećenje sobarica u opsegu datuma
 	public int getTotalCleanedRoomsCounterForDateRange(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        for (LocalDate date : this.cleaningDates) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                count++;
            }
        }
        return count;
    }
 	
 	//4. prikazi Generalni grafik za prihode/rashode u opsegu datuma
 	public NavigableMap<LocalDate, Double> calculateIncomeAndExpensesForDateRange(LocalDate startDate, LocalDate endDate) {
        NavigableMap<LocalDate, Double> balanceByDate = new TreeMap<>();

        // inicijalizacija map'a za svaki dan
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            balanceByDate.put(currentDate, 0.0);
            currentDate = currentDate.plusDays(1);
        }

        // prihod potvrdjenih rezervacija
        for (Rezervacija rezervacija : ukupneRezervacije) {
            LocalDate paymentDate = rezervacija.getPaymentDate();
            if (paymentDate != null && !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate)) {
                balanceByDate.put(paymentDate, balanceByDate.get(paymentDate) + rezervacija.getTotalPrice());
            }
        }

        // oduzimanje ukupnih povracaja odbijenih rezervacija
        double totalRefunds = hotelAccount.getTotalRefunds(startDate, endDate);
        for (LocalDate date : balanceByDate.keySet()) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                balanceByDate.put(date, balanceByDate.get(date) - totalRefunds);
            }
        }

        // oduzimanje ukupnih uplata zarada
        double totalWages = hotelAccount.getTotalWages(startDate, endDate);
        for (LocalDate date : balanceByDate.keySet()) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                balanceByDate.put(date, balanceByDate.get(date) - totalWages);
            }
        }

        return balanceByDate;
    }
 	
 	//5. Prikazi odnos stanja rezervacija u opsegu datuma
    public Map<StanjeRezervacije, Integer> calculateStanjaRezervacijaForDateRange(LocalDate startDate, LocalDate endDate) {
        Map<StanjeRezervacije, Integer> stanjeCount = new HashMap<>();

        // inicijalizacija stanja za svako stanje
        for (StanjeRezervacije stanje : StanjeRezervacije.values()) {
            stanjeCount.put(stanje, 0);
        }

        for (Rezervacija rezervacija : ukupneRezervacije) {
            LocalDate checkInDate = rezervacija.getCheckInDate();
            if (!checkInDate.isBefore(startDate) && !checkInDate.isAfter(endDate)) {
                StanjeRezervacije stanje = rezervacija.getStanjeRezervacije();
                stanjeCount.put(stanje, stanjeCount.get(stanje) + 1);
            }
        }

        return stanjeCount;
    }
    
    @Override
    public String toString() {
        return "\n(" + "Ime: " + ime +
               "\n" + "Prezime: " + prezime +
               "\n" + "Pol: " + pol +
               "\n" + "Datum rođenja: " + datum_rodjenja +
               "\n" + "Telefon: " + broj_telefona +
               "\n" + "Adresa: " + adresa +
               "\n" + "Korisničko ime: " + username +
               "\n" + "Lozinka: " + password +
               "\n" + "Licni racun balance: " + racun.getBalance() +
               "\n" + "Obrazovanje: " + obrazovanje +
               "\n" + "Background: " + background +
               "\n" + "Zarada: " + zarada + 
               "\n" + "Godine iskustva: " + godine_iskustva +
               "\n" + "Broj osoblja za ciscenje: " + (osobljeZaCiscenje != null ? osobljeZaCiscenje.size() : 0) +
               "\n" + "Broj ukupnih rezervacija: " + (ukupneRezervacije != null ? ukupneRezervacije.size() : 0) +
               "\n" + "Broj licnih rezervacija: " + (licneRezervacije != null ? licneRezervacije.size() : 0) +
               "\n" + "Broj gostiju: " + (goste != null ? goste.size() : 0) +
               "\n" + "Broj obaveza: " + (sobe_obaveze != null ? sobe_obaveze.size() : 0) +
               "\n" + "Broj zaposlenih: " + (zaposlene != null ? zaposlene.size() : 0) +
               "\n" + "Broj soba: " + (sobe != null ? sobe.size() : 0) +
               ")";
    }
	
}
