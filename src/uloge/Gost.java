package uloge;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import csvServices.GostCSVService;
import csvServices.RezervacijaCSVService;
import enumeratori.Amenities;
import enumeratori.Pol;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import enumeratori.Uloga;
import interfejsi.GlumiGosta;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public class Gost extends Osoba implements GlumiGosta {
	private List<Rezervacija> licneRezervacije;
	private GostCSVService gostCSVService;
    private RezervacijaCSVService rezervacijaCSVService;
	
    public Gost()
    {
    	this.rezervacijaCSVService = new RezervacijaCSVService();
		this.gostCSVService = new GostCSVService();
    }
    
	public Gost (String ime)
	{
		super(ime);
		this.rezervacijaCSVService = new RezervacijaCSVService();
		this.gostCSVService = new GostCSVService();
	}
	
	public Gost (String ime,
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
		super(ime,
			  prezime,
			  pol,
			  datum_rodjenja,
			  broj_telefona,
			  adresa,
			  username,
			  password,
			  racun,
			  Uloga.GOST
			  );
		this.licneRezervacije = new ArrayList<>();
		this.rezervacijaCSVService = new RezervacijaCSVService();
		this.gostCSVService = new GostCSVService();
	}
	
	public Gost (String ime,
				 String prezime,
				 Pol pol,
				 LocalDate datum_rodjenja,
				 String broj_telefona,
				 String adresa,
				 String username,
				 String password,
				 LicniRacun racun,
				 List<Rezervacija> licneRezervacije,
				 RezervacijaCSVService rezervacijaCSVService,
				 GostCSVService gostCSVService
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
			  Uloga.GOST
			  );
		this.licneRezervacije = licneRezervacije;
		this.rezervacijaCSVService = new RezervacijaCSVService();
		this.gostCSVService = new GostCSVService();
	}
	
	//funkcije za rezervaciju
	public List<Rezervacija> getLicneRezervacije() {
        return licneRezervacije;
    }
	
	public void setLicneRezervacije(List<Rezervacija> licneRezervacije)
	{
		this.licneRezervacije = licneRezervacije;
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
    
    public boolean isEligibleForLoyaltyDiscount(Rezervacija rezervacija) {
        double totalSpendingsPastYear = 0.0;
        LocalDate oneYearAgo = LocalDate.now().minusYears(1); // ukupni troskovi zadnjih 365 dana

        for (Rezervacija pastRezervacija : this.licneRezervacije) {
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
													           TipSobe tipSobe,
													           StanjeRezervacije stanjeRezervacije,
													           boolean sortByTotalPrice
													           ) 
    {
		List<Rezervacija> filteredReservations = new ArrayList<>();

		for (Rezervacija rezervacija : licneRezervacije) {
			boolean matchesTime = (startDate == null || !rezervacija.getCheckOutDate().isBefore(startDate)) &&
					(endDate == null || !rezervacija.getCheckInDate().isAfter(endDate));
			boolean matchesAmenities = (amenities == null || rezervacija.getAmenities().containsAll(amenities));
			boolean matchesRoomType = (tipSobe == null || rezervacija.getSoba().getTipSobe() == tipSobe);
			boolean matchesReservationStatus = (stanjeRezervacije == null || rezervacija.getStanjeRezervacije() == stanjeRezervacije);
		
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
    
    // Method to add a new guest to CSV
    public void addNewGostToCSV() {
        gostCSVService.addGost(this);
    }
    
	
	public String toString() {
        return "\n(" + "Ime: " + ime +
        		"\n" + "Prezime: " + prezime +
        		"\n" + "Pol: " + pol +
        		"\n" + "Datum rođenja: " + datum_rodjenja +
        		"\n" + "Telefon: " + broj_telefona +
        		"\n" + "Adresa: " + adresa +
        		"\n" + "Korisničko ime: " + username +
        		"\n" + "Lozinka: " + password +
        		"\n" + "Stanje na racunu: " + racun.getBalance() +
        		")";
	}
}
