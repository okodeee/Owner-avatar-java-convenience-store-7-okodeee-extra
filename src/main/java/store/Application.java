package store;

import java.util.*;
import store.Model.OrderItem;
import store.Model.Products;
import store.Model.Promotions;
import store.Model.OrderProcessor;
import store.View.OutputView;

public class Application {
    public static void main(String[] args) {
        Promotions promotions = new Promotions();
        promotions.readPromotionsFromFile("src/main/resources/promotions.md");

        Products products = new Products(promotions);
        products.readProductsFromFile("src/main/resources/products.md");

        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        OutputView outputView = new OutputView();
        outputView.printProducts(products.getProducts());

        OrderProcessor orderProcessor = new OrderProcessor(products);
        List<OrderItem> orderItems = orderProcessor.receiveOrder();
        orderProcessor.processOrder(orderItems);

//        outputView.printProducts(products.getProducts());
    }
}
