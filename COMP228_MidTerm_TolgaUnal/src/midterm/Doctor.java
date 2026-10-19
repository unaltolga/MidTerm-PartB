package midterm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/*
 * Multilevel inheritance: Person -> MedicalStaff -> Doctor
 * Hierarchical inheritance: Doctor and Nurse both extend MedicalStaff
 * Multiple inheritance through interfaces: Reportable and Schedulable
 */
public class Doctor extends MedicalStaff implements Reportable, Schedulable {

	public static final double ALLOWANCE_PER_PATIENT = 10.0;

	// "uuuu" with STRICT rejects impossible dates such as 2026-02-30.
	private static final DateTimeFormatter APPOINTMENT_FORMAT = DateTimeFormatter
			.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

	// final: fixed when the doctor is created
	private final double consultationFee;
	private int patientsSeen;
	private final List<LocalDateTime> appointments = new ArrayList<>();

	public Doctor(String staffId, String name, String phoneNumber, String department,
			double monthlySalary, double consultationFee, int patientsSeen) {
		// Checked inside super(...) so invalid data is rejected before any counter changes (see Person).
		super(staffId, name, phoneNumber, department,
				checkDoctorFields(monthlySalary, consultationFee, patientsSeen));
		this.consultationFee = consultationFee;
		this.patientsSeen = patientsSeen;
	}

	// Implements the abstract method of MedicalStaff.
	@Override
	public double calculateMonthlyCompensation() {
		return getMonthlySalary() + patientsSeen * ALLOWANCE_PER_PATIENT;
	}

	// Overloaded: same name, three different parameter lists.
	public double calculateConsultationFee() {
		return consultationFee;
	}

	// discountPercent: 10 means 10%. Calls the no-parameter version,
	// so a SpecialistDoctor's surcharge is included automatically.
	public double calculateConsultationFee(double discountPercent) {
		if (discountPercent < 0 || discountPercent > 100) {
			throw new IllegalArgumentException("Discount must be between 0 and 100 percent.");
		}
		return calculateConsultationFee() * (100 - discountPercent) / 100;
	}

	public double calculateConsultationFee(double discountPercent, double additionalCharge) {
		requireNonNegative(additionalCharge, "Additional charge");
		return calculateConsultationFee(discountPercent) + additionalCharge;
	}

	@Override
	public void displayStaffDetails() {
		super.displayStaffDetails();
		System.out.println("Patients Seen: " + patientsSeen);
		System.out.println("Consultation Fee: " + formatMoney(calculateConsultationFee()));
		System.out.println("Monthly Compensation: " + formatMoney(calculateMonthlyCompensation()));
	}

	@Override
	public String getRoleDescription() {
		return "Doctor";
	}

	// Rejects an empty value, a wrong format, an impossible date and a slot that is already booked.
	@Override
	public void scheduleAppointment(String appointmentDate) {
		if (appointmentDate == null || appointmentDate.isBlank()) {
			printAppointmentFailure("no date was given");
			return;
		}
		LocalDateTime slot;
		try {
			slot = LocalDateTime.parse(appointmentDate.trim(), APPOINTMENT_FORMAT);
		} catch (DateTimeParseException e) {
			printAppointmentFailure("\"" + appointmentDate.trim() + "\" is not a valid yyyy-MM-dd HH:mm date");
			return;
		}
		if (appointments.contains(slot)) {
			printAppointmentFailure("the slot " + slot.format(APPOINTMENT_FORMAT) + " is already booked");
			return;
		}
		appointments.add(slot);
		System.out.println("Appointment with " + getName() + " on " + slot.format(APPOINTMENT_FORMAT)
				+ ": Successful");
	}

	// Role and fee are resolved at runtime, so a SpecialistDoctor's report shows its own values.
	@Override
	public void generateReport() {
		System.out.println("[Staff] " + getStaffId() + " | " + getName() + " | " + getRoleDescription());
		System.out.println("        Patients Seen: " + patientsSeen
				+ " | Fee: " + formatMoney(calculateConsultationFee())
				+ " | Compensation: " + formatMoney(calculateMonthlyCompensation())
				+ " | Appointments: " + appointmentsText());
	}

	public int getPatientsSeen() {
		return patientsSeen;
	}

	public void recordPatientVisit() {
		patientsSeen++;
	}

	public int getAppointmentCount() {
		return appointments.size();
	}

	private String appointmentsText() {
		if (appointments.isEmpty()) {
			return "none";
		}
		List<String> slots = new ArrayList<>();
		for (LocalDateTime slot : appointments) {
			slots.add(slot.format(APPOINTMENT_FORMAT));
		}
		return String.join(", ", slots);
	}

	private void printAppointmentFailure(String reason) {
		System.out.println("Appointment with " + getName() + ": Failed (" + reason + ")");
	}

	private static double checkDoctorFields(double monthlySalary, double consultationFee, int patientsSeen) {
		requireNonNegative(consultationFee, "Consultation fee");
		requireNonNegative(patientsSeen, "Patients seen");
		return monthlySalary;
	}
}
