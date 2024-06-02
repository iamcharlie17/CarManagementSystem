import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        CarRentalSystem rentalSystem = new CarRentalSystem();
        rentalSystem.loadCarsFromFile("cars.txt");
        rentalSystem.loadCustomersFromFile("customers.txt");
        rentalSystem.loadRentalsFromFile("rentals.txt");

        SwingUtilities.invokeLater(() -> {
            RoleSelectionGUI roleSelectionGUI = new RoleSelectionGUI(rentalSystem);
            roleSelectionGUI.setVisible(true);
        });
    }
}




