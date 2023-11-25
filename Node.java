import java.util.ArrayList;
import java.util.List;

public class Node {
    int index;
    int xCoord;
    int yCoord;
    List<Integer[]> items;

    public Node(int index, int xCoord, int yCoord) {
        this.index = index;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.items = new ArrayList<>();
    }

    public void addItem(Integer[] item){
        items.add(item);
    }

    public int getIndex() {
        return index;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public List<Integer[]> getItems() {
        return items;
    }
    public Node clone(){
        Node node = new Node(this.index, this.xCoord, this.yCoord);
        node.items = this.items;
        return node;
    }
}
