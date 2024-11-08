package store;

import java.util.*;
import store.Model.Order;
import store.Model.OrderItem;
import store.Model.Products;
import store.View.OutputView;

public class Application {
    public static void main(String[] args) {
        Products products = new Products();
        products.readProductsFromFile("src/main/resources/products.md");

        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        OutputView outputView = new OutputView();
        outputView.printProducts(products.getProducts());

        Order order = new Order();

        if (products.processOrder(order.receiveOrder())) {
            System.out.println("주문이 완료되었습니다!");
        } else { // else 예약어 변경 필요
            System.out.println("주문이 실패하였습니다.");
        }

        outputView.printProducts(products.getProducts());
    }
}
