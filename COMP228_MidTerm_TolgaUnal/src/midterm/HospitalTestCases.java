package midterm;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

/*
 * Test cases in plain Java (no JUnit needed): Run As > Java Application.
 * Static counters are shared by the whole program, so the counter tests compare
 * the values before and after an action instead of expecting fixed numbers.
 */
public class HospitalTestCases {

	private static int passed = 0;
	private static int failed = 0;

	public static void main(String[] args) {
		System.out.println("========================================");
		System.out.println("   HOSPITAL MANAGEMENT SYSTEM TESTS");
		System.out.println("========================================");

		tc01SuccessfulPatientRegistration();
		tc02RegistrationWithBasicDetails();
		tc03InvalidAgeRejected();
		tc04MissingInformationRejected();
		tc05FeeWithoutDiscount();
		tc06FeeWithDiscountAndCharge();
		tc07MonthlyCompensationByCategory();
		tc08StaticCountersAfterCreation();
		tc09InvalidStaffDataRejected();
		tc10AppointmentScheduling();
		tc11AbstractClassesAndFinalFields();

		System.out.println();
		System.out.println("========================================");
		System.out.println("Result: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
		System.out.println("========================================");
		if (failed > 0) {
			System.exit(1);
		}
	}

	private static void tc01SuccessfulPatientRegistration() {
		begin("TC01", "Successful patient registration (name, age, department)");
		int peopleBefore = Person.getPersonCount();
		int patientsBefore = Patient.getPatientCount();

		Patient[] result = new Patient[1];
		String output = captureOutput(() -> result[0] = Patient.registerPatient("Priya Sharma", 35, "Cardiology"));
		Patient patient = result[0];

		check("patient object created", "not null", patient == null ? "null" : "not null");
		if (patient == null) {
			return;
		}
		check("name", "Priya Sharma", patient.getName());
		check("age", 35, patient.getAge());
		check("department", "Cardiology", patient.getMedicalDepartment());
		check("patient number", "P" + (1000 + Patient.getPatientCount()), patient.getPatientNumber());
		check("patient count", patientsBefore + 1, Patient.getPatientCount());
		check("person count", peopleBefore + 1, Person.getPersonCount());
		check("success message printed", true, output.contains("Registration Status: Successful"));
	}

	private static void tc02RegistrationWithBasicDetails() {
		begin("TC02", "Registration with basic details only (overloaded registerPatient(name, age))");
		Patient[] result = new Patient[1];
		captureOutput(() -> result[0] = Patient.registerPatient("Daniel Brown", 42));

		check("patient object created", "not null", result[0] == null ? "null" : "not null");
		if (result[0] != null) {
			check("department", Patient.DEFAULT_DEPARTMENT, result[0].getMedicalDepartment());
			check("phone", "Not provided", result[0].getPhoneNumber());
		}
	}

	private static void tc03InvalidAgeRejected() {
		begin("TC03", "Rejection of an invalid patient age");
		int peopleBefore = Person.getPersonCount();
		int patientsBefore = Patient.getPatientCount();

		Patient[] result = new Patient[2];
		String output = captureOutput(() -> {
			result[0] = Patient.registerPatient("Tom Lee", 150, "Orthopedics");
			result[1] = Patient.registerPatient("Baby Doe", -1);
		});

		check("age 150 rejected", "null", String.valueOf(result[0]));
		check("age -1 rejected", "null", String.valueOf(result[1]));
		check("failure message printed", true, output.contains("Age must be between 0 and 120."));
		check("patient count unchanged", patientsBefore, Patient.getPatientCount());
		check("person count unchanged", peopleBefore, Person.getPersonCount());
	}

	private static void tc04MissingInformationRejected() {
		begin("TC04", "Rejection of missing required information");
		int peopleBefore = Person.getPersonCount();
		int patientsBefore = Patient.getPatientCount();

		Patient[] result = new Patient[3];
		String output = captureOutput(() -> {
			result[0] = Patient.registerPatient("", 30);
			result[1] = Patient.registerPatient(null, 30, "Cardiology");
			result[2] = Patient.registerPatient("Lena Fox", 30, "   ");
		});

		check("empty name rejected", "null", String.valueOf(result[0]));
		check("null name rejected", "null", String.valueOf(result[1]));
		check("blank department rejected", "null", String.valueOf(result[2]));
		check("name message printed", true, output.contains("Name cannot be empty."));
		check("department message printed", true, output.contains("Department cannot be empty."));
		check("patient count unchanged", patientsBefore, Patient.getPatientCount());
		check("person count unchanged", peopleBefore, Person.getPersonCount());
	}

	private static void tc05FeeWithoutDiscount() {
		begin("TC05", "Consultation fee without a discount");
		Doctor doctor = sampleDoctor();
		Doctor specialist = sampleSpecialist(); // Doctor reference, SpecialistDoctor object

		check("doctor standard fee", "$150.00", money(doctor.calculateConsultationFee()));
		check("specialist fee = 150 + 50 surcharge", "$200.00", money(specialist.calculateConsultationFee()));
		check("0% discount = standard fee", "$150.00", money(doctor.calculateConsultationFee(0)));
	}

	private static void tc06FeeWithDiscountAndCharge() {
		begin("TC06", "Consultation fee with a discount and with an additional charge");
		Doctor doctor = sampleDoctor();
		Doctor specialist = sampleSpecialist();

		check("doctor, 10% discount", "$135.00", money(doctor.calculateConsultationFee(10)));
		check("doctor, 10% discount + $40", "$175.00", money(doctor.calculateConsultationFee(10, 40)));
		check("specialist, 10% discount", "$180.00", money(specialist.calculateConsultationFee(10)));
		check("specialist, 10% discount + $40", "$220.00", money(specialist.calculateConsultationFee(10, 40)));
		check("discount of 120% rejected", true, throwsIllegalArgument(() -> doctor.calculateConsultationFee(120)));
		check("negative charge rejected", true, throwsIllegalArgument(() -> doctor.calculateConsultationFee(10, -5)));
	}

	private static void tc07MonthlyCompensationByCategory() {
		begin("TC07", "Monthly compensation for different staff categories");
		MedicalStaff doctor = sampleDoctor();
		MedicalStaff specialist = sampleSpecialist();
		MedicalStaff nurse = sampleNurse();

		check("doctor: 9000 + 100 x 10", "$10000.00", money(doctor.calculateMonthlyCompensation()));
		check("specialist: 12000 + 80 x 10 + 2000", "$14800.00", money(specialist.calculateMonthlyCompensation()));
		check("nurse: 5000 + 500", "$5500.00", money(nurse.calculateMonthlyCompensation()));

		Doctor amit = (Doctor) doctor;
		amit.recordPatientVisit();
		check("doctor after one more patient", "$10010.00", money(amit.calculateMonthlyCompensation()));
	}

	private static void tc08StaticCountersAfterCreation() {
		begin("TC08", "Correct static counter values after object creation");
		int peopleBefore = Person.getPersonCount();
		int patientsBefore = Patient.getPatientCount();
		int staffBefore = MedicalStaff.getStaffCount();

		captureOutput(() -> Patient.registerPatient("Omar Haddad", 51, "Oncology"));
		sampleDoctor();
		sampleSpecialist(); // runs four constructors: SpecialistDoctor, Doctor, MedicalStaff, Person
		sampleNurse();

		check("person count +4", peopleBefore + 4, Person.getPersonCount());
		check("patient count +1", patientsBefore + 1, Patient.getPatientCount());
		check("staff count +3 (specialist counted once)", staffBefore + 3, MedicalStaff.getStaffCount());
	}

	private static void tc09InvalidStaffDataRejected() {
		begin("TC09", "Rejection of invalid staff data without changing the counters");
		int peopleBefore = Person.getPersonCount();
		int staffBefore = MedicalStaff.getStaffCount();

		check("nurse with negative salary rejected", true, throwsIllegalArgument(
				() -> new Nurse("N9001", "Mark Green", "416-555-0199", "Pediatrics", -100, "Day", 200)));
		check("doctor with negative fee rejected", true, throwsIllegalArgument(
				() -> new Doctor("D9001", "Dr. Test", "416-555-0198", "Cardiology", 9000, -1, 10)));
		check("specialist without specialization rejected", true, throwsIllegalArgument(
				() -> new SpecialistDoctor("D9002", "Dr. Test", "416-555-0197", "Neuro", 9000, 150, 10, "", 50)));
		check("staff without name rejected", true, throwsIllegalArgument(
				() -> new Nurse("N9002", " ", "416-555-0196", "Emergency", 5000, "Night", 500)));

		check("person count unchanged", peopleBefore, Person.getPersonCount());
		check("staff count unchanged", staffBefore, MedicalStaff.getStaffCount());
	}

	private static void tc10AppointmentScheduling() {
		begin("TC10", "Appointment scheduling through the Schedulable interface");
		Doctor doctor = sampleDoctor();
		Schedulable schedule = doctor;

		String output = captureOutput(() -> {
			schedule.scheduleAppointment("2026-11-02 10:00"); // valid
			schedule.scheduleAppointment("2026-11-02 10:00"); // already booked
			schedule.scheduleAppointment("02/11/2026"); // wrong format
			schedule.scheduleAppointment("2026-02-30 09:00"); // impossible date
			schedule.scheduleAppointment(""); // empty
		});

		check("only the valid slot is stored", 1, doctor.getAppointmentCount());
		check("success message printed", true, output.contains("on 2026-11-02 10:00: Successful"));
		check("booked slot rejected", true, output.contains("is already booked"));
		check("four failure messages", 4, countOccurrences(output, ": Failed ("));
		check("specialist inherits scheduling", true, sampleSpecialist() instanceof Schedulable);
	}

	// Checked with reflection because these rules are enforced by the compiler.
	private static void tc11AbstractClassesAndFinalFields() {
		begin("TC11", "Abstract classes, final fields and interfaces are in place");
		check("Person is abstract", true, Modifier.isAbstract(Person.class.getModifiers()));
		check("MedicalStaff is abstract", true, Modifier.isAbstract(MedicalStaff.class.getModifiers()));
		check("Person.personId is final", true, isFinalField(Person.class, "personId"));
		check("Patient.patientNumber is final", true, isFinalField(Patient.class, "patientNumber"));
		check("MedicalStaff.staffId is final", true, isFinalField(MedicalStaff.class, "staffId"));
		check("Doctor.consultationFee is final", true, isFinalField(Doctor.class, "consultationFee"));
		check("SpecialistDoctor.specialization is final", true, isFinalField(SpecialistDoctor.class, "specialization"));
		check("Nurse.shiftType is final", true, isFinalField(Nurse.class, "shiftType"));
		check("Doctor implements both interfaces", true,
				Reportable.class.isAssignableFrom(Doctor.class) && Schedulable.class.isAssignableFrom(Doctor.class));
		check("SpecialistDoctor -> Doctor -> MedicalStaff -> Person", true,
				SpecialistDoctor.class.getSuperclass() == Doctor.class
						&& Doctor.class.getSuperclass() == MedicalStaff.class
						&& MedicalStaff.class.getSuperclass() == Person.class);
	}

	// Same values as in Main
	private static Doctor sampleDoctor() {
		return new Doctor("D2001", "Dr. Amit Kumar", "416-555-0110", "Cardiology", 9000, 150, 100);
	}

	private static SpecialistDoctor sampleSpecialist() {
		return new SpecialistDoctor("D2002", "Dr. Neha Patel", "416-555-0111", "Neurosciences",
				12000, 150, 80, "Neurology", 50);
	}

	private static Nurse sampleNurse() {
		return new Nurse("N3001", "Sarah Wilson", "416-555-0112", "Emergency", 5000, "Night", 500);
	}

	private static void begin(String id, String title) {
		System.out.println();
		System.out.println(id + ": " + title);
	}

	private static void check(String description, Object expected, Object actual) {
		boolean ok = expected.equals(actual);
		if (ok) {
			passed++;
		} else {
			failed++;
		}
		System.out.println("  " + (ok ? "PASS" : "FAIL") + "  " + description
				+ " | expected: " + expected + " | actual: " + actual);
	}

	private static String money(double amount) {
		return Person.formatMoney(amount);
	}

	private static boolean throwsIllegalArgument(Runnable action) {
		try {
			action.run();
			return false;
		} catch (IllegalArgumentException e) {
			return true;
		}
	}

	private static boolean isFinalField(Class<?> type, String fieldName) {
		try {
			return Modifier.isFinal(type.getDeclaredField(fieldName).getModifiers());
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	private static int countOccurrences(String text, String part) {
		int count = 0;
		int index = text.indexOf(part);
		while (index >= 0) {
			count++;
			index = text.indexOf(part, index + part.length());
		}
		return count;
	}

	// Runs the action and returns what it printed, without showing it on the console.
	private static String captureOutput(Runnable action) {
		PrintStream original = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
		try {
			action.run();
		} finally {
			System.setOut(original);
		}
		return buffer.toString(StandardCharsets.UTF_8);
	}
}
