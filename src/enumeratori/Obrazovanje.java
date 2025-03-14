package enumeratori;

public enum Obrazovanje {
	PRIMARY_EDUCATION(0.6),      // Osnovno obrazovanje
    LOWER_SECONDARY_EDUCATION(0.8), // Niža srednja škola
    UPPER_SECONDARY_EDUCATION(1.0),   // Viša srednja škola
    BACHELORS_DEGREE(1.2),       // Diploma osnovnih akademskih studija
    MASTERS_DEGREE(1.5),         // Diploma master studija
    DOCTORAL_DEGREE(2.0);          // Doktorska diploma
	
	public final double koeficijent;

	private Obrazovanje(double koeficijent) {
		this.koeficijent = koeficijent;
	}
}
