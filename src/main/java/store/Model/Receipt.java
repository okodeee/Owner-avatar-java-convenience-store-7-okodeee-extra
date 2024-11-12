package store.Model;

public class Receipt {
    private int totalQuantity;
    private int totalAmount;
    private int promotionDiscountAmount;
    private int membershipDiscountAmount;
    private StringBuilder receiptDetails;
    private StringBuilder giftDetails;

    public Receipt() {
        this.totalQuantity = 0;
        this.totalAmount = 0;
        this.promotionDiscountAmount = 0;
        this.membershipDiscountAmount = 0;
        this.receiptDetails = new StringBuilder();
        this.giftDetails = new StringBuilder();
    }

    public void startReceipt() {
        receiptDetails.append("\n==============W 편의점================\n");
        receiptDetails.append(String.format("%-17s %-5s %-8s\n", "상품명", "수량", "금액"));
    }

    public void addTotal(int quantity, int itemTotalCost) {
        totalQuantity += quantity;
        totalAmount += itemTotalCost;
    }

    public void addItemToReceipt(String productName, int quantity, int itemTotalCost) {
        receiptDetails.append(String.format("%-17s %-5d %,-8d\n", productName, quantity, itemTotalCost));
    }

    public void addPromotionDiscount(int discount) {
        promotionDiscountAmount += discount;
    }

    public void addGiftItem(String productName, int quantity) {
        giftDetails.append(String.format("%-17s %-5d\n", productName, quantity));
    }

    public void applyMembershipDiscount(int remainingAmountAfterPromotion) {
        membershipDiscountAmount = (int) Math.min(remainingAmountAfterPromotion * 0.3, 8000);
    }

    public void finalizeReceipt() {
        if (giftDetails.length() > 0) {
            receiptDetails.append("=============증     정===============\n");
            receiptDetails.append(giftDetails);
        }

        receiptDetails.append("====================================\n");
        receiptDetails.append(String.format("%-17s %-5d %,-8d\n", "총구매액", totalQuantity, totalAmount));
        receiptDetails.append(String.format("%-23s %,-8d\n", "행사할인", -promotionDiscountAmount));
        receiptDetails.append(String.format("%-23s -%,-8d\n", "멤버십할인", membershipDiscountAmount));
        receiptDetails.append(String.format("%-23s %,-8d\n", "내실돈", totalAmount - promotionDiscountAmount - membershipDiscountAmount));
    }

    public StringBuilder getReceiptDetails() {
        return receiptDetails;
    }
}
