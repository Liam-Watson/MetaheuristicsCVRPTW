public enum Configuration {
    INSTANCE;

    // common
    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());

    // genetic algorithm
    public final int populationSize = 2048;
    // public final int populationSize = 10;
    // public final int populationSize = 10;
    public final int maximumNumberOfGenerations = 10000;
    // public final int maximumNumberOfGenerations = 10;
    public final double crossoverRatio = 0.7;
    public final double elitismRatio = 0.1;
    public final double mutationRatio = 0.00005;

    //truck capacity
    public final int capacity = 200;
    public final int numberOfTrucks = 10;

    // customers
    public final int numberOfCustomers = 100;
}