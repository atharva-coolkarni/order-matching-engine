package engine.queue;

import engine.domain.*;
import engine.events.OrderPlacedEvent;
import engine.events.TradeExecutedEvent;
import engine.events.Event;
import engine.matching.MatchingEngine;
import engine.orderbook.OrderBook;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher implements Runnable {

    private final EventQueue eventQueue;
    private final MatchingEngine matchingEngine;
    private final List<TradeExecutedEvent> tradeEvents = new ArrayList<>();
    private volatile boolean running = true;

    public EventDispatcher(EventQueue eventQueue, OrderBook orderBook) {
        this.eventQueue = eventQueue;
        this.matchingEngine = new MatchingEngine(orderBook);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Event event = eventQueue.take();
                handle(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
            }
        }
    }

    private void handle(Event event) {
        if (event instanceof OrderPlacedEvent ope) {
            handleOrderPlaced(ope);
        }
    }

    private void handleOrderPlaced(OrderPlacedEvent e) {
        Order order = new Order(
                e.getOrderId(),
                e.getSymbol(),
                e.getSide(),
                e.getType(),
                e.getPrice(),
                e.getQuantity(),
                e.getQuantity(),
                e.timestamp(),
                OrderStatus.NEW
        );

        matchingEngine.process(order).forEach(trade -> {
            tradeEvents.add(
                    new TradeExecutedEvent(
                            trade.getTradeId(),
                            trade.getBuyOrderId(),
                            trade.getSellOrderId(),
                            trade.getPrice(),
                            trade.getQuantity(),
                            trade.getTimestamp()
                    )
            );
        });
    }

    public List<TradeExecutedEvent> getTradeEvents() {
        return tradeEvents;
    }

    public void stop() {
        running = false;
    }
}
