import java.io.Console;
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
        int fitness = 0; //final fitness value
        float totalDist = 0; //total distance traveled
        float timePenalty = 0; //total time penalty
        for (Truck truck : gene) {
            float time = 0; //reset time for each truck
            for (int i = 0; i < truck.getCustomers().size()-1; i++) {
                totalDist = totalDist + Customer.getDistance(truck.getCustomers().get(i), truck.getCustomers().get(i + 1)); //increment distance
                //Check if time window is violated
                if(time < truck.getCustomers().get(i).getReadyTime() || time > truck.getCustomers().get(i).getDueDate()){
                    timePenalty+=truck.getCustomers().get(i).getServiceTime(); //increment time penalty based on service period
                }
                time += truck.getCustomers().get(i).getServiceTime();
            }
        }
        fitness = (int) totalDist + (int)(0.15*timePenalty); //fitness is total distance + time penalty
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
                int assignToTruck = 10; // assign 10 customers to each truck
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
                custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
                // custToTruck.add(customers.get(0).clone());
                    for(int j = 0; j < assignToTruck; j++){
                        int index = Configuration.INSTANCE.randomGenerator.nextInt(tmpCustLeft);
                        Customer cust = customersCopy.get(index).clone();
                        customersCopy.remove(index);
                        custToTruck.add(cust);
                        custToTruckIndex.add(index);
                        tmpCustLeft--;
                    }
                // custToTruck.add(customers.get(0).clone());
                custToTruck.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
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
        while (!checkValidity(gene1New) || !checkValidity(gene2New) || !hasAllCustomers(gene2New) || !hasAllCustomers(gene1New)) {
            gene1New = orderCrossOver(gene1, gene2);
            gene2New = orderCrossOver(gene2, gene1);
        }

        if(gene1New.get(0).getCustomers().size() != 12 || gene2New.get(0).getCustomers().size() != 12){
            System.out.println("ERROR in fn");
        }
        
        Chromosome [] x = {
            new Chromosome(gene1New), 
            new Chromosome(gene2New)
        };
        return (x);
    }

    /* generate a child by order cross over (OX).
     First randomly select a section from gene1 and copy it to child gene.
     Then copy the remaining customers from gene2 to child gene.
     We need to ensure that customers are only serviced once by all trucks and that each truck has a valid trip.
     Each truck needs to start and end at the depot which has customer id 0.*/ 
    public static ArrayList<Truck> orderCrossOver(ArrayList<Truck> gene1, ArrayList<Truck> gene2){
        ArrayList<Truck> child = new ArrayList<Truck>();

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
            child.add(truckTmp);
        }
        if(child.get(0).getCustomers().get(0).getId() != 0){
            System.out.println("ERROR IN CROSSOVER: " + child.get(0).getCustomers().get(0).getId());
        }
        return child;
        
    }

    public Chromosome doMutation() {
        ArrayList<Truck> geneTmp  = new ArrayList<Truck>();
        ArrayList<Customer> custTmp = new ArrayList<Customer>();
        do{
            //swap between
            int truckIndex1 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks-1);
            int truckIndex2 = Configuration.INSTANCE.randomGenerator.nextInt(Configuration.INSTANCE.numberOfTrucks-1);
            Truck truck1 = this.getGene().get(truckIndex1);
            Truck truck2 = this.getGene().get(truckIndex2);
            int customerIndex1 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(truck1.getCustomers().size()-1));
            int customerIndex2 = Math.max(1,Configuration.INSTANCE.randomGenerator.nextInt(truck2.getCustomers().size()-1));
            Customer customer1 = truck1.getCustomers().get(customerIndex1).clone();
            Customer customer2 = truck2.getCustomers().get(customerIndex2).clone();

            // System.out.println("All information: \n" + truckIndex1 +"\n" + truckIndex2 + "\n" + customerIndex1 + "\n" + customerIndex2 );

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
            // this.gene.set(truckIndex1, new Truck(truckIndex1, customers1));
            // this.gene.set(truckIndex2, new Truck(truckIndex2, customers2));
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

            //Swap within truck
            // for (Truck truck : this.gene) {
            //     Truck truckTmp = null;
            //     custTmp = new ArrayList<Customer>();
            //     custTmp.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
            //     for (Customer customer : truck.getCustomers()) {
            //         if(customer.getId() != 0){
            //             custTmp.add(customer.clone());
            //         }
                    
            //     }
            //     custTmp.add(new Customer(0, 0, 0, 0, 0, Integer.MAX_VALUE, 0));
            //     truckTmp = new Truck(truck.getId(), custTmp);
            //     geneTmp.add(truckTmp);
            // }
            // for(Truck t: geneTmp){
            //     t.setCustomers(Chromosome.swapMutation(t.getCustomers()));
            // }
        }while(!checkValidity(geneTmp) || !hasAllCustomers(geneTmp));

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
        String tmp = "";
        for (Truck truck : gene) {
            tmp += truck.toString() + "";
        }
        return tmp;
        // return "Chromosome{" +
        //         // "gene=" + gene.size() +
        //         ", fitness=" + fitness +
        //         '}';
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