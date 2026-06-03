package com.parking.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {

    LocalDateTime occurredAt();

}
