package midterm;

/*
 * Multilevel inheritance: Person -> MedicalStaff -> Doctor -> SpecialistDoctor
 * Hybrid inheritance: a class inheritance chain combined with interfaces.
 * Doctor already implements both interfaces; they are repeated here on purpose
 * to make the hybrid structure visible.
 */
public class SpecialistDoctor extends Doctor implements Reportable, Schedulable {

	public static final double SPECIALIST_MONTHLY_BONUS = 2000.0;

	// final: fixed when the specialist is created
	private final String specialization;
	private final double specialistSurcharge;

	public SpecialistDoctor(String staffId, String name, String phoneNumber, String department,
			double monthlySalary, double consultationFee, int patientsSeen,
			String specialization, double specialistSurcharge) {
		// Checked inside super(...) so invalid data is rejected before any counter changes (see Person).
		super(staffId, name, phoneNumber, department, monthlySalary, consultationFee,
				checkSpecialistFields(patientsSeen, specialization, specialistSurcharge));
		this.specialization = specialization.trim();
		this.specialistSurcharge = specialistSurcharge;
	}

	// Only the no-parameter version is overridden; the inherited overloads call it (see Doctor).
	@Override
	public double calculateConsultationFee() {
		return super.calculateConsultationFee() + specialistSurcharge;
	}

	@Override
	public double calculateMonthlyCompensation() {
		return super.calculateMonthlyCompensation() + SPECIALIST_MONTHLY_BONUS;
	}

	@Override
	public void displayStaffDetails() {
		super.displayStaffDetails();
		System.out.println("Specialization: " + specialization);
		System.out.println("Specialist Surcharge: " + formatMoney(specialistSurcharge));
	}

	@Override
	public String getRoleDescription() {
		return "Specialist Doctor (" + specialization + ")";
	}

	public String getSpecialization() {
		return specialization;
	}

	public double getSpecialistSurcharge() {
		return specialistSurcharge;
	}

	private static int checkSpecialistFields(int patientsSeen, String specialization, double specialistSurcharge) {
		requireText(specialization, "Specialization");
		requireNonNegative(specialistSurcharge, "Specialist surcharge");
		return patientsSeen;
	}
}
