import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Individual {
    int[] route;
    double totalTime;
    int totalKnapsack;
    int totalValue; // funkcja g(y)
    List<Integer> itemsTaken; // indeksy zabranych przedmiotów w tabeli items (od 0 do n-1)
    Problem problem;
    Double fitnessFunction;

    public Individual(Problem problem) {
        this.problem = problem;
        createRandomIndividual(problem.dimension);
    }

    public Individual(int[] route, Problem problem) {
        this.route = route;
        this.problem = problem;
    }

    private void createRandomIndividual(int dimension) {
        route = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            route[i] = i + 1;
        }
        shuffleArray(route);
    }

    private static void shuffleArray(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public double calculateFitnessFunctionValue() {
        evalTravelCost(problem.minSpeed,
                problem.maxSpeed,
                problem.knapsackCapacity,
                problem.nodes);
        fitnessFunction =  totalValue - totalTime;
        return fitnessFunction;
    }

    public double getFitnessFunction() {
        if(fitnessFunction == null) {
            calculateFitnessFunctionValue();
        }
        return fitnessFunction;
    }

    private void evalTravelCost(double minSpeed,
                                double maxSpeed,
                                int knapsackCapacity,
                                List<Node> nodes) {
        itemsTaken = new ArrayList<>();
        double time = 0;
        int currentKnapsack = 0;
        int routeStartCityIndex = route[0];
        for (int i = 0; i < route.length; i++) {
            // getting two cities
            int firstCityIndex = route[i];
            int secondCityIndex;
            if (i + 1 < route.length) {
                secondCityIndex = route[i + 1];
            } else {
                secondCityIndex = route[0];
            }
            //getting 2 cities' stats
            Node firstCity = nodes.get(firstCityIndex - 1);
            int xFirst = firstCity.getxCoord();
            int yFirst = firstCity.getyCoord();

            Node secondCityStats = nodes.get(secondCityIndex - 1);
            int xSecond = secondCityStats.getxCoord();
            int ySecond = secondCityStats.getyCoord();

            //calculate distance between 2 cities
            double distance = Math.sqrt(Math.pow(Math.abs(xFirst - xSecond), 2) + Math.pow(Math.abs(yFirst - ySecond), 2));

            //calculate velocity
            double velocity = maxSpeed - (currentKnapsack * ((maxSpeed - minSpeed) / knapsackCapacity));

            double sectionTime = distance / velocity;
            time = time + sectionTime;

            List<Integer[]> secondCityItems = secondCityStats.getItems();
            if (secondCityIndex != routeStartCityIndex) {
                currentKnapsack = calculateKnapsack(currentKnapsack, knapsackCapacity, secondCityItems);
            }
        }
        totalTime = time;
        totalKnapsack = currentKnapsack;
        totalValue = calculateTotalKnapsackValue(itemsTaken);
    }

    private int calculateKnapsack(int currentKnapsack, int knapsackCapacity, List<Integer[]> items) {

        if (!items.isEmpty()) {
            Collections.sort(items, (s1, s2) -> Double.compare((s1[1] / (double) s1[2]), (s2[1] / (double) s2[2])));
            Integer[] itemToTake = items.get(0);

            if ((currentKnapsack + itemToTake[2]) > knapsackCapacity) {
                return currentKnapsack;
            } else {
                itemsTaken.add(itemToTake[1]); // dodajemy profit
                return currentKnapsack + itemToTake[2]; // itemToTake.weight
            }
        }
        else {
            return currentKnapsack;
        }
    }

    private int calculateTotalKnapsackValue(List<Integer> items) {
        int value = 0;
        for (int i = 0; i < itemsTaken.size(); i++) {
            value = value + itemsTaken.get(i);
        }
        return value;
    }

    public static Individual mutate(Individual toMutate) {
        int[] route = toMutate.route;
        int[] newRoute = route.clone();
        Random random = new Random();
        int fst = random.nextInt(route.length);
        int snd = random.nextInt(route.length-1);
        for(int left = fst, right = snd; left <right; left++, right-- ) {
            int temp = newRoute[left];
            newRoute[left] = newRoute[right];
            newRoute[right] = temp;
        }
        return new Individual(newRoute, toMutate.problem);
    }

    public static Individual[] crossoverPMX(Individual fstParent, Individual sndParent) {
        int genLength = fstParent.route.length;
        int [] fstParentRoute = fstParent.route;
        int [] sndParentRoute = sndParent.route;
        Individual[] children = new Individual[2];
        int[] offspring1 = Arrays.copyOf(fstParentRoute, genLength);
        int[] offspring2 = Arrays.copyOf(sndParentRoute, genLength);

        Random random = new Random();
        int fstCuttingPoint = random.nextInt(genLength);
        int sndCuttingPoint = random.nextInt(genLength - 1);


        // setting up cutting points, second is always larger than first
        if (fstCuttingPoint == sndCuttingPoint) {
            sndCuttingPoint = genLength - 1;
        }
        if (fstCuttingPoint > sndCuttingPoint) {
            int temp = fstCuttingPoint;
            fstCuttingPoint = sndCuttingPoint;
            sndCuttingPoint = temp;
        }

        // setting up helper tables
        int[] mapping1 = new int[genLength];
        int[] mapping2 = new int[genLength];

        Arrays.fill(mapping1, -1);
        Arrays.fill(mapping2, -1);

        // copying matching section
        for (int i = fstCuttingPoint; i<=sndCuttingPoint; i++) {
            offspring1[i] = sndParentRoute[i];
            offspring2[i] = fstParentRoute[i];

            mapping1[sndParentRoute[i]-1] = fstParentRoute[i];
            mapping2[fstParentRoute[i]-1] = sndParentRoute[i];
        }

        // fill in remaining slots with replacements
        for (int i = 0; i < genLength; i++) {
            if ((i < fstCuttingPoint) || (i > sndCuttingPoint)) {
                int number1 = offspring1[i]; //6
                int map1 = mapping1[number1-1]; // 5

                while (map1 != -1) {
                    number1 = map1; // number1 = 5
                    map1 = mapping1[map1-1]; //  map1 = -1
                }
                offspring1[i] = number1;

                int number2 = offspring2[i];
                int map2 = mapping2[number2-1];

                while (map2 != -1) {
                    number2 = map2;
                    map2 = mapping2[number2-1];
                }
                offspring2[i] = number2;
            }
        }
        children[0] = new Individual(offspring1, fstParent.problem);
        children[1] = new Individual(offspring2, fstParent.problem);
        return children;
    }

    private static void swap(int[] parent, int[] crossedParent, int index) {
        int city = parent[index];
        int temp = crossedParent[index];
        crossedParent[index] = city;
        for(int i = 0; i < crossedParent.length; i++){
            if(i != index && crossedParent[i]==city){
                crossedParent[i] = temp;
            }
        }
    }

    public Individual clone() {
        Individual clone = new Individual(route, problem);
        clone.fitnessFunction = fitnessFunction;
        clone.itemsTaken = itemsTaken;
        clone.totalKnapsack = totalKnapsack;
        clone.totalValue = totalValue;
        clone.totalTime = totalTime;
        return clone;
    }

    public static void main(String[] args) {
        int [] route = {0,1,2,3,4,5,6,7,8,9};
       Individual ind = new Individual(route, null);
       Individual individual = Individual.mutate(ind);
        System.out.println(Arrays.toString(individual.route));
    }
}
