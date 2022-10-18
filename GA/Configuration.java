public enum Configuration {
    INSTANCE;

    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());

    // genetic algorithm
    public final int populationSize = 20048;
    public final int maximumNumberOfGenerations = 10000;
    public final double crossoverRatio = 0.7;
    public final double elitismRatio = 0.1;
    // public final double elitismRatio = 0.5;
    public final double mutationRatio = 0.00005;
    // public final double mutationRatio = 0.7;
    
    //truck capacity
    public final int capacity = 200;
    public final int numberOfTrucks = 10;

    // customers
    public final int numberOfCustomers = 101;

    public final String dataPath = "../data/instance.txt"; // Change this if it's in a different file. This assumes execution from sub directory.
}