package engine.orderbook;

import engine.domain.Order;
import engine.domain.OrderSide;
import engine.domain.OrderType;

import java.util.*;

public class OrderBook {

    // BUY orders: highest price first
    private final NavigableMap<Double, Deque<Order>> bids =
            new TreeMap<>(Comparator.reverseOrder());

    // SELL orders: lowest price first
    private final NavigableMap<Double, Deque<Order>> asks =
            new TreeMap<>();

    public void addOrder(Order order){
        Objects.requireNonNull(order, "Order must not be null");

        if(order.getType()==OrderType.MARKET){
            throw new IllegalArgumentException("Market orders must not be added to the order book");
        }
        NavigableMap<Double, Deque<Order>> bookSide =
                order.getSide() == OrderSide.BUY ? bids : asks;

        bookSide
                .computeIfAbsent(order.getPrice(), p -> new ArrayDeque<>())
                .addLast(order); // FIFO
    }

    public void removeOrder(Order order){
        Objects.requireNonNull(order, "order must not be null");

        NavigableMap<Double, Deque<Order>> bookSide =
                order.getSide() == OrderSide.BUY ? bids : asks;

        Deque<Order> level = bookSide.get(order.getPrice());
        if (level == null) {
            return; // order already removed or never existed
        }

        level.remove(order);

        // Clean up empty price level
        if (level.isEmpty()) {
            bookSide.remove(order.getPrice());
        }
    }

    public Optional<Order> getBestBid(){
        if(bids.isEmpty()){
            return Optional.empty();
        }
        return Optional.ofNullable(bids.firstEntry().getValue().peekFirst());
    }

    public Optional<Order> getBestAsk(){
        if (asks.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(asks.firstEntry().getValue().peekFirst());
    }

    public boolean isEmpty(){
        return bids.isEmpty() && asks.isEmpty();
    }
}
