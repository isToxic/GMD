package is.toxic.GMD.DTO;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class Contacts{
    private List<Phone> phones;
    private List<Email> emails;

    public String getActualEmailString(){
        return emails.stream()
                .max(Comparator.comparing(Email::getActual_date))
                .orElseThrow()
                .getEmail();
    }
}
