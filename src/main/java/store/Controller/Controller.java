package store.Controller;

import java.util.*;
import store.Model.OrderItem;
import store.Model.Product;
import store.Model.Products;
import store.Model.Promotion;
import store.Model.Promotions;
import store.Model.Receipt;
import store.View.InputView;
import store.View.OutputView;

public class Controller {
    private Products products;
    private InputView inputView;
    private OutputView outputView;

    public Controller() {
        Promotions promotions = new Promotions();
        promotions.readPromotionsFromFile("src/main/resources/promotions.md");

        this.products = new Products(promotions);
        products.readProductsFromFile("src/main/resources/products.md");
        this.inputView = new InputView();
        this.outputView = new OutputView();
    }

    public void run() {
        do {
            outputView.printProducts(products.getProducts());

            List<OrderItem> orderItems;
            while (true) {
                orderItems = inputView.getOrderItems();

                if (validateOrderItems(orderItems)) {
                    break;
                }
            }
            processOrder(orderItems);

        } while (inputView.askAdditionalOrder());
    }

    private boolean validateOrderItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Optional<Product> productOpt = products.findProductByName(orderItem.getProductName());

            if (productOpt.isEmpty()) {
                System.out.println("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
                return false;
            }

            Product product = productOpt.get();
            if (orderItem.getQuantity() > product.getAvailableQuantity()) {
                System.out.println("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
                return false;
            }
        }
        return true;
    }

    // 총 결제 금액 계산 및 영수증 작성 메서드
    public void processOrder(List<OrderItem> orderItems) {
        Receipt receipt = new Receipt();
        receipt.startReceipt();

        int remainingAmountAfterPromotion = 0;
        for (OrderItem orderItem : orderItems) {
            Optional<Product> productOpt = products.findProductByName(orderItem.getProductName());

            if (productOpt.isEmpty()) continue;

            Product product = productOpt.get();
            int quantity = orderItem.getQuantity();
            int price = product.getPrice();
            int itemTotalCost = price * quantity;

            receipt.addTotal(quantity, itemTotalCost);

            remainingAmountAfterPromotion += itemTotalCost;

            if (product.getPromotion().isPresent()) {
                Promotion promotion = product.getPromotion().get();
                int buy = promotion.getBuy();
                int get = promotion.getGet();

                if (quantity % (buy + get) == buy && (quantity + 1 <= product.getPromotionQuantity())) {
                    if (inputView.askPromotionAddition(product.getName(), get)) {
                        quantity += 1;
                        itemTotalCost = price * quantity;
                        receipt.addTotal(1, price);

                        remainingAmountAfterPromotion += price;
                    }
                }

                // 주문에 대해 프로모션 재고와 일반 재고 사용을 계산
                PromotionUsage usage = calculatePromotionUsage(quantity, product.getPromotionQuantity(), product.getRegularQuantity(), buy + get, price);
                product.decreasePromotionQuantity(usage.promotionUsed);
                product.decreaseRegularQuantity(usage.regularUsed);
                receipt.addDiscount(usage.discount);

                if (usage.freeItems > 0) {
                    receipt.addGiftItem(product.getName(), usage.freeItems);
                }

                // 프로모션 적용되지 않은 수량 처리
                if (usage.nonDiscountedItems > 0 && !inputView.askPartiallyRegularPrice(product.getName(), usage.nonDiscountedItems)) {
                    System.out.printf("'%s' 상품의 주문이 취소되었습니다.\n", product.getName());
                    continue;
                }

                // 프로모션 적용된 세트 수 계산 후, 해당 금액 제외
                int setsWithPromotion = usage.freeItems;
                int promoItems = setsWithPromotion * (buy + get);
                remainingAmountAfterPromotion -= promoItems * product.getPrice();
            } else {
                // 프로모션이 없는 경우 일반 재고에서 차감
                if (quantity > product.getRegularQuantity()) {
                    System.out.println("[ERROR] 일반 재고가 부족하여 주문을 처리할 수 없습니다.");
                    continue;
                }
                product.decreaseRegularQuantity(quantity);
                receipt.addItemToReceipt(product.getName(), quantity, itemTotalCost);
            }
        }

        if (inputView.askMembershipDiscount()) {
            receipt.applyMembershipDiscount(remainingAmountAfterPromotion);
        }

        receipt.finalizeReceipt();
        outputView.printReceipt(receipt.getReceiptDetails());
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
