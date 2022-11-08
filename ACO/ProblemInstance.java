import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProblemInstance {

    private ArrayList<Customer> customers;

    private double[][] distanceMatrix;

    public ProblemInstance() {
        this.customers = readCustomers();
        this.distanceMatrix = readDistanceMatrix();
    }

    public int getNumberOfCities() {
        return distanceMatrix.length;
    }

    public double getDistance(int from, int to) {
        return distanceMatrix[from][to];
    }

    public Customer getCustomer(int index) {
        return customers.get(index);
    }

    /*
     * calculate the distance matrix from the customer list
     */
    private double[][] readDistanceMatrix() {
        double[][] distanceMatrixTmp = new double[customers.size()][customers.size()];
        int counter1 = 0;
        for (Customer c : this.customers) {
            int counter2 = 0;
            for (Customer c2 : this.customers) {
                distanceMatrixTmp[counter1][counter2] = Customer.getDistance(c, c2);
                counter2++;
            }
            counter1++;
        }
        return distanceMatrixTmp;
    }

    /*
     * Reads the customers from the file and returns them as an ArrayList of customer objects
     */
    public ArrayList<Customer> readCustomers() {
        ArrayList<Customer> customers = new ArrayList<Customer>();
        try {
            Scanner sc = new Scanner(new File(
                    Configuration.INSTANCE.dataDirectory + Configuration.INSTANCE.fileSeparator + "instance.txt"));
            String tmp = sc.nextLine(); // skip first line
            ArrayList<String[]> tmpList = new ArrayList<String[]>();
            while (sc.hasNext()) {
                String[] line = sc.nextLine().split("\\s+");
                tmpList.add(line);

                customers.add(new Customer((int) (Double.parseDouble(line[1]) - 1), (int) Double.parseDouble(line[2]),
                        (int) Double.parseDouble(line[3]), (int) Double.parseDouble(line[4]),
                        (int) Double.parseDouble(line[5]), (int) Double.parseDouble(line[6]),
                        (int) Double.parseDouble(line[7])));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

}