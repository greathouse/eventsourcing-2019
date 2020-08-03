package greenmoonsoftware.es.store;

public interface StorePurger {
    void purge(String aggregateId);
}
