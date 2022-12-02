package de.thb.webbaki.entity.snapshot;

import de.thb.webbaki.entity.Scenario;
import de.thb.webbaki.entity.User;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ReportScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    float threatSituation;

    @ManyToOne
    @JoinColumn(name="report_id", nullable = false)
    private Report Report;

    @ManyToOne
    @JoinColumn(name="scenario_id", nullable = false)
    private Scenario Scenario;

}
