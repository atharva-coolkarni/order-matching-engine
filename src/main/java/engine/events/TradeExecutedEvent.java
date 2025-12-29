package engine.events;

public final class TradeExecutedEvent implements Event {

    private final long tradeId;
    private final long buyOrderId;
    private final long sellOrderId;
    private final double price;
    private final long quantity;
    private final long timestamp;

    public TradeExecutedEvent(
            long tradeId,
            long buyOrderId,
            long sellOrderId,
            double price,
            long quantity,
            long timestamp
    ) {
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public long getTradeId() {
        return tradeId;
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getSellOrderId() {
        return sellOrderId;
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
