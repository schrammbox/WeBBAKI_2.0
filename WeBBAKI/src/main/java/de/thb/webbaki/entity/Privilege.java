package de.thb.webbaki.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "privilege")
public class Privilege {

    public Privilege(String name){
        this.setName(name);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    @ToString.Exclude
    private Collection<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Privilege privilege = (Privilege) o;
        return id != null && Objects.equals(id, privilege.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}