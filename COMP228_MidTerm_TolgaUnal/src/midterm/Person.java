package midterm;

import java.util.Locale;

/*
 * Abstract base class of the hierarchy: "new Person(...)" is a compile error.
 *
 * Counter rule used in every class:
 *   1. Each counter is incremented in exactly one class (personCount here,
 *      patientCount in Patient, staffCount in MedicalStaff), so a SpecialistDoctor
 *      is counted once even though four constructors run.
 *   2. Each subclass checks its own arguments inside its super(...) call, so the
 *      checks run before this constructor. A counter changes only when the whole
 *      object was created successfully.
 */
public abstract class Person {

	// static: one shared copy for all Person objects
	private static int personCount = 0;

	// final: assigned once in the constructor and never reassigned
	private final int personId;
	private String name;
	private String phoneNumber;

	protected Person(String name, String phoneNumber) {
		this.name = requireText(name, "Name");
		this.phoneNumber = phoneOrDefault(phoneNumber);
		personCount++; // reached only when validation passed
		this.personId = personCount; // 1, 2, 3, ...
	}

	public static int getPersonCount() {
		return personCount;
	}

	// Concrete method shared by every subclass.
	public void displayBasicDetails() {
		System.out.println("Person ID: " + personId);
		System.out.println("Name: " + name);
		System.out.println("Phone: " + phoneNumber);
		System.out.println("Role: " + getRoleDescription());
	}

	// abstract: each concrete subclass provides its own version
	public abstract String getRoleDescription();

	// getRoleDescription() is resolved at runtime, so the same line reads
	// "Patient", "Doctor", "Nurse (Night shift)", ...
	@Override
	public String toString() {
		return getRoleDescription() + " #" + personId + ": " + name + " (Phone: " + phoneNumber + ")";
	}

	public int getPersonId() {
		return personId;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	// Contact information may change, so it is not final.
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneOrDefault(phoneNumber);
	}

	// protected helpers: visible to the subclasses, hidden from other packages
	protected static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " cannot be empty.");
		}
		return value.trim();
	}

	protected static double requireNonNegative(double value, String fieldName) {
		if (value < 0) {
			throw new IllegalArgumentException(fieldName + " cannot be negative.");
		}
		return value;
	}

	// Locale.US keeps the decimal point as "." on every computer.
	protected static String formatMoney(double amount) {
		return String.format(Locale.US, "$%.2f", amount);
	}

	// The phone number is optional: registerPatient(name, age) does not ask for it.
	private static String phoneOrDefault(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isBlank()) {
			return "Not provided";
		}
		return phoneNumber.trim();
	}
}
