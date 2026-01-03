package engine.queue;

import engine.domain.*;
import engine.events.OrderPlacedEvent;
import engine.events.TradeExecutedEvent;
import engine.events.Event;
import engine.matching.MatchingEngine;
import engine.orderbook.OrderBook;
import engine.store.EventStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventDispatcher implements Runnable {

    private final EventQueue eventQueue;
    private final MatchingEngine matchingEngine;
    private final List<TradeExecutedEvent> tradeEvents = new ArrayList<>();
    private volatile boolean running = true;
    private final EventStore eventStore;

    public EventDispatcher(
            EventQueue eventQueue,
            OrderBook orderBook,
            EventStore eventStore
    ) {
        this.eventQueue = eventQueue;
        this.matchingEngine = new MatchingEngine(orderBook);
        this.eventStore = eventStore;
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
        try {
            eventStore.append(event);
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist event", e);
        }

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

    public void replay() throws IOException {
        for (Event event : eventStore.replay()) {
            handle(event);
        }
    }

}
