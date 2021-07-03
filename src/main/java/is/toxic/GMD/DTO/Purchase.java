package is.toxic.GMD.DTO;

import lombok.Data;

@Data
public class Purchase{
    private String notificationNumber;
    private int lotNumber;
    private String purchaseObjectInfo;
    private String maxprice;
    private String guarantee;
    private String appguarantee;
    private String publishDate;
    private String endDate;
    private String lowPrice;
    private String href;
    private int fz;
    private String mainokpd2;
    private Customer customer;
    private Warranty warranty;
    private String neuralNetworkId;
    private String deliveryTerm;
    private String deliveryDate;
    private Auktype auktype;
}
