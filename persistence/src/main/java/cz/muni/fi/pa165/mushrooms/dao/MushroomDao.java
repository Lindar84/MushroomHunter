package cz.muni.fi.pa165.mushrooms.dao;

import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;

import java.util.List;

/**
 * @author BohdanCvejn
 */

public interface MushroomDao {

    /**
     * Takes unique id of Mushroom and returns a corresponding entity if found
     *
     * @param id of Mushroom object, non-null
     * @return Mushroom entity if found, null otherwise
     */
    Mushroom findById(Long id);

    /**
     * Takes mushroom type and returns a corresponding List of found entities
     *
     * @param mushroomType non-null
     * @return List of Mushroom, empty List if no Mushroom is found
     */
    List<Mushroom> findByMushroomType(MushroomType mushroomType);

    /**
     * Takes an object of type Mushroom and creates an entry in a database
     *
     * @param mushroom non-null object to be created in a database
     */
    void create(Mushroom mushroom);

    /**
     * Takes an object of type Mushroom and updates an entry in a database
     *
     * @param mushroom non-null object to be updated in a database
     */
    void update(Mushroom mushroom);

    /**
     * Takes a mushroom object that shall be deleted from database
     *
     * @param mushroom non-null object to be deleted from the database
     */
    void delete(Mushroom mushroom);

    /**
     * Returns all Mushroom in the database
     *
     * @return List of all Mushroom, empty List if no Mushroom is found
     */
    List<Mushroom> findAll();

    /**
     * Takes unique name of Mushroom and returns a corresponding entity if found
     *
     * @param name non-null string representing name
     * @return mushroom with given name if exists, null otherwise
     */
    Mushroom findByName(String name);

    /**
     * Takes interval of occurrence and returns all Mushrooms within given range
     *
     * @param fromMonth name of the month, non-null
     * @param toMonth   name of the month, non-null
     * @return List of Mushroom within given range, empty List if no Mushroom is found
     */
    List<Mushroom> findByIntervalOfOccurrence(String fromMonth, String toMonth);
}
