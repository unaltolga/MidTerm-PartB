package midterm;

/*
 * Hierarchical inheritance: Nurse and Doctor both extend MedicalStaff
 * Also implements Reportable.
 */
public class Nurse extends MedicalStaff implements Reportable {

	// final: fixed when the nurse is created
	private final String shiftType;
	private final double shiftAllowance;

	public Nurse(String staffId, String name, String phoneNumber, String department,
			double monthlySalary, String shiftType, double shiftAllowance) {
		// Checked inside super(...) so invalid data is rejected before any counter changes (see Person).
		super(staffId, name, phoneNumber, department,
				checkNurseFields(monthlySalary, shiftType, shiftAllowance));
		this.shiftType = shiftType.trim();
		this.shiftAllowance = shiftAllowance;
	}

	// Implements the abstract method of MedicalStaff.
	@Override
	public double calculateMonthlyCompensation() {
		return getMonthlySalary() + shiftAllowance;
	}

	@Override
	public void displayStaffDetails() {
		super.displayStaffDetails();
		System.out.println("Shift: " + shiftType);
		System.out.println("Shift Allowance: " + formatMoney(shiftAllowance));
		System.out.println("Monthly Compensation: " + formatMoney(calculateMonthlyCompensation()));
	}

	@Override
	public String getRoleDescription() {
		return "Nurse (" + shiftType + " shift)";
	}

	@Override
	public void generateReport() {
		System.out.println("[Staff] " + getStaffId() + " | " + getName() + " | " + getRoleDescription());
		System.out.println("        Department: " + getDepartment()
				+ " | Compensation: " + formatMoney(calculateMonthlyCompensation()));
	}

	public String getShiftType() {
		return shiftType;
	}

	public double getShiftAllowance() {
		return shiftAllowance;
	}

	private static double checkNurseFields(double monthlySalary, String shiftType, double shiftAllowance) {
		requireText(shiftType, "Shift type");
		requireNonNegative(shiftAllowance, "Shift allowance");
		return monthlySalary;
	}
}
