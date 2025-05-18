package edu.pja.sii_lat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Name is required!!")
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank(message = "Currency code is required!")
    @Size(min = 2, max = 10)
    private String fundsCurrencyCode;

    @Min(0)
    private double funds;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CollectionBox> collectionBoxSet = new HashSet<>();
}
