package pl.feature.toggle.service.write.infrastructure.support;

import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

public class ApplicationEventPublishedSpy implements ApplicationEventPublisher {

    private final List<Object> publishedEvents = new ArrayList<>();
    private boolean expectNoEvents = false;

    public void expectNoEvents() {
        expectNoEvents = true;
    }

    @Override
    public void publishEvent(Object event) {
        if (expectNoEvents) {
            throw new AssertionError("Event was not expected");
        }
        publishedEvents.add(event);
    }

    public Object getLastEvent() {
        return publishedEvents.getLast();
    }

    public <T> T getEvent(Class<T> eventType) {
        for (Object event : publishedEvents) {
            if (eventType.isInstance(event)) {
                return eventType.cast(event);
            }
        }
        throw new AssertionError("Event was not send");
    }

    public <T> boolean notContainsEventOfType(Class<T> eventType) {
        for (Object event : publishedEvents) {
            if (eventType.isInstance(event)) {
                return false;
            }
        }
        return true;
    }

    public <T> T getLastEvent(Class<T> eventClass) {
        var event = publishedEvents.getLast();
        return eventClass.cast(event);
    }

    public void reset() {
        publishedEvents.clear();
        expectNoEvents = false;
    }
}
