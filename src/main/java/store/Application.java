package store;

import java.util.*;
import store.Model.Order;
import store.Model.OrderItem;
import store.Model.Products;
import store.Model.Receipt;
import store.View.OutputView;

public class Application {
    public static void main(String[] args) {
        Products products = new Products();
        products.readProductsFromFile("src/main/resources/products.md");

        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        OutputView outputView = new OutputView();
        outputView.printProducts(products.getProducts());

        Order order = new Order();
        List<OrderItem> orderItems = order.receiveOrder();

        if (products.processOrder(orderItems)) {
            Receipt receipt = new Receipt();
            receipt.generateReceipt(orderItems, products);
            receipt.printReceipt();
        } else { // else 예약어 변경 필요
            System.out.println("주문을 완료할 수 없습니다. 재고 부족 혹은 상품 오류가 발생했습니다.");
        }

//        outputView.printProducts(products.getProducts());
    }
}
