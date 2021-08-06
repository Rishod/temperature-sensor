package com.rishod.temperaturesensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class TemperatureController {
    private final TemperatureSensor temperatureSensor;

    public TemperatureController(TemperatureSensor temperatureSensor) {
        this.temperatureSensor = temperatureSensor;
    }

    @GetMapping("/temperature-stream")
    public SseEmitter events(final HttpServletRequest httpServletRequest) {
        final RxSseEmitter emitter = new RxSseEmitter();

        temperatureSensor.getTemperatureStream().subscribe(emitter.getSubscriber());

        return emitter;
    }
}
