package edu.pja.sii_lat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignCollectionBoxReq {
    private Integer idBox;
    private Integer idEvent;
}