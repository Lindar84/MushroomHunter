package cz.muni.fi.pa165.mushrooms.mvc.controllers;


import cz.muni.fi.pa165.mushrooms.dto.MushroomDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterDTO;
import cz.muni.fi.pa165.mushrooms.dto.VisitCreateDTO;
import cz.muni.fi.pa165.mushrooms.dto.VisitDTO;
import cz.muni.fi.pa165.mushrooms.facade.ForestFacade;
import cz.muni.fi.pa165.mushrooms.facade.MushroomFacade;
import cz.muni.fi.pa165.mushrooms.facade.MushroomHunterFacade;
import cz.muni.fi.pa165.mushrooms.facade.VisitFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


/**
 * Controller class for visits
 * 
 * @author Buvko
 */

@Controller
@RequestMapping("/visits")
public class VisitController {

    private final static Logger log = LoggerFactory.getLogger(VisitController.class);

    @Inject
    private ConversionService conversionService;

    @Inject
    private VisitFacade visitFacade;

    @Inject
    private MushroomHunterFacade hunterFacade;

    @Inject
    private MushroomFacade mushroomFacade;

    @Inject
    private ForestFacade forestFacade;


    @RequestMapping(value="", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        log.debug("[VISIT] List all");
        model.addAttribute("visits", visitFacade.listAllVisits());
        return "visits/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String registerVisit(Model model, HttpServletRequest request) {

        MushroomHunterDTO hunter = (MushroomHunterDTO) request.getSession().getAttribute("user");

        log.debug("[VISIT] Register new Visit");
        model.addAttribute("hunter", hunter.getId());
        model.addAttribute("registerVisit", new VisitCreateDTO());
        model.addAttribute("mushrooms", mushroomFacade.findAllMushrooms());
        model.addAttribute("forests", forestFacade.findAllForests());
        return "visits/create";
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("registerVisit") VisitCreateDTO formBean,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           UriComponentsBuilder uriBuilder,
                           HttpServletRequest request) {


        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }

            model.addAttribute("registerVisit", formBean);

            log.error("creating visit" + formBean.getForest() + formBean.getMushrooms(), formBean);
            return "/visits/create";
        }


        VisitDTO visit = visitFacade.createVisit(formBean);

        redirectAttributes.addFlashAttribute("alert_success", "Register of new visit was successful");
        return "redirect:" + uriBuilder.path("/visits/read/{id}").buildAndExpand(visit.getId()).encode().toUriString();
    }


    @RequestMapping(value = "/read/{id}", method = RequestMethod.GET)
    public String read(@PathVariable long id, Model model, UriComponentsBuilder uriBuilder, HttpServletRequest request) {
        log.debug("[VISIT] Read ({})", id);

        model.addAttribute("visit", visitFacade.findById(id));
        return "visits/read";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {

        VisitDTO visit = visitFacade.findById(id);
        visitFacade.deleteVisit(id);
        log.debug("delete visit({})", id);
        redirectAttributes.addFlashAttribute("alert_success", "Visit was deleted.");
        return "redirect:" + uriBuilder.path("/visits").build().toUriString();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editVisit(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {

        log.debug("[VISIT] Edit {}", id);

        MushroomHunterDTO hunter = (MushroomHunterDTO) request.getSession().getAttribute("user");
        VisitDTO visitDTO = visitFacade.findById(id);

        model.addAttribute("visitEdit", visitDTO);
        model.addAttribute("hunter", hunter.getId());
        model.addAttribute("mushrooms", mushroomFacade.findAllMushrooms());
        model.addAttribute("forests", forestFacade.findAllForests());
        return "/visits/edit";
    }

    @RequestMapping(value="/edit/{id}", method = RequestMethod.POST)
    public String update(@PathVariable long id,
                         @Valid @ModelAttribute("visitEdit")VisitDTO formBean,
                         BindingResult bindingResult,
                         Model model,
                         UriComponentsBuilder uriBuilder,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {

        formBean.setId(id);

        log.debug("Visit - update");

        if (bindingResult.hasErrors()) {
            log.debug("Visit - has errors:");

            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }
            model.addAttribute("visitEdit", formBean);
            return "visits/edit";
        }

        log.debug("[VISIT] Update: {}", formBean);
        visitFacade.updateVisit(formBean);

        redirectAttributes.addFlashAttribute("alert_success", "Visit was updated");
        return "redirect:" + uriBuilder.path("/visits/read/{id}").buildAndExpand(id).encode().toUriString();
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, "mushrooms"
                , new CustomCollectionEditor(List.class) {
                    protected Object convertElement(Object element) {
                        if (element instanceof String) {
                            MushroomDTO mushroomDTO = mushroomFacade.findMushroomById(Long.decode((String) element));
                            System.out.println("Looking up mushroom for id" + element + ":" + mushroomDTO);
                            return mushroomDTO;
                        }
                        System.err.println("Incompatible object: " + element);
                        return null;
                    }
                });
    }
}