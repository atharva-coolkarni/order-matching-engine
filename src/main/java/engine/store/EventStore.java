package engine.store;

import engine.events.Event;

import java.io.IOException;
import java.util.List;

public interface EventStore {

    void append(Event event) throws IOException;

    List<Event> replay() throws IOException;
}
