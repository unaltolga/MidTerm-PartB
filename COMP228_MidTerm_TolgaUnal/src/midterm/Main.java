package midterm;

// Main application: demonstrates the system as a clearly organised sequence of steps.
public class Main {

	private static final String LINE = "========================================";

	public static void main(String[] args) {
		System.out.println(LINE);
		System.out.println("          CAREPOINT HOSPITAL");
		System.out.println("          MANAGEMENT SYSTEM");
		System.out.println(LINE);

		// 1. Patient registration: the three overloaded versions
		section("PATIENT REGISTRATION");
		Patient priya = Patient.registerPatient("Priya Sharma", 35, "Cardiology", "416-555-0142"); // 4 parameters
		System.out.println();
		Patient daniel = Patient.registerPatient("Daniel Brown", 42); // 2 parameters: default department
		System.out.println();
		Patient.registerPatient("", 28, "Dermatology"); // 3 parameters: missing name, rejected
		System.out.println();
		Patient.registerPatient("Tom Lee", 150, "Orthopedics"); // 3 parameters: invalid age, rejected

		if (priya == null || daniel == null) {
			System.out.println("\nThe sample patients could not be registered. The demonstration stops here.");
			return;
		}

		// 2. Staff
		Doctor doctor = new Doctor("D2001", "Dr. Amit Kumar", "416-555-0110", "Cardiology",
				9000.00, 150.00, 100);
		SpecialistDoctor specialist = new SpecialistDoctor("D2002", "Dr. Neha Patel", "416-555-0111",
				"Neurosciences", 12000.00, 150.00, 80, "Neurology", 50.00);
		Nurse nurse = new Nurse("N3001", "Sarah Wilson", "416-555-0112", "Emergency",
				5000.00, "Night", 500.00);

		// Runtime polymorphism: the variable type is MedicalStaff, the object type is
		// Doctor, SpecialistDoctor or Nurse. Java runs the overridden method of the object.
		MedicalStaff staff1 = doctor;
		MedicalStaff staff2 = specialist;
		MedicalStaff staff3 = nurse;
		MedicalStaff[] staffMembers = { staff1, staff2, staff3 };

		section("STAFF DETAILS (displayStaffDetails() through MedicalStaff references)");
		for (int i = 0; i < staffMembers.length; i++) {
			if (i > 0) {
				System.out.println();
			}
			staffMembers[i].displayStaffDetails();
		}

		section("STAFF CREATION WITH INVALID DATA");
		try {
			new Nurse("N3002", "Mark Green", "416-555-0113", "Pediatrics", -100.00, "Day", 200.00);
			System.out.println("Staff creation for Mark Green: Successful");
		} catch (IllegalArgumentException e) {
			System.out.println("Staff creation for Mark Green: Failed (" + e.getMessage() + ")");
		}

		// 3. Consultation fees: overloaded methods
		section("CONSULTATION FEE CALCULATIONS");
		printFeeCalculations(doctor);
		System.out.println();
		printFeeCalculations(specialist); // inherited overloads + overridden no-parameter version
		System.out.println();
		try {
			doctor.calculateConsultationFee(120);
		} catch (IllegalArgumentException e) {
			System.out.println("Discount of 120%: Rejected (" + e.getMessage() + ")");
		}

		priya.assignConsultationFee(doctor.calculateConsultationFee(10));
		daniel.assignConsultationFee(specialist.calculateConsultationFee()); // referred to the specialist

		// 4. Monthly compensation: overridden abstract method
		section("MONTHLY COMPENSATION (calculateMonthlyCompensation() through MedicalStaff references)");
		double payroll = 0;
		for (MedicalStaff member : staffMembers) {
			double compensation = member.calculateMonthlyCompensation();
			System.out.println(member.getName() + " (" + member.getRoleDescription() + "): "
					+ Person.formatMoney(compensation));
			payroll += compensation;
		}
		System.out.println("Total Monthly Payroll: " + Person.formatMoney(payroll));

		// 5. Appointments: Schedulable interface
		section("APPOINTMENT SCHEDULING (Schedulable)");
		Schedulable doctorSchedule = doctor;
		Schedulable specialistSchedule = specialist; // inherited from Doctor
		doctorSchedule.scheduleAppointment("2026-11-02 10:00"); // valid
		doctorSchedule.scheduleAppointment("2026-11-02 10:00"); // same slot again, rejected
		specialistSchedule.scheduleAppointment("2026-11-03 14:30"); // valid
		specialistSchedule.scheduleAppointment("03/11/2026"); // wrong format, rejected
		specialistSchedule.scheduleAppointment("2026-02-30 09:00"); // impossible date, rejected

		// 6. Reports: Reportable interface, patients and staff in one array
		section("REPORTS (Reportable)");
		Reportable[] reportables = { priya, daniel, doctor, specialist, nurse };
		for (Reportable reportable : reportables) {
			reportable.generateReport();
		}

		// 7. Summary: static counters and the overridden toString()
		section("REGISTRATION SUMMARY");
		System.out.println("Total People: " + Person.getPersonCount());
		System.out.println("Total Patients: " + Patient.getPatientCount());
		System.out.println("Total Medical Staff: " + MedicalStaff.getStaffCount());
		System.out.println();
		System.out.println("Registered people (toString()):");
		Person[] everyone = { priya, daniel, doctor, specialist, nurse };
		for (Person person : everyone) {
			System.out.println("  " + person);
		}

		System.out.println();
		System.out.println(LINE);
		System.out.println("            END OF REPORT");
		System.out.println(LINE);
	}

	// The parameter type is Doctor, so it accepts a SpecialistDoctor too.
	private static void printFeeCalculations(Doctor doctor) {
		System.out.println(doctor.getName() + " (" + doctor.getRoleDescription() + ")");
		System.out.println("Standard Consultation Fee: "
				+ Person.formatMoney(doctor.calculateConsultationFee()));
		System.out.println("Consultation Fee After 10% Discount: "
				+ Person.formatMoney(doctor.calculateConsultationFee(10)));
		System.out.println("Consultation Fee With 10% Discount and $40.00 Additional Charge: "
				+ Person.formatMoney(doctor.calculateConsultationFee(10, 40)));
	}

	private static void section(String title) {
		System.out.println();
		System.out.println(title);
	}
}
