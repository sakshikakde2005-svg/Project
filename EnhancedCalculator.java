import java.util.Scanner;

public class EnhancedCalculator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n Enhanced Console-based Calculator ");
            System.out.println("1. Basic Arithmetic Operations");
            System.out.println("2. Scientific Calculations");
            System.out.println("3. Unit Conversions");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    basicOperations(sc);
                    break;
                case 2:
                    scientificOperations(sc);
                    break;
                case 3:
                    unitConversions(sc);
                    break;
                case 4:
                    System.out.println("Exiting program... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice! Try again.");
            }
        } while (choice != 4);

        sc.close();
    }

    // BASIC ARITHMETIC 
    public static void basicOperations(Scanner sc) {
        System.out.println("\n Basic Arithmetic Operations");
        System.out.print("Enter first number: ");
        double num1 = sc.nextDouble();
        System.out.print("Enter second number: ");
        double num2 = sc.nextDouble();

        System.out.println("Choose operation: +  -  *  /");
        char op = sc.next().charAt(0);

        try {
            switch (op) {
                case '+':
                    System.out.println("Result: " + (num1 + num2));
                    break;
                case '-':
                    System.out.println("Result: " + (num1 - num2));
                    break;
                case '*':
                    System.out.println("Result: " + (num1 * num2));
                    break;
                case '/':
                    if (num2 == 0)
                        System.out.println("Error: Division by zero!");
                    else
                        System.out.println("Result: " + (num1 / num2));
                    break;
                default:
                    System.out.println("Invalid operator!");
            }
        } catch (Exception e) {
            System.out.println("Error performing calculation: " + e.getMessage());
        }
    }

    // SCIENTIFIC CALCULATIONS 
    public static void scientificOperations(Scanner sc) {
        System.out.println("\n--- Scientific Calculations ---");
        System.out.println("1. Square Root");
        System.out.println("2. Power (x^y)");
        System.out.println("3. Exponential (e^x)");
        System.out.println("4. Logarithm (base 10)");
        System.out.print("Enter your choice: ");
        int sciChoice = sc.nextInt();

        switch (sciChoice) {
            case 1:
                System.out.print("Enter number: ");
                double num = sc.nextDouble();
                if (num < 0)
                    System.out.println("Error: Negative number!");
                else
                    System.out.println("√" + num + " = " + Math.sqrt(num));
                break;

            case 2:
                System.out.print("Enter base: ");
                double base = sc.nextDouble();
                System.out.print("Enter exponent: ");
                double exp = sc.nextDouble();
                System.out.println(base + "^" + exp + " = " + Math.pow(base, exp));
                break;

            case 3:
                System.out.print("Enter number: ");
                double eNum = sc.nextDouble();
                System.out.println("e^" + eNum + " = " + Math.exp(eNum));
                break;

            case 4:
                System.out.print("Enter number: ");
                double logNum = sc.nextDouble();
                if (logNum <= 0)
                    System.out.println("Error: Logarithm undefined for non-positive numbers!");
                else
                    System.out.println("log10(" + logNum + ") = " + Math.log10(logNum));
                break;

            default:
                System.out.println("Invalid choice!");
        }
    }

    // UNIT CONVERSIONS 
    public static void unitConversions(Scanner sc) {
        System.out.println("\n--- Unit Conversions ---");
        System.out.println("1. Temperature (Celsius <-> Fahrenheit)");
        System.out.println("2. Currency (INR <-> USD)");
        System.out.print("Enter your choice: ");
        int convChoice = sc.nextInt();

        switch (convChoice) {
            case 1:
                System.out.println("1. Celsius to Fahrenheit");
                System.out.println("2. Fahrenheit to Celsius");
                System.out.print("Choose option: ");
                int tempChoice = sc.nextInt();

                if (tempChoice == 1) {
                    System.out.print("Enter temperature in Celsius: ");
                    double c = sc.nextDouble();
                    double f = (c * 9 / 5) + 32;
                    System.out.println("Fahrenheit: " + f);
                } else if (tempChoice == 2) {
                    System.out.print("Enter temperature in Fahrenheit: ");
                    double f = sc.nextDouble();
                    double c = (f - 32) * 5 / 9;
                    System.out.println("Celsius: " + c);
                } else {
                    System.out.println("Invalid option!");
                }
                break;

            case 2:
                System.out.println("1. INR to USD");
                System.out.println("2. USD to INR");
                System.out.print("Choose option: ");
                int currChoice = sc.nextInt();

                System.out.print("Enter amount: ");
                double amount = sc.nextDouble();

                // Example conversion rate
                double rate = 83.0; // 1 USD = 83 INR (can be updated)

                if (currChoice == 1)
                    System.out.println("USD: " + (amount / rate));
                else if (currChoice == 2)
                    System.out.println("INR: " + (amount * rate));
                else
                    System.out.println("Invalid option!");
                break;

            default:
                System.out.println("Invalid choice!");
        }
    }
}
