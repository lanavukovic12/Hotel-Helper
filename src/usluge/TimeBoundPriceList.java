package usluge;

import java.time.LocalDate;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.TipSobe;

public class TimeBoundPriceList {
	private NavigableMap<LocalDate, Cenovnik> timeBoundPriceLists;
	
	public TimeBoundPriceList()
	{
		this.timeBoundPriceLists = new TreeMap<>();
	}
	
	public TimeBoundPriceList(NavigableMap<LocalDate, Cenovnik> initialPriceLists)
	{
		this.timeBoundPriceLists = new TreeMap<>(initialPriceLists);
	}

	public NavigableMap<LocalDate, Cenovnik> getTimeBoundPriceLists() {
		return timeBoundPriceLists;
	}

	public void setTimeBoundPriceLists(NavigableMap<LocalDate, Cenovnik> timeBoundPriceLists) {
		this.timeBoundPriceLists = timeBoundPriceLists;
	}
	
	public void addPriceList(LocalDate startDate, Cenovnik priceList) {
        timeBoundPriceLists.put(startDate, priceList);
    }
	
	public Cenovnik getPriceList(LocalDate date) { // metoda koja vraca posledni vazeci cenovnik, uporedeci po datumu
        Map.Entry<LocalDate, Cenovnik> entry = timeBoundPriceLists.floorEntry(date);
        if (entry != null) {
            return entry.getValue();
        }
        throw new IllegalArgumentException("Za ovaj datum ne postoji cenovnik!");
    }
	
	public double getTimeBoundRoomPrice(TipSobe roomType, LocalDate date) {
        return getPriceList(date).getRoomPrice(roomType);
    }

    public double getTimeBoundAmenityPrice(Amenities amenity, LocalDate date) {
        return getPriceList(date).getAmenityPrice(amenity);
    }

    public double getTimeBoundBaseSalary(LocalDate date) {
        return getPriceList(date).getBaseSalary();
    }

    public double getTimeBoundExperienceBonus(LocalDate date) {
        return getPriceList(date).getExperienceBonus();
    }

    public double getTimeBoundObrazovanjeKoeficijenti(Obrazovanje obrazovanje, LocalDate date) {
        return getPriceList(date).getObrazovanjeKoeficijenti(obrazovanje);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeBoundPriceList: \n");

        for (Map.Entry<LocalDate, Cenovnik> entry : timeBoundPriceLists.entrySet()) {
            LocalDate startDate = entry.getKey();
            Cenovnik cenovnik = entry.getValue();
            sb.append("Start Date: ").append(startDate).append("\n");
            sb.append(cenovnik.toString()).append("\n");
        }

        return sb.toString();
    }
	
}
