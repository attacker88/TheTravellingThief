import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Problem {

    private static final int NEXT_LINE_NORMAL = 0;
    private static final int NEXT_LINE_NODE = 1;
    private static final int NEXT_LINE_ITEM = 2;
    int dimension = 0;
    int itemsNumber = 0;
    int knapsackCapacity = 0;
    double minSpeed = 0;
    double maxSpeed = 0;
    List<Node> nodes;
    int itemsRead = 0;

    public Problem(String path) {
        nodes = new ArrayList<>();
        read(path);
    }

    public void read(String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line = bufferedReader.readLine();
            int whichLineIsNext = 0;
            while (line != null) {
                String[] args = line.split("\t");

                if (whichLineIsNext == NEXT_LINE_NORMAL) {
                    whichLineIsNext = readArgs(args);
                }
                else if (whichLineIsNext == NEXT_LINE_NODE){
                    if (nodes.size() != dimension){
                        readNode(args);
                    }
                    else {
                        whichLineIsNext = readArgs(args);
                    }
                }
                else if (whichLineIsNext == NEXT_LINE_ITEM){
                    if (itemsRead != itemsNumber){
                        readItem(args);
                    }
                    else {
                        whichLineIsNext = readArgs(args);
                    }
                }
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readArgs(String[] args) {
        if(dimension==0 && args[0].contains("DIMENSION:")){
            dimension = Integer.parseInt(args[1]);
        }
        else if(itemsNumber==0 && args[0].contains("NUMBER OF ITEMS:")){
            itemsNumber = Integer.parseInt(args[1]);
        }
        else if (knapsackCapacity == 0 && args[0].contains("CAPACITY OF KNAPSACK:")) {
            knapsackCapacity = Integer.parseInt(args[1]);
        }
        else if (minSpeed == 0 && args[0].contains("MIN SPEED:")) {
            minSpeed = Double.parseDouble(args[1]);
        }
        else if (maxSpeed == 0 && args[0].contains("MAX SPEED: ")) {
            maxSpeed = Double.parseDouble(args[1]);
        }
        else if(args[0].contains("NODE_COORD_SECTION")) {
            return NEXT_LINE_NODE;
        }
        else if(args[0].contains("ITEMS SECTION")) {
            return NEXT_LINE_ITEM;
        }
        return NEXT_LINE_NORMAL;
    }

    private void readNode(String[] args) {
        int index = (int)Math.floor(Double.parseDouble(args[0]));
        int xCoord = (int)Math.floor(Double.parseDouble(args[1]));
        int yCoord = (int)Math.floor(Double.parseDouble(args[2]));
        nodes.add(new Node(index, xCoord, yCoord));
    }

    private void readItem(String[] args) {
        itemsRead++;
        Integer[] item = new Integer[3];
        item[0] = Integer.parseInt(args[0]);
        item[1] = Integer.parseInt(args[1]);
        item[2] = Integer.parseInt(args[2]);
        int nodeNr = Integer.parseInt(args[3]);
        for (Node node:nodes) {
            if (node.index == nodeNr) {
                node.addItem(item);
            }
        }
    }
}
