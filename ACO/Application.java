public class Application {
    public static void main(String... args) {
        Configuration.INSTANCE.logEngine.write("--- starting");

        Configuration.INSTANCE.data = new ProblemInstance();

        AntColony antColony = new AntColony();
        antColony.solve();
        Configuration.INSTANCE.logEngine.write(antColony.toString());
        System.out.println(antColony.toString());
        Configuration.INSTANCE.logEngine.close();
    }
}