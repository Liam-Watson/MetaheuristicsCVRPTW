public enum Configuration {
    INSTANCE;

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());


    public final int maximumNumberOfGenerations = 10000;

    public int populationSize = 3000;
    public double crossoverRatio = 0.2;
    public double elitismRatio = 0.1;
    public double mutationRatio = 0.05;

    public final int capacity = 200;

    public final int numberOfTrucks = 20;

    // customers
    public final int numberOfCustomers = 100;

    public final int assignToTruck = numberOfCustomers/numberOfTrucks; 

    public final String dataPath = "../data/instance.txt"; // Change this if it's in a different file. This assumes execution from sub directory.

    public final boolean debug = true;
}