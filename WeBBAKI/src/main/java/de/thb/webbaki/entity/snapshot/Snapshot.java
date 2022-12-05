package de.thb.webbaki.entity.snapshot;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name="snapshot")
@Table
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String questionaireIDs;

    private LocalDateTime date;

    @OneToMany(mappedBy = "snapshot")
    private List<Report> reports;

    public String getDateAsString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");

        String formattedDate = date.format(formatter);
        String formattedTime = date.format(formatter2);
        return "am " + formattedDate + " um " + formattedTime + " Uhr";
    }
}
