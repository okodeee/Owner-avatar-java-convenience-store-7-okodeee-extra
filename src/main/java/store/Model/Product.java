package store.Model;

public class Product {
    private final String name;
    private final int price;
    private int quantity;
    private String promotion;

    public Product(String name, int price, int quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public Product(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getPromotion() { return promotion; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        if (promotion == null) {
            return "- " + name + " " + String.format("%,d", price) + "원 " + quantity + "개";
        }
        return "- " + name + " " + String.format("%,d", price) + "원 " + quantity + "개 " + promotion;
    }
}
