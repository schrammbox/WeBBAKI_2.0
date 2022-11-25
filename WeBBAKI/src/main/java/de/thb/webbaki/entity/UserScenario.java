package de.thb.webbaki.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float probability;

    private float impact;

    private float threatSituation;

    @Column(length = 1000)
    @Size(max = 10000)
    private String smallComment;

    @ManyToOne
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name="questionnaire_id", nullable=false)
    private Questionnaire questionnaire;
}
