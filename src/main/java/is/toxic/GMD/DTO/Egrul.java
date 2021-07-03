package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Egrul{
    private String inn;
    private String kpp;
    private String shortname;
    private String fullname;
    private String fio;
    private String regdate;
    private String ogrn;
    private String address;
    private String oktmo;
    private String okato;
    private String okved;
    private String okpo;
    private String msp_date;
    private Contacts contacts;
    private Finans finans;
    private Region region;
}
