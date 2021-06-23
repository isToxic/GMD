package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Purchase{
    public String notificationNumber;
    public int lotNumber;
    public String purchaseObjectInfo;
    public String maxprice;
    public String guarantee;
    public String appguarantee;
    public String publishDate;
    public String endDate;
    public String lowPrice;
    public String href;
    public int fz;
    public String mainokpd2;
    public Customer customer;
    public Warranty warranty;
    public String neuralNetworkId;
    public String deliveryTerm;
    public String deliveryDate;
    public Auktype auktype;
}
