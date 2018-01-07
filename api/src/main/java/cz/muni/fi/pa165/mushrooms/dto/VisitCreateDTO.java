package cz.muni.fi.pa165.mushrooms.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @author Buvko
 */
public class VisitCreateDTO {
    @NotNull
    private MushroomHunterDTO hunter;
    @NotNull
    private ForestDTO forest;
    @NotNull
    private String date;
    private String note;
    private List<MushroomDTO> mushrooms = new ArrayList<>();

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "VisitCreateDTO{" +
                "hunter=" + hunter +
                ", forest=" + forest +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", mushrooms=" + mushrooms +
                '}';
    }
}
