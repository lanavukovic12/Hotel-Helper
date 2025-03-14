package enumeratori;

public enum StanjeRezervacije {
	PENDING, //recepcioner mora da potvrdi rezervaciju
	PAYED_FOR,
    CONFIRMED,
    CANCELLED,
    CHECKED_IN,
    CHECKED_OUT,
    EXPIRED
}
