import java.util.ArrayList;
import java.util.List;

public class Greedy {

    private Problem problem;

    public Greedy(Problem problem) {
        this.problem = problem;
    }

    public void getBest() {
        List<Node> nodes = new ArrayList<>();
        for(Node node: problem.nodes){
            nodes.add(node.clone());
        }
        List<Node> visitedNodes = new ArrayList<>();
        // zaczynamy od pierszego miasta
        visitedNodes.add(nodes.get(0));

        int bestIndex;
        double bestDistance;
        for (int i = 1; i < nodes.size(); i++) {
            Node previous = visitedNodes.get(i - 1);
            bestDistance = Double.MAX_VALUE;
            bestIndex = i;
            for (int j = 1; j < nodes.size(); j++) {
                double distance = calculateDistance(nodes.get(j), previous);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestIndex = j;
                }
            }
            visitedNodes.add(nodes.get(bestIndex));
            nodes.remove(bestIndex);
        }
        int[] route = new int[visitedNodes.size()];
        for (int i = 0; i < route.length; i++) {
            route[i] = visitedNodes.get(i).getIndex();
        }

        Individual individual = new Individual(route, problem);
        double fitness = individual.calculateFitnessFunctionValue();
        System.out.println("Greedy algorithm: "+ fitness);
    }

    private double calculateDistance(Node fst, Node snd) {
        int xFirst = fst.getxCoord();
        int yFirst = fst.getyCoord();
        int xSecond = snd.getxCoord();
        int ySecond = snd.getyCoord();
        return Math.sqrt(Math.pow(Math.abs(xFirst - xSecond), 2) + Math.pow(Math.abs(yFirst - ySecond), 2));
    }

    public static void main(String[] args) {
        Problem problem = new Problem("resources/medium_4.ttp");
        Greedy greedy = new Greedy(problem);
        greedy.getBest();
    }

}
