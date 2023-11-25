import java.util.*;

public class Population {
    List<Individual> population;
    Problem problem;
    int popSize;
    List<Individual> nextPopulation;
    int tournament;
    double crossingProb;
    double mutationProb;

    public Population(Problem problem, int popSize, int tournament, double crossingProb, double mutationProb) {
        this.population = new ArrayList<>();
        this.problem = problem;
        this.popSize = popSize;
        this.nextPopulation = new ArrayList<>();
        this.tournament = tournament;
        this.crossingProb = crossingProb;
        this.mutationProb = mutationProb;
    }

    public void initializePopulation() {
        for (int i = 0; i < popSize; i++) {
            population.add(new Individual(problem));
        }
    }

    public void evaluation() {
        for (Individual individual : population) {
            individual.calculateFitnessFunctionValue();
        }
    }

    public void selectionByTournament() {
        while (nextPopulation.size() < popSize) {
            Individual first = tournament();
            Individual second = tournament();
            Random random = new Random();
            if (random.nextInt(100) / 100.0 < crossingProb) {
                Individual[] newIndividuals = Individual.crossoverPMX(first, second);
                if (random.nextInt(100) / 100.0 < mutationProb) {
                    newIndividuals[0] = Individual.mutate(newIndividuals[0]);
                }
                if (random.nextInt(100) / 100.0 < mutationProb) {
                    newIndividuals[1] = Individual.mutate(newIndividuals[1]);
                }
                nextPopulation.add(newIndividuals[0]);
                nextPopulation.add(newIndividuals[1]);
            } else {
                nextPopulation.add(first);
                nextPopulation.add(second);
            }
        }

    }

    public void setNextPopulation() {
        population = nextPopulation;
        nextPopulation = new ArrayList<>();
    }

    public Individual tournament() {
        List<Individual> tournamentMembers = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < tournament; i++) {
            int randomIndex = random.nextInt(popSize);
            tournamentMembers.add(population.get(randomIndex));
        }
        //tournamentSize
        Collections.sort(tournamentMembers, Comparator.comparing(Individual::getFitnessFunction));
        return tournamentMembers.get(tournamentMembers.size() - 1);
    }

    public Map<Integer, Double> roulettePrep() {

        List<Individual> populationClone = new ArrayList<>();
        for (Individual individual: population){
            populationClone.add(individual.clone());
        }


        double fitnessSum = 0;
        double worstFitness = findWorstFitness();
        if(worstFitness < 0){
            for(Individual individual : populationClone){
                individual.fitnessFunction += worstFitness * (-1);
            }
        }
        for(Individual individual : populationClone){
            fitnessSum += individual.fitnessFunction;
        }

        //tworzenie tablicy dla indeks osobnika - prawdopodobienstwo
        Map<Integer, Double> mapings = new HashMap<>();

        double probabilitySum = 0;
        for (int i = 0; i < popSize; i++) {
            Individual ind = populationClone.get(i);
            double probability = probabilitySum + ind.getFitnessFunction() / fitnessSum;
            mapings.put(i, probability);
            probabilitySum = probability;
        }
        return mapings;
    }

    public Individual roulette(Map<Integer, Double> mapings) {
        Random random = new Random();
        double number = random.nextDouble();
        int index = 0;
        for (int i = 0; i < mapings.size(); i++) {
            if (i == 0) {
                if (0 <= number && number < mapings.get(i)) {
                    index = i;
                }
            } else {
                if (mapings.get(i - 1) <= number && number <= mapings.get(i)) {
                    index = i;
                }
            }
        }
        return population.get(index);
    }

    public void selectionByRoulette() {
        Map<Integer, Double> mapings = roulettePrep();
        while (nextPopulation.size() < popSize) {
            Individual first = roulette(mapings);
            Individual second = roulette(mapings);
            Random random = new Random();
            if (random.nextInt(100) / 100.0 < crossingProb) {
                Individual[] newIndividuals = Individual.crossoverPMX(first, second);
                if (random.nextInt(100) / 100.0 < mutationProb) {
                    newIndividuals[0] = Individual.mutate(newIndividuals[0]);
                }
                if (random.nextInt(100) / 100.0 < mutationProb) {
                    newIndividuals[1] = Individual.mutate(newIndividuals[1]);
                }
                nextPopulation.add(newIndividuals[0]);
                nextPopulation.add(newIndividuals[1]);
            } else {
                nextPopulation.add(first);
                nextPopulation.add(second);
            }
        }
    }

    private double findWorstFitness() {
        double worst = Integer.MAX_VALUE;
        for(Individual individual : population){
            if(individual.getFitnessFunction()< worst){
                worst = individual.getFitnessFunction();
            }
        }
        return worst;
    }

    public double[] getPopulationStats() {
        double best = population.get(0).getFitnessFunction();
        double worst = best;
        double sum = best;
        for (int i = 1; i < population.size(); i++) {
            double curr = population.get(i).getFitnessFunction();
            if (best < curr) {
                best = curr;
            }
            if (worst > curr) {
                worst = curr;
            }
            sum = sum + curr;
        }
        double[] stats = new double[3];
        stats[0] = best;
        stats[1] = worst;
        stats[2] = sum / popSize;
        return stats;
    }

}
