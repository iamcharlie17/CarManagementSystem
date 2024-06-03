import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class RoleSelectionGUI extends JFrame {
    private CarRentalSystem rentalSystem;

    public RoleSelectionGUI(CarRentalSystem rentalSystem) {
        this.rentalSystem = rentalSystem;
        setTitle("Select your role");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("car1.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(150,100,150,100));
        panel.setLayout(new GridLayout(1, 2, 50, 50));

        JButton btnManager = new JButton("Manager");
        btnManager.setFont(new Font("Arial", Font.BOLD, 16));
        btnManager.addActionListener(e -> openManagerGUI());
        panel.add(btnManager);

        JButton btnCustomer = new JButton("Customer");
        btnCustomer.setFont(new Font("Arial", Font.BOLD, 16));
        btnCustomer.addActionListener(e -> openCustomerGUI());
        panel.add(btnCustomer);

        add(panel);
    }

    private void openManagerGUI() {
        String managerEmail = JOptionPane.showInputDialog(this, "Enter Manager Email:");
        if (managerEmail == null || managerEmail.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Manager Email.");
            return;
        }

        if (verifyManagerEmail(managerEmail)) {
            RentalSystemGUI managerGUI = new RentalSystemGUI(rentalSystem, true);
            managerGUI.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Access Denied. Invalid Manager Email.");
        }
    }

    private boolean verifyManagerEmail(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader("manager.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(email.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void openCustomerGUI() {
        RentalSystemGUI customerGUI = new RentalSystemGUI(rentalSystem, false);
        customerGUI.setVisible(true);
        dispose();
    }
}



