package uloge;

import java.time.LocalDate;

import enumeratori.Pol;
import enumeratori.Uloga;
import usluge.LicniRacun;

public abstract class Osoba{
	protected String ime;
	protected String prezime;
	protected Pol pol;
	protected LocalDate datum_rodjenja;
	protected String broj_telefona;
	protected String adresa;
	protected String username;
	protected String password;
	protected LicniRacun racun;
	protected Uloga uloga;
	
	public Osoba()
	{}
	
	public Osoba(String ime)
	{
		this.ime = ime;
	}
	
	public Osoba(String ime,
				 String prezime,
				 Pol pol,
				 LocalDate datum_rodjenja,
				 String broj_telefona,
				 String adresa,
				 String username,
				 String password,
				 LicniRacun racun,
				 Uloga uloga
				 )
	{
		this(ime);
		this.prezime = prezime;
		this.pol = pol;
		this.datum_rodjenja = datum_rodjenja;
		this.broj_telefona = broj_telefona;
		this.adresa = adresa;
		this.username = username;
		this.password = password;
		this.racun = racun;
		this.uloga = uloga;
	}
	
	public void setIme (String ime)
	{
		this.ime = ime;
	}
	
	public String getIme()
	{
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Pol getPol() {
		return pol;
	}

	public void setPol(Pol pol) {
		this.pol = pol;
	}

	public LocalDate getDatumRodjenja() {
		return datum_rodjenja;
	}

	public void setDatumRodjenja(LocalDate datum_rodjenja) {
		this.datum_rodjenja = datum_rodjenja;
	}

	public String getBrojTelefona() {
		return broj_telefona;
	}

	public void setBrojTelefona(String broj_telefona) {
		this.broj_telefona = broj_telefona;
	}

	public String getAdresa() {
		return adresa;
	}

	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public LicniRacun getRacun() {
		return racun;
	}

	public void setRacun(LicniRacun racun) {
		this.racun = racun;
	}

    public Uloga getUloga() {
		return uloga;
	}

	public void setUloga(Uloga uloga) {
		this.uloga = uloga;
	}
	
	// metoda za autentifikaciju
	public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
	
	//public abstract boolean equals(Object obj); // metoda nema implementacije, apstraktna metoda
	public abstract String toString();
	
}
