package store;

import store.Model.Products;

public class Application {
    public static void main(String[] args) {
        Products products = new Products();
        products.readProductsFromFile("src/main/resources/products.md");

        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        products.displayProducts();
    }
}
