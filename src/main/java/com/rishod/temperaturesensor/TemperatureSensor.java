package com.rishod.temperaturesensor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

@Component
public class TemperatureSensor {
    private SecureRandom random = new SecureRandom();
    private ApplicationEventPublisher eventPublisher;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public TemperatureSensor(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.executorService.schedule(this::probe, 1, TimeUnit.SECONDS);
    }

    private void probe() {
        double temperature = 16 + random.nextGaussian() * 10;
        eventPublisher.publishEvent(new Temperature(temperature));

        executorService.schedule(this::probe, random.nextInt(5000), TimeUnit.MILLISECONDS);
    }
}
