package engine.orderbook;

import engine.domain.Order;
import engine.domain.OrderSide;
import engine.domain.OrderType;
import engine.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
    }

    private Order createLimitOrder(
            long id,
            OrderSide side,
            double price,
            long quantity,
            long timestamp
    ) {
        return new Order(
                id,
                "AAPL",
                side,
                OrderType.LIMIT,
                price,
                quantity,
                quantity,
                timestamp,
                OrderStatus.NEW
        );
    }

    // TEST 1: BUY price priority
    @Test
    void bestBid_shouldBeHighestPrice() {
        Order buy100 = createLimitOrder(1, OrderSide.BUY, 100.0, 10, 1);
        Order buy101 = createLimitOrder(2, OrderSide.BUY, 101.0, 10, 2);

        orderBook.addOrder(buy100);
        orderBook.addOrder(buy101);

        Optional<Order> bestBid = orderBook.getBestBid();

        assertTrue(bestBid.isPresent());
        assertEquals(101.0, bestBid.get().getPrice());
    }

    // TEST 2: FIFO at same price
    @Test
    void buyOrdersAtSamePrice_shouldFollowFifo() {
        Order first = createLimitOrder(1, OrderSide.BUY, 100.0, 10, 1);
        Order second = createLimitOrder(2, OrderSide.BUY, 100.0, 10, 2);

        orderBook.addOrder(first);
        orderBook.addOrder(second);

        Optional<Order> bestBid = orderBook.getBestBid();

        assertTrue(bestBid.isPresent());
        assertEquals(first.getOrderId(), bestBid.get().getOrderId());
    }

    // TEST 3: SELL price priority
    @Test
    void bestAsk_shouldBeLowestPrice() {
        Order sell101 = createLimitOrder(1, OrderSide.SELL, 101.0, 10, 1);
        Order sell99 = createLimitOrder(2, OrderSide.SELL, 99.0, 10, 2);

        orderBook.addOrder(sell101);
        orderBook.addOrder(sell99);

        Optional<Order> bestAsk = orderBook.getBestAsk();

        assertTrue(bestAsk.isPresent());
        assertEquals(99.0, bestAsk.get().getPrice());
    }

    // TEST 4: Remove order & cleanup
    @Test
    void removingLastOrder_shouldRemovePriceLevel() {
        Order buy = createLimitOrder(1, OrderSide.BUY, 100.0, 10, 1);

        orderBook.addOrder(buy);
        orderBook.removeOrder(buy);

        assertTrue(orderBook.getBestBid().isEmpty());
        assertTrue(orderBook.isEmpty());
    }

    // TEST 5: Market order rejected
    @Test
    void marketOrder_shouldNotBeAddedToOrderBook() {
        Order marketOrder = new Order(
                1,
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                0.0,
                10,
                10,
                1,
                OrderStatus.NEW
        );

        assertThrows(IllegalArgumentException.class,
                () -> orderBook.addOrder(marketOrder));
    }
}
