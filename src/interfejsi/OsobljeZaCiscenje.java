package interfejsi;

import java.time.LocalDate;
import java.util.List;

import enumeratori.Uloga;
import uloge.Osoba;
import usluge.Soba;

public interface OsobljeZaCiscenje {
	public abstract int getTotalCleanedRoomsCounter();
	public abstract void setTotalCleanedRoomsCounter(int totalCleanedRoomsCounter);
	public abstract int getTotalCleanedRoomsCounterForDateRange(LocalDate startDate, LocalDate endDate);
	public abstract void addCleaningEvent();
	public abstract List<LocalDate> getCleaningDates();
	public abstract void setCleaningDates(List<LocalDate> cleaningDates);
	public abstract List<Soba> getSobeObaveze();
	public abstract void setSobeObaveze(List<Soba> sobe_obaveze);
	public abstract void zavrsiSredjivanjeSobe(Soba soba);
	default String getUsername() {
        if (this instanceof Osoba) {
            return ((Osoba) this).getUsername();
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
