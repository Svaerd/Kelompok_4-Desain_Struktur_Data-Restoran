public class Order {
    String orderCode;
    Item headItem; // Head Linked List
    Item tailItem; // Tail Linked List untuk insert O(1)

    public Order(String code) {
        this.orderCode = code;
        this.headItem = null;
        this.tailItem = null;
    }
}
//Nama: Gede Satya Putra Aryanta
//NRP: 5027251012