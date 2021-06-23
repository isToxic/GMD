package is.toxic.GMD.service;

import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableScheduling
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasks {

    private final AtomicInteger value = new AtomicInteger(0);
    private final MailSendingService sendingService;
    private final GosbaseService gosbaseService;

    @Value("${GMD.mail-subject}")
    private String mailSubject;

    @Scheduled(cron = "10 * * * * *")
    public void distributeOffers() {
        GosbaseTradeResponse[] trades = gosbaseService.getTradesPage(value.get());
        while (trades.length != 0) {
            value.getAndIncrement();
            Arrays.stream(trades)
                    .forEach(trade ->
                            sendingService.sendMail(gosbaseService.getMails(trade),
                                    gosbaseService.getFIO(trade),
                                    mailSubject
                            )
                    );
            trades = gosbaseService.getTradesPage(value.get());
        }
    }


    @Scheduled(cron = "0 0 * * * *")
    public void dropActualPageNum() {
        value.set(0);
    }
}
