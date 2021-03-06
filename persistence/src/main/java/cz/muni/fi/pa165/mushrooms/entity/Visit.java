package cz.muni.fi.pa165.mushrooms.entity;

import cz.muni.fi.pa165.mushrooms.utils.LocalDateAttributeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author bkompis
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"visit_hunter", "visit_forest", "date"})})
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "visit_hunter", nullable = false)
    private MushroomHunter hunter;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "visit_forest", nullable = false)
    private Forest forest;

    @NotNull
    @Column(nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    //@Temporal(TemporalType.DATE) should not be used
    // solution from https://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
    private LocalDate date;

    @Column
    private String note;

    // getters
    public Long getId() {
        return id;
    }

    public MushroomHunter getHunter() {
        return hunter;
    }

    public Forest getForest() {
        return forest;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    //setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setHunter(MushroomHunter hunter) {
        this.hunter = hunter;
    }

    public void setForest(Forest forest) {
        this.forest = forest;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Visit)) {
            return false;
        }
        Visit visit = (Visit) o;
        return Objects.equals(getHunter(), visit.getHunter()) &&
                Objects.equals(getForest(), visit.getForest()) &&
                Objects.equals(getDate(), visit.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHunter(), getForest(), getDate());
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", hunter=" + hunter +
                ", forest=" + forest +
                ", date=" + date +
                ", note='" + note + '\'' +
                '}';
    }
}
