package kt.bpm.models.meteo;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Meteo {

    private Title title;
    private Condition condition;

}
