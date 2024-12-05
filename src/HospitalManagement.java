
import java.sql.*;
import java.util.Scanner;


 public class HospitalManagement  {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Admin@123";

    public static void main(String [] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
               Patient patient = new Patient(connection, scanner);
               Doctor doctor = new Doctor(connection);
             while (true) {
                System.out.println("Hospital management System");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.println("Emter Your Choise");
                int choise = scanner.nextInt();

                switch (choise) {
                    case 1:
                        //Add Patient
                        patient.addPatient();
                    case 2:
                        //View Patients
                        patient.viewPatients();
                    case 3:
                        //View Doctors
                        doctor.viewDoctors();
                    case 4:
                        //Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);

                    case 5:
                        return;
                    default:
                        System.out.println("Enter vailed choise");

                }
            }


        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("Enter patient Id");
        int patientId = scanner.nextInt();
        System.out.println("Enter Doctor Id");
        int doctorId = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD: ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorsById(doctorId)) {
            if (checkDoctoravailability(doctorId, appointmentDate, connection)) {
                String appointmentQueery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQueery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    int rowAffected = preparedStatement.executeUpdate();
                    if (rowAffected > 0) {
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed to book appointment ");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static boolean checkDoctoravailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctors_id=? AND appoientment_date =?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}