import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. Sum Balance");
            System.out.println("4. DeleteBalance");
            System.out.println("5. EditData");
            System.out.println("6. Exit");

            System.out.print("Enter you choice: " );
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    Transaction.add(sc);
                    break;
                case 2:
                    Transaction.view(sc);
                    break;
                case 3:
                    Transaction.summation(sc);
                    break;
                case 4:
                    Transaction.deleteData(sc);
                    break;
                case 5:
                    Transaction.updateData(sc);
                    break;
                case 6:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}