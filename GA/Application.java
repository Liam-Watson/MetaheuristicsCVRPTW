import java.text.DecimalFormat;

public class Application {
    public static void main(String[] args) {
        DecimalFormat decimalFormat = new DecimalFormat("000000");
        double currentBestFitness = Double.MAX_VALUE;
        if(args.length > 2) {
            Configuration.INSTANCE.populationSize = Integer.parseInt(args[2]);
            Configuration.INSTANCE.crossoverRatio = Double.parseDouble(args[0]);
            Configuration.INSTANCE.mutationRatio = Double.parseDouble(args[1]);
        }


        long runtimeStart = System.currentTimeMillis();
        
        Population population = new Population(Configuration.INSTANCE.populationSize,
                Configuration.INSTANCE.crossoverRatio,
                Configuration.INSTANCE.elitismRatio,
                Configuration.INSTANCE.mutationRatio);

        int i = 0;
        Chromosome bestChromosome = population.getPopulation()[0];
        Chromosome lastValid = null;
        while ((i++ <= Configuration.INSTANCE.maximumNumberOfGenerations)) {
            population.evolve();
            bestChromosome = population.getPopulation()[0];
                       
            if (bestChromosome.getFitness() < currentBestFitness) {
                currentBestFitness = bestChromosome.getFitness();
                if(Chromosome.checkValidity(bestChromosome.getGene())){
                    lastValid = new Chromosome(bestChromosome.getGene(), population.getDepot());
                }
                if(Configuration.INSTANCE.debug){
                    System.out.println("generation " + decimalFormat.format(i) + " : " + " - " + bestChromosome.getFitness() + "\t Time window: "  + bestChromosome.checkTimeWindows());
                }
            }
        }
        if(Configuration.INSTANCE.debug){
            System.out.println(lastValid.getRoutes());
            // System.out.println("generation                  : " + decimalFormat.format(i) + " : " + bestChromosome.getGene());
            System.out.println("runtime                     : " + (System.currentTimeMillis() - runtimeStart) + " ms");
            System.out.println("numberOfCrossoverOperations : " + population.getNumberOfCrossoverOperations());
            System.out.println("numberOfMutationOperations  : " + population.getNumberOfMutationOperations());
        }
        System.out.println(lastValid.getFitness());
    }

    public String convertCommandLineArgumentsToString(String... args) {
        StringBuilder targetString = new StringBuilder();

        for (String string : args) {
            targetString.append(string).append(" ");
        }

        return targetString.toString();
    }
}