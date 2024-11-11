package store.View;

import java.util.*;
import store.Model.Product;

public class OutputView {
    public void printProducts(List<Product> products) {
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        for (Product product : products) {
            System.out.println(product);
        }
    }

    public void printReceipt(StringBuilder receiptDetails) {
        System.out.println(receiptDetails);
    }
}
