package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Customer{
    private String placerRegNum;
    private String fullName;
    private String shortName;
    private String inn;
    private String kpp;
    private Region region;
}
