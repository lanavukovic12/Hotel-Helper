package interfejsi;

import java.time.LocalDate;
import java.util.List;

import enumeratori.Amenities;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import uloge.Administrator;
import uloge.Gost;
import uloge.Osoba;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public interface GlumiRecepcionera {
	// funkcije za dodavanje sobarica/obaveza
	public abstract List<OsobljeZaCiscenje> getOsobljeZaCiscenje();
	public abstract void setOsobljeZaCiscenje(List<OsobljeZaCiscenje> osobljeZaCiscenje);
	public abstract OsobljeZaCiscenje pronadjiNajmanjeZauzetuSobaricu();
	public abstract void dodeliSobu (Soba soba, OsobljeZaCiscenje najmanjeZauzeta);
	
	// funkcije za kreiranje/promenu rezervacija
	public abstract Rezervacija napraviRezervacijuNekome(GlumiGosta gost,
												Soba soba,
												LocalDate checkInDate,
												LocalDate checkOutDate,
												List<Amenities> amenities,
												Cenovnik cenovnik,
												RacunHotela hotelAccount
											   );
	public abstract void promeniStanjeRezervacije(Rezervacija rezervacija,
										 StanjeRezervacije stanje,
										 RacunHotela hotelAccount
										);
	public abstract void checkIn(Rezervacija rezervacija);
	public abstract void checkOut(Rezervacija rezervacija);
	public abstract void checkForExpiredReservations();
	public abstract List<Rezervacija> getUkupneRezervacije();
	public abstract void setUkupneRezervacije(List<Rezervacija> rezervacije);
	public List<Rezervacija> filterAndSortAllReservations(LocalDate startDate,
											           LocalDate endDate,
											           List<Amenities> amenities,
											           TipSobe roomType,
											           StanjeRezervacije stanjeRezervacije,
											           boolean sortByTotalPrice
											           );
	
	//funkcije za dodavanje gostija
	public abstract List<GlumiGosta> getGoste();
	public abstract void setGoste(List<GlumiGosta> goste);
	public abstract void dodajGosta(GlumiGosta gost);
	
	//funkcije za sobe
	public abstract List<Soba> getSobe();
	public abstract void setSobe(List<Soba> sobe);
	public abstract List<Soba> filterRooms(List<Soba> sobe,
										   StanjeSobe status,
										   TipSobe roomType,
										   Double maxPricePerNight,
										   Cenovnik cenovnik
										   );
	default String getIme() {
        if (this instanceof Osoba) {
            return ((Osoba) this).getIme();
        }
        return null;
    }
	default String getUsername() {
        if (this instanceof Osoba) {
            return ((Osoba) this).getUsername();
        }
        return null;
    }
	default LicniRacun getRacun() {
        if (this instanceof Osoba) {
            return ((Osoba) this).getRacun();
        }
        return null;
    }
}
