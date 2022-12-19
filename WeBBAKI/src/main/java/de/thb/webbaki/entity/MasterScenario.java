package de.thb.webbaki.entity;


import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name="master_scenario")
@Table
public class MasterScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private short layer;
    boolean active;

    @OneToMany(mappedBy = "masterScenario")
    private List<Scenario> scenarios;

    public String getLayerColorAsString(){
        switch (layer){
            case 5:
                return "#9fd8ee";
            case 4:
                return "#b5d48c";
            case 3:
                return "#f6e37b";
            case 2:
                return "#ffcf93";
            default:
                return "#fe80a4";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterScenario that = (MasterScenario) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
