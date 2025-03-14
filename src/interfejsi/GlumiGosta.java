package interfejsi;

import java.time.LocalDate;
import java.util.List;

import enumeratori.Amenities;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import enumeratori.Uloga;
import uloge.Osoba;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public interface GlumiGosta {
	public abstract Rezervacija napraviRezervacijuSebi(Soba soba,
											  LocalDate checkInDate,
											  LocalDate checkOutDate,
											  List<Amenities> amenities,
											  Cenovnik cenovnik,
											  List<Rezervacija> ukupneRezervacije
											  );
	public abstract double calculateTotalOwnSpendings();
	public abstract void uplatiRezervaciju(Rezervacija rezervacija, RacunHotela hotelAccount);
	public abstract boolean isEligibleForLoyaltyDiscount(Rezervacija rezervacija);
	public abstract double calculateDiscountedPrice(Rezervacija rezervacija);
	public abstract List<Rezervacija> getLicneRezervacije();
	public abstract void setLicneRezervacije(List<Rezervacija> rezervacije);
	public List<Rezervacija> filterAndSortPersonalReservations(LocalDate startDate,
													           LocalDate endDate,
													           List<Amenities> amenities,
													           TipSobe roomType,
													           StanjeRezervacije stanjeRezervacije,
													           boolean sortByTotalPrice
													           );
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
	default Uloga getUloga() {
        if (this instanceof Osoba) {
            return ((Osoba) this).getUloga();
        }
        return null;
    }
	
}
