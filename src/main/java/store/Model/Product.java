package store.Model;

import java.util.Optional;

public class Product {
    private final String name;
    private final int price;
    private int regularQuantity;
    private int promotionQuantity;
    private Promotion promotion;

    public Product(String name, int price, int quantity, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.promotion = promotion;
        if (promotion == null) {
            this.regularQuantity = quantity;
            this.promotionQuantity = 0;
        } else { // 추후 처리
            this.promotionQuantity = quantity;
            this.regularQuantity = 0;
        }
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getRegularQuantity() { return regularQuantity; }
    public int getPromotionQuantity() { return promotionQuantity; }
    public Optional<Promotion> getPromotion() { return Optional.ofNullable(promotion); }

    public void increaseRegularQuantity(int quantity) {
        this.regularQuantity += quantity;
    }

    public void increasePromotionQuantity(int quantity) {
        this.promotionQuantity += quantity;
    }

    public void decreaseRegularQuantity(int quantity) {
        if (quantity <= regularQuantity) {
            regularQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("일반 재고가 부족합니다.");
        }
    }

    public void decreasePromotionQuantity(int quantity) {
        if (quantity <= promotionQuantity) {
            promotionQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("프로모션 재고가 부족합니다.");
        }
    }

    @Override
    public String toString() {
        if (promotion == null) {
            if (regularQuantity <= 0) return "- " + name + " " + String.format("%,d", price) + "원 " + "재고 없음";
            return "- " + name + " " + String.format("%,d", price) + "원 " + regularQuantity + "개";
        }

        if (promotionQuantity <= 0 && regularQuantity > 0) {
            return "- " + name + " " + String.format("%,d", price) + "원 " + "재고 없음 " + promotion.getName()
                    + "\n- " + name + " " + String.format("%,d", price) + "원 " + regularQuantity + "개";
        } else if (promotionQuantity > 0 && regularQuantity <= 0) {
            return "- " + name + " " + String.format("%,d", price) + "원 " + promotionQuantity + "개 " + promotion.getName()
                    + "\n- " + name + " " + String.format("%,d", price) + "원 " + "재고 없음";
        }
        return "- " + name + " " + String.format("%,d", price) + "원 " + promotionQuantity + "개 " + promotion.getName()
                + "\n- " + name + " " + String.format("%,d", price) + "원 " + regularQuantity + "개";
    }
}
