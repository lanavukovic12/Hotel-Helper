package csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import enumeratori.Amenities;
import enumeratori.Obrazovanje;
import enumeratori.Pol;
import enumeratori.StanjeRezervacije;
import enumeratori.StanjeSobe;
import enumeratori.TipSobe;
import enumeratori.Uloga;
import interfejsi.GlumiGosta;
import interfejsi.OsobljeZaCiscenje;
import uloge.Administrator;
import uloge.Employee;
import uloge.Gost;
import uloge.Osoba;
import uloge.Recepcioner;
import uloge.Sobarica;
import usluge.Cenovnik;
import usluge.LicniRacun;
import usluge.RacunHotela;
import usluge.Rezervacija;
import usluge.Soba;
import usluge.TimeBoundPriceList;

public class CSVHandler {
	private String osobeFilePath = "src/csv/Osobe.csv";
	private String sobeFilePath = "src/csv/Sobe.csv";
	private String gosteFilePath = "src/csv/Goste.csv";
	private String employeesFilePath = "src/csv/Employees.csv";
	private String timeBoundPriceListFilePath = "src/csv/TimeBoundPriceList.csv";
	private String rezervacijeFilePath = "src/csv/Rezervacije.csv";
	private String osobljeZaCiscenjeFilePath = "src/csv/OsobljeZaCiscenje.csv";
	
	//0.0 Write Osobe Headers to CSV
	public void writeOsobeHeadersToCSV() {
	    File file = new File(osobeFilePath);

	    // Check if the file needs headers
	    boolean writeHeaders = !file.exists() || file.length() == 0; // Write headers if file doesn't exist or is empty

	    // Create the file if it doesn't exist
	    if (!file.exists()) {
	        try {
	            file.createNewFile(); // Ensure the file is created
	        } catch (IOException e) {
	            e.printStackTrace();
	            return; // Exit if the file could not be created
	        }
	    }

	    // Write the headers if necessary
	    if (writeHeaders) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
	            String header = "Ime," +
	                            "Prezime," +
	                            "Pol," +
	                            "DatumRodjenja," +
	                            "BrojTelefona," +
	                            "Adresa," +
	                            "Username," +
	                            "Password," +
	                            "Balance," +
	                            "Uloga," +
	                            "Obrazovanje," +
	                            "Background," +
	                            "GodineIskustva";
	            bw.write(header);
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	//0.1 Load Osobe from CSV
	public List<Osoba> loadOsobeFromCSV() {
	    List<Osoba> osobe = new ArrayList<>();
	    
	    File csvFile = new File(osobeFilePath);
	    if (!csvFile.exists()) {
	        System.out.println("File not found: " + osobeFilePath);
	        return osobe;
	    }

	    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        String line;

	        // Skip the header line
	        if ((line = br.readLine()) != null) {
	            // Proceed with reading the data after the header
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",");
	                if (data.length < 10) {
	                    System.out.println("Incomplete data: " + line);
	                    continue;
	                }

	                String ime = data[0];
	                String prezime = data[1];
	                Pol pol = Pol.valueOf(data[2]);
	                LocalDate datumRodjenja = LocalDate.parse(data[3]);
	                String brojTelefona = data[4];
	                String adresa = data[5];
	                String username = data[6];
	                String password = data[7];
	                LicniRacun racun = new LicniRacun(Double.parseDouble(data[8]));
	                Uloga uloga = Uloga.valueOf(data[9]);

	                Osoba osoba = null;

	                if (uloga == Uloga.GOST) {
	                    osoba = new Gost(ime,
	                                     prezime,
	                                     pol,
	                                     datumRodjenja,
	                                     brojTelefona,
	                                     adresa,
	                                     username,
	                                     password,
	                                     racun);
	                } else if (uloga == Uloga.ADMINISTRATOR ||
	                           uloga == Uloga.RECEPCIONER ||
	                           uloga == Uloga.SOBARICA) {

	                    if (data.length < 13) {
	                        System.out.println("Incomplete employee data: " + line);
	                        continue;
	                    }

	                    Obrazovanje obrazovanje = Obrazovanje.valueOf(data[10]);
	                    String background = data[11];
	                    double godineIskustva = Double.parseDouble(data[12]);
	                    TimeBoundPriceList timeBoundPriceList = loadTimeBoundPriceListsFromCSV(); // Assuming this method loads the time-bound price list correctly

	                    switch (uloga) {
	                        case ADMINISTRATOR:
	                            osoba = new Administrator(ime,
	                                                      prezime,
	                                                      pol,
	                                                      datumRodjenja,
	                                                      brojTelefona,
	                                                      adresa,
	                                                      username,
	                                                      password,
	                                                      racun,
	                                                      obrazovanje,
	                                                      background,
	                                                      godineIskustva,
	                                                      timeBoundPriceList);
	                            break;
	                        case RECEPCIONER:
	                            osoba = new Recepcioner(ime,
	                                                    prezime,
	                                                    pol,
	                                                    datumRodjenja,
	                                                    brojTelefona,
	                                                    adresa,
	                                                    username,
	                                                    password,
	                                                    racun,
	                                                    obrazovanje,
	                                                    background,
	                                                    godineIskustva,
	                                                    timeBoundPriceList);
	                            break;
	                        case SOBARICA:
	                            osoba = new Sobarica(ime,
	                                                 prezime,
	                                                 pol,
	                                                 datumRodjenja,
	                                                 brojTelefona,
	                                                 adresa,
	                                                 username,
	                                                 password,
	                                                 racun,
	                                                 obrazovanje,
	                                                 background,
	                                                 godineIskustva,
	                                                 timeBoundPriceList);
	                            break;
	                        default:
	                            throw new IllegalArgumentException("Unknown role: " + uloga);
	                    }
	                } else {
	                    throw new IllegalArgumentException("Unsupported role: " + uloga);
	                }

	                osobe.add(osoba);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	        System.out.println("Error in parsing data: " + e.getMessage());
	    }

	    return osobe;
	}
	
	//0.2 Write Osoba to CSV
	public void writeOsobaToCSV(Osoba osoba) {
	    File csvFile = new File(osobeFilePath);

	    // Check if the file exists
	    if (!csvFile.exists()) {
	        // If not, create the file and write headers
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobeFilePath, true))) {
	            writeOsobeHeadersToCSV(); // Write the headers
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Load existing entries
	    List<Osoba> osobe = loadOsobeFromCSV(); // Assuming this method doesn't throw IOException
	    boolean exists = false;

	    for (Osoba existingOsoba : osobe) {
	        if (existingOsoba.getUsername().equals(osoba.getUsername())) {
	            updateOsobaInCSV(osoba); // If exists, update it
	            exists = true;
	            break;
	        }
	    }

	    if (!exists) {
	        // If the entry does not exist, write a new entry
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobeFilePath, true))) {
	            StringBuilder sb = new StringBuilder();

	            sb.append(osoba.getIme()).append(",");
	            sb.append(osoba.getPrezime()).append(",");
	            sb.append(osoba.getPol()).append(",");
	            sb.append(osoba.getDatumRodjenja()).append(",");
	            sb.append(osoba.getBrojTelefona()).append(",");
	            sb.append(osoba.getAdresa()).append(",");
	            sb.append(osoba.getUsername()).append(",");
	            sb.append(osoba.getPassword()).append(",");
	            sb.append(osoba.getRacun().getBalance()).append(",");
	            sb.append(osoba.getUloga()).append(",");

	            // Check if the osoba is an Employee (Admin, Receptionist, etc.) and include extra fields if necessary
	            if (osoba instanceof Employee) {
	                Employee employee = (Employee) osoba;
	                sb.append(employee.getObrazovanje()).append(",");
	                sb.append(employee.getBackground()).append(",");
	                sb.append(employee.getGodineIskustva());
	            }

	            bw.write(sb.toString());
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	//0.3 Update Osoba in CSV
	public void updateOsobaInCSV(Osoba updatedOsoba) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(osobeFilePath))) {
	        String line;

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            String existingUsername = data[6]; // Assuming username is at index 6

	            if (existingUsername.equals(updatedOsoba.getUsername())) {
	                // Create the updated line for the CSV
	                StringBuilder updatedLine = new StringBuilder();

	                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	                // Common fields for all Osoba
	                updatedLine.append(updatedOsoba.getIme()).append(",")
	                           .append(updatedOsoba.getPrezime()).append(",")
	                           .append(updatedOsoba.getPol()).append(",")
	                           .append(updatedOsoba.getDatumRodjenja().format(dateFormatter)).append(",")
	                           .append(updatedOsoba.getBrojTelefona()).append(",")
	                           .append(updatedOsoba.getAdresa()).append(",")
	                           .append(updatedOsoba.getUsername()).append(",")
	                           .append(updatedOsoba.getPassword()).append(",")
	                           .append(updatedOsoba.getRacun().getBalance()).append(",")
	                           .append(updatedOsoba.getUloga());

	                // Additional fields for Employees
	                if (updatedOsoba instanceof Employee) {
	                    Employee employee = (Employee) updatedOsoba;
	                    updatedLine.append(",")
	                               .append(employee.getObrazovanje()).append(",")
	                               .append(employee.getBackground()).append(",")
	                               .append(employee.getGodineIskustva());
	                }

	                fileContent.add(updatedLine.toString());
	                isUpdated = true;
	            } else {
	                fileContent.add(line);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // If the record was found and updated, write the file content back to the CSV
	    if (isUpdated) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobeFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching Osoba found for the given username: " + updatedOsoba.getUsername());
	    }
	}
	

	//0.4 Delete Osoba from CSV
	public void deleteOsobaFromCSV(String username) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isDeleted = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(osobeFilePath))) {
	        String line;

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            String existingUsername = data[6]; // Assuming username is at index 6

	            if (existingUsername.equals(username)) {
	                // Skip this line to delete the Osoba entry
	                isDeleted = true;
	                continue;
	            }

	            fileContent.add(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // If the record was found and deleted, rewrite the file content
	    if (isDeleted) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobeFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching Osoba found for the given username: " + username);
	    }
	}
	
	
	
	//1.0 Write Sobe Headers to CSV
	public void writeSobaHeadersToCSV() {
	    File file = new File(sobeFilePath);
	    try {
	        // Check if the file exists, and create it if it doesn't
	        if (!file.exists()) {
	            boolean fileCreated = file.createNewFile(); // Attempt to create the file
	            
	            if (!fileCreated) {
	                System.out.println("Could not create the file: " + sobeFilePath);
	                return; // If the file wasn't created, exit the method
	            }
	        }
	        
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
	            String header = "BrojSobe," +
	                            "TipSobe," +
	                            "StanjeSobe";
	            bw.write(header);
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (IOException e) {
	        System.out.println("Error during file creation or writing headers: " + sobeFilePath);
	        e.printStackTrace();
	    }
	}
	
	//1.1 Load Sobe from CSV
	public List<Soba> loadSobeFromCSV() {
	    List<Soba> sobe = new ArrayList<>();
	    File file = new File(sobeFilePath);

	    if (!file.exists()) {
	        System.out.println("The file " + sobeFilePath + " does not exist.");
	        return sobe;
	    }

	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line;

	        // Skip the header line
	        if ((line = br.readLine()) != null) {
	            // Proceed with reading the data after the header
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",");
	                if (data.length < 3) {
	                    // Skip lines that don't have enough data
	                    System.out.println("Skipping malformed line: " + line);
	                    continue;
	                }

	                int brojSobe = Integer.parseInt(data[0]);
	                String tipSobeOpis = data[1]; // Get the description string from CSV
	                TipSobe tipSobe = findTipSobeByOpis(tipSobeOpis); // Find the enum by description
	                StanjeSobe stanje = StanjeSobe.valueOf(data[2]);

	                Soba soba = new Soba(brojSobe, tipSobe, stanje);
	                sobe.add(soba);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return sobe;
	}

	// 1.2 Write a new Soba to CSV
	public void writeSobaToCSV(Soba soba) {
	    File file = new File(sobeFilePath);
	    boolean isNewFile = !file.exists();

	    // Load existing Soba list to check for duplicates
	    List<Soba> existingSobe = loadSobeFromCSV();
	    boolean alreadyExists = false;

	    // Check if the Soba already exists by comparing the room number
	    for (Soba existingSoba : existingSobe) {
	        if (existingSoba.getBrojSobe() == soba.getBrojSobe()) {
	            updateSobaInCSV(soba); // Call update method if the room number is found
	            alreadyExists = true;
	            break;
	        }
	    }

	    if (!alreadyExists) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // 'true' to append if file exists
	            if (isNewFile) {
	                // Call the existing method to insert headers
	                writeSobaHeadersToCSV();
	            }

	            // Write the Soba data
	            bw.write(soba.getBrojSobe() + "," + soba.getTipSobe() + "," + soba.getStanjeSobe());
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	// 1.3 Update Soba in CSV
	public void updateSobaInCSV(Soba updatedSoba) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(sobeFilePath))) {
	        String line;

	        // Read the header line and add it to fileContent
	        String header = br.readLine();
	        if (header != null) {
	            fileContent.add(header);
	        }

	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            int existingBrojSobe = Integer.parseInt(data[0]); // Assuming brojSobe is at index 0

	            if (existingBrojSobe == updatedSoba.getBrojSobe()) {
	                // Create the updated line for the CSV
	                String updatedLine = updatedSoba.getBrojSobe() + "," +
	                                     updatedSoba.getTipSobe() + "," +
	                                     updatedSoba.getStanjeSobe();
	                fileContent.add(updatedLine);
	                isUpdated = true;
	            } else {
	                // Keep the line unchanged
	                fileContent.add(line);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // If the record was found and updated, write the file content back to the CSV
	    if (isUpdated) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sobeFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching Soba found for the given brojSobe: " + updatedSoba.getBrojSobe());
	    }
	}
	
	// 1.4 Delete Soba from CSV
	public void deleteSobaFromCSV(int brojSobe) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isDeleted = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(sobeFilePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            int existingBrojSobe = Integer.parseInt(data[0]); // Assuming brojSobe is at index 0

	            if (existingBrojSobe == brojSobe) {
	                // Skip this line to delete the Soba
	                isDeleted = true;
	                continue;
	            }

	            // If not deleting, add the line to the list
	            fileContent.add(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // If the record was found and deleted, rewrite the file content
	    if (isDeleted) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sobeFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching Soba found for the given brojSobe: " + brojSobe);
	    }
	}
	
	
	//2.0 Write TimeBoundPriceList headers to CSV
	public void writeTimeBoundPriceListHeadersToCSV() {
	    File file = new File(timeBoundPriceListFilePath);
	    if (!file.exists()) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
	            String header = 
	                "StartDate," +
	                "BaseSalary," +
	                "ExperienceBonus," +
	                "PrimaryEducation," +
	                "LowerSecondaryEducation," +
	                "UpperSecondaryEducation," +
	                "BachelorsDegree," +
	                "MastersDegree," +
	                "DoctoralDegree," +
	                "JEDNOKREVETNA," +
	                "DVOKREVETNA_BRACNI," +
	                "DVOKREVETNA_ODVOJENI," +
	                "TROKREVETNA_BRACNI," +
	                "TROKREVETNA_ODVOJENI," +
	                "WIFI," +
	                "BREAKFAST," +
	                "PARKING," +
	                "SPAPOOL," +
	                "AIRCONDITIONING," +
	                "TV," +
	                "MINIBAR," +
	                "SMOKING," +
	                "BALCONY," +
	                "LoyaltyThreshold," +
	                "LoyaltyDiscount";
	            bw.write(header);
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	// 2.1 Load TimeBoundPriceList from CSV
	public TimeBoundPriceList loadTimeBoundPriceListsFromCSV() {
	    NavigableMap<LocalDate, Cenovnik> priceLists = new TreeMap<>();
	    File file = new File(timeBoundPriceListFilePath);

	    if (!file.exists()) {
	        System.out.println("The file " + timeBoundPriceListFilePath + " does not exist.");
	        return new TimeBoundPriceList(priceLists); // Return an empty TimeBoundPriceList
	    }

	    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line;

	        // Skip the header line
	        if ((line = br.readLine()) != null) {
	            // Proceed with reading the data after the header
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",");

	                LocalDate startDate = LocalDate.parse(data[0]);
	                double baseSalary = Double.parseDouble(data[1]);
	                double experienceBonus = Double.parseDouble(data[2]);

	                Map<Obrazovanje, Double> obrazovanjeKoeficijenti = new EnumMap<>(Obrazovanje.class);
	                obrazovanjeKoeficijenti.put(Obrazovanje.PRIMARY_EDUCATION, Double.parseDouble(data[3]));
	                obrazovanjeKoeficijenti.put(Obrazovanje.LOWER_SECONDARY_EDUCATION, Double.parseDouble(data[4]));
	                obrazovanjeKoeficijenti.put(Obrazovanje.UPPER_SECONDARY_EDUCATION, Double.parseDouble(data[5]));
	                obrazovanjeKoeficijenti.put(Obrazovanje.BACHELORS_DEGREE, Double.parseDouble(data[6]));
	                obrazovanjeKoeficijenti.put(Obrazovanje.MASTERS_DEGREE, Double.parseDouble(data[7]));
	                obrazovanjeKoeficijenti.put(Obrazovanje.DOCTORAL_DEGREE, Double.parseDouble(data[8]));

	                Map<TipSobe, Double> roomPrices = new EnumMap<>(TipSobe.class);
	                roomPrices.put(TipSobe.JEDNOKREVETNA, Double.parseDouble(data[9]));
	                roomPrices.put(TipSobe.DVOKREVETNA_BRACNI, Double.parseDouble(data[10]));
	                roomPrices.put(TipSobe.DVOKREVETNA_ODVOJENI, Double.parseDouble(data[11]));
	                roomPrices.put(TipSobe.TROKREVETNA_BRACNI, Double.parseDouble(data[12]));
	                roomPrices.put(TipSobe.TROKREVETNA_ODVOJENI, Double.parseDouble(data[13]));

	                Map<Amenities, Double> amenityPrices = new EnumMap<>(Amenities.class);
	                amenityPrices.put(Amenities.WIFI, Double.parseDouble(data[14]));
	                amenityPrices.put(Amenities.BREAKFAST, Double.parseDouble(data[15]));
	                amenityPrices.put(Amenities.PARKING, Double.parseDouble(data[16]));
	                amenityPrices.put(Amenities.SPAPOOL, Double.parseDouble(data[17]));
	                amenityPrices.put(Amenities.AIRCONDITIONING, Double.parseDouble(data[18]));
	                amenityPrices.put(Amenities.TV, Double.parseDouble(data[19]));
	                amenityPrices.put(Amenities.MINIBAR, Double.parseDouble(data[20]));
	                amenityPrices.put(Amenities.SMOKING, Double.parseDouble(data[21]));
	                amenityPrices.put(Amenities.BALCONY, Double.parseDouble(data[22]));

	                double loyaltyThreshold = Double.parseDouble(data[23]);
	                double loyaltyDiscount = Double.parseDouble(data[24]);

	                Cenovnik cenovnik = new Cenovnik(baseSalary,
	                                                 experienceBonus,
	                                                 obrazovanjeKoeficijenti,
	                                                 roomPrices,
	                                                 amenityPrices,
	                                                 loyaltyThreshold,
	                                                 loyaltyDiscount
	                                                 );
	                priceLists.put(startDate, cenovnik);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (DateTimeParseException e) {
	        System.out.println("Error parsing date in the file: " + e.getMessage());
	        e.printStackTrace();
	    }

	    return new TimeBoundPriceList(priceLists);
	}
	
	//2.2 Write a new TimeBoundPriceList to CSV
	public void writeTimeBoundPriceListToCSV(LocalDate startDate, Cenovnik cenovnik) {
	    File file = new File(timeBoundPriceListFilePath);
	    boolean isNewFile = !file.exists();

	    // Load existing TimeBoundPriceLists to check for duplicates
	    TimeBoundPriceList existingPriceLists = loadTimeBoundPriceListsFromCSV();
	    boolean alreadyExists = existingPriceLists.getTimeBoundPriceLists().containsKey(startDate);

	    if (alreadyExists) {
	        // If the TimeBoundPriceList for the given startDate exists, update it instead
	        updateTimeBoundPriceListInCSV(startDate, cenovnik);
	    } else {
	        // If it's a new file, write headers first
	        if (isNewFile) {
	            writeTimeBoundPriceListHeadersToCSV();
	        }

	        // Append new data
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
	            StringBuilder sb = new StringBuilder();

	            // Append start date
	            sb.append(startDate).append(",");

	            // Append base salary and experience bonus
	            sb.append(cenovnik.getBaseSalary()).append(",");
	            sb.append(cenovnik.getExperienceBonus()).append(",");

	            // Append education coefficients
	            for (Obrazovanje obrazovanje : Obrazovanje.values()) {
	                sb.append(cenovnik.getObrazovanjeKoeficijenti(obrazovanje)).append(",");
	            }

	            // Append room prices
	            for (TipSobe tipSobe : TipSobe.values()) {
	                sb.append(cenovnik.getRoomPrice(tipSobe)).append(",");
	            }

	            // Append amenity prices
	            for (Amenities amenity : Amenities.values()) {
	                sb.append(cenovnik.getAmenityPrice(amenity)).append(",");
	            }

	            // Append loyalty threshold and discount
	            sb.append(cenovnik.getLoyaltyThreshold()).append(",");
	            sb.append(cenovnik.getLoyaltyDiscount());

	            // Write to file
	            bw.write(sb.toString());
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	//2.3 Update TimeBoundPriceLists in CSV 
	public void updateTimeBoundPriceListInCSV(LocalDate newStartDate, Cenovnik updatedCenovnik) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(timeBoundPriceListFilePath))) {
	        String line;
	        boolean isFirstLine = true; // Flag to skip the header
	        while ((line = br.readLine()) != null) {
	            if (isFirstLine) {
	                fileContent.add(line); // Add header line to fileContent
	                isFirstLine = false;
	                continue;
	            }
	            
	            String[] data = line.split(",");
	            LocalDate existingStartDate = LocalDate.parse(data[0]);

	            if (existingStartDate.equals(newStartDate)) {
	                StringBuilder sb = new StringBuilder();

	                sb.append(newStartDate).append(",");
	                sb.append(updatedCenovnik.getBaseSalary()).append(",");
	                sb.append(updatedCenovnik.getExperienceBonus()).append(",");

	                for (Obrazovanje obrazovanje : Obrazovanje.values()) {
	                    sb.append(updatedCenovnik.getObrazovanjeKoeficijenti(obrazovanje)).append(",");
	                }

	                for (TipSobe tipSobe : TipSobe.values()) {
	                    sb.append(updatedCenovnik.getRoomPrice(tipSobe)).append(",");
	                }

	                for (Amenities amenity : Amenities.values()) {
	                    sb.append(updatedCenovnik.getAmenityPrice(amenity)).append(",");
	                }

	                sb.append(updatedCenovnik.getLoyaltyThreshold()).append(",");
	                sb.append(updatedCenovnik.getLoyaltyDiscount());

	                fileContent.add(sb.toString());
	                isUpdated = true;
	            } else {
	                fileContent.add(line);
	            }
	        }
	    } catch (IOException | DateTimeParseException e) {
	        e.printStackTrace();
	    }

	    if (isUpdated) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(timeBoundPriceListFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching TimeBoundPriceList found for the given start date: " + newStartDate);
	    }
	}
	
	// 2.4 Delete TimeBoundPriceList from CSV
	public void deleteTimeBoundPriceListFromCSV(LocalDate startDate) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isDeleted = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(timeBoundPriceListFilePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            LocalDate existingStartDate = LocalDate.parse(data[0]); // Assuming startDate is at index 0

	            if (existingStartDate.equals(startDate)) {
	                // Skip this line to delete the TimeBoundPriceList entry
	                isDeleted = true;
	                continue;
	            }

	            // If not deleting, add the line to the list
	            fileContent.add(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // If the record was found and deleted, rewrite the file content
	    if (isDeleted) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(timeBoundPriceListFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching TimeBoundPriceList entry found for the given start date: " + startDate);
	    }
	}
	
	
	// 3.0 Write Goste headers to CSV
	public void writeGostHeadersToCSV() {
	    File file = new File(gosteFilePath);
	    try {
	        if (!file.exists()) {
	            boolean fileCreated = file.createNewFile();
	            if (!fileCreated) {
	                System.out.println("Could not create the file: " + gosteFilePath);
	                return;
	            }
	        }

	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
	            String header = "Ime," +
	                            "Prezime," +
	                            "Pol," +
	                            "DatumRodjenja," +
	                            "BrojTelefona," +
	                            "Adresa," +
	                            "Username," +
	                            "Password," +
	                            "StanjeNaRacunu," +
	                            "Uloga";
	            bw.write(header);
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } catch (IOException e) {
	        System.out.println("Error during file creation or writing headers: " + gosteFilePath);
	        e.printStackTrace();
	    }
	}
	
	// 3.1 Load Goste from CSV
	public List<GlumiGosta> loadGosteFromCSV() {
	    List<GlumiGosta> goste = new ArrayList<>();
	    File file = new File(gosteFilePath);

	    if (!file.exists()) {
	        System.out.println("The file " + gosteFilePath + " does not exist.");
	        return goste;
	    }

	    try (BufferedReader br = new BufferedReader(new FileReader(gosteFilePath))) {
	        String line;
	        
	        if ((line = br.readLine()) != null) {
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",");
	                
	                String ime = data[0];
	                String prezime = data[1];
	                Pol pol = Pol.valueOf(data[2]);
	                LocalDate datumRodjenja = LocalDate.parse(data[3]);
	                String brojTelefona = data[4];
	                String adresa = data[5];
	                String username = data[6];
	                String password = data[7];
	                LicniRacun racun = new LicniRacun(Double.parseDouble(data[8]));
	                Uloga uloga = Uloga.valueOf(data[9]);

	                GlumiGosta gost;

	                if (uloga == Uloga.ADMINISTRATOR) {
	                    gost = new Administrator(ime,
	                                             prezime,
	                                             pol,
	                                             datumRodjenja,
	                                             brojTelefona,
	                                             adresa,
	                                             username,
	                                             password,
	                                             racun
	                                             );
	                } else {
	                    gost = new Gost(ime,
	                                    prezime,
	                                    pol,
	                                    datumRodjenja,
	                                    brojTelefona,
	                                    adresa,
	                                    username,
	                                    password,
	                                    racun
	                                    );
	                }
	                
	                goste.add(gost);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return goste;
	}
    
    //3.2 Load a new Gost to CSV
	public void writeGostToCSV(GlumiGosta gost) {
	    File file = new File(gosteFilePath);
	    boolean isNewFile = !file.exists();

	    List<GlumiGosta> existingGoste = loadGosteFromCSV();
	    boolean alreadyExists = false;

	    for (GlumiGosta existingGost : existingGoste) {
	        if (existingGost.getUsername().equals(gost.getUsername())) {
	            updateGostInCSV(gost);
	            alreadyExists = true;
	            break;
	        }
	    }

	    if (!alreadyExists) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
	            if (isNewFile) {
	                writeGostHeadersToCSV();
	            }

	            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	            Osoba osoba = (Osoba) gost;
	            String ime = osoba.getIme();
	            String prezime = osoba.getPrezime();
	            Pol pol = osoba.getPol();
	            String datumRodjenja = osoba.getDatumRodjenja().format(dateFormatter);
	            String brojTelefona = osoba.getBrojTelefona();
	            String adresa = osoba.getAdresa();
	            String username = osoba.getUsername();
	            String password = osoba.getPassword();
	            double stanjeNaRacunu = osoba.getRacun().getBalance();
	            Uloga uloga = osoba.getUloga();

	            String line = ime + "," +
	                          prezime + "," +
	                          pol + "," +
	                          datumRodjenja + "," +
	                          brojTelefona + "," +
	                          adresa + "," +
	                          username + "," +
	                          password + "," +
	                          stanjeNaRacunu + "," +
	                          uloga;

	            bw.write(line);
	            bw.newLine();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
    
    //3.3 Update Gost in CSV
	public void updateGostInCSV(GlumiGosta updatedGost) {
	    List<String> fileContent = new ArrayList<>();
	    boolean isUpdated = false;

	    try (BufferedReader br = new BufferedReader(new FileReader(gosteFilePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] data = line.split(",");

	            String existingUsername = data[6]; // Assuming username is at index 6

	            if (existingUsername.equals(updatedGost.getUsername())) {
	                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                Osoba osoba = (Osoba) updatedGost;
	                
	                String updatedLine = osoba.getIme() + "," +
	                                     osoba.getPrezime() + "," +
	                                     osoba.getPol() + "," +
	                                     osoba.getDatumRodjenja().format(dateFormatter) + "," +
	                                     osoba.getBrojTelefona() + "," +
	                                     osoba.getAdresa() + "," +
	                                     osoba.getUsername() + "," +
	                                     osoba.getPassword() + "," +
	                                     osoba.getRacun().getBalance() + "," +
	                                     osoba.getUloga();
	                                     
	                fileContent.add(updatedLine);
	                isUpdated = true;
	            } else {
	                fileContent.add(line);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    if (isUpdated) {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(gosteFilePath))) {
	            for (String fileLine : fileContent) {
	                bw.write(fileLine);
	                bw.newLine();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    } else {
	        System.out.println("No matching Gost found for the given username: " + updatedGost.getUsername());
	    }
	}
    
    // 3.4 Delete Gost from CSV
    public void deleteGostFromCSV(String username) {
        List<String> fileContent = new ArrayList<>();
        boolean isDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(gosteFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[6]; // Assuming username is at index 6

                if (existingUsername.equals(username)) {
                    // Skip this line to delete the Gost entry
                    isDeleted = true;
                    continue;
                }

                // If not deleting, add the line to the list
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and deleted, rewrite the file content
        if (isDeleted) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(gosteFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching Gost found for the given username: " + username);
        }
    }
    
	
    // 4.0 Write Rezervacije headers to CSV
    public void writeRezervacijeHeadersToCSV() {
        File file = new File(rezervacijeFilePath);
        try {
            // Check if the file exists, and create it if it doesn't
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile(); // Attempt to create the file
                if (!fileCreated) {
                    System.out.println("Could not create the file: " + rezervacijeFilePath);
                    return; // If the file wasn't created, exit the method
                }
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                String header = "Username," +
                                "BrojSobe," +
                                "CheckInDate," +
                                "CheckOutDate," +
                                "StanjeRezervacije," +
                                "Amenities," +
                                "TotalPrice," +
                                "PaymentDate";
                bw.write(header);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error during file creation or writing headers: " + rezervacijeFilePath);
            e.printStackTrace();
        }
    }

	// 4.1 Load Rezervacije from CSV 
    public List<Rezervacija> loadRezervacijeFromCSV(List<GlumiGosta> goste, List<Soba> sobe, Cenovnik cenovnik) {
        List<Rezervacija> rezervacije = new ArrayList<>();
        File file = new File(rezervacijeFilePath);

        if (!file.exists()) {
            System.out.println("The file " + rezervacijeFilePath + " does not exist.");
            return rezervacije;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rezervacijeFilePath))) {
            String line;

            // Skip the header line
            if ((line = br.readLine()) != null) {
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    // Parse the data with null and empty checks
                    String username = data[0];
                    int brojSobe = Integer.parseInt(data[1]);
                    LocalDate checkInDate = LocalDate.parse(data[2]);
                    LocalDate checkOutDate = LocalDate.parse(data[3]);
                    StanjeRezervacije stanjeRezervacije = StanjeRezervacije.valueOf(data[4]);

                    // Handle empty amenities field
                    List<Amenities> amenities = new ArrayList<>();
                    if (!data[5].equals("\"\"")) {
                        String[] amenitiesData = data[5].split(";");
                        for (String amenity : amenitiesData) {
                            amenities.add(Amenities.valueOf(amenity));
                        }
                    }

                    double totalPrice = Double.parseDouble(data[6]);

                    // Handle empty payment date field
                    LocalDate paymentDate = null;
                    if (!data[7].equals("\"\"")) {
                        paymentDate = LocalDate.parse(data[7]);
                    }

                    GlumiGosta gost = null;
                    for (GlumiGosta tempGost : goste) {
                        if (tempGost.getUsername().equals(username)) {
                            gost = tempGost;
                            break;
                        }
                    }

                    Soba soba = null;
                    for (Soba tempSoba : sobe) {
                        if (tempSoba.getBrojSobe() == brojSobe) {
                            soba = tempSoba;
                            break;
                        }
                    }

                    if (gost == null || soba == null) {
                        System.out.println("Could not find matching guest or room for reservation: " + line);
                        continue;
                    }

                    Rezervacija rezervacija = new Rezervacija(
                        gost,
                        soba,
                        checkInDate,
                        checkOutDate,
                        stanjeRezervacije,
                        amenities,
                        cenovnik,
                        paymentDate
                    );

                    rezervacije.add(rezervacija);
                    gost.getLicneRezervacije().add(rezervacija);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rezervacije;
    }
    
    //4.2 Write a new Rezervacija to CSV
    public void writeRezervacijaToCSV(Rezervacija rezervacija) {
        File file = new File(rezervacijeFilePath);
        boolean isNewFile = !file.exists();

        // Load existing reservations to check for duplicates
        List<Rezervacija> existingRezervacije = loadRezervacijeFromCSV(
            loadGosteFromCSV(), // Load guests
            loadSobeFromCSV(), // Load rooms
            loadTimeBoundPriceListsFromCSV().getPriceList(LocalDate.now()) // Load current price list
        );
        boolean alreadyExists = false;

        for (Rezervacija existingRezervacija : existingRezervacije) {
            if (existingRezervacija.getGost().getUsername().equals(rezervacija.getGost().getUsername()) &&
                existingRezervacija.getSoba().getBrojSobe() == rezervacija.getSoba().getBrojSobe() &&
                existingRezervacija.getCheckInDate().equals(rezervacija.getCheckInDate())) 
            {
                // If exists, update the record
                updateRezervacijaInCSV(rezervacija);
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (isNewFile) {
                    // Call the existing method to insert headers
                    writeRezervacijeHeadersToCSV();
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String username = rezervacija.getGost().getUsername();
                int brojSobe = rezervacija.getSoba().getBrojSobe();
                String checkInDate = rezervacija.getCheckInDate().format(dateFormatter);
                String checkOutDate = rezervacija.getCheckOutDate().format(dateFormatter);
                String stanjeRezervacije = rezervacija.getStanjeRezervacije().name();

                // Handle empty amenities
                StringBuilder amenities = new StringBuilder();
                if (rezervacija.getAmenities().isEmpty()) {
                    amenities.append("\"\"");
                } else {
                    for (Amenities amenity : rezervacija.getAmenities()) {
                        if (amenities.length() > 0) {
                            amenities.append(";");
                        }
                        amenities.append(amenity.name());
                    }
                }

                double totalPrice = rezervacija.getTotalPrice();

                // Handle empty payment date
                String paymentDate = rezervacija.getPaymentDate() != null ? rezervacija.getPaymentDate().format(dateFormatter) : "\"\"";

                String line = username + "," +
                              brojSobe + "," +
                              checkInDate + "," +
                              checkOutDate + "," +
                              stanjeRezervacije + "," +
                              amenities + "," +
                              totalPrice + "," +
                              paymentDate;

                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //4.3 Update a Rezervacija in CSV
    public void updateRezervacijaInCSV(Rezervacija updatedRezervacija) {
        List<String> fileContent = new ArrayList<>();
        boolean isUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(rezervacijeFilePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    fileContent.add(line); // Keep the header line
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                String existingUsername = data[0];
                int existingBrojSobe = Integer.parseInt(data[1]);
                LocalDate existingCheckInDate = LocalDate.parse(data[2]);

                if (existingUsername.equals(updatedRezervacija.getGost().getUsername()) &&
                    existingBrojSobe == updatedRezervacija.getSoba().getBrojSobe() &&
                    existingCheckInDate.equals(updatedRezervacija.getCheckInDate())) 
                {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String username = updatedRezervacija.getGost().getUsername();
                    int brojSobe = updatedRezervacija.getSoba().getBrojSobe();
                    String checkInDate = updatedRezervacija.getCheckInDate().format(dateFormatter);
                    String checkOutDate = updatedRezervacija.getCheckOutDate().format(dateFormatter);
                    String stanjeRezervacije = updatedRezervacija.getStanjeRezervacije().name();

                    // Handle empty amenities
                    StringBuilder amenities = new StringBuilder();
                    if (updatedRezervacija.getAmenities().isEmpty()) {
                        amenities.append("\"\"");
                    } else {
                        for (Amenities amenity : updatedRezervacija.getAmenities()) {
                            if (amenities.length() > 0) {
                                amenities.append(";");
                            }
                            amenities.append(amenity.name());
                        }
                    }

                    double totalPrice = updatedRezervacija.getTotalPrice();

                    // Handle empty payment date
                    String paymentDate = updatedRezervacija.getPaymentDate() != null ? updatedRezervacija.getPaymentDate().format(dateFormatter) : "\"\"";

                    String updatedLine = username + "," +
                                         brojSobe + "," +
                                         checkInDate + "," +
                                         checkOutDate + "," +
                                         stanjeRezervacije + "," +
                                         amenities + "," +
                                         totalPrice + "," +
                                         paymentDate;

                    fileContent.add(updatedLine);
                    isUpdated = true;
                } else {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isUpdated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(rezervacijeFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching Rezervacija found for the given username, room number, and check-in date.");
        }
    }
    
    //4.4 Delete a Rezervacija from CSV
    public void deleteRezervacijaFromCSV(String username, int brojSobe, LocalDate checkInDate) {
        List<String> fileContent = new ArrayList<>();
        boolean isDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(rezervacijeFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[0]; // Assuming username is at index 0
                int existingBrojSobe = Integer.parseInt(data[1]); // Assuming brojSobe is at index 1
                LocalDate existingCheckInDate = LocalDate.parse(data[2]); // Assuming checkInDate is at index 2

                // Match the reservation by username, room number, and check-in date
                if (existingUsername.equals(username) && existingBrojSobe == brojSobe && existingCheckInDate.equals(checkInDate)) {
                    // Skip this line to delete the reservation
                    isDeleted = true;
                    continue;
                }

                // If not deleting, add the line to the list
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and deleted, rewrite the file content
        if (isDeleted) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(rezervacijeFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching Rezervacija found for the given username, room number, and check-in date.");
        }
    }
    
    //5.0 Write OsobljeZaCiscenje headers to CSV
    public void writeOsobljeZaCiscenjeHeadersToCSV() {
        File file = new File(osobljeZaCiscenjeFilePath);
        try {
            // Check if the file exists, and create it if it doesn't
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    System.out.println("Could not create the file: " + osobljeZaCiscenjeFilePath);
                    return;
                }
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                String header = "Ime," +
                                "Prezime," +
                                "Pol," +
                                "DatumRodjenja," +
                                "BrojTelefona," +
                                "Adresa," +
                                "Username," +
                                "Password," +
                                "Balance," +
                                "Uloga," + // Add the Uloga field here
                                "Obrazovanje," +
                                "Background," +
                                "GodineIskustva," +
                                "TotalCleanedRoomsCounter," +
                                "CleaningDates," +
                                "SobeObaveze";
                bw.write(header);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error during file creation or writing headers: " + osobljeZaCiscenjeFilePath);
            e.printStackTrace();
        }
    }
    
    //5.1 Load OsobljeZaCiscenje from CSV
    public List<OsobljeZaCiscenje> loadOsobljeZaCiscenjeFromCSV(List<Soba> sobe, TimeBoundPriceList cenovnik) {
        List<OsobljeZaCiscenje> osobljeZaCiscenje = new ArrayList<>();
        File file = new File(osobljeZaCiscenjeFilePath);

        if (!file.exists()) {
            System.out.println("The file " + osobljeZaCiscenjeFilePath + " does not exist.");
            return osobljeZaCiscenje;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(osobljeZaCiscenjeFilePath))) {
            String line;
            
            // Skip the header line
            if ((line = br.readLine()) != null) {
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    if (data.length < 16) { // Adjust the number based on the columns
                        System.out.println("Incomplete data: " + line);
                        continue;  // Skip this line if the data is incomplete
                    }

                    String ime = data[0];
                    String prezime = data[1];
                    Pol pol = Pol.valueOf(data[2]);
                    LocalDate datumRodjenja = LocalDate.parse(data[3]);
                    String brojTelefona = data[4];
                    String adresa = data[5];
                    String username = data[6];
                    String password = data[7];
                    double balance = Double.parseDouble(data[8]);
                    LicniRacun racun = new LicniRacun(balance);
                    Uloga uloga = Uloga.valueOf(data[9]); // Load the Uloga field here
                    Obrazovanje obrazovanje = Obrazovanje.valueOf(data[10]);
                    String background = data[11];
                    double godineIskustva = Double.parseDouble(data[12]);

                    int sobaricaTotalCleanedRoomsCounter = Integer.parseInt(data[13]);
                    List<LocalDate> sobaricaCleaningDates = data.length > 14 ? parseCleaningDates(data[14]) : new ArrayList<>();
                    List<Soba> sobeObaveze = data.length > 15 ? parseSobeObaveze(data[15], sobe) : new ArrayList<>();

                    OsobljeZaCiscenje osoblje;

                    if (uloga == Uloga.SOBARICA) {
                        osoblje = new Sobarica(ime,
                        					   prezime,
                        					   pol,
                        					   datumRodjenja,
                        					   brojTelefona,
                        					   adresa,
                        					   username,
                        					   password,
                                               racun,
                                               obrazovanje,
                                               background,
                                               godineIskustva,
                                               cenovnik,
                                               sobaricaTotalCleanedRoomsCounter,
                                               sobaricaCleaningDates,
                                               sobeObaveze
                                               );
                    } else if (uloga == Uloga.ADMINISTRATOR) {
                        osoblje = new Administrator(ime,
                        						    prezime,
                        						    pol,
                        						    datumRodjenja,
                        						    brojTelefona,
                        						    adresa,
                        						    username,
                        						    password,
                                                    racun,
                                                    obrazovanje,
                                                    background,
                                                    godineIskustva,
                                                    cenovnik,
                                                    sobaricaTotalCleanedRoomsCounter,
                                                    sobaricaCleaningDates,
                                                    sobeObaveze
                                                    );
                    } else {
                        continue;  // Skip unknown roles
                    }

                    osobljeZaCiscenje.add(osoblje);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return osobljeZaCiscenje;
    }

    //5.2 Write a new OsobljeZaCiscenje to CSV
    public void writeOsobljeZaCiscenjeToCSV(OsobljeZaCiscenje osobljeZaCiscenje) {
        File file = new File(osobljeZaCiscenjeFilePath);
        boolean isNewFile = !file.exists();

        // Load existing entries to check for duplicates
        List<OsobljeZaCiscenje> existingEntries = loadOsobljeZaCiscenjeFromCSV(loadSobeFromCSV(),
                                                                               loadTimeBoundPriceListsFromCSV());
        boolean alreadyExists = false;

        for (OsobljeZaCiscenje existingEntry : existingEntries) {
            if (existingEntry.getUsername().equals(osobljeZaCiscenje.getUsername())) {
                updateOsobljeZaCiscenjeInCSV(osobljeZaCiscenje); // Call the update method if it exists
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // 'true' to append to the file
                if (isNewFile) {
                    // Call the existing method to insert headers
                    writeOsobljeZaCiscenjeHeadersToCSV();
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String ime, prezime, datumRodjenja, brojTelefona, adresa, username, password, background;
                Pol pol;
                double balance, godineIskustva;
                Obrazovanje obrazovanje;
                int totalCleanedRoomsCounter;
                Uloga uloga;
                String cleaningDates = "", sobeObaveze = "";

                // Check if the object is a Sobarica or Administrator at the start
                if (osobljeZaCiscenje instanceof Sobarica) {
                    Sobarica sobarica = (Sobarica) osobljeZaCiscenje;
                    ime = sobarica.getIme();
                    prezime = sobarica.getPrezime();
                    pol = sobarica.getPol();
                    datumRodjenja = sobarica.getDatumRodjenja().format(dateFormatter);
                    brojTelefona = sobarica.getBrojTelefona();
                    adresa = sobarica.getAdresa();
                    username = sobarica.getUsername();
                    password = sobarica.getPassword();
                    balance = sobarica.getRacun().getBalance();
                    uloga = sobarica.getUloga();
                    obrazovanje = sobarica.getObrazovanje();
                    background = sobarica.getBackground();
                    godineIskustva = sobarica.getGodineIskustva();
                    totalCleanedRoomsCounter = sobarica.getTotalCleanedRoomsCounter();
                    cleaningDates = formatCleaningDates(sobarica.getCleaningDates());
                    sobeObaveze = formatSobeObaveze(sobarica.getSobeObaveze());
                } else if (osobljeZaCiscenje instanceof Administrator) {
                    Administrator admin = (Administrator) osobljeZaCiscenje;
                    ime = admin.getIme();
                    prezime = admin.getPrezime();
                    pol = admin.getPol();
                    datumRodjenja = admin.getDatumRodjenja().format(dateFormatter);
                    brojTelefona = admin.getBrojTelefona();
                    adresa = admin.getAdresa();
                    username = admin.getUsername();
                    password = admin.getPassword();
                    balance = admin.getRacun().getBalance();
                    uloga = admin.getUloga();
                    obrazovanje = admin.getObrazovanje();
                    background = admin.getBackground();
                    godineIskustva = admin.getGodineIskustva();
                    totalCleanedRoomsCounter = admin.getTotalCleanedRoomsCounter();
                    cleaningDates = formatCleaningDates(admin.getCleaningDates());
                    sobeObaveze = formatSobeObaveze(admin.getSobeObaveze());
                } else {
                    throw new IllegalArgumentException("Unsupported OsobljeZaCiscenje type.");
                }

                // Ensure all fields are non-null, and if empty, set to ""
                if (cleaningDates == null || cleaningDates.isEmpty()) {
                    cleaningDates = "\"\"";
                }
                if (sobeObaveze == null || sobeObaveze.isEmpty()) {
                    sobeObaveze = "\"\"";
                }

                // Create the CSV line for the cleaning staff
                String line = ime + "," +
                              prezime + "," +
                              pol + "," +
                              datumRodjenja + "," +
                              brojTelefona + "," +
                              adresa + "," +
                              username + "," +
                              password + "," +
                              balance + "," +
                              uloga + "," +
                              obrazovanje + "," +
                              background + "," +
                              godineIskustva + "," +
                              totalCleanedRoomsCounter + "," +
                              cleaningDates + "," +
                              sobeObaveze;

                // Write the line to the CSV
                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //5.3 Update OsobljeZaCiscenje in CSV
    public void updateOsobljeZaCiscenjeInCSV(OsobljeZaCiscenje updatedOsoblje) {
        List<String> fileContent = new ArrayList<>();
        boolean isUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(osobljeZaCiscenjeFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[6]; // Assuming username is at index 6

                if (existingUsername.equals(updatedOsoblje.getUsername())) {
                    String updatedLine = "";

                    if (updatedOsoblje instanceof Sobarica) {
                        Sobarica sobaricaObj = (Sobarica) updatedOsoblje;

                        // Create the updated line specific to Sobarica
                        updatedLine = sobaricaObj.getIme() + "," +
                                      sobaricaObj.getPrezime() + "," +
                                      sobaricaObj.getPol() + "," +
                                      sobaricaObj.getDatumRodjenja().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," +
                                      sobaricaObj.getBrojTelefona() + "," +
                                      sobaricaObj.getAdresa() + "," +
                                      sobaricaObj.getUsername() + "," +
                                      sobaricaObj.getPassword() + "," +
                                      sobaricaObj.getRacun().getBalance() + "," +
                                      sobaricaObj.getUloga() + "," +
                                      sobaricaObj.getObrazovanje() + "," +
                                      sobaricaObj.getBackground() + "," +
                                      sobaricaObj.getGodineIskustva() + "," +
                                      sobaricaObj.getTotalCleanedRoomsCounter() + "," +
                                      (formatCleaningDates(sobaricaObj.getCleaningDates()).isEmpty() ? "\"\"" : formatCleaningDates(sobaricaObj.getCleaningDates())) + "," +
                                      (formatSobeObaveze(sobaricaObj.getSobeObaveze()).isEmpty() ? "\"\"" : formatSobeObaveze(sobaricaObj.getSobeObaveze()));
                    } else if (updatedOsoblje instanceof Administrator) {
                        Administrator adminObj = (Administrator) updatedOsoblje;

                        // Create the updated line specific to Administrator acting as cleaning staff
                        updatedLine = adminObj.getIme() + "," +
                                      adminObj.getPrezime() + "," +
                                      adminObj.getPol() + "," +
                                      adminObj.getDatumRodjenja().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "," +
                                      adminObj.getBrojTelefona() + "," +
                                      adminObj.getAdresa() + "," +
                                      adminObj.getUsername() + "," +
                                      adminObj.getPassword() + "," +
                                      adminObj.getRacun().getBalance() + "," +
                                      adminObj.getUloga() + "," +
                                      adminObj.getObrazovanje() + "," +
                                      adminObj.getBackground() + "," +
                                      adminObj.getGodineIskustva() + "," +
                                      adminObj.getTotalCleanedRoomsCounter() + "," +
                                      (formatCleaningDates(adminObj.getCleaningDates()).isEmpty() ? "\"\"" : formatCleaningDates(adminObj.getCleaningDates())) + "," +
                                      (formatSobeObaveze(adminObj.getSobeObaveze()).isEmpty() ? "\"\"" : formatSobeObaveze(adminObj.getSobeObaveze()));
                    }

                    fileContent.add(updatedLine);
                    isUpdated = true;
                } else {
                    // Keep the line unchanged
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and updated, write the file content back to the CSV
        if (isUpdated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobljeZaCiscenjeFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching OsobljeZaCiscenje found for the given username: " + updatedOsoblje.getUsername());
        }
    }
    
    // 5.4 Delete OsobljeZaCiscenje from CSV
    public void deleteOsobljeZaCiscenjeFromCSV(String username) {
        List<String> fileContent = new ArrayList<>();
        boolean isDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(osobljeZaCiscenjeFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[6]; // Assuming username is at index 6

                if (existingUsername.equals(username)) {
                    // Skip this line to delete the OsobljeZaCiscenje entry
                    isDeleted = true;
                    continue;
                }

                // If not deleting, add the line to the list
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and deleted, rewrite the file content
        if (isDeleted) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(osobljeZaCiscenjeFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching OsobljeZaCiscenje found for the given username: " + username);
        }
    }
    
    //6.0 Write Employees headers to CSV
    public void writeEmployeesHeadersToCSV() {
        File file = new File(employeesFilePath);
        try {
            // Check if the file exists, and create it if it doesn't
            if (!file.exists()) {
                boolean fileCreated = file.createNewFile(); // Attempt to create the file
                if (!fileCreated) {
                    System.out.println("Could not create the file: " + employeesFilePath);
                    return; // If the file wasn't created, exit the method
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                String header = "Ime," +
                                "Prezime," +
                                "Pol," +
                                "DatumRodjenja," +
                                "BrojTelefona," +
                                "Adresa," +
                                "Username," +
                                "Password," +
                                "Balance," +
                                "Uloga," +
                                "Obrazovanje," +
                                "Background," +
                                "GodineIskustva";
                bw.write(header);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error during file creation or writing headers: " + employeesFilePath);
            e.printStackTrace();
        }
    }
    
	//6.1 Load Employees from CSV
    public List<Employee> loadEmployeesFromCSV(TimeBoundPriceList cenovnik) {
        List<Employee> employees = new ArrayList<>();
        File file = new File(employeesFilePath);

        if (!file.exists()) {
            System.out.println("The file " + employeesFilePath + " does not exist.");
            return employees;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(employeesFilePath))) {
            String line;
            
            // Skip the header line
            if ((line = br.readLine()) != null) {
                // Proceed with reading the data after the header
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");

                    String ime = data[0];
                    String prezime = data[1];
                    Pol pol = Pol.valueOf(data[2]);
                    LocalDate datum_rodjenja = LocalDate.parse(data[3]);
                    String broj_telefona = data[4];
                    String adresa = data[5];
                    String username = data[6];
                    String password = data[7];
                    LicniRacun racun = new LicniRacun(Double.parseDouble(data[8]));
                    Uloga uloga = Uloga.valueOf(data[9]);
                    Obrazovanje obrazovanje = Obrazovanje.valueOf(data[10]);
                    String background = data[11];
                    double godine_iskustva = Double.parseDouble(data[12]);

                    Employee employee = null;

                    switch (uloga) {
                        case ADMINISTRATOR:
                            employee = new Administrator(ime,
                                                         prezime,
                                                         pol,
                                                         datum_rodjenja,
                                                         broj_telefona,
                                                         adresa,
                                                         username,
                                                         password,
                                                         racun,
                                                         obrazovanje,
                                                         background,
                                                         godine_iskustva,
                                                         cenovnik
                            );
                            break;

                        case SOBARICA:
                            employee = new Sobarica(ime,
                                                    prezime,
                                                    pol,
                                                    datum_rodjenja,
                                                    broj_telefona,
                                                    adresa,
                                                    username,
                                                    password,
                                                    racun,
                                                    obrazovanje,
                                                    background,
                                                    godine_iskustva,
                                                    cenovnik
                            );
                            break;

                        case RECEPCIONER:
                            employee = new Recepcioner(ime,
                                                       prezime,
                                                       pol,
                                                       datum_rodjenja,
                                                       broj_telefona,
                                                       adresa,
                                                       username,
                                                       password,
                                                       racun,
                                                       obrazovanje,
                                                       background,
                                                       godine_iskustva,
                                                       cenovnik
                            );
                            break;

                        default:
                            throw new IllegalArgumentException("Unknown role: " + uloga);
                    }

                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }
    
    //6.2 Write an Employee to CSV
    public void writeEmployeeToCSV(Employee employee) {
        File file = new File(employeesFilePath);
        boolean isNewFile = !file.exists();

        // Load existing entries to check for duplicates
        List<Employee> existingEmployees = loadEmployeesFromCSV(null);
        boolean alreadyExists = false;

        for (Employee existingEmployee : existingEmployees) {
            if (existingEmployee.getUsername().equals(employee.getUsername())) {
                updateEmployeeInCSV(employee); // Call the update method if it exists
                alreadyExists = true;
                break;
            }
        }

        if (!alreadyExists) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // 'true' to append to the file
                if (isNewFile) {
                    // Call the existing method to insert headers
                    writeEmployeesHeadersToCSV();
                }

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Write the employee details to the CSV
                String line = employee.getIme() + "," +
                              employee.getPrezime() + "," +
                              employee.getPol() + "," +
                              employee.getDatumRodjenja().format(dateFormatter) + "," +
                              employee.getBrojTelefona() + "," +
                              employee.getAdresa() + "," +
                              employee.getUsername() + "," +
                              employee.getPassword() + "," +
                              employee.getRacun().getBalance() + "," +
                              employee.getUloga() + "," +
                              employee.getObrazovanje() + "," +
                              employee.getBackground() + "," +
                              employee.getGodineIskustva();

                // Write the line to the CSV
                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    //6.3 Update Employee in CSV
    public void updateEmployeeInCSV(Employee updatedEmployee) {
        List<String> fileContent = new ArrayList<>();
        boolean isUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(employeesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[6]; // Assuming username is at index 6

                if (existingUsername.equals(updatedEmployee.getUsername())) {
                    // Update the employee's record with the new data
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    String updatedLine = updatedEmployee.getIme() + "," +
                                         updatedEmployee.getPrezime() + "," +
                                         updatedEmployee.getPol() + "," +
                                         updatedEmployee.getDatumRodjenja().format(dateFormatter) + "," +
                                         updatedEmployee.getBrojTelefona() + "," +
                                         updatedEmployee.getAdresa() + "," +
                                         updatedEmployee.getUsername() + "," +
                                         updatedEmployee.getPassword() + "," +
                                         updatedEmployee.getRacun().getBalance() + "," +
                                         updatedEmployee.getUloga() + "," +
                                         updatedEmployee.getObrazovanje() + "," +
                                         updatedEmployee.getBackground() + "," +
                                         updatedEmployee.getGodineIskustva();

                    fileContent.add(updatedLine);
                    isUpdated = true;
                } else {
                    // Keep the line unchanged
                    fileContent.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and updated, write the file content back to the CSV
        if (isUpdated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(employeesFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching Employee found for the given username: " + updatedEmployee.getUsername());
        }
    }
    
    // 6.4 Delete an Employee from CSV
    public void deleteEmployeeFromCSV(String username) {
        List<String> fileContent = new ArrayList<>();
        boolean isDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(employeesFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                String existingUsername = data[6]; // Assuming username is at index 6

                if (existingUsername.equals(username)) {
                    // Skip this line to delete the Employee entry
                    isDeleted = true;
                    continue;
                }

                // If not deleting, add the line to the list
                fileContent.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If the record was found and deleted, rewrite the file content
        if (isDeleted) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(employeesFilePath))) {
                for (String fileLine : fileContent) {
                    bw.write(fileLine);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No matching Employee found for the given username: " + username);
        }
    }

	// Helper method to find TipSobe by its description
	public TipSobe findTipSobeByOpis(String opis) {
	    for (TipSobe tip : TipSobe.values()) {
	        if (tip.getOpis().equalsIgnoreCase(opis)) { // Compare descriptions
	            return tip;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for description: " + opis);
	}
	
	// Helper method to find Amenities by its description
	public Amenities findAmenitiesByOpis(String opis) {
	    for (Amenities amenity : Amenities.values()) {
	        if (amenity.getOpis().equalsIgnoreCase(opis)) { // Compare descriptions
	            return amenity;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for description: " + opis);
	}
	
	// Helper method to parse cleaningDates from a CSV field
	public List<LocalDate> parseCleaningDates(String cleaningDates) {
	    List<LocalDate> dates = new ArrayList<>();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    if (cleaningDates != null && !cleaningDates.trim().isEmpty() && !cleaningDates.equals("\"\"")) {
	        String[] dateStrings = cleaningDates.split(";");

	        for (String dateString : dateStrings) {
	            if (!dateString.trim().isEmpty()) {
	                try {
	                    LocalDate date = LocalDate.parse(dateString.trim(), formatter);
	                    dates.add(date);
	                } catch (DateTimeParseException e) {
	                    System.err.println("Invalid date format: " + dateString);
	                    // Handle the error or skip invalid date
	                }
	            }
	        }
	    }

	    return dates;
	}
    
	public List<Soba> parseSobeObaveze(String sobe_obaveze_brojevi, List<Soba> sobe) {
	    List<Soba> sobe_obaveze = new ArrayList<>();
	    
	    if (sobe_obaveze_brojevi != null && !sobe_obaveze_brojevi.trim().isEmpty() && !sobe_obaveze_brojevi.equals("\"\"")) {
	        String[] roomNumbers = sobe_obaveze_brojevi.split(";");
	        
	        for (String roomNumberString : roomNumbers) {
	            if (!roomNumberString.trim().isEmpty()) {
	                try {
	                    int brojSobe = Integer.parseInt(roomNumberString.trim());

	                    // Find the matching room in the list of all rooms
	                    for (Soba soba : sobe) {
	                        if (soba.getBrojSobe() == brojSobe) {
	                            sobe_obaveze.add(soba);
	                            break; // Stop searching once we find the room with the correct number
	                        }
	                    }
	                } catch (NumberFormatException e) {
	                    System.err.println("Invalid room number format: " + roomNumberString);
	                    // Handle the error or skip invalid room numbers
	                }
	            }
	        }
	    }

	    return sobe_obaveze;
	}
    
    // Helper method to format cleaningDates to a CSV field
    public String formatCleaningDates(List<LocalDate> cleaningDates) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cleaningDates.size(); i++) {
            sb.append(cleaningDates.get(i).toString());
            if (i < cleaningDates.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    // Helper method to format sobeObaveze to a CSV field
    public String formatSobeObaveze(List<Soba> sobeObaveze) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sobeObaveze.size(); i++) {
            sb.append(sobeObaveze.get(i).getBrojSobe());
            if (i < sobeObaveze.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }
    
    
    
    // inicijalizacija podataka
    public void initializeCSVData() {
        // Initialize Sobe
        List<Soba> sobe = Arrays.asList(
            new Soba(101, TipSobe.JEDNOKREVETNA, StanjeSobe.AVAILABLE),
            new Soba(102, TipSobe.DVOKREVETNA_BRACNI, StanjeSobe.AVAILABLE),
            new Soba(103, TipSobe.TROKREVETNA_ODVOJENI, StanjeSobe.CLEANING) // Room in "CLEANING" state
        );
        for (Soba soba : sobe) {
            writeSobaToCSV(soba);
        }
        
        // Initialize TimeBoundPriceList
        LocalDate datumVazenja = LocalDate.now();
        double baseSalary = 25000.0;
        double experienceBonus = 2500.0;
        Map<Obrazovanje, Double> obrazovanjeKoeficijenti = Map.of(
            Obrazovanje.PRIMARY_EDUCATION, 1.2,
            Obrazovanje.LOWER_SECONDARY_EDUCATION, 1.6,
            Obrazovanje.UPPER_SECONDARY_EDUCATION, 2.0,
            Obrazovanje.BACHELORS_DEGREE, 2.4,
            Obrazovanje.MASTERS_DEGREE, 3.0,
            Obrazovanje.DOCTORAL_DEGREE, 4.0
        );
        Map<TipSobe, Double> roomPrices = Map.of(
            TipSobe.JEDNOKREVETNA, 1000.0,
            TipSobe.DVOKREVETNA_BRACNI, 1400.0,
            TipSobe.DVOKREVETNA_ODVOJENI, 1500.0,
            TipSobe.TROKREVETNA_BRACNI, 1800.0,
            TipSobe.TROKREVETNA_ODVOJENI, 1900.0
        );
        Map<Amenities, Double> amenityPrices = Map.of(
            Amenities.WIFI, 200.0,
            Amenities.BREAKFAST, 500.0,
            Amenities.PARKING, 200.0,
            Amenities.SPAPOOL, 400.0,
            Amenities.AIRCONDITIONING, 200.0,
            Amenities.TV, 200.0,
            Amenities.MINIBAR, 300.0,
            Amenities.SMOKING, 0.0,
            Amenities.BALCONY, 100.0
        );
        
        double loyaltyThreshold = 100000.0;
        double loyaltyDiscount = 20.0;
        Cenovnik cenovnik = new Cenovnik(
            baseSalary,
            experienceBonus,
            obrazovanjeKoeficijenti,
            roomPrices,
            amenityPrices,
            loyaltyThreshold,
            loyaltyDiscount
        );
        writeTimeBoundPriceListToCSV(datumVazenja, cenovnik);
        
        // Initialize Goste
        List<GlumiGosta> goste = Arrays.asList(
            new Gost("Marko",
                     "Lukic",
                     Pol.MUSKI,
                     LocalDate.of(1990, 1, 1),
                     "0123456789",
                     "Novi Sad",
                     "broj_pasosa_1",
                     "password",
                     new LicniRacun(1000.0)),
            new Gost("Sara",
                     "Lukic",
                     Pol.ZENSKI,
                     LocalDate.of(1985, 5, 20),
                     "012345678910",
                     "Novi Sad",
                     "broj_pasosa_2",
                     "password",
                     new LicniRacun(1500.0))
        );
        for (GlumiGosta gost : goste) {
            writeGostToCSV(gost);
            writeOsobaToCSV((Osoba) gost); // Add to Osoba.csv
        }
        
     // Initialize Rezervacije
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);
        GlumiGosta gost = goste.get(0);
        Soba soba = sobe.get(0);
        List<Amenities> amenities = Arrays.asList(Amenities.WIFI, Amenities.BREAKFAST);
        Cenovnik cenovnik_rez = loadTimeBoundPriceListsFromCSV().getPriceList(checkInDate);
        LocalDate paymentDate = LocalDate.now();
        List<Rezervacija> rezervacije = Arrays.asList(
            new Rezervacija(gost,
            			    soba,
            			    checkInDate,
            			    checkOutDate,
            			    StanjeRezervacije.PAYED_FOR,
            			    amenities,
            			    cenovnik_rez, //poslednji vazeci cenovnik
            			    paymentDate
            			    )
        );
        for (Rezervacija rezervacija : rezervacije) {
            writeRezervacijaToCSV(rezervacija);
        }
        
        
        // Initialize Employees
        Administrator admin = new Administrator("Administrator",
                                                "1",
                                                Pol.MUSKI,
                                                LocalDate.of(1980, 3, 15),
                                                "1122334455",
                                                "Novi Sad",
                                                "admin",
                                                "adminpass",
                                                new LicniRacun(5000.0),
                                                Obrazovanje.BACHELORS_DEGREE,
                                                "background_text",
                                                10,
                                                loadTimeBoundPriceListsFromCSV()
                                                );

        Sobarica sobarica = new Sobarica("Sobarica",
                                         "1",
                                         Pol.ZENSKI,
                                         LocalDate.of(1988, 11, 30),
                                         "3344556677",
                                         "Novi Sad",
                                         "sobarica2",
                                         "cleanpass",
                                         new LicniRacun(1800.0),
                                         Obrazovanje.LOWER_SECONDARY_EDUCATION,
                                         "background_text",
                                         8,
                                         loadTimeBoundPriceListsFromCSV()
                                         );

        // Add the room in "CLEANING" state to Sobarica's sobe_obaveze
        admin.getSobeObaveze().add(sobe.get(2)); // Adding room 103

        List<Employee> employees = Arrays.asList(
            admin,
            new Recepcioner("Recepcioner",
                            "1",
                            Pol.ZENSKI,
                            LocalDate.of(1992, 7, 25),
                            "2233445566",
                            "Novi Sad",
                            "recepcioner1",
                            "recpass",
                            new LicniRacun(2000.0),
                            Obrazovanje.MASTERS_DEGREE,
                            "background_text",
                            5,
                            loadTimeBoundPriceListsFromCSV()
                            ),
            sobarica
        );
        for (Employee employee : employees) {
            writeEmployeeToCSV(employee);
            writeOsobaToCSV((Osoba) employee); // Add to Osoba.csv
            
            if (employee instanceof OsobljeZaCiscenje) {
                writeOsobljeZaCiscenjeToCSV((OsobljeZaCiscenje) employee);
            }
            
            if (employee instanceof GlumiGosta) {
                writeGostToCSV((GlumiGosta) employee);
            }
        }
        
        System.out.println("\nUspesno ste inicijalizovali pocetni niz podataka.");
    }
}
