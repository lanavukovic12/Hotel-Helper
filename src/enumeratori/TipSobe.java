package enumeratori;

public enum TipSobe {
	JEDNOKREVETNA(1, "Jednokrevetna Soba"),
	DVOKREVETNA_BRACNI(1, "Dvokrevetna Soba sa Bračnim Krevetom"),
	DVOKREVETNA_ODVOJENI(2, "Dvokrevetna Soba sa Odvojenim Krevetima"),
	TROKREVETNA_BRACNI(2, "Trokrevetna Soba sa jednim bracnim krevetom i jednim odvojenim krevetom"),
	TROKREVETNA_ODVOJENI(3, "Trokrevetna soba sa 3 odvojena kreveta");
	
	private final int brojKreveta;
	private final String opis; 
	
	private TipSobe(int brojKreveta, String opis)
	{
		this.brojKreveta = brojKreveta;
		this.opis = opis;
	}

	public int getBrojKreveta() {
		return brojKreveta;
	}
	
	public String getOpis() {
        return this.opis;
    }
	
	public String toString() {
        return this.opis;
    }
}
