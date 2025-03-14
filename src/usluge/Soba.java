package usluge;

import enumeratori.StanjeSobe;
import enumeratori.TipSobe;

public class Soba {
	private int brojSobe;
	private TipSobe tipSobe;
	private StanjeSobe stanje;
	
	public Soba()
	{
		
	}
	
	public Soba(TipSobe tipSobe)
	{
		this.tipSobe = tipSobe;
	}
	
	public Soba(int brojSobe, TipSobe tipSobe, StanjeSobe stanje)
	{
		this(tipSobe);
		this.brojSobe = brojSobe;
		this.stanje = stanje;
	}

	public int getBrojSobe() {
		return brojSobe;
	}

	public void setBrojSobe(int brojSobe) {
		this.brojSobe = brojSobe;
	}

	public TipSobe getTipSobe() {
		return tipSobe;
	}

	public void setTipSobe(TipSobe tipSobe) {
		this.tipSobe = tipSobe;
	}

	public StanjeSobe getStanjeSobe() {
		return stanje;
	}

	public void setStanjeSobe(StanjeSobe stanje) {
		this.stanje = stanje;
	}

	public String toString() {
        return "\n" + "Soba: {" + 
        	   "\n" + "Broj Sobe: " + this.brojSobe + 
        	   "\n" + "Tip sobe: " + this.tipSobe + 
        	   "\n" + "Kolicina kreveta: " + this.tipSobe.getBrojKreveta() +
        	   "\n" + "Stanje sobe: " + this.stanje +
        	   "}";
    }
	
}
