import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chromosome implements Comparable<Chromosome> {
    private final ArrayList<Truck> gene;
    private final int fitness;
    private Customer depot;

    public Chromosome(ArrayList<Truck> gene, Customer depot) {
        this.gene = gene;
        fitness = calculateFitness(gene);
        this.depot = depot;
    }

    private static int calculateFitness(ArrayList<Truck> gene) {
        int fitness = 0; //final fitness value
        float totalDist = 0; //total distance traveled
        float timePenalty = 0; //total time penalty
        for (Truck truck : gene) {
            float time = 0; //reset time for each truck
            for (int i = 0; i < truck.getCustomers().size()-1; i++) {
                totalDist = totalDist + Customer.getDistance(truck.getCustomers().get(i), truck.getCustomers().get(i + 1)); //increment distance

                if(time > truck.getCustomers().get(i+1).getDueDate()){
                    timePenalty += Math.abs(truck.getCustomers().get(i+1).getDueDate()-time); //set time to ready time if it is less than ready time
                }
                if(time < truck.getCustomers().get(i+1).getReadyTime()){
                    time = truck.getCustomers().get(i+1).getReadyTime();
                    // timePenalty += Math.abs(truck.getCustomers().get(i).getReadyTime()-time); //set time to ready time if it is less than ready time
                }
                time += truck.getCustomers().get(i+1).getServiceTime();
            }
        }
        fitness = (int)(totalDist)  + 30*(int)(timePenalty);
        return fitness;
    }

    protected static Chromosome generateRandom(ArrayList<Customer> customersMain, Customer depot) {
        ArrayList<Truck> trucks = new ArrayList<>();
        do{
            trucks = new ArrayList<Truck>();
            int numCustLeft = customersMain.size(); // keep track of number of customers
            ArrayList<Customer> customers = new ArrayList<Customer>();
            ArrayList<Customer> customersCopy = new ArrayList<Customer>();
            for (Customer c : customersMain) {
                customers.add(c.clone()); // clone customers so we don't mess up the original
            }
            for (Customer c : customersMain) {
                customersCopy.add(c.clone()); // clone customers so we don't mess up the original
            }
        
            for (int i = 0; i < Configuration.INSTANCE.numberOfTrucks; i++) {
                int assignToTruck = Configuration.INSTANCE.assignToTruck; // assign 10 customers to each truck
                if(i == Configuration.INSTANCE.numberOfTrucks - 1){
                    assignToTruck = numCustLeft; // assign remaining customers to last truck
                }
                Truck truckTmp = new Truck(i, new ArrayList<Customer>());
                ArrayList<Integer> custToTruckIndex = new ArrayList<Integer>();
                
                customersCopy = new ArrayList<Customer>();
                for (Customer c : customers) {
                    customersCopy.add(c.clone()); // clone customers so we don't mess up the original
                }
                int tmpCustLeft = numCustLeft;
                ArrayList<Customer> custToTruck = new ArrayList<Customer>();
                
                custToTruckIndex = new ArrayList<Integer>();
                custToTruck.add(depot.clone());
                    for(int j = 0; j < assignToTruck; j++){
                        int index = Configuration.INSTANCE.randomGenerator.nextInt(tmpCustLeft);
                        Customer cust = customersCopy.get(index).clone();
                        customersCopy.remove(index);
                        custToTruck.add(cust.clone());
                        custToTruckIndex.add(index);
                        tmpCustLeft--;
                    }
                custToTruck.add(depot.clone());
                truckTmp = new Truck(i, custToTruck);
                    
                for(Integer index : custToTruckIndex){
                    customers.remove((int)index);
                }
                if( truckTmp.getCustomers().size() == 0){
                    System.out.println("ERROR");
                }
                trucks.add(truckTmp);
                numCustLeft = numCustLeft-assignToTruck;
            } 
        }while(!checkValidity(trucks) || !hasAllCustomers(trucks));
        //Check if route is valid
        ArrayList<Integer> tmpCheck = new ArrayList<Integer>();
        for(Truck truck : trucks){
            for(Customer c: truck.getCustomers()){
                
                if(tmpCheck.contains(c.getId()) && c.getId() != 0){
                    System.out.println("ERROR NOT ALL CUSTOMERS CHECKED");
                    System.exit(0);
                }
                tmpCheck.add(c.getId());
            }
        }
        Collections.sort(tmpCheck);
        return new Chromosome(trucks, depot);
    }

    public ArrayList<Truck> getGene() {
        return gene;
    }

    public int getFitness() {
        return fitness;
    }

    public Chromosome[] doCrossover(Chromosome chromosome) {
        ArrayList<Truck> gene1 = chromosome.getGene(); // get gene from chromosome
        ArrayList<Truck> gene2 = this.getGene(); // get gene from this chromosome
        gene1 = chromosome.clone().getGene();
        gene2 = this.clone().getGene();
        ArrayList<Truck> gene1New = orderCrossOver(gene1, gene2, depot);
        ArrayList<Truck> gene2New = orderCrossOver(gene2, gene1, depot);
        while (!checkValidity(gene1New) || !checkValidity(gene2New) || !hasAllCustomers(gene2New) || !hasAllCustomers(gene1New)) {
            gene1New = orderCrossOver(gene1, gene2, depot);
            gene2New = orderCrossOver(gene2, gene1, depot);
        }

        if(gene1New.get(0).getCustomers().size() != Configuration.INSTANCE.assignToTruck + 2 || gene2New.get(0).getCustomers().size() != Configuration.INSTANCE.assignToTruck + 2){
            System.out.println("ERROR in fn");
        }
        
        Chromosome [] x = {
            new Chromosome(gene1New, depot), 
            new Chromosome(gene2New, depot)
        };
        return (x);
    }

    /* generate a child by order cross over (OX).
     First randomly select a section from gene1 and copy it to child gene.
     Then copy the remaining customers from gene2 to child gene.
     We need to ensure that customers are only serviced once by all trucks and that each truck has a valid trip.
     Each truck needs to start and end at the depot which has customer id 0.*/ 
    public static ArrayList<Truck> orderCrossOver(ArrayList<Truck> gene1, ArrayList<Truck> gene2, Customer depot){
        ArrayList<Truck> child = new ArrayList<Truck>();

        ArrayList<Customer> AllCustomers1 = new ArrayList<Customer>();
        ArrayList<Customer> AllCustomers2 = new ArrayList<Customer>();

        for (Truck truck : gene1) {
            for (Customer customer : truck.getCustomers()) {
                if(customer.getId() != 0){ 
                    AllCustomers1.add(customer.clone());
                }
               
            }
        }
        
        for (Truck truck : gene2) {
            for (Customer customer : truck.getCustomers()) {
                if(customer.getId() != 0){
                    AllCustomers2.add(customer.clone());
                }
            }
        }

        int pos1 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-1);
        int pos2 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-1);

        int start = Math.max(1, Math.min(pos1, pos2));
        int end = Math.min(AllCustomers1.size()-2, Math.max(pos1, pos2));

        ArrayList<Customer> childCustomers = new ArrayList<Customer>();
        ArrayList<Integer> childCustomersTmp = new ArrayList<Integer>();

        //Extract region from Gene1
        for (int i = start; i < end; i++) {
            childCustomersTmp.add(AllCustomers1.get(i).getId());
        }
        //
        int counterStart = 0;
        while(counterStart < start){
            //Check that customer is not in region
            if(!childCustomersTmp.contains(AllCustomers2.get(counterStart).getId())){
                childCustomers.add(AllCustomers2.get(counterStart).clone());
                
            }
            counterStart++;
        }
        for (int i = start; i < end; i++) {
            childCustomers.add(AllCustomers1.get(i));
        }

        while(counterStart < AllCustomers2.size()){
            if(!childCustomersTmp.contains(AllCustomers2.get(counterStart).getId())){
                childCustomers.add(AllCustomers2.get(counterStart).clone());
                
            }
            counterStart++;
        }

        int assignToTruck = Configuration.INSTANCE.assignToTruck;
        int counter = 0;
        Truck truckTmp;
        for (int i = 0; i < Configuration.INSTANCE.numberOfTrucks; i++){
            ArrayList<Customer> custToTruck = new ArrayList<Customer>();
            custToTruck.add(depot);
            for(int j = 0; j < assignToTruck; j++ ){
                custToTruck.add(childCustomers.get(counter).clone());
                counter++;
            }
            custToTruck.add(depot);
            truckTmp = new Truck(i, custToTruck);
            child.add(truckTmp);
        }
        if(child.get(0).getCustomers().get(0).getId() != 0){
            System.out.println("ERROR IN CROSSOVER: " + child.get(0).getCustomers().get(0).getId());
        }
        return child;
        
    }

    // public Chromosome doMutation() {
    //     ArrayList<Truck> geneTmp  = this.clone().getGene();
    //     //swap mutation
    //     do{
    //         geneTmp  = this.clone().getGene();
    //         int truck1 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks);
    //         int truck2 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks);
    //         int customer1 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.assignToTruck));
    //         int customer2 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.assignToTruck));
    //         Customer tmp = geneTmp.get(truck1).getCustomers().get(customer1);
    //         geneTmp.get(truck1).getCustomers().set(customer1, geneTmp.get(truck2).getCustomers().get(customer2));
    //         geneTmp.get(truck2).getCustomers().set(customer2, tmp);
            
    //     }while(!checkValidity(geneTmp) || !hasAllCustomers(geneTmp));
    //     return new Chromosome(geneTmp, depot);
    // }

    public Chromosome doMutation() {
        ArrayList<Truck> geneTmp  = new ArrayList<Truck>();
        ArrayList<Customer> custTmp = new ArrayList<Customer>();
        do{
            int truckIndex1 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks);
            int truckIndex2 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks);
            Truck truck1 = this.getGene().get(truckIndex1);
            Truck truck2 = this.getGene().get(truckIndex2);
            int customerIndex1 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(truck1.getCustomers().size()-1));
            int customerIndex2 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(truck2.getCustomers().size()-1));
            Customer customer1 = truck1.getCustomers().get(customerIndex1).clone();
            Customer customer2 = truck2.getCustomers().get(customerIndex2).clone();

            ArrayList<Customer> customers1 = new ArrayList<Customer>();
            ArrayList<Customer> customers2 = new ArrayList<Customer>();
            // System.out.println("this");
            for(int k = 0; k < truck1.getCustomers().size(); k++){
                // System.out.println("this " + k + "\t" + truck1.getCustomers().size());
                if(k == customerIndex1){
                    customers1.add(customer2.clone());
                }else{
                customers1.add(truck1.getCustomers().get(k).clone());
                }
            }
            for(int i = 0; i < truck2.getCustomers().size(); i++){
                if(i == customerIndex2){
                    customers2.add(customer1.clone());
                }else{
                    customers2.add(truck2.getCustomers().get(i).clone());
                }
            }
            ArrayList<Truck> newGene = new ArrayList<Truck>();
            for(int i = 0; i < Configuration.INSTANCE.numberOfTrucks; i++){
                if(i == truckIndex1){
                    newGene.add(new Truck(truckIndex1, customers1));
                }
                if(i == truckIndex2){
                    newGene.add(new Truck(truckIndex2, customers2));
                }
                if(i != truckIndex1 && i != truckIndex2){
                    newGene.add(this.gene.get(i));
                }
            }
            geneTmp = newGene;
        }while(!checkValidity(geneTmp) || !hasAllCustomers(geneTmp));

        if(geneTmp.get(0).getCustomers().size() != Configuration.INSTANCE.assignToTruck + 2){
            System.out.println("ERROR IN MUTATION: " + geneTmp.get(0).getCustomers().size());
        }
        return new Chromosome(geneTmp, depot);
    }

    public static ArrayList<Customer> swapMutation(ArrayList<Customer> customers){
        for(int i = 1; i < customers.size()-1; i++){
            // int index1 = Math.min(1,Configuration.INSTANCE.randomGenerator.nextInt(customers.size()));
            int index1 = i;
            int index2 = Math.min(1,Configuration.INSTANCE.randomGenerator.nextInt(customers.size()));
            Customer tmp = customers.get(index1);
            customers.set(index1, customers.get(index2));
            customers.set(index2, tmp);
        }
        return customers;
    }


    public int compareTo(Chromosome chromosome) {
        return Integer.compare(fitness, chromosome.fitness);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Chromosome chromosome)) {
            return false;
        }

        return (gene.equals(chromosome.gene)) && (fitness == chromosome.fitness);
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        String tmp = "";
        for (Truck truck : gene) {
            tmp += truck.toString() + "";
        }
        return tmp;
    }

    public String getRoutes(){
        String tmp = "";
        for (Truck truck : gene) {
            tmp += truck.getId() + "\t" + "[ ";
            for(Customer customer : truck.getCustomers()){
                tmp += customer.getId() + ",";
            }
            tmp += " ]\n";
        }
        tmp += Chromosome.calculateFitness(gene) + "\n Valid: \t" + Chromosome.hasAllCustomers(this.gene) + "\n" + checkTimeWindows() + "\n Length:" + calculateRouteLength();
        return tmp;
    }

    public String checkTimeWindows(){
        for(Truck t : gene){
            if(!t.checkTimeWindows()){
                return "Failed Time window";
            }
        }
        return "TIME WINDOW PASSED";
    }

    public static boolean checkValidity(ArrayList<Truck> gene){
        for(Truck t : gene){
            if(!t.isValidTrip()){
                return false;
            }
        }
        return true;
    }

    public static boolean hasAllCustomers(ArrayList<Truck> gene){
        ArrayList<Integer> allCustomers = new ArrayList<Integer>();
        for (Truck truck : gene) {
            for (Customer customer : truck.getCustomers()) {
                if(allCustomers.contains(customer.getId())){
                    return false;
                }
                if(customer.getId() != 0){
                    allCustomers.add(customer.getId());
                }
                
            }
        }
        if(allCustomers.size() == Configuration.INSTANCE.numberOfCustomers){
            return true;
        }
        return false;
    }

    private int calculateRouteLength() {
        ArrayList<Truck> gene = this.gene;
        float totalDist = 0; //total distance traveled
        for (Truck truck : gene) {
            for (int i = 0; i < truck.getCustomers().size()-1; i++) {
                totalDist = totalDist + Customer.getDistance(truck.getCustomers().get(i), truck.getCustomers().get(i + 1)); //increment distance
            }
        }
        return (int)totalDist;
    }

    //Clone method
    public Chromosome clone() {
        ArrayList<Truck> newGene = new ArrayList<Truck>();
        for (Truck truck : gene) {
            newGene.add(truck.clone());
        }
        return new Chromosome(newGene, depot);
    }
}