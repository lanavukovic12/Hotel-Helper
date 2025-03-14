package usluge;

import java.util.EnumMap;
import java.util.Map;

import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.TipSobe;

public class Cenovnik {
	private double baseSalary;
    private double experienceBonus;
    private Map<Obrazovanje, Double> obrazovanjeKoeficijenti;
    private Map<TipSobe, Double> roomPrices;
    private Map<Amenities, Double> amenityPrices;
    private double loyaltyThreshold;  // minimalna vrednost za sistem lojalnosti
    private double loyaltyDiscount;
    
    // base cenovnik konstruktor
    public Cenovnik(double baseSalary, double experienceBonus) {
        this.baseSalary = baseSalary;
        this.experienceBonus = experienceBonus;
        this.obrazovanjeKoeficijenti = new EnumMap<>(Obrazovanje.class);
        this.roomPrices = new EnumMap<>(TipSobe.class);
        this.amenityPrices = new EnumMap<>(Amenities.class);
        this.loyaltyThreshold = 50000.0;
        this.loyaltyDiscount = 10.0;

        // vrednosti map'a
        for (Obrazovanje obrazovanje : Obrazovanje.values()) {
            obrazovanjeKoeficijenti.put(obrazovanje, obrazovanje.koeficijent);
        }
        
        // cenovnik, koristeci map
        roomPrices.put(TipSobe.JEDNOKREVETNA, 500.0);
        roomPrices.put(TipSobe.DVOKREVETNA_BRACNI, 700.0);
        roomPrices.put(TipSobe.DVOKREVETNA_ODVOJENI, 750.0);
        roomPrices.put(TipSobe.TROKREVETNA_BRACNI, 900.0);
        roomPrices.put(TipSobe.TROKREVETNA_ODVOJENI, 950.0);

        amenityPrices.put(Amenities.WIFI, 100.0);
        amenityPrices.put(Amenities.BREAKFAST, 250.0);
        amenityPrices.put(Amenities.PARKING, 100.0);
        amenityPrices.put(Amenities.SPAPOOL, 200.0);
        amenityPrices.put(Amenities.AIRCONDITIONING, 100.0);
        amenityPrices.put(Amenities.TV, 100.0);
        amenityPrices.put(Amenities.MINIBAR, 150.0);
        amenityPrices.put(Amenities.SMOKING, 0.0);
        amenityPrices.put(Amenities.BALCONY, 50.0);
    }
    
    public Cenovnik (double baseSalary,
    				 double experienceBonus,
    				 Map<Obrazovanje, Double> obrazovanjeKoeficijenti,
    				 Map<TipSobe, Double> roomPrices,
    				 Map<Amenities, Double> amenityPrices,
    				 double loyaltyThreshold,
    				 double loyaltyDiscount
    		)
    {
    	this.baseSalary = baseSalary;
    	this.experienceBonus = experienceBonus;
    	this.obrazovanjeKoeficijenti = obrazovanjeKoeficijenti;
    	this.roomPrices = roomPrices;
    	this.amenityPrices = amenityPrices;
    	this.loyaltyThreshold = loyaltyThreshold;
    	this.loyaltyDiscount = loyaltyDiscount;
    }
    
    // metode za cenovnik zarade, bonusa, koeficijenata;
    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public double getExperienceBonus() {
        return experienceBonus;
    }

    public void setExperienceBonus(double experienceBonus) {
        this.experienceBonus = experienceBonus;
    }

    public double getObrazovanjeKoeficijenti(Obrazovanje obrazovanje) {
        return obrazovanjeKoeficijenti.getOrDefault(obrazovanje, 1.0); // vraca vrednost od kljuca "obrazovanje", u suprotnom slucaju - koeficijent je 1.0
    }

    public void setObrazovanjeKoeficijenti(Obrazovanje obrazovanje, double koeficijent) {
        obrazovanjeKoeficijenti.put(obrazovanje, koeficijent);
    }

    
    // metoda za cenovnik soba/amenities
    public double getRoomPrice(TipSobe roomType) {
        return roomPrices.getOrDefault(roomType, 0.0);
    }

    public void setRoomPrice(TipSobe roomType, double price) {
        roomPrices.put(roomType, price);
    }

    public double getAmenityPrice(Amenities amenity) {
        return amenityPrices.getOrDefault(amenity, 0.0);
    }

    public void setAmenityPrice(Amenities amenity, double price) {
        amenityPrices.put(amenity, price);
    }
    
    public double getLoyaltyDiscount() {
		return loyaltyDiscount;
	}

	public void setLoyaltyDiscount(double loyaltyDiscount) {
		this.loyaltyDiscount = loyaltyDiscount;
	}

	public double getLoyaltyThreshold() {
        return loyaltyThreshold;
    }

    public void setLoyaltyThreshold(double loyaltyThreshold) {
        this.loyaltyThreshold = loyaltyThreshold;
    }

    // metoda za obnavljanje svih cenovnih podataka
    public void updateCenovnik(double baseSalary,
    						 double experienceBonus,
    						 Map<Obrazovanje, Double> obrazovanjeKoeficijenti,
    						 Map<TipSobe, Double> roomPrices,
    						 Map<Amenities, Double> amenityPrices,
    						 double loyaltyThreshold,
    						 double loyaltyDiscount
    						 ) 
    {
        this.baseSalary = baseSalary;
        this.experienceBonus = experienceBonus;
        this.obrazovanjeKoeficijenti.putAll(obrazovanjeKoeficijenti);
        this.roomPrices.putAll(roomPrices);
        this.amenityPrices.putAll(amenityPrices);
        this.loyaltyThreshold = loyaltyThreshold;
        this.loyaltyDiscount = loyaltyDiscount;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cenovnik:\n");
        sb.append("Base Salary: ").append(baseSalary).append("\n");
        sb.append("Experience Bonus: ").append(experienceBonus).append("\n");
        sb.append("Loyalty Threshold: ").append(loyaltyThreshold).append("\n");
        sb.append("Loyalty Discount: ").append(loyaltyDiscount).append("%\n");

        sb.append("Obrazovanje Koeficijenti:\n");
        for (Map.Entry<Obrazovanje, Double> entry : obrazovanjeKoeficijenti.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append("Room Prices:\n");
        for (Map.Entry<TipSobe, Double> entry : roomPrices.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        sb.append("Amenity Prices:\n");
        for (Map.Entry<Amenities, Double> entry : amenityPrices.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}
