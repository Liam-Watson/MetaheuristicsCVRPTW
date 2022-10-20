

import java.text.DecimalFormat;
import java.util.Vector;
import java.util.ArrayList;

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
            for(ArrayList<Integer> route : tours){
                objectiveValue += Customer.getDistance(data.getCustomer(0), new Customer(0, 0, 0, 0, 0, 0, 0));
                for(int i = 0; i < route.size() - 1; i++){
                    objectiveValue += data.getDistance(route.get(i), route.get(i + 1));
                }
                objectiveValue += Customer.getDistance(data.getCustomer(route.size()-1), new Customer(0, 0, 0, 0, 0, 0, 0));
            }
        }

        return objectiveValue;
    }

    public void newRound() {
        objectiveValue = 0.0;
        tours = new ArrayList<ArrayList<Integer>>();
        notYetVisited = new Vector<>();

        for (int i = 1; i <= data.getNumberOfCities(); i++) {
            notYetVisited.addElement(i);
            System.out.println(i);
        }
    }

    public void layPheromone() {
        double pheromone = Configuration.INSTANCE.decayFactor / objectiveValue;
        int count = data.getNumberOfCities();

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- Ant.layPheromone()");
            Configuration.INSTANCE.logEngine.write("decay factor   : " + Configuration.INSTANCE.decayFactor);
            Configuration.INSTANCE.logEngine.write("objectiveValue : " + objectiveValue);
            Configuration.INSTANCE.logEngine.write("pheromone      : " + pheromone);
        }
        for(int j = 0; j < Configuration.INSTANCE.numberOfTrucks; j++){
            ArrayList<Integer> route = tours.get(j);
            for(int i = 0; i < route.size() - 1; i++){
                antColony.addPheromone(route.get(i), route.get(i + 1), pheromone);
                antColony.addPheromone(route.get(i + 1), route.get(i), pheromone);
            }
            antColony.addPheromone(route.get(route.size() - 1), route.get(0), pheromone);
            antColony.addPheromone(route.get(0), route.get(route.size() - 1), pheromone);
        }
        // for (int i = 0; i < count - 1; i++) {
        //     antColony.addPheromone(tour[i], tour[i + 1], pheromone);
        //     antColony.addPheromone(tour[i + 1], tour[i], pheromone);
        // }

        // antColony.addPheromone(tour[count - 1], tour[0], pheromone);
        // antColony.addPheromone(tour[0], tour[count - 1], pheromone);

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public void lookForWay() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- Ant.lookForWay");
        }

        int numberOfCities = data.getNumberOfCities();
        int randomIndexOfTownToStart = (int) (numberOfCities * Configuration.INSTANCE.randomGenerator.nextDouble() + 1);

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("numberOfCities           : " + numberOfCities);
            Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
        }
    
        for(int k = 0; k < Configuration.INSTANCE.numberOfTrucks; k++){
            ArrayList<Integer> route = new ArrayList<Integer>();
            route.add(randomIndexOfTownToStart);
            int capacity = 0;
            for (int i = 1; i < numberOfCities; i++) {
                // ArrayList<Integer> tour = new ArrayList<Integer>();
                double sum = 0.0;
                // System.out.println(i);

                if(route.size() == 10){
                    break;
                }
                
                notYetVisited.removeElement(randomIndexOfTownToStart);
                if (Configuration.INSTANCE.isDebug) {
                    Configuration.INSTANCE.logEngine.write("i : " + i + " - notYetVisited : " + notYetVisited);
                }

                for (int j = 0; j < notYetVisited.size(); j++) {
                    int position = notYetVisited.elementAt(j);
                    sum += antColony.getPheromone(route.get(i-1), position) / data.getDistance(route.get(i-1), position);
                }

                double selectionProbability = 0.0;
                double randomNumber = Configuration.INSTANCE.randomGenerator.nextDouble();

                if (Configuration.INSTANCE.isDebug) {
                    Configuration.INSTANCE.logEngine.write("i : " + i + " - sum : " + decimalFormat.format(sum) +
                            " - randomNumber : " + decimalFormat.format(randomNumber));
                    Configuration.INSTANCE.logEngine.write("-");
                }

                for (int j = 0; j < notYetVisited.size(); j++) {
                    int position = notYetVisited.elementAt(j);
                    // System.out.println("position: " + data.getCustomers().size() + " " + position);
                    selectionProbability += antColony.getPheromone(route.get(i-1), position) /
                            data.getDistance(route.get(i-1), position) /
                            sum;
                    if(capacity + data.getCustomer(position).getDemand() > Configuration.INSTANCE.capacity){
                        continue;
                    }
                    if (Configuration.INSTANCE.isDebug)
                        if (position < 10) {
                            Configuration.INSTANCE.logEngine.write("position : 0" + position +
                                    " - selectionProbability : " + decimalFormat.format(selectionProbability));
                        } else {
                            Configuration.INSTANCE.logEngine.write("position : " + position +
                                    " - selectionProbability : " + decimalFormat.format(selectionProbability));
                        }

                    if (randomNumber < selectionProbability) {
                        randomIndexOfTownToStart = position;
                        capacity+=data.getCustomer(position).getDemand();
                        break;
                    }
                }

                if (Configuration.INSTANCE.isDebug) {
                    Configuration.INSTANCE.logEngine.write("randomIndexOfTownToStart : " + randomIndexOfTownToStart);
                }

                route.add(randomIndexOfTownToStart);
                notYetVisited.removeElement(randomIndexOfTownToStart);

                if (Configuration.INSTANCE.isDebug) {
                    Configuration.INSTANCE.logEngine.write("-");
                }
            }
            tours.add(route);
    }
        getObjectiveValue();

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
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

        return stringBuilder.toString();
    }

    @Override
    public void run() {
        newRound();
        lookForWay();
    }
}