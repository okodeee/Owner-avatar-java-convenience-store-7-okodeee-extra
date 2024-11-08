package store.Model;

import java.io.*;
import java.util.*;

public class Products {
    private List<Product> products;

    public Products() {
        products = new ArrayList<>();
    }

    public void readProductsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    String name = parts[0].trim();
                    int price = Integer.parseInt(parts[1].trim());
                    int quantity = Integer.parseInt(parts[2].trim());
                    String promotion = parts[3].trim();

                    Product product;
                    if (promotion.equals("null"))
                    {
                        product = new Product(name, price, quantity);
                        products.add(product);
                        continue;
                    }
                    product = new Product(name, price, quantity, promotion);
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public Optional<Product> findProductByName(String productName) {
        return products.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst();
    }

    public boolean processOrder(List<OrderItem> order) {
        for (OrderItem orderItem : order) {
            Optional<Product> productOpt = findProductByName(orderItem.getProductName());

            if (productOpt.isEmpty()) {
                System.out.println("상품 " + orderItem.getProductName() + "을(를) 찾을 수 없습니다.");
                return false;
            }

            Product product = productOpt.get();
            int currentStock = product.getQuantity();

            if (currentStock < orderItem.getQuantity()) {
                System.out.println("상품 " + orderItem.getProductName() + "의 재고가 부족합니다. 현재 재고: " + currentStock);
                return false;
            }
        }

        for (OrderItem orderItem : order) {
            Product product = findProductByName(orderItem.getProductName()).get();
            int updatedStock = product.getQuantity() - orderItem.getQuantity();
            product.setQuantity(updatedStock);
        }

        return true;
    }

    public List<Product> getProducts() {
        return products;
    }
}
