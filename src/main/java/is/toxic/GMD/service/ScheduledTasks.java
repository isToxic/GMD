package is.toxic.GMD.service;

import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@EnableScheduling
@ConditionalOnProperty(value = "GMD.distribution-on", havingValue = "true")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasks {

    private final AtomicInteger value = new AtomicInteger(0);
    private final MailSendingService sendingService;
    private final GosbaseService gosbaseService;

    @Scheduled(cron = "10 * * * * *")
    public void distributeOffers() {
        GosbaseTradeResponse[] trades = gosbaseService.getTradesPage(value.get());
        while (trades.length != 0) {
            value.getAndIncrement();
            Arrays.stream(trades)
                    .forEach(trade ->
                            log.error("")
//                            sendingService.sendMail(gosbaseService.getEmail(trade),
//                                    gosbaseService.getFIO(trade),
//                                    gosbaseService.getFirmName(trade)
//                            )
                    );
            trades = gosbaseService.getTradesPage(value.get());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void dropActualPageNum() {
        log.info("reset pages num to 0");
        value.set(0);
    }
}
