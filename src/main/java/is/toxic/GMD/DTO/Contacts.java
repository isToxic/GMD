package is.toxic.GMD.DTO;

import lombok.Data;

import java.util.List;

@Data
public class Contacts{
    public List<Phone> phones;
    public List<Email> emails;
}
