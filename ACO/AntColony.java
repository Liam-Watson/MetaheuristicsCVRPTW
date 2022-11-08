import java.util.List;
import java.util.ArrayList;

public class AntColony {
    private final double[][] pheromoneMatrix;
    private final Ant[] antArray;
    public double bestFitness = Double.MAX_VALUE;

    public AntColony() {
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
            antArray[i] = new Ant(Configuration.INSTANCE.data, this);
        }

        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("---");
        }
    }

    public void addPheromone(int from, int to, double pheromoneValue) {
        pheromoneMatrix[from][to] += pheromoneValue;
    }

    public double getPheromone(int from, int to) {
        return pheromoneMatrix[from][to];
    }

    public void doDecay() {
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.doDecay()");
        }

        for (int i = 0; i < pheromoneMatrix.length; i++) {
            for (int j = 0; j < pheromoneMatrix[i].length; j++) {
                pheromoneMatrix[i][j] = pheromoneMatrix[i][j] * (1.0 - Configuration.INSTANCE.decayFactor);
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
        double bestObjective = Double.POSITIVE_INFINITY;
        String bestRoute = "";
        while (iteration < Configuration.INSTANCE.numberOfIterations) {
            Configuration.INSTANCE.logEngine.write("*** iteration - " + iteration);

            List<Thread> threads = new ArrayList<Thread>();
            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                threads.add(new Thread(antArray[i], "Ant " + i));
            }

            printPheromoneMatrix();

            iteration++;

            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                threads.get(i).start();
            }
            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                try {
                    threads.get(i).join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (Configuration.INSTANCE.isDebug) {
                System.out.println("Iteration " + iteration + " finished");
                Configuration.INSTANCE.logEngine.write("---All ants finished looking for way");
            }

            doDecay();

            for (int i = 0; i < Configuration.INSTANCE.numberOfAnts; i++) {
                antArray[i].layPheromone(i);
            }
            printPheromoneMatrix();
            if (getBestAnt().getObjectiveValue() < bestObjective) {
                bestObjective = getBestAnt().getObjectiveValue();
                bestRoute = getBestAnt().toString();
                bestFitness = bestObjective;
            }

            if (Configuration.INSTANCE.isDebug) {
                System.out.println("Best objective value: " + bestObjective);
                System.out.println("Best route: " + bestRoute);
                System.out.println(getBestAnt().getObjectiveValue());
                // System.out.println(getBestAnt().toString());
                System.out.println("Is valid: " + getBestAnt().hasAllCustomers());
                System.out.println("BEST ANT DISTANCE: " + getBestAnt().calculateRouteLength());
                System.out.println("BEST ANT DELTA TIME: " + getBestAnt().calculateDeltaTime());
                Configuration.INSTANCE.logEngine.write("*** Iteration " + iteration + " finished");
            }
        }
    }

    public void printPheromoneMatrix() {
        // Pheromone matrix is very latge, uncomment if you want to see it
        if (Configuration.INSTANCE.isDebug) {
            Configuration.INSTANCE.logEngine.write("--- AntColony.printPheromoneMatrix()");
        }

        int n = pheromoneMatrix.length;
        for (double[] matrix : pheromoneMatrix) {
            for (int j = 0; j < n; j++) {
                // System.out.print(Configuration.INSTANCE.decimalFormat.format(matrix[j]) + "
                // ");
                // Configuration.INSTANCE.logEngine.writeNoFormatting(Configuration.INSTANCE.decimalFormat.format(matrix[j])
                // + " ");
            }
            // System.out.println();
            // Configuration.INSTANCE.logEngine.writeNoFormatting("\n");
        }

        // System.out.println("---");
        // Configuration.INSTANCE.logEngine.write("---");
    }

    public String toString() {
        return getBestAnt().toString();
    }
}