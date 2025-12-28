package engine.domain;

public class Order {

    private final long orderId;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double price;
    private final long quantity;
    private long remainingQuantity;
    private final long timestamp;
    private OrderStatus status;

    public Order(
            long orderId,
            String symbol,
            OrderSide side,
            OrderType type,
            double price,
            long quantity,
            long remainingQuantity,
            long timestamp,
            OrderStatus status
    ) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = remainingQuantity;
        this.timestamp = timestamp;
        this.status = status;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setRemainingQuantity(long remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
