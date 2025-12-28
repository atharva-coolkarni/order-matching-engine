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

    // constructor(s)

    // getters only (no setters except remainingQuantity & status)
}
