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

                    if (promotion.equals("null"))
                    {
                        Product product = new Product(name, price, quantity);
                        products.add(product);
                        continue;
                    }
                    Product product = new Product(name, price, quantity, promotion);
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public void displayProducts() {
        for (Product product : products) {
            System.out.println(product);
        }
    }

    public List<Product> getProducts() {
        return products;
    }
}
