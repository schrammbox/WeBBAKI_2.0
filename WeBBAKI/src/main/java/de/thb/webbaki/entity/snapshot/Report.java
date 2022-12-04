package de.thb.webbaki.entity.snapshot;

import de.thb.webbaki.entity.Branch;
import de.thb.webbaki.entity.Sector;
import de.thb.webbaki.entity.User;
import de.thb.webbaki.entity.snapshot.Snapshot;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //Number of questionnaires used for the calculation
    int numberOfQuestionnaires;

    @ManyToOne
    @JoinColumn(name="snapshot_id", nullable=false)
    private Snapshot snapshot;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name="sector_id")
    private Sector sector;

    @OneToMany(mappedBy = "report")
    private List<ReportScenario> reportScenarios;
}
