public class LinkedListService {

  // Nama: Husam Danish
  // NRP: 5027251060
  public static void printOrderItems(String orderCode) {
    Order order = DataRepository.searchOrder(orderCode);
    if (order == null || order.headItem == null) {
      System.out.println("Pesanan tidak ditemukan atau kosong.");
      return;
    }

    System.out.println("Daftar Item Pesanan [" + orderCode + "]:");
    Item current = order.headItem;
    while (current != null) {
      System.out.println(" - " + current.itemCode + " (" + current.name + ") [" + current.currentStatus + "]");
      current = current.next;
    }
  }
}
