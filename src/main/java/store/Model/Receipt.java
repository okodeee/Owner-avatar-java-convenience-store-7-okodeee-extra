package store.Model;

import java.util.List;
import java.util.Optional;

public class Receipt {
    private int totalAmount;
    private StringBuilder receiptDetails;

    public Receipt() {
        this.totalAmount = 0;
        this.receiptDetails = new StringBuilder();
    }

    // 총 결제 금액 계산 및 영수증 작성 메서드
    public void generateReceipt(List<OrderItem> orderItems, Products products) {
        receiptDetails.append("==============W 편의점================\n");
        receiptDetails.append(String.format("%-10s %5s %8s\n", "상품명", "수량", "금액"));

        for (OrderItem orderItem : orderItems) {
            Optional<Product> productOpt = products.findProductByName(orderItem.getProductName());

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                int itemTotalPrice = product.getPrice() * orderItem.getQuantity();
                totalAmount += itemTotalPrice;

                // 출력 양식 변경 필요
                receiptDetails.append(String.format("%-10s %5d %,8d\n",
                        product.getName(),
                        orderItem.getQuantity(),
                        itemTotalPrice));
            }
        }

        receiptDetails.append("====================================\n");
        receiptDetails.append("총구매액 ").append(String.format("%,d", totalAmount));
    }

    public void printReceipt() {
        System.out.println(receiptDetails.toString());
    }

    public int getTotalAmount() {
        return totalAmount;
    }
}
