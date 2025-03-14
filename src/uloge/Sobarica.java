package uloge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import csvServices.SobaCSVService;
import csvServices.SobaricaCSVService;
import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.StanjeSobe;
import enumeratori.Uloga;
import interfejsi.OsobljeZaCiscenje;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public class Sobarica extends Employee implements OsobljeZaCiscenje {
	private int totalCleanedRoomsCounter;
	private List<LocalDate> cleaningDates;
	private List<Soba> sobe_obaveze;
	
	// Service instances
    private SobaCSVService sobaCSVService;
    private SobaricaCSVService sobaricaCSVService;

	public Sobarica()
	{
		this.sobaCSVService = new SobaCSVService();
        this.sobaricaCSVService = new SobaricaCSVService();
	}
	
	public Sobarica(String ime)
	{
		super(ime);
		this.sobaCSVService = new SobaCSVService();
        this.sobaricaCSVService = new SobaricaCSVService();
	}
	
	public Sobarica(String ime,
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
			  Uloga.SOBARICA,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.sobe_obaveze = new ArrayList<>();
		this.cleaningDates = new ArrayList<>();
		this.totalCleanedRoomsCounter = 0;
		this.sobaCSVService = new SobaCSVService();
        this.sobaricaCSVService = new SobaricaCSVService();
	}
	
	public Sobarica(String ime, // opsti deo employee
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
			  Uloga.SOBARICA,
			  obrazovanje,
			  background,
			  godine_iskustva,
			  timeBoundPriceList
			  );
		this.sobe_obaveze = sobe_obaveze;
		this.cleaningDates = cleaningDates;
		this.totalCleanedRoomsCounter = totalCleanedRoomsCounter;
		this.sobaCSVService = new SobaCSVService();
        this.sobaricaCSVService = new SobaricaCSVService();
	}

	public List<Soba> getSobeObaveze() {
		return sobe_obaveze;
	}

	public void setSobeObaveze(List<Soba> sobe_obaveze) {
		this.sobe_obaveze = sobe_obaveze;
	}
	
	public int getTotalCleanedRoomsCounter() {
		return totalCleanedRoomsCounter;
	}

	public void setTotalCleanedRoomsCounter(int totalCleanedRoomsCounter) {
		this.totalCleanedRoomsCounter = totalCleanedRoomsCounter;
	}

	public List<LocalDate> getCleaningDates() {
		return cleaningDates;
	}

	public void setCleaningDates(List<LocalDate> cleaningDates) {
		this.cleaningDates = cleaningDates;
	}
	
	public SobaCSVService getSobaCSVService() {
		return sobaCSVService;
	}

	public void setSobaCSVService(SobaCSVService sobaCSVService) {
		this.sobaCSVService = sobaCSVService;
	}

	public SobaricaCSVService getSobaricaCSVService() {
		return sobaricaCSVService;
	}

	public void setSobaricaCSVService(SobaricaCSVService sobaricaCSVService) {
		this.sobaricaCSVService = sobaricaCSVService;
	}

	public void addCleaningEvent() { // razmatramo svako sredjiavnje kao "cleaning event" sa tekucim datumom
        cleaningDates.add(LocalDate.now());
        totalCleanedRoomsCounter++;
    }
	
	// metoda za kolicinu sredjenih soba u opsegu datuma
    public int getTotalCleanedRoomsCounterForDateRange(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        for (LocalDate date : cleaningDates) {
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                count++;
            }
        }
        return count;
    }

    public void zavrsiSredjivanjeSobe(Soba soba) {
        if (soba.getStanjeSobe() == StanjeSobe.CLEANING) {
            soba.setStanjeSobe(StanjeSobe.AVAILABLE);
            this.sobe_obaveze.remove(soba);
            addCleaningEvent();
            
            // Update the room status in the CSV
            sobaCSVService.updateSoba(soba);
            
            // Update the cleaning lady's data in the CSV
            sobaricaCSVService.updateSobarica(this);

            System.out.println("Soba je sredjena i sada je dostupna za rezervaciju.");
        } else {
            throw new IllegalStateException("Samo sobe u toku sredjivanja mogu biti obelezene kao slobodne.");
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
	           "\n" + "Broj trenutnih obaveza: " + (sobe_obaveze != null ? sobe_obaveze.size() : 0) +
	           "\n" + "Broj datuma sredjivanja soba: " + (cleaningDates != null ? cleaningDates.size() : 0) +
	           "\n" + "Ukupan broj sredjenih soba: " + totalCleanedRoomsCounter +
	           ")";
	}
	
}
