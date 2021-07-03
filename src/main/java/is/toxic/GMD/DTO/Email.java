package is.toxic.GMD.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Email{
    private String email;
    private LocalDate actual_date;
}