package cz.muni.fi.pa165.mushrooms.entity;

import cz.muni.fi.pa165.mushrooms.enums.MushroomType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Lindar84
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Mushroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(nullable = false)
    private MushroomType type;

    /*
     * Interval of occurrence of the mushroom in format Month - Month
     */
    @NotNull
    @Column(nullable = false)
    private String intervalOfOccurrence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MushroomType getType() {
        return type;
    }

    public void setType(MushroomType type) {
        this.type = type;
    }

    public String getIntervalOfOccurrence() {
        return intervalOfOccurrence;
    }

    public void setIntervalOfOccurrence(String startMonth, String endMonth) {
        this.intervalOfOccurrence = startMonth + " - " + endMonth;
    }

    @Override
    public String toString() {
        return "Mushroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", intervalOfOccurrence='" + intervalOfOccurrence + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Mushroom)) return false;
        Mushroom mushroom = (Mushroom) o;
        return Objects.equals(getName(), mushroom.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
