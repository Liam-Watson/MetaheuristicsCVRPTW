import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Population {
    private final double elitismRatio;
    private final double mutationRatio;
    private final double crossoverRatio;
    private Chromosome[] population;
    private int numberOfCrossoverOperations = 0;
    private int numberOfMutationOperations = 0;
    private ArrayList<Customer> customers;
    private Customer depot;

    public Population(int size, double crossoverRatio, double elitismRatio, double mutationRatio) {
        this.crossoverRatio = crossoverRatio;
        this.elitismRatio = elitismRatio;
        this.mutationRatio = mutationRatio;
        population = new Chromosome[size];
        customers = readCustomers();

        if(Configuration.INSTANCE.debug){
            System.out.println("Starting population generation...");
        }

        for (int i = 0; i < size; i++) {
            if(Configuration.INSTANCE.debug){
                System.out.println("Generating chromosome: " + i);
            }
            population[i] = Chromosome.generateRandom(customers, depot);
        }
        if(Configuration.INSTANCE.debug){
            System.out.println("Finished population generation.");
        }

        Arrays.sort(population);
    }

    public int getNumberOfCrossoverOperations() {
        return numberOfCrossoverOperations;
    }

    public int getNumberOfMutationOperations() {
        return numberOfMutationOperations;
    }

    public void evolve() {
        Chromosome[] chromosomeArray = new Chromosome[population.length];
        int index = (int) Math.round(population.length * elitismRatio);
        System.arraycopy(population, 0, chromosomeArray, 0, index);
        while (index < chromosomeArray.length) {
            if (Configuration.INSTANCE.randomGenerator.nextFloat() <= crossoverRatio) {
                Chromosome[] parents = selectParents();
                Chromosome[] children = parents[0].doCrossover(parents[1]);
                numberOfCrossoverOperations++;

                if (Configuration.INSTANCE.randomGenerator.nextFloat() <= mutationRatio) {
                    chromosomeArray[(index++)] = children[0].doMutation();
                    numberOfMutationOperations++;
                } else {
                    chromosomeArray[(index++)] = children[0];
                }

                if (index < chromosomeArray.length) {
                    if (Configuration.INSTANCE.randomGenerator.nextFloat() <= mutationRatio) {
                        chromosomeArray[index] = children[1].doMutation();
                        numberOfMutationOperations++;
                    } else {
                        chromosomeArray[index] = children[1];
                    }
                }
            } else if (Configuration.INSTANCE.randomGenerator.nextFloat() <= mutationRatio) {
                chromosomeArray[index] = population[index].doMutation();
                numberOfMutationOperations++;
            } else {
                chromosomeArray[index] = population[index];
            }

            index++;
        }
        Arrays.sort((chromosomeArray));
        population = chromosomeArray;
    }

    public Chromosome[] getPopulation() {
        Chromosome[] chromosomeArray = new Chromosome[population.length];
        System.arraycopy(population, 0, chromosomeArray, 0, population.length);
        
        return chromosomeArray;
    }

    private Chromosome[] selectParents() {
        Chromosome[] parentArray = new Chromosome[2];

        for (int i = 0; i < 2; i++) {
            parentArray[i] = population[Configuration.INSTANCE.randomGenerator.nextInt(population.length)];
            for (int j = 0; j < 30; j++) {
                int index = Configuration.INSTANCE.randomGenerator.nextInt(population.length);
                if (population[index].compareTo(parentArray[i]) < 0) {
                    parentArray[i] = population[index].clone();
                }
            }
        }
        return parentArray;
    }

    public ArrayList<Customer> readCustomers(){
        ArrayList<Customer> customers = new ArrayList<Customer>();
        try {
            Scanner sc = new Scanner(new File(Configuration.INSTANCE.dataPath));
            String tmp = sc.nextLine(); //skip first line
            tmp = sc.nextLine();
            String[] tmpArray = tmp.split("\\s+");
            this.depot = new Customer((int)Double.parseDouble("0"), (int)Double.parseDouble(tmpArray[2]),(int)Double.parseDouble(tmpArray[3]), (int)Double.parseDouble(tmpArray[4]), (int)Double.parseDouble(tmpArray[5]), (int)Double.parseDouble(tmpArray[6]),(int)Double.parseDouble(tmpArray[7]));
            ArrayList<String []> tmpList = new ArrayList<String []>();
            while(sc.hasNext()){
                String [] line = sc.nextLine().split("\\s+");
                tmpList.add(line);
                customers.add(new Customer((int)Double.parseDouble(line[1]), (int)Double.parseDouble(line[2]),(int)Double.parseDouble(line[3]), (int)Double.parseDouble(line[4]), (int)Double.parseDouble(line[5]), (int)Double.parseDouble(line[6]),(int)Double.parseDouble(line[7])));
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer getDepot(){
        return this.depot;
    }
}