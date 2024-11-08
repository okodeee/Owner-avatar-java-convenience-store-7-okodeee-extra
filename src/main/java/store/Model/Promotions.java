package store.Model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Promotions {
    private List<Promotion> promotions;

    public Promotions() {
        promotions = new ArrayList<>();
    }

    public void readPromotionsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int buy = Integer.parseInt(parts[1].trim());
                int get = Integer.parseInt(parts[2].trim());
                LocalDate startDate = LocalDate.parse(parts[3].trim());
                LocalDate endDate = LocalDate.parse(parts[4].trim());

                promotions.add(new Promotion(name, buy, get, startDate, endDate));
            }
        } catch (IOException e) {
            System.err.println("Error reading promotion file: " + e.getMessage());
        }
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public Promotion findPromotionByName(String name) {
        return promotions.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
