package is.toxic.GMD.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Email{
    public String email;
    public LocalDate actual_date;
}