import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Controller {
    int populationSize;
    int generationsNumber;
    double crossingProb;
    double mutationProb;
    int tournamentSize;

    public Controller(int populationSize, int generationsNumber, double crossingProb, double mutationProb, int tournamentSize) {
        this.populationSize = populationSize;
        this.generationsNumber = generationsNumber;
        this.crossingProb = crossingProb;
        this.mutationProb = mutationProb;
        this.tournamentSize = tournamentSize;
    }

    public void run(String path) {
        Problem problem = new Problem(path); // Initialize a class called Problem, which is used to parse the file
        Population population = new Population(problem, populationSize, tournamentSize, crossingProb, mutationProb); // Initialize population by tournament selection
        population.initializePopulation();
        for (int i = 0; i < generationsNumber; i++) {
            population.evaluation();
            population.selectionByTournament();
            //population.selectionByRoulette();
            writeStats(i, population.getPopulationStats()); // replacement function
            population.setNextPopulation();
//            System.out.print(population);
        }
    }

    public void writeStats(int popNumber, double[] stats) { // generate result
        String filename = "pies2.csv";
        try (FileWriter fileWriter = new FileWriter(filename, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            String data = popNumber + ";" + (int)stats[0] + ";" +(int)stats[1] + ";" +(int) stats[2];
            printWriter.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Controller controller = new Controller(500, 100, 0.8, 0.4, 10);
        controller.run("resources/a280-n279.txt");
    }

}
