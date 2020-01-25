package greenmoonsoftware.es.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import greenmoonsoftware.es.event.Event;
import greenmoonsoftware.es.event.jdbcstore.EventSerializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
public class JacksonJsonEventSerializer implements EventSerializer<Event> {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Override
    public InputStream serialize(Event event) throws IOException {
        return new ByteArrayInputStream(mapper.writeValueAsString(event).getBytes(UTF_8));
    }
    @Override
    public Event deserialize(String eventType, InputStream stream) throws IOException {
        try {
            byte[] targetArray = new byte[stream.available()];
            stream.read(targetArray);
            String output = new String(targetArray, UTF_8);
            return (Event)mapper.readValue(output, Class.forName(eventType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
