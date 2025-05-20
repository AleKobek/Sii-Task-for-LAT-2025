package edu.pja.sii_lat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListCollectionBoxResponse {
    private Integer id;
    private boolean isAssigned;
    private boolean isEmpty;
}
