import java.io.*;
import java.util.ArrayList;
import java.util.List;

class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;


    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }
    public void removeCar(Car car) {
        cars.remove(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));
            saveRentalsToFile("rentals.txt");
            saveCustomersToFile("customers.txt");
        }
    }

    public void returnCar(Car car) {
        Customer customer = findCustomerByCar(car);
        if (customer != null) {
            customers.remove(customer);
            saveCustomersToFile("customers.txt");
        }
        car.returnCar();
        rentals.removeIf(rental -> rental.getCar().equals(car));
    }


    public Car findCarById(String carId) {
        for (Car car : cars) {
            if (car.getCarId().equals(carId)) {
                return car;
            }
        }
        return null;
    }

    public Customer findCustomerByName(String name) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findCustomerByCar(Car car) {
        for (Rental rental : rentals) {
            if (rental.getCar().equals(car)) {
                return rental.getCustomer();
            }
        }
        return null;
    }

    public List<Car> getAvailableCars() {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable()) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public List<String> getCustomersWithRentedCars() {
        List<String> rentedCustomers = new ArrayList<>();
        if (rentals.isEmpty()) {
            rentedCustomers.add("No cars rented by any customer.");
            return rentedCustomers;
        }
        for (Rental rental : rentals) {
            Car car = rental.getCar();
            Customer customer = rental.getCustomer();
            int rentalDays = rental.getDays();
            rentedCustomers.add("Customer: " + customer.getName() + " | Car: " + car.getBrand() + " " + car.getModel() + " | (ID: " + car.getCarId() + ")"+ " | Days:"+ rentalDays);
        }
        return rentedCustomers;
    }

    public List<Car> getRentedCarsByCustomer(Customer customer) {
        List<Car> rentedCars = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCustomer().equals(customer)) {
                rentedCars.add(rental.getCar());
            }
        }
        return rentedCars;
    }

    public List<Rental> getRentalsByCustomer(Customer customer) {
        List<Rental> customerRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCustomer().equals(customer)) {
                customerRentals.add(rental);
            }
        }
        return customerRentals;
    }




    public List<Car> getCars() {
        return cars;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void saveCarsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Car car : cars) {
                writer.write(String.join(",", car.getCarId(), car.getBrand(), car.getModel(), String.valueOf(car.calculatePrice(1)), String.valueOf(car.isAvailable())));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomersToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Customer customer : customers) {
                writer.write(String.join(",", customer.getCustomerId(), customer.getName()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void saveManagerEmailToFile(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("manager.txt", true))) {
            writer.write(email);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveRentalsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Rental rental : rentals) {
                writer.write(String.join(",", rental.getCar().getCarId(), rental.getCustomer().getCustomerId(), String.valueOf(rental.getDays())));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCarsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Car car = new Car(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), true);
                if (Boolean.parseBoolean(parts[4]) == false) {
                    car.rent();
                }
                cars.add(car);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCustomersFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                customers.add(new Customer(parts[0], parts[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRentalsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Car car = findCarById(parts[0]);
                    Customer customer = findCustomerById(parts[1]);
                    int days = Integer.parseInt(parts[2]);
                    if (car != null && customer != null) {
                        rentals.add(new Rental(car, customer, days));
                    } else {
                        System.out.println("Invalid rental information: " + line);
                    }
                } else {
                    System.out.println("Invalid line in rentals file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Customer findCustomerById(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }
}
