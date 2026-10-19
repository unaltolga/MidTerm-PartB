package midterm;

/*
 * Multilevel inheritance: Person -> MedicalStaff
 * Hierarchical inheritance: Doctor and Nurse both extend MedicalStaff
 *
 * Abstract: it holds what all staff have in common, but each kind of staff
 * member calculates the monthly pay differently.
 */
public abstract class MedicalStaff extends Person {

	// static: shared by all staff objects
	private static int staffCount = 0;

	// final: a staff ID never changes
	private final String staffId;
	private String department;
	private double monthlySalary;

	protected MedicalStaff(String staffId, String name, String phoneNumber, String department,
			double monthlySalary) {
		// Checked inside super(...) so invalid data is rejected before any counter changes (see Person).
		super(checkStaffFields(name, staffId, department, monthlySalary), phoneNumber);
		this.staffId = staffId.trim();
		this.department = department.trim();
		this.monthlySalary = monthlySalary;
		staffCount++; // counted once here, never again in Doctor, SpecialistDoctor or Nurse
	}

	public static int getStaffCount() {
		return staffCount;
	}

	// abstract: Doctor, SpecialistDoctor and Nurse each calculate it differently
	public abstract double calculateMonthlyCompensation();

	// Concrete method; the subclasses override it and call super.displayStaffDetails() first.
	public void displayStaffDetails() {
		System.out.println("Staff ID: " + staffId);
		System.out.println("Name: " + getName());
		System.out.println("Role: " + getRoleDescription());
		System.out.println("Department: " + department);
		System.out.println("Phone: " + getPhoneNumber());
		System.out.println("Monthly Salary: " + formatMoney(monthlySalary));
	}

	@Override
	public String toString() {
		return super.toString() + " | Staff ID: " + staffId;
	}

	public String getStaffId() {
		return staffId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = requireText(department, "Department");
	}

	public double getMonthlySalary() {
		return monthlySalary;
	}

	public void setMonthlySalary(double monthlySalary) {
		this.monthlySalary = requireNonNegative(monthlySalary, "Monthly salary");
	}

	private static String checkStaffFields(String name, String staffId, String department, double monthlySalary) {
		requireText(staffId, "Staff ID");
		requireText(department, "Department");
		requireNonNegative(monthlySalary, "Monthly salary");
		return name;
	}
}
