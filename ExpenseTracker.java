import java.io.*;
import java.util.*;

class Expense {
    private String date;
    private String category;
    private double amount;

    public Expense(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Date: " + date + ", Category: " + category + ", Amount: $" + amount;
    }


    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}


class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Username: " + username;
    }
}


class FileHandler {
    private static final String USERS_FILE = "users.txt";
    private static final String EXPENSES_FILE = "expenses.txt";

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
           
        }
        return users;
    }

 
    public static void saveExpenses(Map<String, List<Expense>> expenses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXPENSES_FILE))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 
    public static Map<String, List<Expense>> loadExpenses() {
        Map<String, List<Expense>> expenses = new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXPENSES_FILE))) {
            expenses = (Map<String, List<Expense>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
           
        }
        return expenses;
    }
}

public class ExpenseTracker {
    private List<User> users;
    private Map<String, List<Expense>> expenses;

    public ExpenseTracker() {
        
        users = FileHandler.loadUsers();
        expenses = FileHandler.loadExpenses();
        if (users == null) users = new ArrayList<>();
        if (expenses == null) expenses = new HashMap<>();
    }

   
    public void registerUser(String username, String password) {
        User newUser = new User(username, password);
        users.add(newUser);
        FileHandler.saveUsers(users);
        System.out.println("User registered successfully: " + newUser);
    }

  
    public boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    
    public void addExpense(String username, String date, String category, double amount) {
        Expense newExpense = new Expense(date, category, amount);
        if (!expenses.containsKey(username)) {
            expenses.put(username, new ArrayList<>());
        }
        expenses.get(username).add(newExpense);
        FileHandler.saveExpenses(expenses);
        System.out.println("Expense added successfully: " + newExpense);
    }

   
    public void listExpenses(String username) {
        if (expenses.containsKey(username)) {
            List<Expense> userExpenses = expenses.get(username);
            System.out.println("Expenses for " + username + ":");
            for (Expense expense : userExpenses) {
                System.out.println(expense);
            }
        } else {
            System.out.println("No expenses found for user: " + username);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseTracker expenseTracker = new ExpenseTracker();

        System.out.println("Welcome to Expense Tracker!");

        boolean exit = false;
        while (!exit) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String newPassword = scanner.nextLine();
                    expenseTracker.registerUser(newUsername, newPassword);
                    break;
                case 2:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    if (expenseTracker.authenticateUser(username, password)) {
                        System.out.println("Login successful!");
                        boolean loggedIn = true;
                        while (loggedIn) {
                            System.out.println("\n1. Add Expense\n2. List Expenses\n3. Logout");
                            System.out.print("Choose option: ");
                            int userChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (userChoice) {
                                case 1:
                                    System.out.print("Enter date (MM-DD-YYYY): ");
                                    String date = scanner.nextLine();
                                    System.out.print("Enter category: ");
                                    String category = scanner.nextLine();
                                    System.out.print("Enter amount: ");
                                    double amount = scanner.nextDouble();
                                    expenseTracker.addExpense(username, date, category, amount);
                                    break;
                                case 2:
                                    expenseTracker.listExpenses(username);
                                    break;
                                case 3:
                                    loggedIn = false;
                                    break;
                                default:
                                    System.out.println("Invalid option.");
                            }
                        }
                    } else {
                        System.out.println("Login failed. Invalid username or password.");
                    }
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        System.out.println("Exiting Expense Tracker. Goodbye!");
        scanner.close();
    }
}
