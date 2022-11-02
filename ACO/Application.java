public class Application {
    public static void main(String[] args) {
        Configuration.INSTANCE.logEngine.write("--- starting");

        Configuration.INSTANCE.data = new ProblemInstance();

        //Set params if we have args
        if(args.length > 3){
            Configuration.INSTANCE.decayFactor = Double.parseDouble(args[4]);
            Configuration.INSTANCE.alpha = Double.parseDouble(args[1]);
            Configuration.INSTANCE.beta = Double.parseDouble(args[2]);
            Configuration.INSTANCE.numberOfAnts = Integer.parseInt(args[0]);
        }


        AntColony antColony = new AntColony();
        antColony.solve();
        Configuration.INSTANCE.logEngine.write(antColony.toString());
        System.out.println(antColony.toString());
        Configuration.INSTANCE.logEngine.close();
    }
}