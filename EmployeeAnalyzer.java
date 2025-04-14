
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Employee {
    String firstName, lastName, department, position;
    int salary, yearsOfService;
    boolean isLead;

    public Employee(String firstName, String lastName, String department, String position, int salary, int yearsOfService, boolean isLead) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.position = position;
        this.salary = salary;
        this.yearsOfService = yearsOfService;
        this.isLead = isLead;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("%s %s | Dept: %s | Position: %s | Salary: %d | Years: %d | Lead: %s",
                firstName, lastName, department, position, salary, yearsOfService, isLead ? "Yes" : "No");
    }
}

class Company {
    List<Employee> employees = new ArrayList<>();

    public void loadFromCSV(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            Employee e = new Employee(
                    parts[0], parts[1], parts[2], parts[3],
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5]),
                    parts[6].trim().equalsIgnoreCase("Yes")
            );
            employees.add(e);
        }
        br.close();
    }

    public void lowestPaidByDepartment() {
        System.out.println("\nLowest Paid Employee in Each Department:");
        employees.stream()
                .collect(Collectors.groupingBy(e -> e.department))
                .forEach((dept, emps) -> {
                    Employee min = Collections.min(emps, Comparator.comparingInt(e -> e.salary));
                    System.out.printf("%s -> %s ($%d)\n", dept, min.getFullName(), min.salary);
                });
    }

    public void newEmployees() {
        System.out.println("\nEmployees with < 4 Years of Service:");
        employees.stream()
                .filter(e -> e.yearsOfService < 4)
                .forEach(e -> System.out.println(e));
    }

    public void avgSalaryMarketing() {
        System.out.println("\nAverage Salary in Marketing:");
        List<Employee> marketing = employees.stream()
                .filter(e -> e.department.equalsIgnoreCase("Marketing"))
                .collect(Collectors.toList());
        double avg = marketing.stream()
                .mapToInt(e -> e.salary)
                .average().orElse(0);
        System.out.printf("Average Marketing Salary: $%.2f\n", avg);
    }

    public void longestTenure() {
        System.out.println("\nLongest-Tenured Employee(s):");
        int maxYears = employees.stream()
                .mapToInt(e -> e.yearsOfService)
                .max().orElse(0);
        employees.stream()
                .filter(e -> e.yearsOfService == maxYears)
                .forEach(System.out::println);
    }

    public void leadsPerDepartment() {
        System.out.println("\nLead Count by Department:");
        employees.stream()
                .filter(e -> e.isLead)
                .collect(Collectors.groupingBy(e -> e.department, Collectors.counting()))
                .forEach((dept, count) -> System.out.printf("%s: %d lead(s)\n", dept, count));
    }
}

public class EmployeeAnalyzer {
    public static void main(String[] args) throws IOException {
        Company company = new Company();
        company.loadFromCSV("employee_data.csv");

        company.lowestPaidByDepartment();
        company.newEmployees();
        company.avgSalaryMarketing();
        company.longestTenure();
        company.leadsPerDepartment();
    }
}
