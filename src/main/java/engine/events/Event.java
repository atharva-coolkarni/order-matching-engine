package engine.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderPlacedEvent.class, name = "ORDER_PLACED"),
        @JsonSubTypes.Type(value = TradeExecutedEvent.class, name = "TRADE_EXECUTED")
})
public interface Event {
    long timestamp();
}
