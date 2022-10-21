import java.util.concurrent.CyclicBarrier;

public class AntColony {
    private final double[][] pheromoneMatrix;
    private final Ant[] antArray;
    final CyclicBarrier barrier;

    public AntColony() {
        this.barrier = new CyclicBarrier(Configuration.INSTANCE.numberOfAnts);
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony()");
        }

        int count = Configuration.INSTANCE.data.getNumberOfCities();
        pheromoneMatrix = new double[count][count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromoneMatrix[i][j] = Configuration.INSTANCE.startPheromoneValue;
            }
        }

        antArray = new Ant[Configuration.INSTANCE.numberOfAnts];

        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            antArray[i] = new Ant(Configuration.INSTANCE.data, this, barrier);
        }

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public void addPheromone(int from, int to, double pheromoneValue) {
        pheromoneMatrix[from - 1][to - 1] += pheromoneValue;
    }

    public double getPheromone(int from, int to) {
        return pheromoneMatrix[from - 1][to - 1];
    }

    public void doDecay() {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.doDecay()");
        }

        int count = Configuration.INSTANCE.data.getNumberOfCities();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                pheromoneMatrix[i][j] *= (1.0 - Configuration.INSTANCE.decayFactor);
            }
        }

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    private Ant getBestAnt() {
        int indexOfAntWithBestObjectiveValue = 0;
        double objectiveValue = Double.MAX_VALUE;

        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            double currentObjectiveValue = antArray[i].getObjectiveValue();
            if (currentObjectiveValue < objectiveValue) {
                objectiveValue = currentObjectiveValue;
                indexOfAntWithBestObjectiveValue = i;
            }
        }

        return antArray[indexOfAntWithBestObjectiveValue];
    }


    public void solve() {
        int iteration = 0;
        
        while (iteration < Configuration.INSTANCE.numberOfIterations) {
            Configuration.INSTANCE.logEngine.write("*** iteration - " + iteration);

            printPheromoneMatrix();

            iteration++;

            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                // antArray[i].newRound();
                // antArray[i].lookForWay();
                antArray[i].start();
            }
            for(int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                try {
                    antArray[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Iteration " + iteration + " finished");
            doDecay();
            getBestAnt().layPheromone();
            
            printPheromoneMatrix();
            System.out.println(getBestAnt().toString());
            System.out.println("Is valid: " + getBestAnt().hasAllCustomers());
            Configuration.INSTANCE.logEngine.write("***");
            getNewAnts();
        }
    }

    public void printPheromoneMatrix() {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.printPheromoneMatrix()");
        }

        int n = pheromoneMatrix.length;
        for (double[] matrix : pheromoneMatrix) {
            for (int j = 0; j < n; j++) {
                // System.out.print(Configuration.INSTANCE.decimalFormat.format(matrix[j]) + " ");
            }
            // System.out.println();
        }

        // System.out.println("---");
    }

    private void getNewAnts(){
        for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
            antArray[i] = new Ant(Configuration.INSTANCE.data, this, barrier);
        }
    }

    public String toString() {
        return getBestAnt().toString();
    }
}