package is.toxic.GMD.repository;

import is.toxic.GMD.entity.MailEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MailsRepository extends CrudRepository<MailEntity, String> {

    boolean existsEmailsByEmailAndUnsubscribe(String email, boolean unsubscribe);

    boolean existsEmailsByEmailAndSendYet(String email, boolean sendYet);

    long countBySendYetAndUnsubscribe(boolean sendYet, boolean unsubscribe);

    List<MailEntity> findBySendYetAndUnsubscribeAndAddingDataBetween(boolean sendYet, boolean unsubscribe, Instant from, Instant to);

    List<MailEntity> findTop50ByUnsubscribeAndSendYetAndSubjectNotNullAndMessageNotNullOrderByAddingData(boolean unsubscribe, boolean sendYet);
}