package pl.feature.toggle.service.write;

import org.springframework.kafka.support.Acknowledgment;

public class FakeAcknowledgment implements Acknowledgment {

    private boolean ack;

    @Override
    public void acknowledge() {
        ack = true;
    }
}
