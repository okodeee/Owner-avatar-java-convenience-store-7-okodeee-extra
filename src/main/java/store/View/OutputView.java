package store.View;

import java.util.*;
import store.Model.Product;

public class OutputView {
    public void printProducts(List<Product> products) {
        for (Product product : products) {
            System.out.println(product);
        }
    }
}
