public class Customer {
    private int id;
    private int x;
    private int y;
    private int demand;
    private int readyTime;
    private int dueDate;
    private int serviceTime;


    public Customer(int id, int x, int y, int demand, int readyTime, int dueDate, int serviceTime){
        this.id = id;
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.readyTime = readyTime;
        this.dueDate = dueDate;
        this.serviceTime = serviceTime;
    }

    public int getId() {
        return id;
    }

    public static float getDistance(Customer c1, Customer c2){
        return (float) Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2) + Math.pow(c1.getY() - c2.getY(), 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDemand() {
        return demand;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public int getDueDate() {
        return dueDate;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", demand=" + demand +
                ", readyTime=" + readyTime +
                ", dueDate=" + dueDate +
                ", serviceTime=" + serviceTime +
                '}';
    }

    public Customer clone(){
        return new Customer(this.id, this.x, this.y, this.demand, this.readyTime, this.dueDate, this.serviceTime);
    }
}
