package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class GosbaseTradeResponse {
    public int contract_idx;
    public String name;
    public String placer_name;
    public String link;
    public String price;
    public String guaranteeing;
    public String protokol_date;
    public String winner_name;
    public String winner_inn;
    public Object winner_kpp;
    public String notificationNumber;
    public int lotNumber;
    public String last_update;
    public int fz;
    public int transaction;
    public Region region;
    public Egrul egrul;
    public Purchase purchase;
    public ContractTerm contractTerm;
    public StatInfo statinfo;
    public int not_required;
    public Warranty warranty;
}
