import java.util.ArrayList;

public class Truck {
    private int id;
    private int capacity;
    private ArrayList<Customer> customers;
    public Truck(int id, ArrayList<Customer> customers){
        this.id = id;
        this.capacity = Configuration.INSTANCE.capacity;
        this.customers = customers;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void reduceCapacity(int capacity) {
        this.capacity -= capacity;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", customers=" + customers +
                '}';
    }

    public String customerString(){
        String s = id +": [\n";
        for (Customer c : customers){
            s += c.getId() + "{"  + ", \n"
            + "x: " + c.getX() + ", \n"
            + "y: " + c.getY() + ", \n"
            + "demand: " + c.getDemand() + ", \n"
            + "readyTime: " + c.getReadyTime() + ", \n"
            + "dueDate: " + c.getDueDate() + ", \n"
            + "serviceTime: " + c.getServiceTime() + "\n }";
        }
        s += "\n],";
        return s;
    }

    public boolean isValidTrip(){
        int totalDemand = 0;
        int time = 0;
        int counter = 1;
        for (Customer customer : customers) {
            totalDemand += customer.getDemand();

            // if(counter <= customers.size()){
            //     // time += Customer.getDistance(customer, customers.get(counter));
            // }
            // if(time > customer.getDueDate()){
            //     // System.out.println("DUE DATE FAIl");
            //     return false;
            // }
            // if(time < customer.getReadyTime()){
            //     System.out.println("READY TIME FAIL");
            //     return false;
            // }
            time+=customer.getServiceTime();
            counter++;
        }
        // System.out.println("here");
        if(totalDemand > capacity){
            return false;
        }
        return totalDemand <= capacity;
    }

}
