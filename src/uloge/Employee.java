package uloge;

import java.time.LocalDate;

import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.Uloga;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.TimeBoundPriceList;

public abstract class Employee extends Osoba {
	// obrazovanje, background, salary, years_of_experience
	protected Obrazovanje obrazovanje;
	protected String background;
	protected double zarada;
	protected double godine_iskustva;
	protected TimeBoundPriceList timeBoundPriceList;
	
	public Employee()
	{
		
	}
	
	public Employee (String ime)
	{
		super(ime);
	}
	
	public Employee(String ime,
					String prezime,
					Pol pol,
					LocalDate datum_rodjenja,
					String broj_telefona,
					String adresa,
					String username,
					String password,
					LicniRacun racun,
					Uloga uloga,
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
			  uloga
			  );
		this.obrazovanje = obrazovanje;
		this.background = background;
		this.godine_iskustva = godine_iskustva;
		this.timeBoundPriceList = timeBoundPriceList;
		this.zarada = 0.0;
	}

	public Obrazovanje getObrazovanje() {
		return obrazovanje;
	}

	public void setObrazovanje(Obrazovanje obrazovanje) {
		this.obrazovanje = obrazovanje;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public double getZarada() {
		return zarada;
	}

	public void setZarada(double zarada) {
		this.zarada = zarada;
	}

	public double getGodineIskustva() {
		return godine_iskustva;
	}

	public void setGodineIskustva(double godine_iskustva) {
		this.godine_iskustva = godine_iskustva;
		this.zarada = izracunajZaradu(); //Preracunavanje zarade kada se menja iskustvo
	}
	
	public TimeBoundPriceList getTimeBoundPriceList() {
		return timeBoundPriceList;
	}

	public void setTimeBoundPriceList(TimeBoundPriceList timeBoundPriceList) {
		this.timeBoundPriceList = timeBoundPriceList;
	}

	protected abstract double izracunajZaradu();
	
	public abstract String toString();
}
