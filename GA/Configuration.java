public enum Configuration {
    INSTANCE;

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());

    // genetic algorithm
    // public  int populationSize = 2048;
    public final int maximumNumberOfGenerations = 10000;
    // public double crossoverRatio = 0.7;
    // public final double elitismRatio = 0.1;
    // public double elitismRatio = 0.5;
    // public final double mutationRatio = 0.00005;
    // public double mutationRatio = 0.7;

    //tunable params
    // public  int populationSize = 2048;
    public int populationSize = 3000;
    public double crossoverRatio = 0.8;
    public double elitismRatio = 0.01;
    public double mutationRatio = 0.8;
    //truck capacity
    public final int capacity = 200;
    // public final int numberOfTrucks = 10;
    public final int numberOfTrucks = 20;

    // customers
    public final int numberOfCustomers = 100;

    public final int assignToTruck = numberOfCustomers/numberOfTrucks; 

    public final String dataPath = "../data/instance.txt"; // Change this if it's in a different file. This assumes execution from sub directory.

    public final boolean debug = true;
}