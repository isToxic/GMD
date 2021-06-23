package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Egrul{
    public String inn;
    public String kpp;
    public String shortname;
    public String fullname;
    public String fio;
    public String regdate;
    public String ogrn;
    public String address;
    public String oktmo;
    public String okato;
    public String okved;
    public String okpo;
    public String msp_date;
    public Contacts contacts;
    public Finans finans;
    public Region region;
}
