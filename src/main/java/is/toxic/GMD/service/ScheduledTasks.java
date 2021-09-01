package is.toxic.GMD.service;

import is.toxic.GMD.DTO.GosbaseTradeResponse;
import is.toxic.GMD.entity.MailEntity;
import is.toxic.GMD.repository.MailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduledTasks {

    private final AtomicInteger value = new AtomicInteger(0);
    private final MailSendingService sendingService;
    private final MessageService messageService;
    private final GosbaseService gosbaseService;
    private final MailsRepository repository;

    @Value("${GMD.storage-time}")
    private int storageTime;
    @Value("${GMD.storage-unit}")
    private String unit;

    @Async
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void dropActualPageNum() {
        log.info("reset pages num to 0");
        value.set(0);
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void clearSended() {
        log.info("clear emails older {} {}", storageTime, unit);
        Instant from = Instant.now().minus(storageTime, ChronoUnit.valueOf(unit.toUpperCase(Locale.ROOT)));
        Instant to = Instant.now();
        List<String> mailsForDelete =
                repository.findBySendYetAndUnsubscribeAndAddingDataBetween(true, false, from, to)
                        .stream()
                        .filter(mailEntity -> !mailEntity.isUnsubscribe())
                        .map(MailEntity::getEmail)
                        .collect(Collectors.toList());
        repository.deleteAllById(mailsForDelete);
    }

    @Async
    @Scheduled(cron = "0 0/10 8-20 * * ?", zone = "Europe/Moscow")
    public void prepareOffers() {
        GosbaseTradeResponse[] trades = gosbaseService.getTradesPage(value.get());
        while (trades.length != 0) {
            value.getAndIncrement();
            log.info("Start preparing messages for sending: {}", trades.length);
            GosbaseTradeResponse[] filteredTrades = filterRequired(trades);
            log.info("Messages for sending after filer: {}", filteredTrades.length);
            messageService.prepareMessagesForSend(filteredTrades);
            trades = gosbaseService.getTradesPage(value.get());
            if (Arrays.equals(filteredTrades, filterRequired(trades))) {
                break;
            }
        }
    }

    @Async
    @Scheduled(cron = "0 0/5 7-21 * * ?", zone = "Europe/Moscow")
    public void distributeOffers() {
        log.info("Start distribution task");
        sendingService.sendOffers();
    }

    @NonNull
    private GosbaseTradeResponse[] filterRequired(@NonNull GosbaseTradeResponse... messages) {
        return Arrays.stream(messages)
                .filter(response -> response.getNot_required() == 0)
                .toArray(GosbaseTradeResponse[]::new);
    }
}