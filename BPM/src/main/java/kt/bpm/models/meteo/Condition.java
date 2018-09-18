package kt.bpm.models.meteo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Condition {
    private String code;
    private String date;
    private String temp;
    private String text;

}
