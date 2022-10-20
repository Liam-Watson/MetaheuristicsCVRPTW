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
    public final DecimalFormat decimalFormat = new DecimalFormat("#0.000000000000000");
    public final MersenneTwister randomGenerator = new MersenneTwister(System.currentTimeMillis());
    // ant colony optimization
    public final double decayFactor = 0.4;
    public final double startPheromoneValue = 0.0000005;
    public final int numberOfAnts = 200;
    public final int numberOfIterations = 10000;

    public final int capacity = 200;
    public final int numberOfTrucks = 10;

    public ProblemInstance data;
}