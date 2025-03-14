package usluge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import enumeratori.Amenities;
import enumeratori.StanjeRezervacije;
import enumeratori.TipSobe;
import interfejsi.GlumiGosta;
import uloge.Gost;
import uloge.Osoba;

public class Rezervacija {
	private GlumiGosta gost;
    private Soba soba;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private StanjeRezervacije stanjeRezervacije;
    private List<Amenities> amenities; // lista svih dodatnih usluga
    private double totalPrice; // ukupna cena rezervacije
    private Cenovnik cenovnik;
    private LocalDate paymentDate;
    
    public Rezervacija()
    {
    	
    }
    
    public Rezervacija(GlumiGosta gost,
    				   Soba soba,
    				   LocalDate checkInDate,
    				   LocalDate checkOutDate,
    				   StanjeRezervacije stanjeRezervacije,
    				   List<Amenities> amenities,
    				   Cenovnik cenovnik
    				   )
    {
    	this.gost = gost;
    	this.soba = soba;
    	this.checkInDate = checkInDate;
    	this.checkOutDate = checkOutDate;
    	this.stanjeRezervacije = stanjeRezervacije;
    	this.amenities = amenities;
    	this.cenovnik = cenovnik;
    	this.totalPrice = calculateTotalPrice();
    }
    
    public Rezervacija(GlumiGosta gost,
    				   Soba soba,
    				   LocalDate checkInDate,
    				   LocalDate checkOutDate,
    				   StanjeRezervacije stanjeRezervacije,
    				   List<Amenities> amenities,
    				   Cenovnik cenovnik,
    				   LocalDate paymentDate
    				   )
    {
    	this.gost = gost;
    	this.soba = soba;
    	this.checkInDate = checkInDate;
    	this.checkOutDate = checkOutDate;
    	this.stanjeRezervacije = stanjeRezervacije;
    	this.amenities = amenities;
    	this.cenovnik = cenovnik;
    	this.totalPrice = calculateTotalPrice();
    	this.paymentDate = paymentDate;
    }

	public GlumiGosta getGost() {
		return gost;
	}

	public void setGost(GlumiGosta gost) {
		this.gost = gost;
	}

	public Soba getSoba() {
		return soba;
	}

	public void setSoba(Soba soba) {
		this.soba = soba;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}

	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public StanjeRezervacije getStanjeRezervacije() {
		return stanjeRezervacije;
	}

	public void setStanjeRezervacije(StanjeRezervacije stanje) {
		this.stanjeRezervacije = stanje;
	}
    
	public List<Amenities> getAmenities() {
		return amenities;
	}

	public void setAmenities(List<Amenities> amenities) {
		this.amenities = amenities;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public Cenovnik getCenovnik() {
		return cenovnik;
	}

	public void setCenovnik(Cenovnik cenovnik) {
		this.cenovnik = cenovnik;
	}
	
	public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
	
	private double calculateRoomPrice() {
		LocalDate today = LocalDate.now();
		long daysStayed = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
        return this.cenovnik.getRoomPrice(soba.getTipSobe()) * daysStayed;
    }

	private double calculateAmenitiesPrice() {
		LocalDate today = LocalDate.now();
		long daysStayed = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
		double total = 0.0;
        for (Amenities amenity : amenities) {
        	total += cenovnik.getAmenityPrice(amenity) * daysStayed;
        }
        return total;
    }
	
	public double calculateTotalPrice()
	{
		double roomPrice = calculateRoomPrice();
        double amenitiesPrice = calculateAmenitiesPrice();
        return roomPrice + amenitiesPrice;
	}
	
	public String toString() {
        return "\n Rezervacija: {" +
        	   "\n" + "Gost username: " + gost.getUsername() +
        	   "\n" + "Soba: " + soba +
        	   "\n" + "Check In Date: " + checkInDate +
        	   "\n" + "Check Out Date: " + checkOutDate +
        	   "\n" + "Stanje Rezervacije: " + stanjeRezervacije +
        	   "\n" + "Amenities: " + amenities +
        	   "\n" + "Ukupna cena rezervacije: " + totalPrice +
        	   "\n" + "Datum uplate rezervacije: " + paymentDate +
        	   "}";
    }
}
