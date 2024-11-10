package store.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import store.View.InputView;

public class OrderProcessor {
    private Products products;
    private int totalQuantity;
    private int totalAmount;      // 총 구매액
    private int discountAmount;    // 총 할인 금액
    private StringBuilder receiptDetails;
    private StringBuilder giftDetails;

    public OrderProcessor(Products products) {
        this.products = products;
        this.totalQuantity = 0;
        this.totalAmount = 0;
        this.discountAmount = 0;
        this.receiptDetails = new StringBuilder();
        this.giftDetails = new StringBuilder();
    }

    // 사용자 입력을 받아 주문 목록 생성
    public List<OrderItem> receiveOrder() {
        InputView inputView = new InputView();
        String inputOrder = inputView.readOrder();

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

    // 총 결제 금액 계산 및 영수증 작성 메서드
    public void processOrder(List<OrderItem> orderItems) {
        receiptDetails.append("==============W 편의점================\n");
        receiptDetails.append(String.format("%-17s %-5s %-8s\n", "상품명", "수량", "금액"));

        for (OrderItem orderItem : orderItems) {
            Optional<Product> productOpt = products.findProductByName(orderItem.getProductName());

            if (productOpt.isEmpty()) {
                System.out.println("상품 " + orderItem.getProductName() + "을(를) 찾을 수 없습니다.");
                continue;
            }

            Product product = productOpt.get();
            int quantity = orderItem.getQuantity();
            int price = product.getPrice();
            int itemTotalCost = price * quantity;
            int discount = 0;
            int freeItems = 0;

            totalQuantity += quantity;
            totalAmount += itemTotalCost;

            // 프로모션이 있는 경우
            if (product.getPromotion().isPresent()) {
                Promotion promotion = product.getPromotion().get();
                int buy = promotion.getBuy();
                int get = promotion.getGet();

                // 프로모션 혜택 계산
                freeItems = (quantity / (buy + get)) * get;
                discount = freeItems * price;  // 할인 금액
                discountAmount += discount;     // 총 할인 금액 업데이트

                // 증정 상품 포함한 프로모션 재고 차감
                int promotionUsed = quantity; // 전체 구매 수량을 프로모션 재고에서 차감
                if (promotionUsed > product.getPromotionQuantity()) {
                    System.out.println("프로모션 재고가 부족하여 주문을 처리할 수 없습니다.");
                    continue;
                }
                product.decreasePromotionQuantity(promotionUsed);

                // 증정 상품 내역에 추가
                if (freeItems > 0) {
                    giftDetails.append(String.format("%-17s %-5d\n", product.getName(), freeItems));
                }
            } else {
                // 프로모션이 없는 경우 일반 재고 차감
                if (quantity > product.getRegularQuantity()) {
                    System.out.println("일반 재고가 부족하여 주문을 처리할 수 없습니다.");
                    continue;
                }
                product.decreaseRegularQuantity(quantity);
            }

            receiptDetails.append(String.format("%-17s %-5d %,-8d\n", product.getName(), quantity, itemTotalCost));
        }

        if (giftDetails.length() > 0) {
            receiptDetails.append("=============증     정===============\n");
            receiptDetails.append(giftDetails);
        }

        receiptDetails.append("====================================\n");
        receiptDetails.append(String.format("%-17s %-5d %,-8d\n", "총구매액", totalQuantity, totalAmount));
        receiptDetails.append(String.format("%-23s %,-8d\n", "행사할인", -discountAmount));
        receiptDetails.append(String.format("%-23s %,-8d\n", "내실돈", totalAmount - discountAmount));

        printReceipt();
    }

    public void printReceipt() {
        System.out.println(receiptDetails.toString());
    }
}
