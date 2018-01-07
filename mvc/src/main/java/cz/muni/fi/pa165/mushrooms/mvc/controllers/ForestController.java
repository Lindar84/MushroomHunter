package cz.muni.fi.pa165.mushrooms.mvc.controllers;

import cz.muni.fi.pa165.mushrooms.dto.AddEditForestDTO;
import cz.muni.fi.pa165.mushrooms.dto.ForestDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomDTO;
import cz.muni.fi.pa165.mushrooms.dto.VisitDTO;
import cz.muni.fi.pa165.mushrooms.entity.Mushroom;
import cz.muni.fi.pa165.mushrooms.facade.ForestFacade;
import cz.muni.fi.pa165.mushrooms.facade.MushroomFacade;
import cz.muni.fi.pa165.mushrooms.facade.VisitFacade;
import cz.muni.fi.pa165.mushrooms.mvc.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/forests")
public class ForestController {

    private final static Logger log = LoggerFactory.getLogger(cz.muni.fi.pa165.mushrooms.mvc.controllers.ForestController.class);

    @Inject
    private ForestFacade forestFacade;

    @Inject
    private MushroomFacade mushroomFacade;

    @Inject
    private VisitFacade visitFacade;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        log.debug("[FOREST] List all");
        model.addAttribute("forests", forestFacade.findAllForests());
        return "forests/list";
    }

    @RequestMapping(value = "/read/{id}", method = RequestMethod.GET)
    public String read(@PathVariable long id, Model model) {
        log.debug("[FOREST] Read ({})", id);

        ForestDTO forest = forestFacade.findById(id);

        model.addAttribute("forests", forestFacade.findById(id));
        model.addAttribute("visits", visitFacade.listAllVisitsForForest(forest));
        return "forests/read";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editForest(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        log.debug("[Forest] Edit {}", id);
        ForestDTO forestDTO = forestFacade.findById(id);

        model.addAttribute("forestEdit", forestDTO);
        return "/forests/edit";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable long id, Model model, HttpServletRequest request,
                         UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {

        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        ForestDTO forest = forestFacade.findById(id);
        forestFacade.deleteForest(id);
        log.debug("delete forest({})", id);
        redirectAttributes.addFlashAttribute("alert_success", "Forest \"" + forest.getName() + "\" was deleted.");
        return "redirect:" + uriBuilder.path("/forests").build().toUriString();
    }


    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String update(@PathVariable long id,
                         @Valid @ModelAttribute("forestEdit") AddEditForestDTO formBean,
                         BindingResult bindingResult,
                         Model model,
                         HttpServletRequest request,
                         UriComponentsBuilder uriBuilder,
                         RedirectAttributes redirectAttributes) {

        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        log.debug("Forest - update");
        if (bindingResult.hasErrors()) {
            log.debug("Forest - has errors:");

            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }
            model.addAttribute("forestEdit", formBean);
            return "forests/edit";
        }

        log.debug("[FOREST] Update: {}", formBean);
        ForestDTO result = forestFacade.updateForest(formBean);

        redirectAttributes.addFlashAttribute("alert_success", "Forest " + result.getName() + " was updated");
        return "redirect:" + uriBuilder.path("/forests/read/{id}").buildAndExpand(id).encode().toUriString();
    }


    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("forestCreate") AddEditForestDTO formBean,HttpServletRequest request,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
                         UriComponentsBuilder uriBuilder) {

        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        log.info("create(forestCreate={})", formBean);
        //in case of validation error forward back to the the form
        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.trace("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.trace("FieldError: {}", fe);
            }
            return "forests/register";
        }

        if(forestFacade.findByName(formBean.getName()) != null){
            redirectAttributes.addFlashAttribute("alert_warning", "Forest " + formBean.getName() + " already exists!");
            return "redirect:" + uriBuilder.path("/forests").build().toUriString();
        }

        //create forest
        ForestDTO createdDTO = forestFacade.createForest(formBean);
        //report success
        redirectAttributes.addFlashAttribute("alert_success", "Forest " + createdDTO.getName() + " was created");
        return "redirect:" + uriBuilder.path("/forests/read/{id}").buildAndExpand(createdDTO.getId()).encode().toUriString();
    }


    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public String findTopForest(Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        log.info("[FOREST] Find best - get");

        model.addAttribute("mushrooms", mushroomFacade.findAllMushrooms());
        model.addAttribute("forests", new HashMap<ForestDTO, Integer>());

        return "forests/find";
    }

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public String findTopForest_post(@RequestParam(required=false) Long mushroomId, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        log.info("[FOREST] Find best - post");
        log.info("mushroom id={}", mushroomId);

        MushroomDTO mushroomById = mushroomFacade.findMushroomById(mushroomId);

        List<ForestDTO> forests = forestFacade.listAllForestsWithMushroom(mushroomById);

        Map<ForestDTO, Integer> forestsWithNumbers = getForestsNumbers(forests, mushroomById);

        model.addAttribute("mushrooms", mushroomFacade.findAllMushrooms());
        model.addAttribute("forests", forestsWithNumbers);

        return "forests/find";
    }

    private Map<ForestDTO, Integer> getForestsNumbers(List<ForestDTO> forests, MushroomDTO mushroom){
        Map<ForestDTO, Integer> res = new HashMap<>();
        for (ForestDTO forest : forests){
            res.put(forest, getForestsNumFinds(forest, mushroom));
        }
        return res;
    }

    private Integer getForestsNumFinds(ForestDTO forest, MushroomDTO mushroom)
    {
        int result = 0;
        for (VisitDTO v : visitFacade.listAllVisitsForForest(forest)){
            if (v.getMushrooms().contains(mushroom)) ++result;
        }
        return result;
    }


        /**
         * Prepares an empty form.
         *
         * @param model data to be displayed
         * @return JSP page
         */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForest(Model model,HttpServletRequest request, RedirectAttributes redirectAttributes,
                            UriComponentsBuilder uriBuilder) {
        log.debug("new()");
        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        model.addAttribute("forestCreate", new ForestDTO());
        return "forests/register";
    }


}

