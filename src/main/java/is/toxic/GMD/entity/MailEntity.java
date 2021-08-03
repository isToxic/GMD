package is.toxic.GMD.entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Data
@Entity
public class MailEntity {

    @Id
    @Column
    private String email;

    @Column
    private Instant addingData;

    @Column
    private boolean unsubscribe;

}
