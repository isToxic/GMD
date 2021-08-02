package is.toxic.GMD.service;

import is.toxic.GMD.DTO.GosbaseTradeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasks {

    private final AtomicInteger value = new AtomicInteger(0);
    private final MailSendingService sendingService;
    private final GosbaseService gosbaseService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void dropActualPageNum() {
        log.info("reset pages num to 0");
        value.set(0);
    }

    @Scheduled(cron = "0 0/10 8-20 * * ?", zone = "Europe/Moscow")
    public void distributeOffers() {
        GosbaseTradeResponse[] trades = gosbaseService.getTradesPage(value.get());
        while (trades.length != 0) {
            value.getAndIncrement();
            log.info("Start preparing messages for sending: {}", trades.length);
            trades = filterRequired(trades);
            log.info("Messages for sending after filer: {}", trades.length);
            sendingService.sendMail(trades);
            trades = gosbaseService.getTradesPage(value.get());
        }
    }

    @NonNull
    private GosbaseTradeResponse[] filterRequired(@NonNull GosbaseTradeResponse... messages) {
        return Arrays.stream(messages)
                .filter(response -> response.getNot_required() == 0)
                .toArray(GosbaseTradeResponse[]::new);
    }
}
