import javax.swing.*;
import java.awt.*;

class RoleSelectionGUI extends JFrame {
    private CarRentalSystem rentalSystem;

    public RoleSelectionGUI(CarRentalSystem rentalSystem) {
        this.rentalSystem = rentalSystem;
        setTitle("Select your role");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {

        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnManager = new JButton("Manager");
        btnManager.addActionListener(e -> openManagerGUI());
        panel.add(btnManager);

        JButton btnCustomer = new JButton("Customer");
        btnCustomer.addActionListener(e -> openCustomerGUI());
        panel.add(btnCustomer);

        add(panel);
    }

    private void openManagerGUI() {
        RentalSystemGUI managerGUI = new RentalSystemGUI(rentalSystem, true);
        managerGUI.setVisible(true);
        dispose();
    }

    private void openCustomerGUI() {
        RentalSystemGUI customerGUI = new RentalSystemGUI(rentalSystem, false);
        customerGUI.setVisible(true);
        dispose();
    }
}



