package is.toxic.GMD.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@DynamicUpdate
@RequiredArgsConstructor
@Table(indexes = {@Index(columnList = "unsubscribe"), @Index(columnList = "sendYet")})
public class MailEntity {

    @Id
    @Column(unique = true)
    private String email;

    @Column
    private String fio;

    @Column
    private String organisation;

    @Column(length = 86000)
    private String message;


    @Column(length = 400)
    private String subject;

    @Column
    private Instant addingData;

    @Column
    private boolean unsubscribe;

    @Column
    private boolean sendYet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MailEntity that = (MailEntity) o;

        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return 1652704802;
    }
}
