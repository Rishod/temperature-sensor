package com.rishod.temperaturesensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class TemperatureController {
    private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    @GetMapping("/temperature-stream")
    public SseEmitter events(final HttpServletRequest httpServletRequest) {
        final SseEmitter sseEmitter = new SseEmitter();

        emitters.add(sseEmitter);

        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        sseEmitter.onTimeout(() -> emitters.remove(sseEmitter));

        return sseEmitter;
    }

    @Async
    @EventListener
    public void handleTemperatureEvent(final Temperature temperature) {
        final Set<SseEmitter> deadEmitters = new HashSet<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                log.error("Error ", e);
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }

}
