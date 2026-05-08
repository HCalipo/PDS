package com.tasku.core.application.port;

public interface EventPublisher {
    void publish(Object event);
}
