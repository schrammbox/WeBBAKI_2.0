package de.thb.webbaki.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "sector")
@Getter
@Setter
@NoArgsConstructor
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @OneToMany(mappedBy = "sector")
    private List<Branch> branches;

    public Sector(String name, List<Branch> branches) {
        this.name = name;
        this.branches = branches;
    }

    @Override
    public String toString(){
        return name;
    }
}
