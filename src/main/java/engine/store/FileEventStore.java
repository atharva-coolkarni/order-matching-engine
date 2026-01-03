package engine.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.events.Event;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileEventStore implements EventStore {

    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileEventStore(String fileName) throws IOException {
        this.filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }

    @Override
    public synchronized void append(Event event) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.APPEND
        )) {
            writer.write(mapper.writeValueAsString(event));
            writer.newLine();
        }
    }

    @Override
    public List<Event> replay() throws IOException {
        List<Event> events = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Event event = mapper.readValue(line, Event.class);
                events.add(event);
            }
        }
        return events;
    }
}
