import java.text.DecimalFormat;

public enum Configuration {
    INSTANCE;

    // common
    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String dataDirectory = userDirectory + fileSeparator + "data" + fileSeparator;
    public final String logDirectory = userDirectory + fileSeparator + "log" + fileSeparator;
    public final LogEngine logEngine = new LogEngine(logDirectory + "debug.log");
    public final boolean isDebug = false;
    public final DecimalFormat decimalFormat = new DecimalFormat("#0.00000");
    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());
    // ant colony optimization
    public final double startPheromoneValue = 0.0000005;
    public final int numberOfIterations = 150;

    public final int capacity = 200;
    public final int numberOfTrucks = 20;
    

    public final int numberOfCustomers = 100;

    public final int assignToTruck = numberOfCustomers/numberOfTrucks;

    //parameters
    public  double decayFactor = 0.5;
    public  double alpha = 1.5;
    public  double beta = 2;
    public  int numberOfAnts = 3000;

    public double timeWindowObjectivePunishment = 1000;
    public double timeWindowEtaPunishment = 1000;
    public double distanceObjectivePunishment = 1;

    public double distanceEtaPunishment = 0.5;

    public double Q = 1;

    public ProblemInstance data;
}