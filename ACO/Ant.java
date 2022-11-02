import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;

public class Ant extends Thread {
    private final ProblemInstance data;
    private final AntColony antColony;
    private double objectiveValue = 0.0;
    private ArrayList<ArrayList<Integer>> tours;
    private Vector<Integer> notYetVisited = null;

    public Ant(ProblemInstance data, AntColony antColony) {
        this.data = data;
        this.antColony = antColony;
    }

    public double getObjectiveValue() {
        
        if (objectiveValue == 0.0) {
            double timePenalty = 0;
            double distance = 0;
            for(ArrayList<Integer> route : tours){
                double time = 0;


                for(int i = 0; i < route.size()-1; i++){
                    distance += data.getDistance(route.get(i), route.get(i + 1));

                    if(time > data.getCustomer(route.get(i+1)).getDueDate()){
                        timePenalty += Math.abs(time - data.getCustomer(route.get(i+1)).getDueDate());
                    }
                    if(time < data.getCustomer(route.get(i+1)).getReadyTime()){
                        time = data.getCustomer(route.get(i+1)).getReadyTime();
                    }
                    time += data.getCustomer(route.get(i+1)).getServiceTime();
                }
            }
            objectiveValue = Configuration.INSTANCE.distanceObjectivePunishment*distance + Configuration.INSTANCE.timeWindowObjectivePunishment*timePenalty;
        }

        return objectiveValue;
    }

    public void newRound() {
        objectiveValue = 0.0;
        
        if(notYetVisited != null && notYetVisited.size() > 0){
            System.out.println("ERROR");
        }
        tours = new ArrayList<ArrayList<Integer>>();
        notYetVisited = new Vector<>();

        for (int i = 1; i <= data.getCustomers().size()-1; i++) {
            notYetVisited.addElement(i);
        }
        Collections.shuffle(notYetVisited);
    }

    public void layPheromone() {
        double pheromone = Configuration.INSTANCE.Q / objectiveValue;

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- Ant.layPheromone()");
            Configuration.INSTANCE.logEngine.write("decay factor   : " + Configuration.INSTANCE.decayFactor);
            Configuration.INSTANCE.logEngine.write("objectiveValue : " + objectiveValue);
            Configuration.INSTANCE.logEngine.write("pheromone      : " + pheromone);
        }
        for(int j = 0; j < Configuration.INSTANCE.numberOfTrucks; j++){
            if(tours.size() != Configuration.INSTANCE.numberOfTrucks){
                System.out.println("Tour length ERROR: " + tours.size());
            }
           
            ArrayList<Integer> route = tours.get(j);
            for(int i = 0; i < route.size() - 1; i++){
                antColony.addPheromone(route.get(i), route.get(i + 1), pheromone);
                // antColony.addPheromone(route.get(i + 1), route.get(i), pheromone);
            }
            // System.out.println("Tour length: " + route.get(route.size() - 2));
            // antColony.addPheromone(route.get(route.size() - 2), route.get(0), pheromone);
            // antColony.addPheromone(route.get(0), route.get(route.size() - 2), pheromone);
        }
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }


    public void lookForWay(){
        //loop over trucks
        int counter = 0;
        for(int k = 0; k < Configuration.INSTANCE.numberOfTrucks; k++){
            // System.out.println(Thread.currentThread().getName() + "\t" + objectiveValue);
            ArrayList<Integer> route = new ArrayList<Integer>();
            int capacity = 0;
            double time = 0;
            route.add(0); // add depot
            //over truck route assignment 
            for(int i = 0; i < Configuration.INSTANCE.assignToTruck; i++){
                double sum = 0.0; //init sum
                //calculate sum
                for(int j = 0; j < notYetVisited.size(); j++){
                    double tau = Math.pow(antColony.getPheromone(route.get(i), notYetVisited.get(j)), Configuration.INSTANCE.alpha);
                    double distance =  Configuration.INSTANCE.distanceEtaPunishment*data.getDistance(route.get(i), notYetVisited.get(j));
                    double eta = Math.pow((1/distance), Configuration.INSTANCE.beta);
                    
                    if(time > data.getCustomer(notYetVisited.get(j)).getDueDate()){
                        double dueDelta = Configuration.INSTANCE.timeWindowEtaPunishment*Math.abs(data.getCustomer(notYetVisited.get(j)).getDueDate()- time);
                        
                        eta = Math.pow((1/(dueDelta+distance)), Configuration.INSTANCE.beta);
                        // System.out.println("dueDelta: " + dueDelta);
                    }
                    sum = sum + tau * eta;
                    // sum += Math.pow(antColony.getPheromone(route.get(i), notYetVisited.get(j)),Configuration.INSTANCE.alpha)
                    //         / Math.pow(data.getDistance(route.get(i), notYetVisited.get(j)),Configuration.INSTANCE.beta);
                }
                //calculate probabilities and select next node
                double selectionProbability = 0.0;

                double total = 0;
                for(int l = 0; l < notYetVisited.size(); l++){

                    double distance =  Configuration.INSTANCE.distanceEtaPunishment*data.getDistance(route.get(i), notYetVisited.get(l));
                    double tau = Math.pow(antColony.getPheromone(route.get(i), notYetVisited.get(l)),Configuration.INSTANCE.alpha);
                    double eta = Math.pow((1/distance),Configuration.INSTANCE.beta);

                    if(time > data.getCustomer(notYetVisited.get(l)).getDueDate()){
                        double dueDelta = Configuration.INSTANCE.timeWindowEtaPunishment*Math.abs(data.getCustomer(notYetVisited.get(l)).getDueDate()- time);
                        eta = Math.pow((1/(dueDelta+distance)), Configuration.INSTANCE.beta);
                    }


                    total += (tau*eta)/sum;
                }

                // System.out.println(total);
                double random = Configuration.INSTANCE.randomGenerator.nextDouble();
                double cumulation = 0;
                for(int j = 0; j < notYetVisited.size(); j++){
                    
                    double distance =  Configuration.INSTANCE.distanceEtaPunishment*data.getDistance(route.get(i), notYetVisited.get(j));
                    double tau = Math.pow(antColony.getPheromone(route.get(i), notYetVisited.get(j)),Configuration.INSTANCE.alpha);
                    double eta = Math.pow((1/distance),Configuration.INSTANCE.beta);

                    if(time > data.getCustomer(notYetVisited.get(j)).getDueDate()){
                        double dueDelta = Configuration.INSTANCE.timeWindowEtaPunishment*Math.abs(data.getCustomer(notYetVisited.get(j)).getDueDate()- time);
                        eta = Math.pow((1/(dueDelta+distance)), Configuration.INSTANCE.beta);

                    }


                    selectionProbability = (tau*eta)/sum;

                    if(Double.isNaN(selectionProbability)){
                        System.out.println("selectionProbability is NaN: " + tau + " " + eta + " " + sum);
                        selectionProbability = 0;
                    }

                    if(data.getCustomer(notYetVisited.get(j)).getDemand() + capacity > Configuration.INSTANCE.capacity){
                        selectionProbability = 0;
                    }

                    // System.out.println(random);
                    // System.out.println("Selection probability: " + selectionProbability);
                    // System.out.println("Selection Probability: " + selectionProbability + 
                    // "\n Random: " + random + "\n Tau: " + tau + "\n Eta: " + eta + "\n Sum: " + sum);
                    if(random < selectionProbability + cumulation){
                        route.add(notYetVisited.get(j));
                        // breakBool = true;
                        // System.out.println(selectionProbability + "\t" + j + "\t" + notYetVisited.size() );
                        if(time < data.getCustomer(notYetVisited.get(j)).getReadyTime()){
                            time = data.getCustomer(notYetVisited.get(j)).getReadyTime();
                        }
                        time += data.getCustomer(notYetVisited.get(j)).getServiceTime();
                        notYetVisited.remove(j);
                        break;
                    }
                    cumulation += selectionProbability;
                }
            }
            route.add(0);
            tours.add(route);
            counter++;
        }
        //calculate objective value
        // System.out.println("counter: " + counter);
        getObjectiveValue();
    }
    public void ingnoreTHis(){
    // public void lookForWay() {
    //     DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");
    //     ArrayList<Integer> tmpList = new ArrayList<Integer>();
    //     if (Configuration.INSTANCE.isDebug) {
    //         Configuration.INSTANCE.logEngine.write("--- Ant.lookForWay");
    //     }
    //     int numberOfCities = data.getNumberOfCities();
    //     int randomIndexOfTownToStart = Math.max(1,(int) (numberOfCities * Configuration.INSTANCE.randomGenerator.nextDouble() + 1));

    //     if (Configuration.INSTANCE.isDebug) {
    //         Configuration.INSTANCE.logEngine.write("numberOfCities           : " + numberOfCities);
    //         Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
    //     }
    
    //     for(int k = 0; k < Configuration.INSTANCE.numberOfTrucks; k++){
    //         ArrayList<Integer> route = new ArrayList<Integer>();

    //         route.add(0);
    //         int capacity = 0;

    //         for (int i = 0; i < Configuration.INSTANCE.assignToTruck; i++) {
    //             double sum = 0.0;

    //             if (Configuration.INSTANCE.isDebug) {
    //                 Configuration.INSTANCE.logEngine.write("i : " + i + " - notYetVisited : " + notYetVisited);
    //             }

    //             for (int j = 0; j < notYetVisited.size(); j++) {
    //                 int position = notYetVisited.elementAt(j);
    //                 // sum += Math.pow(antColony.getPheromone(position, position),Configuration.INSTANCE.alpha) / Math.pow(data.getDistance(notYetVisited.get(j), position),Configuration.INSTANCE.beta);
    //                 sum += Math.pow(antColony.getPheromone(route.get(i), position),Configuration.INSTANCE.alpha) * 
    //                         (1/ Math.pow(data.getDistance(route.get(i), position),Configuration.INSTANCE.beta));
    //                 System.out.println(j + "\t" + i);
    //                 System.out.println((1/ Math.pow(data.getDistance(route.get(i), position),Configuration.INSTANCE.beta)));
    //                 // sum += Math.pow(antColony.getPheromone(route.get(i-1), position),Configuration.INSTANCE.alpha) / Math.pow(data.getDistance(route.get(i-1), position),Configuration.INSTANCE.beta);
    //             }

    //             double selectionProbability = 0.0;
    //             double randomNumber = Configuration.INSTANCE.randomGenerator.nextDouble();

    //             if (Configuration.INSTANCE.isDebug) {
    //                 Configuration.INSTANCE.logEngine.write("i : " + i + " - sum : " + decimalFormat.format(sum) +
    //                         " - randomNumber : " + decimalFormat.format(randomNumber));
    //                 Configuration.INSTANCE.logEngine.write("-");
    //             }
    //             boolean breakFlag = false;
    //             do{
    //                 for (int j = 0; j < notYetVisited.size(); j++) {
    //                     int position = notYetVisited.elementAt(j);
    //                     // System.out.println("Position: " + position + "\t at j = " + j);
    //                     // selectionProbability += (Math.pow(antColony.getPheromone(route.get(i-1), position),Configuration.INSTANCE.alpha) * (1/
    //                             // Math.pow(data.getDistance(route.get(i-1), position),Configuration.INSTANCE.beta))) /
    //                             // sum;
    //                     selectionProbability += (Math.pow(antColony.getPheromone(route.get(i), position),Configuration.INSTANCE.alpha) * (1/
    //                     Math.pow(data.getDistance(route.get(i), position),Configuration.INSTANCE.beta))) /
    //                     sum;
    //                     // System.out.println("Selection Probability: " + selectionProbability + "\n pheramone: "+ Math.pow(antColony.getPheromone(route.get(i), position),Configuration.INSTANCE.alpha)
    //                     // + "\n distance: " + Math.pow(data.getDistance(route.get(i), position),Configuration.INSTANCE.beta) 
    //                     // + "\n sum: " + sum);
    //                     // if(capacity + data.getCustomer(position-1).getDemand() > Configuration.INSTANCE.capacity){
    //                     if(capacity + data.getCustomer(position).getDemand() > Configuration.INSTANCE.capacity){
    //                         continue;
    //                     }
    //                     if (Configuration.INSTANCE.isDebug)
    //                         if (position < 10) {
    //                             Configuration.INSTANCE.logEngine.write("position : 0" + position +
    //                                     " - selectionProbability : " + decimalFormat.format(selectionProbability));
    //                         } else {
    //                             Configuration.INSTANCE.logEngine.write("position : " + position +
    //                                     " - selectionProbability : " + decimalFormat.format(selectionProbability));
    //                         }

    //                     if (randomNumber < selectionProbability) {
    //                         randomIndexOfTownToStart = position;
    //                         // System.out.println("randomIndexOfTownToStart: " + randomIndexOfTownToStart + "\t at j = " + data.getCustomer(position).getId());
    //                         capacity+=data.getCustomer(position).getDemand();
    //                         route.add(randomIndexOfTownToStart);
    //                         notYetVisited.removeElement(randomIndexOfTownToStart);
    //                         breakFlag=true;
    //                         // System.out.println("BREAK");
    //                         break;
    //                     }
    //                 }
    //                 // System.out.println(notYetVisited.size());
    //                 // System.out.println("BreakFlag: " + Math.pow(antColony.getPheromone(route.get(i), position);
    //             }while(!breakFlag);

    //             if (Configuration.INSTANCE.isDebug) {
    //                 Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
    //             }
    //             if(tmpList.contains(randomIndexOfTownToStart)){
    //                 System.out.println("BRUH: " + randomIndexOfTownToStart);
    //             }
    //             tmpList.add(randomIndexOfTownToStart);
    //             // route.add(randomIndexOfTownToStart);
    //             // notYetVisited.removeElement(randomIndexOfTownToStart);
    //             if((notYetVisited.contains(randomIndexOfTownToStart))){
    //                 System.out.println("ERR");
    //                 System.exit(0);
    //             }
    //             if (Configuration.INSTANCE.isDebug) {
    //                 Configuration.INSTANCE.logEngine.write("-");
    //             }
    //         }
    //         route.add(0);
    //         tours.add(route);
    // }
    //     getObjectiveValue();

    //     if (Configuration.INSTANCE.isDebug) {
    //         Configuration.INSTANCE.logEngine.write("---");
    //     }
    // }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int numberOfCities = data.getNumberOfCities();

        stringBuilder.append("tour : ");
        int counter = 0;
        for(ArrayList<Integer> route : tours){
            stringBuilder.append("\n");
            stringBuilder.append("Truck ").append(counter).append(": " );
            for(Integer city : route){
                stringBuilder.append(city);
                stringBuilder.append(" ");
            }
            counter++;
        }

        stringBuilder.append("\n");
        stringBuilder.append("objectiveValue : ").append(objectiveValue);

        stringBuilder.append("\n");
        stringBuilder.append("Delta Time: ").append(calculateDeltaTime());

        stringBuilder.append("\n");
        stringBuilder.append("Length: ").append(calculateRouteLength());

        return stringBuilder.toString();
    }

    @Override
    public void run() {
        newRound();
        lookForWay();

    }

    public boolean hasAllCustomers(){
        ArrayList<Integer> allCustomers = new ArrayList<Integer>();
        for (ArrayList<Integer> t : tours) {
            for (Integer i : t) {
                if(allCustomers.contains(i)){
                    System.out.println("Duplicate customer: " + i);
                    return false;
                }
                if(i != 0){
                    allCustomers.add(i);
                }
                
            }
        }
        if(allCustomers.size() == Configuration.INSTANCE.numberOfCustomers){
            return true;
        }
        System.out.println("size failure: " + allCustomers.size());
        return false;
    }

    public double calculateDeltaTime() {
            double timePenalty = 0;
            for(ArrayList<Integer> route : tours){
                double time = 0;
                for(int i = 0; i < route.size()-1; i++){
                    if(time > data.getCustomer(route.get(i+1)).getDueDate()){
                        timePenalty += Math.abs(data.getCustomer(route.get(i+1)).getDueDate() - time);
                    }
                    if(time < data.getCustomer(route.get(i+1)).getReadyTime()){
                        time = data.getCustomer(route.get(i)).getReadyTime();
                    }
                    time += data.getCustomer(route.get(i+1)).getServiceTime();
                }
        }

        return timePenalty ;
    }

    public double calculateRouteLength() {
        double length = 0;
        // String tmp = "";
        for(ArrayList<Integer> route : tours){
            // length += Customer.getDistance(data.getCustomer(0), data.getCustomer(route.get(0)-1));
            for(int i = 0; i < route.size() - 1; i++){
                length += data.getDistance(route.get(i), route.get(i + 1));
                // tmp += route.get(i) + " -> " + route.get(i+1) + " ";
            }
            // length += Customer.getDistance(data.getCustomer(0), data.getCustomer(route.size()-1));
            // System.out.println(tmp);
            // tmp = "";
        }

        
        return length ;
    }

}