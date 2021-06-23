package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Customer{
    public String placerRegNum;
    public String fullName;
    public String shortName;
    public String inn;
    public String kpp;
    public Region region;
}
