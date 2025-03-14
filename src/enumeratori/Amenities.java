package enumeratori;

public enum Amenities {
	WIFI("Wifi password"),
	BREAKFAST("Breakfast service to your room"),
	PARKING("Parking spot price"),
	SPAPOOL("Access to SPA center and pool"),
	AIRCONDITIONING("A/C remote controller"),
	TV("TV remote controller"),
	MINIBAR("Mini-bar billing"),
	SMOKING("Smoking"),
	BALCONY("Balcony access");
	
	private final String opis;
	
	private Amenities(String opis)
	{
		this.opis = opis;
	}
	
	public String getOpis() {
        return this.opis;
    }
	
	public String toString() {
        return this.opis;
    }
	
}
