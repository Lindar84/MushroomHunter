package cz.muni.fi.pa165.mushrooms.mvc.controllers;


import cz.muni.fi.pa165.mushrooms.dto.MushroomDTO;
import cz.muni.fi.pa165.mushrooms.enums.MushroomType;
import cz.muni.fi.pa165.mushrooms.facade.MushroomFacade;
import cz.muni.fi.pa165.mushrooms.mvc.Tools;
import cz.muni.fi.pa165.mushrooms.mvc.forms.MushroomDTOValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;

/**
 * @author bencikpeter
 */

@Controller
@RequestMapping("/mushrooms")
public class MushroomController {

    final static Logger log = LoggerFactory.getLogger(MushroomController.class);

    @Autowired
    private MushroomFacade facade;

    /**
     * Shows a list of products with the ability to add, delete or edit.
     *
     * @param model data to display
     * @return JSP page name
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("mushrooms", facade.findAllMushrooms());
        return "mushrooms/list";
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable long id, Model model, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        facade.deleteMushroom(id);
        log.debug("delete({})", id);
        redirectAttributes.addFlashAttribute("alert_success", "Mushroom with id: \"" + id + "\" was deleted.");
        return "redirect:" + uriBuilder.path("/mushrooms").build().toUriString();
    }

    @RequestMapping(value = "/read/{id}", method = RequestMethod.GET)
    public String view(@PathVariable long id, Model model) {
        log.debug("view({})", id);
        model.addAttribute("mushroom", facade.findMushroomById(id));
        return "mushrooms/read";
    }

    /**
     * Prepares an empty form.
     *
     * @param model data to be displayed
     * @return JSP page
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newMushroom(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder) {
        log.debug("new()");
        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;

        model.addAttribute("mushroomCreate", new MushroomDTO());
        return "mushrooms/register";
    }


    @ModelAttribute("mushroomType")
    public MushroomType[] colors() {
        log.debug("mushroomType()");
        return MushroomType.values();
    }

    @ModelAttribute("months")
    public String[] months() {
        log.debug("mushroomType()");
        String[] months = {"january", "february", "march", "april",
                            "may", "june", "july", "september", "october",
                            "november", "decemer"};
        return months;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() instanceof MushroomDTO) {
            binder.addValidators(new MushroomDTOValidator());
        }
    }


    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("mushroomCreate") MushroomDTO formBean, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder) {
        log.debug("create(mushroomCreate={})", formBean);
        //in case of validation error forward back to the the form
        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.trace("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                System.out.println(fe.getField() + "_error");
                log.trace("FieldError: {}", fe);
            }
            return "mushrooms/register";
        }

        if(facade.findMushroomByName(formBean.getName()) != null){
            redirectAttributes.addFlashAttribute("alert_warning", "Mushroom " + formBean.getName() + " already exists!");
            return "redirect:" + uriBuilder.path("/mushrooms").build().toUriString();
        }

        //create mushroom
        MushroomDTO createdDTO = facade.createMushroom(formBean);
        //report success
        redirectAttributes.addFlashAttribute("alert_success", "Mushroom " + createdDTO.getId() + " was created");
        return "redirect:" + uriBuilder.path("/mushrooms/read/{id}").buildAndExpand(createdDTO.getId()).encode().toUriString();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editUser(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {

        log.debug("[Mushroom] Edit {}", id);
        MushroomDTO mushroomDTO = facade.findMushroomById(id);

        model.addAttribute("mushroomEdit", mushroomDTO);
        return "/mushrooms/edit";
    }

    @RequestMapping(value="/edit/{id}", method = RequestMethod.POST)
    public String update(@PathVariable long id,
                         @Valid @ModelAttribute("mushroomEdit")MushroomDTO formBean,
                         BindingResult bindingResult,
                         Model model,
                         UriComponentsBuilder uriBuilder,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {

        log.debug("Mushroom - update");

        if (bindingResult.hasErrors()) {
            log.debug("Mushroom - has errors:");

            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }
            model.addAttribute("mushroomEdit", formBean);
            return "mushrooms/edit";
        }

        log.debug("[Mushroom] Update: {}", formBean);
        MushroomDTO result = facade.updateMushroom(formBean);

        redirectAttributes.addFlashAttribute("alert_success", "Mushroom " + result.getName() + " was updated");
        return "redirect:" + uriBuilder.path("/mushrooms/read/{id}").buildAndExpand(id).encode().toUriString();
    }
}


