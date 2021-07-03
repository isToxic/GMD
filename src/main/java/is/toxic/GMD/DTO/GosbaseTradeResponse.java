package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class GosbaseTradeResponse {
    private int contract_idx;
    private String name;
    private String placer_name;
    private String link;
    private String price;
    private String guaranteeing;
    private String protokol_date;
    private String winner_name;
    private String winner_inn;
    private Object winner_kpp;
    private String notificationNumber;
    private int lotNumber;
    private String last_update;
    private int fz;
    private int transaction;
    private Region region;
    private Egrul egrul;
    private Purchase purchase;
    private ContractTerm contractTerm;
    private StatInfo statinfo;
    private int not_required;
    private Warranty warranty;
}
