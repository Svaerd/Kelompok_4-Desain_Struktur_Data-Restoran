/*	Nama	: Catur Setyo Ragil
	NRP		: 5027251066 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryService {
    public static HashMap<String, List<String>> categoryMap = new HashMap<>();

    public static void mapOrderToCategory(String orderCode, String category) {
        List<String> orders = categoryMap.computeIfAbsent(category, k -> new ArrayList<>());
        if (!orders.contains(orderCode)) {
            orders.add(orderCode);
        }
    }

    public static void getOrdersByCategory(String category) {
        System.out.println("Pesanan yang memiliki item di kategori [" + category + "]:");
        List<String> orders = categoryMap.getOrDefault(category, new ArrayList<>());
        for (String code : orders) {
            System.out.println(" - " + code);
        }
    }
}
