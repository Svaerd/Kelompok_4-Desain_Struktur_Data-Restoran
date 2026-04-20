import java.util.Stack;

public class Item {
    String itemCode, name, category, currentStatus;
    Stack<HistoryChange> history; // Stack untuk histori status
    Item next; // Referensi (Node) untuk Linked List

    public Item(String code, String name, String category) {
        this.itemCode = code;
        this.name = name;
        this.category = category;
        this.currentStatus = "Dibuat";
        this.history = new Stack<>();
        this.next = null;
    }
}
//Nama: Gede Satya Putra Aryanta
//NRP: 5027251012