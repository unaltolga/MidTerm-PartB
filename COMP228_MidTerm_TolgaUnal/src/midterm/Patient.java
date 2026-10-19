package midterm;

/*
 * Single inheritance: Patient extends Person
 * Also implements Reportable.
 *
 * The constructor is private: patients are created only through registerPatient(...),
 * so every registration is validated and prints a success or failure message.
 */
public class Patient extends Person implements Reportable {

	public static final String DEFAULT_DEPARTMENT = "General Medicine";
	private static final int MIN_AGE = 0;
	private static final int MAX_AGE = 120;

	// static: shared by all Patient objects
	private static int patientCount = 0;

	// final: a patient number never changes after registration
	private final String patientNumber;
	private int age;
	private String medicalDepartment;
	private double consultationFee;
	private boolean consultationFeeAssigned;

	private Patient(String name, int age, String medicalDepartment, String phoneNumber) {
		// Checked inside super(...) so invalid data is rejected before any counter changes (see Person).
		super(checkPatientFields(name, age, medicalDepartment), phoneNumber);
		this.age = age;
		this.medicalDepartment = medicalDepartment.trim();
		patientCount++;
		this.patientNumber = "P" + (1000 + patientCount); // P1001, P1002, ...
	}

	// Overloaded: same name, three different parameter lists.
	public static Patient registerPatient(String name, int age) {
		return registerPatient(name, age, DEFAULT_DEPARTMENT);
	}

	public static Patient registerPatient(String name, int age, String department) {
		return registerPatient(name, age, department, null);
	}

	// Returns the new patient, or null when the data is invalid.
	public static Patient registerPatient(String name, int age, String department, String phoneNumber) {
		try {
			Patient patient = new Patient(name, age, department, phoneNumber);
			patient.displayPatientDetails();
			System.out.println("Registration Status: Successful");
			return patient;
		} catch (IllegalArgumentException e) {
			boolean nameMissing = name == null || name.isBlank();
			System.out.println("Name: " + (nameMissing ? "(missing)" : name.trim()));
			System.out.println("Age: " + age);
			System.out.println("Registration Status: Failed (" + e.getMessage() + ")");
			return null;
		}
	}

	public static int getPatientCount() {
		return patientCount;
	}

	// Implements the abstract method of Person.
	@Override
	public String getRoleDescription() {
		return "Patient";
	}

	public void displayPatientDetails() {
		System.out.println("Patient Number: " + patientNumber);
		displayBasicDetails(); // inherited from Person
		System.out.println("Age: " + age);
		System.out.println("Department: " + medicalDepartment);
		System.out.println("Consultation Fee: " + consultationFeeText());
	}

	// The fee comes from a doctor, e.g. doctor.calculateConsultationFee(10).
	public void assignConsultationFee(double fee) {
		this.consultationFee = requireNonNegative(fee, "Consultation fee");
		this.consultationFeeAssigned = true;
	}

	@Override
	public void generateReport() {
		System.out.println("[Patient] " + patientNumber + " | " + getName() + " | Age " + age);
		System.out.println("        Department: " + medicalDepartment + " | Consultation Fee: " + consultationFeeText());
	}

	public String getPatientNumber() {
		return patientNumber;
	}

	public int getAge() {
		return age;
	}

	public String getMedicalDepartment() {
		return medicalDepartment;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	private String consultationFeeText() {
		return consultationFeeAssigned ? formatMoney(consultationFee) : "Not assigned yet";
	}

	private static String checkPatientFields(String name, int age, String department) {
		requireText(name, "Name");
		if (age < MIN_AGE || age > MAX_AGE) {
			throw new IllegalArgumentException("Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
		}
		requireText(department, "Department");
		return name;
	}
}
