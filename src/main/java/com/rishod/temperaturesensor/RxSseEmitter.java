package com.rishod.temperaturesensor;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import rx.Subscriber;

public class RxSseEmitter extends SseEmitter {
    private static final long SSE_SESSION_TIMEOUT = 30 * 60 * 1000L;
    private final Subscriber<Temperature> subscriber;

    public RxSseEmitter() {
        super(SSE_SESSION_TIMEOUT);
        this.subscriber = new TemperatureSubscriber(this);

        onCompletion(subscriber::unsubscribe);
        onTimeout(subscriber::unsubscribe);
    }

    public Subscriber<Temperature> getSubscriber() {
        return subscriber;
    }

    public static class TemperatureSubscriber extends Subscriber<Temperature> {
        private final RxSseEmitter emitter;

        public TemperatureSubscriber(RxSseEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onNext(Temperature temperature) {
            try {
                emitter.send(temperature, MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                unsubscribe();
            }
        }
    }
}
