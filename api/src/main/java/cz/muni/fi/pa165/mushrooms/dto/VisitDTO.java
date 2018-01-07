package cz.muni.fi.pa165.mushrooms.dto;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bkompis, Buvko
 */
public class VisitDTO {
    @NotNull
    private Long id;
    @NotNull
    private MushroomHunterDTO hunter;
    @NotNull
    private ForestDTO forest;
    private List<MushroomDTO> mushrooms = new ArrayList<>();
    @NotNull
    private String date;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MushroomHunterDTO getHunter() {
        return hunter;
    }

    public void setHunter(MushroomHunterDTO hunter) {
        this.hunter = hunter;
    }

    public ForestDTO getForest() {
        return forest;
    }

    public void setForest(ForestDTO forest) {
        this.forest = forest;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<MushroomDTO> getMushrooms() {
        return mushrooms;
    }

    public void setMushrooms(List<MushroomDTO> mushrooms) {
        this.mushrooms = mushrooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitDTO)) return false;

        VisitDTO visitDTO = (VisitDTO) o;

        if (!getHunter().equals(visitDTO.getHunter())) return false;
        if (!getForest().equals(visitDTO.getForest())) return false;
        if (getMushrooms() != null ? !getMushrooms().equals(visitDTO.getMushrooms()) : visitDTO.getMushrooms() != null)
            return false;
        return getNote() != null ? getNote().equals(visitDTO.getNote()) : visitDTO.getNote() == null;
    }

    @Override
    public int hashCode() {
        int result = getHunter().hashCode();
        result = 31 * result + getForest().hashCode();
        result = 31 * result + (getMushrooms() != null ? getMushrooms().hashCode() : 0);
        result = 31 * result + (getNote() != null ? getNote().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VisitDTO{" +
                "hunter=" + hunter +
                ", forest=" + forest +
                ", mushrooms=" + mushrooms +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
