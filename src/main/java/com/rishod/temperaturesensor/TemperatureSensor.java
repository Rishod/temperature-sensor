package com.rishod.temperaturesensor;

import org.springframework.stereotype.Component;
import rx.Observable;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Component
public class TemperatureSensor {
    private SecureRandom random = new SecureRandom();

    private final Observable<Temperature> temperatureStream = Observable.range(0, Integer.MAX_VALUE)
            .concatMap(tick -> Observable.just(tick)
                    .delay(random.nextInt(5000), TimeUnit.MILLISECONDS)
                    .map(tickValue -> this.probe()))
            .publish()
            .refCount();

    private Temperature probe() {
        return new Temperature(16 + random.nextGaussian() * 10);
    }

    public Observable<Temperature> getTemperatureStream() {
        return temperatureStream;
    }
}
