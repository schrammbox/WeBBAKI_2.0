package de.thb.webbaki.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity(name = "questionnaire")
@Table
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(length = 1000)
    @Size(max = 1000)
    private String comment;

    @Column(length = 1000)
    @Size(max = 10000)
    private String smallComment;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(length = 1000)
    @Size(max = 1000)
    private String mapping;



    //EQUALS & HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Questionnaire that = (Questionnaire) o;
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
