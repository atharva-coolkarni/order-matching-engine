package engine.domain;

public class Trade {

    private final long tradeId;
    private final long buyOrderId;
    private final long sellOrderId;
    private final String symbol;
    private final double price;
    private final long quantity;
    private final long timestamp;

    // constructor + getters

    public Trade(long tradeId, long buyOrderId, long sellOrderId, String symbol, double price, long quantity, long timestamp) {
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public long getSellOrderId() {
        return sellOrderId;
    }

    public long getBuyOrderId() {
        return buyOrderId;
    }

    public long getTradeId() {
        return tradeId;
    }
}
