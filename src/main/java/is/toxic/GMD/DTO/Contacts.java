package is.toxic.GMD.DTO;

import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Data
public class Contacts{
    public List<Phone> phones;
    public List<Email> emails;

    public String getActualEmailString(){
        return emails.stream()
                .max(Comparator.comparing(Email::getActual_date))
                .orElseThrow()
                .getEmail();
    }
}
