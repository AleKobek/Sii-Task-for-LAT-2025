package edu.pja.sii_lat.DTO;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicCollectionBoxDTO {
    private Integer id;
    private Integer idEvent;
    private Map<String, Double> funds;
}
