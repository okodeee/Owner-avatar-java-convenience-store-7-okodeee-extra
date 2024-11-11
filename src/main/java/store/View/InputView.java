package store.View;

import camp.nextstep.edu.missionutils.Console;
import store.Model.OrderItem;
import java.util.*;

public class InputView {
    public List<OrderItem> getOrderItems() {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String inputOrder = Console.readLine();

        List<OrderItem> orderItems = new ArrayList<>();
        inputOrder = inputOrder.replace("[", "").replace("]", "");
        String[] items = inputOrder.split(",");

        for (String item : items) {
            String[] parts = item.split("-");
            String productName = parts[0].trim();
            int quantity = Integer.parseInt(parts[1].trim());
            orderItems.add(new OrderItem(productName, quantity));
        }
        return orderItems;
    }

    public boolean askPromotionAddition(String productName, int additionalQuantity) {
        System.out.printf("\n현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", productName, additionalQuantity);
        String response = Console.readLine().trim().toUpperCase();
        return response.equals("Y");
    }

    public boolean askPartiallyRegularPrice(String productName, int quantity) {
        System.out.printf("\n현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", productName, quantity);
        String response = Console.readLine().trim().toUpperCase();
        return response.equals("Y");
    }

    public boolean askMembershipDiscount() {
        System.out.print("\n멤버십 할인을 받으시겠습니까? (Y/N)\n");
        String response = Console.readLine().trim().toUpperCase();
        return response.equals("Y");
    }

    public boolean askAdditionalOrder() {
        System.out.print("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)\n");
        String response = Console.readLine().trim().toUpperCase();
        return response.equals("Y");
    }
}
