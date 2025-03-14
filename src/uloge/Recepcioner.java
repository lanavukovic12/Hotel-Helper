package uloge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

public class Recepcioner extends Employee implements GlumiRecepcionera {
	private List<OsobljeZaCiscenje> osobljeZaCiscenje;
	private List<Rezervacija> ukupneRezervacije;
	private List<GlumiGosta> goste;
	private List<Soba> sobe;
	private GostCSVService gostCSVService;
	private SobaricaCSVService sobaricaCSVService;
	private RezervacijaCSVService rezervacijaCSVService;
	private SobaCSVService sobaCSVService;
	
	public Recepcioner()
	{
	    gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	}
	
	public Recepcioner(String ime)
	{
		super(ime);
		gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	}
	
	public Recepcioner(String ime,
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
			  Uloga.RECEPCIONER,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.osobljeZaCiscenje = new ArrayList<>();
		this.ukupneRezervacije = new ArrayList<>();
		this.goste = new ArrayList<>();
		this.sobe = new ArrayList<>();
		gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	}
	
	public Recepcioner(String ime,
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
					   
					   List<OsobljeZaCiscenje> osobljeZaCiscenje,
					   List<Rezervacija> ukupneRezervacije,
					   List<GlumiGosta> goste,
					   List<Soba> sobe
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
			  Uloga.RECEPCIONER,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.osobljeZaCiscenje = osobljeZaCiscenje;
		this.ukupneRezervacije = ukupneRezervacije;
		this.goste = goste;
		this.sobe = sobe;
		gostCSVService = new GostCSVService();
	    sobaricaCSVService = new SobaricaCSVService();
	    rezervacijaCSVService = new RezervacijaCSVService();
	    sobaCSVService = new SobaCSVService();
	}
	
	// funkcije za sobe
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

	// funkcije za goste
	public List<GlumiGosta> getGoste() {
		return goste;
	}

	public void setGoste(List<GlumiGosta> goste) {
		this.goste = goste;
	}

	public void dodajGosta(GlumiGosta gost)
	{
		this.goste.add(gost);
		
		// Add to CSV
	    gostCSVService.addGost(gost);
	}

	//funkcije za dodeljivanje obaveza
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
		
		// Update sobe_obaveze in CSV
	    sobaricaCSVService.updateSobarica(najmanjeZauzeta);
	}

	// funkcije za rezervaciju
	public List<Rezervacija> getUkupneRezervacije() {
        return ukupneRezervacije;
    }

	public void setUkupneRezervacije(List<Rezervacija> ukupneRezervacije)
	{
		this.ukupneRezervacije = ukupneRezervacije;
	}

	public Rezervacija napraviRezervacijuNekome(GlumiGosta gost,
												Soba soba,
												LocalDate checkInDate,
												LocalDate checkOutDate,
												List<Amenities> amenities,
												Cenovnik cenovnik,
												RacunHotela hotelAccount
												) 
	    {
		
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
		
		// Check if the room is available for the selected date range
        if (!rezervacijaCSVService.isRoomAvailable(soba, checkInDate, checkOutDate)) {
            throw new IllegalStateException("Soba nije dostupna za odabrani period!");
        }
        
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
                
                System.out.println("Uspesno ste zavrsili check-in gosta.");
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
                    
                    // Update in CSV
                    rezervacijaCSVService.updateRezervacija(rezervacija);
                    
                    System.out.println("Rezervacija za " + rezervacija.getGost().getIme() + " je istekla.");
                }
            }
        }
    }
	
	@Override
    protected double izracunajZaradu() {
		LocalDate today = LocalDate.now();
        double baseSalary = timeBoundPriceList.getTimeBoundBaseSalary(today);
        double experienceBonus = timeBoundPriceList.getTimeBoundExperienceBonus(today) * this.getGodineIskustva();
        double koeficijentObrazovanja = timeBoundPriceList.getTimeBoundObrazovanjeKoeficijenti(this.getObrazovanje(), today);
        return (baseSalary + experienceBonus) * koeficijentObrazovanja;
    }
	
	public List<Rezervacija> filterAndSortAllReservations(
	        LocalDate startDate,
	        LocalDate endDate,
	        List<Amenities> amenities,
	        TipSobe roomType,
	        StanjeRezervacije reservationStatus,
	        boolean sortByTotalPrice) {

	    List<Rezervacija> filteredReservations = new ArrayList<>();

	    for (Rezervacija rezervacija : ukupneRezervacije) {
	        boolean matchesTime = (startDate == null || !rezervacija.getCheckOutDate().isBefore(startDate)) &&
	                              (endDate == null || !rezervacija.getCheckInDate().isAfter(endDate));
	        boolean matchesAmenities = (amenities == null || amenities.isEmpty() || rezervacija.getAmenities().containsAll(amenities));
	        boolean matchesRoomType = (roomType == null || rezervacija.getSoba().getTipSobe() == roomType);
	        boolean matchesReservationStatus = (reservationStatus == null || rezervacija.getStanjeRezervacije() == reservationStatus);

	        if (matchesTime && matchesAmenities && matchesRoomType && matchesReservationStatus) {
	            filteredReservations.add(rezervacija);
	        }
	    }

	    // Sorting by total price if requested
	    if (sortByTotalPrice) {
	        filteredReservations.sort(Comparator.comparingDouble(Rezervacija::getTotalPrice));
	    }

	    return filteredReservations;
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
	           "\n" + "Broj gostiju: " + (goste != null ? goste.size() : 0) +
	           "\n" + "Broj soba: " + (sobe != null ? sobe.size() : 0) +
	           ")";
	}
}
