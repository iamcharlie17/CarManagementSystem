import javax.swing.*;
import java.awt.*;
import java.util.List;

class RentalSystemGUI extends JFrame {
    private CarRentalSystem rentalSystem;
    private boolean isManager;

    public RentalSystemGUI(CarRentalSystem rentalSystem, boolean isManager) {
        this.rentalSystem = rentalSystem;
        this.isManager = isManager;
        setTitle("Car Rental System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
    }

    private void initializeUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());


        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        JLabel titleLabel = new JLabel("Car Rental System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        northPanel.add(titleLabel);


        JPanel centerPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("car1.jpg");
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        centerPanel.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));
        centerPanel.setLayout(new GridLayout(3, 2, 50, 50));

        if (isManager) {

            JLabel titleLabel2 = new JLabel("(Manager)");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            northPanel.add(titleLabel2);

            JButton btnDisplayCars = new JButton("Display All Cars");
            btnDisplayCars.setFont(new Font("Arial", Font.BOLD, 16));
            btnDisplayCars.addActionListener(e -> displayCars());
            centerPanel.add(btnDisplayCars);

            JButton btnDisplayRentedCustomers = new JButton("Display Customers");
            btnDisplayRentedCustomers.setFont(new Font("Arial", Font.BOLD, 16));
            btnDisplayRentedCustomers.addActionListener(e -> displayRentedCustomers());
            centerPanel.add(btnDisplayRentedCustomers);

            JButton btnAddCar = new JButton("Add a Car");
            btnAddCar.setFont(new Font("Arial", Font.BOLD, 16));
            btnAddCar.addActionListener(e -> addCar());
            centerPanel.add(btnAddCar);

            JButton btnRemoveCar = new JButton("Remove a Car");
            btnRemoveCar.setFont(new Font("Arial", Font.BOLD, 16));
            btnRemoveCar.addActionListener(e -> removeCar());
            centerPanel.add(btnRemoveCar);

            JButton btnAddManager = new JButton("Add a Manager");
            btnAddManager.setFont(new Font("Arial", Font.BOLD, 16));
            btnAddManager.addActionListener(e -> addManager());
            centerPanel.add(btnAddManager);



        } else {
            JLabel titleForCustomer = new JLabel("(Customer)");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            northPanel.add(titleForCustomer);

            JButton btnRentCar = new JButton("Rent a Car");
            btnRentCar.setFont(new Font("Arial", Font.BOLD, 16));
            btnRentCar.addActionListener(e -> rentCar());
            centerPanel.add(btnRentCar);

            JButton btnReturnCar = new JButton("Return a Car");
            btnReturnCar.setFont(new Font("Arial", Font.BOLD, 16));
            btnReturnCar.addActionListener(e -> returnCar());
            centerPanel.add(btnReturnCar);

            JButton btnDisplayCars = new JButton("Display All Cars");
            btnDisplayCars.setFont(new Font("Arial", Font.BOLD, 16));
            btnDisplayCars.addActionListener(e -> displayCars());
            centerPanel.add(btnDisplayCars);

            JButton btnViewRentalHistory = new JButton("View Rental History");
            btnViewRentalHistory.setFont(new Font("Arial", Font.BOLD, 16));
            btnViewRentalHistory.addActionListener(e -> viewRentalHistory());
            centerPanel.add(btnViewRentalHistory);


        }

        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Arial", Font.BOLD, 16));
        btnExit.addActionListener(e -> {
            rentalSystem.saveCarsToFile("cars.txt");
            rentalSystem.saveCustomersToFile("customers.txt");
            rentalSystem.saveRentalsToFile("rentals.txt");
            System.exit(0);
        });
        centerPanel.add(btnExit);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        add(panel);
    }

    private void rentCar() {
        String customerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (customerName == null || customerName.trim().isEmpty()) return;

        Customer existingCustomer = rentalSystem.findCustomerByName(customerName);
        if (existingCustomer == null) {
            existingCustomer = new Customer("CUS" + (rentalSystem.getCustomers().size() + 1), customerName);
            rentalSystem.addCustomer(existingCustomer);
        }

        List<Car> availableCars = rentalSystem.getAvailableCars();
        if (availableCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available for rent.");
            return;
        }

        String[] carOptions = availableCars.stream().map(Car::toString).toArray(String[]::new);
        String selectedCar = (String) JOptionPane.showInputDialog(this, "Select a car:", "Available Cars",
                JOptionPane.QUESTION_MESSAGE, null, carOptions, carOptions[0]);
        if (selectedCar == null) return;

        String carId = selectedCar.split(" ")[0];
        Car car = rentalSystem.findCarById(carId);
        if (car == null || !car.isAvailable()) return;

        String rentalDaysStr = JOptionPane.showInputDialog(this, "Enter number of days for rental:");
        if (rentalDaysStr == null || rentalDaysStr.trim().isEmpty()) return;

        int rentalDays;
        try {
            rentalDays = Integer.parseInt(rentalDaysStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of days.");
            return;
        }

        double totalPrice = car.calculatePrice(rentalDays);
        int confirm = JOptionPane.showConfirmDialog(this, String.format("Total Price: $%.2f\nConfirm rental by %s?", totalPrice, existingCustomer.getName()),
                "Confirm Rental", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            rentalSystem.rentCar(car, existingCustomer, rentalDays);
            JOptionPane.showMessageDialog(this, "Car rented successfully by " + existingCustomer.getName());
        }
    }

    private void returnCar() {
        String customerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (customerName == null || customerName.trim().isEmpty()) return;

        Customer customer = rentalSystem.findCustomerByName(customerName);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "No rentals found for the specified customer.");
            return;
        }

        List<Car> rentedCars = rentalSystem.getRentedCarsByCustomer(customer);
        if (rentedCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars rented by this customer.");
            return;
        }

        String[] carOptions = rentedCars.stream().map(Car::toString).toArray(String[]::new);
        String selectedCar = (String) JOptionPane.showInputDialog(this, "Select a car to return:", "Rented Cars",
                JOptionPane.QUESTION_MESSAGE, null, carOptions, carOptions[0]);
        if (selectedCar == null) return;

        String carId = selectedCar.split(" ")[0];
        Car car = rentalSystem.findCarById(carId);
        if (car == null || car.isAvailable()) {
            JOptionPane.showMessageDialog(this, "Invalid car ID or the car is already returned.");
            return;
        }

        rentalSystem.returnCar(car);
        JOptionPane.showMessageDialog(this, "Car returned successfully by " + customer.getName());
    }


    private void displayCars() {
        StringBuilder carList = new StringBuilder();
        for (Car car : rentalSystem.getCars()) {
            carList.append(car);
            if (!car.isAvailable()) { // If the car is rented, append the customer's name
                Customer customer = rentalSystem.findCustomerByCar(car);
                if (customer != null) {
                    carList.append(" by ").append(customer.getName());
                }
            }
            carList.append("\n");
        }
        JOptionPane.showMessageDialog(this, carList.toString(), "All Cars", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayRentedCustomers() {

        StringBuilder rentedCustomersList = new StringBuilder();
        List<String> rentedCustomers = rentalSystem.getCustomersWithRentedCars();
        for (String customerInfo : rentedCustomers) {
            rentedCustomersList.append(customerInfo).append("\n");
        }
        JOptionPane.showMessageDialog(this, rentedCustomersList.toString(), "Customers with Rented Cars", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addCar() {
        JTextField carIdField = new JTextField(10);
        JTextField brandField = new JTextField(10);
        JTextField modelField = new JTextField(10);
        JTextField priceField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Car ID:"));
        panel.add(carIdField);
        panel.add(new JLabel("Brand:"));
        panel.add(brandField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Price per day:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Car", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String carId = carIdField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            int price = (int) Double.parseDouble(priceField.getText());

            Car newCar = new Car(carId, brand, model, price, true);
            rentalSystem.addCar(newCar);
            JOptionPane.showMessageDialog(this, "Car added successfully.");
        }
    }

    private void addManager() {
        JTextField emailField = new JTextField(20);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Manager Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Manager", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email cannot be empty.");
                return;
            }

            // Save the manager's email to the file
            rentalSystem.saveManagerEmailToFile(email);
            JOptionPane.showMessageDialog(this, "Manager added successfully.");
        }
    }




    private void removeCar() {
        List<Car> allCars = rentalSystem.getCars();
        if (allCars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available to remove.");
            return;
        }

        String[] carOptions = allCars.stream().map(Car::toString).toArray(String[]::new);
        String selectedCar = (String) JOptionPane.showInputDialog(this, "Select a car to remove:", "Remove Car",
                JOptionPane.QUESTION_MESSAGE, null, carOptions, carOptions[0]);
        if (selectedCar == null) return;

        String carId = selectedCar.split(" ")[0];
        Car car = rentalSystem.findCarById(carId);
        if (car == null) {
            JOptionPane.showMessageDialog(this, "Invalid car ID.");
            return;
        }

        rentalSystem.removeCar(car);
        rentalSystem.saveCarsToFile("cars.txt"); // Save the updated car information to the text file
        JOptionPane.showMessageDialog(this, "Car with ID " + carId + " removed successfully.");
    }

    private void viewRentalHistory() {
        String customerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (customerName == null || customerName.trim().isEmpty()) return;

        Customer customer = rentalSystem.findCustomerByName(customerName);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "No rental history found for the specified customer.");
            return;
        }

        List<Rental> rentalHistory = rentalSystem.getRentalsByCustomer(customer);
        if (rentalHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rental history found for the specified customer.");
            return;
        }

        StringBuilder history = new StringBuilder();
        for (Rental rental : rentalHistory) {
            history.append("Car: ").append(rental.getCar().getBrand()).append(" ").append(rental.getCar().getModel())
                    .append(" | Rental Days: ").append(rental.getDays()).append("\n");
        }

        JOptionPane.showMessageDialog(this, history.toString(), "Rental History", JOptionPane.INFORMATION_MESSAGE);
    }


}