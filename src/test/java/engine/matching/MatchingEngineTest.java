package engine.matching;

import engine.domain.*;
import engine.orderbook.OrderBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    private OrderBook orderBook;
    private MatchingEngine matchingEngine;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
        matchingEngine = new MatchingEngine(orderBook);
    }

    private Order limitOrder(
            long id,
            OrderSide side,
            double price,
            long qty,
            long timestamp
    ) {
        return new Order(
                id,
                "AAPL",
                side,
                OrderType.LIMIT,
                price,
                qty,
                qty,
                timestamp,
                OrderStatus.NEW
        );
    }

    // TEST 1: Full match
    @Test
    void buyAndSell_sameQuantity_shouldFullyMatch() {
        Order sell = limitOrder(1, OrderSide.SELL, 100.0, 10, 1);
        Order buy  = limitOrder(2, OrderSide.BUY,  105.0, 10, 2);

        orderBook.addOrder(sell);

        List<Trade> trades = matchingEngine.process(buy);

        assertEquals(1, trades.size());
        assertEquals(10, trades.get(0).getQuantity());
        assertEquals(100.0, trades.get(0).getPrice()); // resting order price

        assertEquals(OrderStatus.FILLED, buy.getStatus());
        assertEquals(OrderStatus.FILLED, sell.getStatus());
        assertTrue(orderBook.isEmpty());
    }

    // TEST 2: BUY partially filled
    @Test
    void buyLargerThanSell_shouldPartiallyFillBuy() {
        Order sell = limitOrder(1, OrderSide.SELL, 100.0, 10, 1);
        Order buy  = limitOrder(2, OrderSide.BUY,  105.0, 25, 2);

        orderBook.addOrder(sell);

        List<Trade> trades = matchingEngine.process(buy);

        assertEquals(1, trades.size());
        assertEquals(10, trades.get(0).getQuantity());

        assertEquals(15, buy.getRemainingQuantity());
        assertEquals(OrderStatus.PARTIALLY_FILLED, buy.getStatus());

        // remaining BUY must rest in book
        assertTrue(orderBook.getBestBid().isPresent());
        assertEquals(105.0, orderBook.getBestBid().get().getPrice());
    }

    // TEST 3: SELL partially filled
    @Test
    void sellLargerThanBuy_shouldPartiallyFillSell() {
        Order buy  = limitOrder(1, OrderSide.BUY,  101.0, 10, 1);
        Order sell = limitOrder(2, OrderSide.SELL, 99.0, 25, 2);

        orderBook.addOrder(buy);

        List<Trade> trades = matchingEngine.process(sell);

        assertEquals(1, trades.size());
        assertEquals(10, trades.get(0).getQuantity());
        assertEquals(101.0, trades.get(0).getPrice()); // resting BUY price

        assertEquals(15, sell.getRemainingQuantity());
        assertEquals(OrderStatus.PARTIALLY_FILLED, sell.getStatus());

        assertTrue(orderBook.getBestAsk().isPresent());
        assertEquals(99.0, orderBook.getBestAsk().get().getPrice());
    }

    // TEST 4: No match → order rests
    @Test
    void noCompatiblePrice_shouldRestOrder() {
        Order sell = limitOrder(1, OrderSide.SELL, 110.0, 10, 1);
        Order buy  = limitOrder(2, OrderSide.BUY,  100.0, 10, 2);

        orderBook.addOrder(sell);

        List<Trade> trades = matchingEngine.process(buy);

        assertTrue(trades.isEmpty());
        assertEquals(OrderStatus.NEW, buy.getStatus());
        assertTrue(orderBook.getBestBid().isPresent());
    }
}
