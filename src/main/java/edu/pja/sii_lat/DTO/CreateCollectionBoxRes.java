package edu.pja.sii_lat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionBoxRes {
    private Integer id;
    private Map<String, Double> funds;
}
