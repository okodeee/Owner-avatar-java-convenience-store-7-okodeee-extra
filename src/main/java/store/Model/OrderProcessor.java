package store.Model;

import camp.nextstep.edu.missionutils.Console;
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
            int freeItems = 0;

            totalQuantity += quantity;
            totalAmount += itemTotalCost;

            if (product.getPromotion().isPresent()) {
                Promotion promotion = product.getPromotion().get();
                int buy = promotion.getBuy();
                int get = promotion.getGet();

                if (quantity % (buy + get) == buy && (quantity + 1 <= product.getPromotionQuantity())) {
                    System.out.printf("현재 %s은(는) 1개를 더 구매하시면 %d개를 무료로 받을 수 있습니다. 추가하시겠습니까? (Y/N): ",
                            product.getName(), get);
                    String response = Console.readLine().trim().toUpperCase();
                    if (response.equals("Y")) {
                        quantity += 1;
                        itemTotalCost = price * quantity;
                        totalQuantity += 1;
                        totalAmount += price; // 추가된 1개의 가격만큼 총 금액 증가
                    }
                }

                // 주문에 대해 프로모션 재고와 일반 재고 사용을 계산
                PromotionUsage usage = calculatePromotionUsage(quantity, product.getPromotionQuantity(), product.getRegularQuantity(), buy + get, price);
                product.decreasePromotionQuantity(usage.promotionUsed);
                product.decreaseRegularQuantity(usage.regularUsed);
                discountAmount += usage.discount;

                // 증정 상품 내역에 추가
                if (usage.freeItems > 0) {
                    giftDetails.append(String.format("%-17s %-5d\n", product.getName(), usage.freeItems));
                }

                // 프로모션 적용되지 않은 수량 처리
                if (usage.nonDiscountedItems > 0) {
                    System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N): ",
                            product.getName(), usage.nonDiscountedItems);
                    String response = Console.readLine().trim().toUpperCase();

                    if (!response.equals("Y")) {
                        System.out.printf("'%s' 상품의 주문이 취소되었습니다.\n", product.getName());
                        continue;
                    }
                }
            } else {
                // 프로모션이 없는 경우 일반 재고에서 차감
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

    /**
     * 프로모션 재고와 일반 재고를 고려하여 사용량을 계산하는 메서드
     */
    private PromotionUsage calculatePromotionUsage(int quantity, int promotionQuantity, int regularQuantity, int setQuantity, int price) {
        int requiredPromotionStock = 0;
        int requiredRegularStock = 0;
        int freeItems = 0;
        int discount = 0;

        // 최대한 프로모션 혜택을 적용 가능한 세트 수 계산
        while (quantity >= setQuantity && promotionQuantity >= setQuantity) {
            quantity -= setQuantity;
            promotionQuantity -= setQuantity;
            freeItems++;
            discount += price;
            requiredPromotionStock += setQuantity;
        }

        if (promotionQuantity - quantity >= 0) {
            requiredPromotionStock += quantity;
        } else if (promotionQuantity - quantity < 0) {
            requiredPromotionStock += promotionQuantity;
            requiredRegularStock = quantity - promotionQuantity;
        }

        return new PromotionUsage(requiredPromotionStock, requiredRegularStock, freeItems, discount, quantity);
    }

    // 프로모션 사용량을 계산하여 반환하는 보조 클래스
    private static class PromotionUsage {
        int promotionUsed;        // 사용된 프로모션 재고
        int regularUsed;          // 사용된 일반 재고
        int freeItems;            // 증정되는 상품 수
        int discount;             // 할인 금액
        int nonDiscountedItems;   // 프로모션 혜택을 받지 못한 수량

        PromotionUsage(int promotionUsed, int regularUsed, int freeItems, int discount, int nonDiscountedItems) {
            this.promotionUsed = promotionUsed;
            this.regularUsed = regularUsed;
            this.freeItems = freeItems;
            this.discount = discount;
            this.nonDiscountedItems = nonDiscountedItems;
        }
    }
}
