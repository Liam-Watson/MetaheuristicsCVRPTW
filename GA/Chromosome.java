import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chromosome implements Comparable<Chromosome> {
    private final ArrayList<Truck> gene;
    private final int fitness;

    public Chromosome(ArrayList<Truck> gene) {
        this.gene = gene;
        fitness = calculateFitness(gene);
        
    }

    private static int calculateFitness(ArrayList<Truck> gene) {
        int fitness = 0;
        float totalDist = 0;
        // System.out.println("Gene length fitness: " + gene.size());
        float time = 0;
        float timePenalty = 0;
        for (Truck truck : gene) {
            for (int i = 0; i < truck.getCustomers().size()-1; i++) {
                totalDist = totalDist + Customer.getDistance(truck.getCustomers().get(i), truck.getCustomers().get(i + 1));
                timePenalty += Math.max(0, time - truck.getCustomers().get(i).getDueDate());
                timePenalty += Math.max(0, truck.getCustomers().get(i).getReadyTime() - time);
                time += truck.getCustomers().get(i).getServiceTime();
                // System.out.println("FITENESS" + totalDist);
            }
            // if(truck.getCustomers().size() > 0){
            //     totalDist = totalDist + Customer.getDistance(truck.getCustomers().get(0), truck.getCustomers().get(truck.getCustomers().size() - 1));
            //     // System.out.println("FITENESS" + totalDist);
            // }
        }
        fitness = (int) totalDist;// + (int)timePenalty;
        // System.out.println("FITENESS" + totalDist);
        return fitness;
    }

    protected static Chromosome generateRandom(ArrayList<Customer> customersMain) {
        ArrayList<Truck> trucks = new ArrayList<>();
        do{
            trucks = new ArrayList<Truck>();
            int numCustLeft = customersMain.size()-1; // keep track of number of customers
            ArrayList<Customer> customers = new ArrayList<Customer>();
            ArrayList<Customer> customersCopy = new ArrayList<Customer>();
            for (Customer c : customersMain) {
                customers.add(c.clone()); // clone customers so we don't mess up the original
            }
            for (Customer c : customersMain) {
                customersCopy.add(c.clone()); // clone customers so we don't mess up the original
            }
        
            for (int i = 0; i < Configuration.INSTANCE.numberOfTrucks; i++) {
                // int assignToTruck = Configuration.INSTANCE.randomGenerator.nextInt(Math.max(Math.min(numCustLeft, 9),));
                int assignToTruck = 10; // assign 10 customers to each truck
                if(i == Configuration.INSTANCE.numberOfTrucks - 1){
                    assignToTruck = numCustLeft; // assign remaining customers to last truck
                }
                Truck truckTmp = new Truck(i, new ArrayList<Customer>());
                ArrayList<Integer> custToTruckIndex = new ArrayList<Integer>();
                
                // checkValidity
                
                    customersCopy = new ArrayList<Customer>();
                    for (Customer c : customers) {
                        customersCopy.add(c.clone()); // clone customers so we don't mess up the original
                    }
                    int tmpCustLeft = numCustLeft;
                    ArrayList<Customer> custToTruck = new ArrayList<Customer>();
                    
                    custToTruckIndex = new ArrayList<Integer>();
                    custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
                    // custToTruck.add(customers.get(0).clone());
                        for(int j = 0; j < assignToTruck; j++){
                            int index = Configuration.INSTANCE.randomGenerator.nextInt(tmpCustLeft);
                            // System.out.println("index: " +index);
                            // System.out.println("tmpCustLeft: " +tmpCustLeft);
                            // System.out.println("customer size: " +customersCopy.size());
                            Customer cust = customersCopy.get(index).clone();
                            customersCopy.remove(index);
                            custToTruck.add(cust);
                            custToTruckIndex.add(index);
                            tmpCustLeft--;
                        }
                    // custToTruck.add(customers.get(0).clone());
                    custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
                    truckTmp = new Truck(i, custToTruck);
                    // System.out.println("is valid: " + truckTmp.isValidTrip());
                    // System.out.println("length: " + truckTmp.getCustomers().size());
                // }while(!truckTmp.isValidTrip());
                    
                for(Integer index : custToTruckIndex){
                    customers.remove((int)index);
                }
                if( truckTmp.getCustomers().size() == 0){
                    System.out.println("ERROR");
                }
                // System.out.println("Truck " + i + " has " + truckTmp.getCustomers().size() + " customers");
                trucks.add(truckTmp);
                numCustLeft = numCustLeft-assignToTruck;
            } 
        
        }while(!checkValidity(trucks) || !hasAllCustomers(trucks));
        //Check if route is valid
        ArrayList<Integer> tmpCheck = new ArrayList<Integer>();
        for(Truck truck : trucks){
            for(Customer c: truck.getCustomers()){
                
                // System.out.println("Truck " + truck.getId() + " has customer " + c.getId());
                if(tmpCheck.contains(c.getId()) && c.getId() != 0){
                    System.out.println("ERROR NOT ALL CUSTOMERS CHECKED");
                    System.exit(0);
                }
                tmpCheck.add(c.getId());
            }
        }
        Collections.sort(tmpCheck);
        // for(Integer i : tmpCheck){
        //     System.out.println("Customer " + i + " checked");
        // }
        // System.out.println("Truck length: " + trucks.size());
        return new Chromosome(trucks);
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
        
        ArrayList<Truck> gene1New = orderCrossOver(gene1, gene2);
        ArrayList<Truck> gene2New = orderCrossOver(gene2, gene1);
        // System.out.println("Crossover before:\n" + gene1.get(0).getCustomers().get(0).getId() + "\n" + gene2.get(0).getCustomers().get(0).getId());
        // System.out.println("BEFORE CROSSOVER:\n" + Chromosome.hasAllCustomers(gene1) + "\t" + Chromosome.hasAllCustomers(gene2));
        while (!checkValidity(gene1New) || !checkValidity(gene2New) || !hasAllCustomers(gene2New) || !hasAllCustomers(gene1New)) {
            gene1New = orderCrossOver(gene1, gene2);
            gene2New = orderCrossOver(gene2, gene1);
        }

        if(gene1New.get(0).getCustomers().size() != 12 || gene2New.get(0).getCustomers().size() != 12){
            System.out.println("ERROR in fn");
        }
        
        
        // System.out.println("BEFORE CROSSOVER:\n" + Chromosome.hasAllCustomers(gene1New) + "\t" + Chromosome.hasAllCustomers(gene2New));
        // System.out.println("gene1: " + gene2.size());
        // System.out.println("gene2: " + gene1.size());
        // System.out.println("Crossover after:\n" + gene1New.get(0).getCustomers().get(0).getId() + "\n" + gene2New.get(0).getCustomers().get(0).getId());
        Chromosome [] x = {
            new Chromosome(gene1New), 
            new Chromosome(gene2New)
        };
        return (x);
    }

    /* generate a child by order corss over (OX).
     First randomly select a section from gene1 and copy it to child gene.
     Then copy the remaining customers from gene2 to child gene.
     We need to ensure that customers are only serviced once by all trucks and that each truck has a valid trip.
     Each truck needs to start and end at the depot which has customer id 0.*/ 
    public static ArrayList<Truck> orderCrossOver(ArrayList<Truck> gene1, ArrayList<Truck> gene2){
        ArrayList<Truck> child = new ArrayList<Truck>();

        int test1 = gene1.get(0).getCustomers().get(0).getId();
        int test2 = gene2.get(0).getCustomers().get(0).getId();

        ArrayList<Customer> AllCustomers1 = new ArrayList<Customer>();
        ArrayList<Customer> AllCustomers2 = new ArrayList<Customer>();

        for (Truck truck : gene1) {
            for (Customer customer : truck.getCustomers()) {
                if(customer.getId() != 0){ //possible id = 1??
                    AllCustomers1.add(customer.clone());
                }
               
            }
        }
        
        for (Truck truck : gene2) {
            for (Customer customer : truck.getCustomers()) {
                if(customer.getId() != 0){//possible id = 1??
                    AllCustomers2.add(customer.clone());
                }
            }
        }
        // int pos1 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-Configuration.INSTANCE.numberOfTrucks*2);
        // int pos2 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-Configuration.INSTANCE.numberOfTrucks*2);

        int pos1 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-1);
        int pos2 = Configuration.INSTANCE.randomGenerator.nextInt(AllCustomers1.size()-1);

        int start = Math.max(1, Math.min(pos1, pos2));
        int end = Math.min(AllCustomers1.size()-2, Math.max(pos1, pos2));
        // System.out.println("start: " + start);
        // System.out.println("end: " + end);
        ArrayList<Customer> childCustomers = new ArrayList<Customer>();
        // ArrayList<Customer> childCustomersTmp = new ArrayList<Customer>();
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
        // System.out.println("counterStart: " + counterStart);
        for (int i = start; i < end; i++) {
            childCustomers.add(AllCustomers1.get(i));
        }

        while(counterStart < AllCustomers2.size()){
            if(!childCustomersTmp.contains(AllCustomers2.get(counterStart).getId())){
                childCustomers.add(AllCustomers2.get(counterStart).clone());
                
            }
            counterStart++;
        }

        // System.out.println("childCustomers: " + childCustomers.size());
        //TODO: FIX THIS
        //Create trucks from childCustomers
        int assignToTruck = 10;
        int counter = 0;
        Truck truckTmp;
        for (int i = 0; i < Configuration.INSTANCE.numberOfTrucks; i++){
            ArrayList<Customer> custToTruck = new ArrayList<Customer>();
            custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
            for(int j = 0; j < assignToTruck; j++ ){
                custToTruck.add(childCustomers.get(counter).clone());
                counter++;
            }
            custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
            truckTmp = new Truck(i, custToTruck);
            // System.out.println("Truck " + i + " has " + truckTmp.getCustomers().size() + " customers");
            child.add(truckTmp);
        }
        if(child.get(0).getCustomers().get(0).getId() != 0){
            System.out.println("ERROR IN CROSSOVER: " + child.get(0).getCustomers().get(0).getId());
        }
        // System.out.println(child.size());
        return child;
        
    }

    public Chromosome doMutation() {
        ArrayList<Truck> geneTmp  = new ArrayList<Truck>();
        ArrayList<Customer> custTmp = new ArrayList<Customer>();
        // System.out.println("BEFORE MURTATION:\n" + Chromosome.hasAllCustomers(this.gene) );
        do{
            for (Truck truck : this.gene) {
                Truck truckTmp = null;
                custTmp = new ArrayList<Customer>();
                custTmp.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
                for (Customer customer : truck.getCustomers()) {
                    if(customer.getId() != 0){
                        custTmp.add(customer.clone());
                    }
                    
                }
                custTmp.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
                truckTmp = new Truck(truck.getId(), custTmp);
                geneTmp.add(truckTmp);
            }
            for(Truck t: geneTmp){
                t.setCustomers(Chromosome.swapMutation(t.getCustomers()));
            }
        }while(!checkValidity(geneTmp) || !hasAllCustomers(geneTmp));
        // System.out.println("Mutation length" + gene.size());
        // System.out.println("AFTER MURTATION:\n" + Chromosome.hasAllCustomers(geneTmp));
        if(geneTmp.get(0).getCustomers().size() != 12){
            System.out.println("ERROR IN MUTATION: " + geneTmp.get(0).getCustomers().size());
        }

        return new Chromosome(geneTmp);
    }

    public static ArrayList<Customer> swapMutation(ArrayList<Customer> customers){
        int index1 = Math.min(1,Configuration.INSTANCE.randomGenerator.nextInt(customers.size()-1));
        int index2 = Math.min(1,Configuration.INSTANCE.randomGenerator.nextInt(customers.size()-1));
        Customer tmp = customers.get(index1);
        customers.set(index1, customers.get(index2));
        customers.set(index2, tmp);
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
        // String tmp = "";
        // for (Truck truck : gene) {
        //     tmp += truck.toString() + "";
        // }
        return "Chromosome{" +
                // "gene=" + gene.size() +
                ", fitness=" + fitness +
                '}';
    }

    public String getRoutes(){
        // String tmp = "Trucks: [\n";
        // for (Truck truck : gene) {
        //     tmp += truck.customerString() + ", \n";
        // }
        // tmp += "\n],";
        // return tmp;
        String tmp = "";
        for (Truck truck : gene) {
            tmp += truck.getId() + "\t" + "[ ";
            for(Customer customer : truck.getCustomers()){
                tmp += customer.getId() + ",";
            }
            tmp += " ]\n";
        }
        tmp += Chromosome.calculateFitness(gene) + "\n Valid: \t" + Chromosome.hasAllCustomers(this.gene);
        return tmp;
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
}