package store.View;

import camp.nextstep.edu.missionutils.Console;
import store.Model.OrderItem;
import java.util.*;

public class InputView {
    public List<OrderItem> getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();

        while (true) {
            System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
            String inputOrder = Console.readLine();

            try {
                orderItems.clear();
                inputOrder = inputOrder.replace("[", "").replace("]", "");
                String[] items = inputOrder.split(",");

                for (String item : items) {
                    String[] parts = item.split("-");
                    String productName = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    orderItems.add(new OrderItem(productName, quantity));
                }
                return orderItems;
            } catch (Exception e) {
                System.out.println("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
        }
    }

    public boolean askPromotionAddition(String productName, int additionalQuantity) {
        while (true) {
            System.out.printf("\n현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n", productName, additionalQuantity);
            String response = Console.readLine().trim().toUpperCase();
            if (validateResponse(response)) {
                return response.equals("Y");
            }
        }
    }

    public boolean askPartiallyRegularPrice(String productName, int quantity) {
        while (true) {
            System.out.printf("\n현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n", productName, quantity);
            String response = Console.readLine().trim().toUpperCase();
            if (validateResponse(response)) {
                return response.equals("Y");
            }
        }
    }

    public boolean askMembershipDiscount() {
        while (true) {
            System.out.print("\n멤버십 할인을 받으시겠습니까? (Y/N)\n");
            String response = Console.readLine().trim().toUpperCase();
            if (validateResponse(response)) {
                return response.equals("Y");
            }
        }
    }

    public boolean askAdditionalOrder() {
        while (true) {
            System.out.print("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)\n");
            String response = Console.readLine().trim().toUpperCase();
            if (validateResponse(response)) {
                return response.equals("Y");
            }
        }
    }

    private static boolean validateResponse(String response) {
        if (response.equals("Y") || response.equals("N")) {
            return true;
        } else {
            System.out.println("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
        return false;
    }
}
