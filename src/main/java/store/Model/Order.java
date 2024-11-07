package store.Model;

import java.util.*;
import store.View.InputView;

public class Order {
    public List<OrderItem> receiveOrder() {
        InputView inputView = new InputView();
        String inputOrder = inputView.readOrder();

        List<OrderItem> orders = new ArrayList<>();

        inputOrder = inputOrder.replace("[", "").replace("]", "");
        String[] items = inputOrder.split(",");

        for (String item : items) {
            String[] parts = item.split("-");
            String productName = parts[0].trim();
            int quantity = Integer.parseInt(parts[1].trim());
            orders.add(new OrderItem(productName, quantity));
        }

        return orders;
    }
}
