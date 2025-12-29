package engine.events;

import engine.domain.OrderSide;
import engine.domain.OrderType;

public final class OrderPlacedEvent implements Event {

    private final long orderId;
    private final String symbol;
    private final OrderSide side;
    private final OrderType type;
    private final double price;
    private final long quantity;
    private final long timestamp;

    public OrderPlacedEvent(
            long orderId,
            String symbol,
            OrderSide side,
            OrderType type,
            double price,
            long quantity,
            long timestamp
    ) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
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

    @Override
    public long timestamp() {
        return timestamp;
    }
}
