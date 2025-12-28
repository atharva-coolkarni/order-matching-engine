package engine.matching;

import engine.domain.*;
import engine.orderbook.OrderBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MatchingEngine {

    private final OrderBook orderBook;

    public MatchingEngine(OrderBook orderBook) {
        this.orderBook = Objects.requireNonNull(orderBook);
    }

    /**
     * Process an incoming order and return all trades generated.
     */
    public List<Trade> process(Order incoming) {
        Objects.requireNonNull(incoming, "incoming order must not be null");

        List<Trade> trades = new ArrayList<>();

        if (incoming.getSide() == OrderSide.BUY) {
            matchBuy(incoming, trades);
        } else {
            matchSell(incoming, trades);
        }

        // If order not fully filled, rest it in the book
        if (incoming.getRemainingQuantity() > 0 &&
                incoming.getType() == OrderType.LIMIT) {

            orderBook.addOrder(incoming);
        }

        return trades;
    }

    // BUY MATCHING

    private void matchBuy(Order buy, List<Trade> trades) {

        while (buy.getRemainingQuantity() > 0) {

            Optional<Order> bestAskOpt = orderBook.getBestAsk();
            if (bestAskOpt.isEmpty()) {
                break;
            }

            Order sell = bestAskOpt.get();

            // Price compatibility check
            if (sell.getPrice() > buy.getPrice()) {
                break;
            }

            long tradeQty = Math.min(
                    buy.getRemainingQuantity(),
                    sell.getRemainingQuantity()
            );

            // Trade executes at RESTING order price (SELL)
            Trade trade = new Trade(
                    System.nanoTime(),           // tradeId (simple for now)
                    buy.getOrderId(),
                    sell.getOrderId(),
                    buy.getSymbol(),
                    sell.getPrice(),
                    tradeQty,
                    System.currentTimeMillis()
            );
            trades.add(trade);

            // Update quantities
            buy.setRemainingQuantity(buy.getRemainingQuantity() - tradeQty);
            sell.setRemainingQuantity(sell.getRemainingQuantity() - tradeQty);

            // Update SELL order state
            if (sell.getRemainingQuantity() == 0) {
                sell.setStatus(OrderStatus.FILLED);
                orderBook.removeOrder(sell);
            } else {
                sell.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
        }

        // Update BUY order state
        if (buy.getRemainingQuantity() == 0) {
            buy.setStatus(OrderStatus.FILLED);
        } else if (buy.getRemainingQuantity() < buy.getQuantity()) {
            buy.setStatus(OrderStatus.PARTIALLY_FILLED);
        }
    }

    // SELL MATCHING

    private void matchSell(Order sell, List<Trade> trades) {

        while (sell.getRemainingQuantity() > 0) {

            Optional<Order> bestBidOpt = orderBook.getBestBid();
            if (bestBidOpt.isEmpty()) {
                break;
            }

            Order buy = bestBidOpt.get();

            // Price compatibility check
            if (buy.getPrice() < sell.getPrice()) {
                break;
            }

            long tradeQty = Math.min(
                    sell.getRemainingQuantity(),
                    buy.getRemainingQuantity()
            );

            // Trade executes at RESTING order price (BUY)
            Trade trade = new Trade(
                    System.nanoTime(),
                    buy.getOrderId(),
                    sell.getOrderId(),
                    sell.getSymbol(),
                    buy.getPrice(),
                    tradeQty,
                    System.currentTimeMillis()
            );
            trades.add(trade);

            // Update quantities
            sell.setRemainingQuantity(sell.getRemainingQuantity() - tradeQty);
            buy.setRemainingQuantity(buy.getRemainingQuantity() - tradeQty);

            // Update BUY order state
            if (buy.getRemainingQuantity() == 0) {
                buy.setStatus(OrderStatus.FILLED);
                orderBook.removeOrder(buy);
            } else {
                buy.setStatus(OrderStatus.PARTIALLY_FILLED);
            }
        }

        // Update SELL order state
        if (sell.getRemainingQuantity() == 0) {
            sell.setStatus(OrderStatus.FILLED);
        } else if (sell.getRemainingQuantity() < sell.getQuantity()) {
            sell.setStatus(OrderStatus.PARTIALLY_FILLED);
        }
    }
}
