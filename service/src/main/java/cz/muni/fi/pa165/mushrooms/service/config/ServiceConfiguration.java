package cz.muni.fi.pa165.mushrooms.service.config;

import cz.muni.fi.pa165.mushrooms.facade.MushroomHunterFacade;
import cz.muni.fi.pa165.mushrooms.service.MushroomHunterService;
import cz.muni.fi.pa165.mushrooms.validation.PersistenceSampleApplicationContext;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Dozer is a Java Bean to Java Bean mapper that recursively copies data from one object to another.
 *
 * @author bkompis
 */
@Configuration
@Import(PersistenceSampleApplicationContext.class)
@ComponentScan(basePackageClasses = {MushroomHunterService.class, MushroomHunterFacade.class})
public class ServiceConfiguration {
    @Bean
    public Mapper dozer() {
        DozerBeanMapper dozer = new DozerBeanMapper();
        return dozer;
    }

}
