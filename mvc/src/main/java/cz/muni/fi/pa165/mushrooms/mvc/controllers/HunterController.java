package cz.muni.fi.pa165.mushrooms.mvc.controllers;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterUpdateDTO;
import cz.muni.fi.pa165.mushrooms.facade.MushroomHunterFacade;
import cz.muni.fi.pa165.mushrooms.facade.VisitFacade;
import cz.muni.fi.pa165.mushrooms.mvc.Tools;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
@RequestMapping("/hunters")
public class HunterController {

    private final static Logger log = LoggerFactory.getLogger(HunterController.class);

    @Inject
    private MushroomHunterFacade hunterFacade;

    @Inject
    private VisitFacade visitFacade;


    @RequestMapping(value="", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        log.debug("[HUNTER] List all");
        String res = Tools.redirectNonAdmin(request, uriBuilder, redirectAttributes);
        if(res != null) return res;
        model.addAttribute("hunters", hunterFacade.findAllHunters());
        return "hunters/list";
    }

    @RequestMapping(value = "/read/{id}", method = RequestMethod.GET)
    public String read(@PathVariable long id, Model model, UriComponentsBuilder uriBuilder, HttpServletRequest request) {
        MushroomHunterDTO hunter = (MushroomHunterDTO) request.getSession().getAttribute("user");

        if(!hunter.isAdmin() && hunter.getId() != id){
            return "redirect:" + uriBuilder.path("/").build().toUriString();
        }

        log.debug("[HUNTER] Read ({})", id);

        model.addAttribute("hunter", hunterFacade.findHunterById(id));
        model.addAttribute("visits", visitFacade.listAllVisitsForMushroomHunter(hunter));
        return "hunters/read";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String delete(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {
        Long idToDelete = id;
        MushroomHunterDTO logUser = (MushroomHunterDTO) request.getSession().getAttribute("user");

        MushroomHunterDTO hunter = hunterFacade.findHunterById(id);
        hunterFacade.deleteHunter(id);
        log.debug("delete hunter({})", id);

        if (idToDelete.equals(logUser.getId())){
            redirectAttributes.addFlashAttribute("alert_warning", "You have been logged out after your profile was deleted.");
            return "redirect:" + uriBuilder.path("/auth/logout").build().toUriString();
        }

        redirectAttributes.addFlashAttribute("alert_success", "Mushroom hunter \"" + hunter.getUserNickname() + "\" was deleted.");
        return "redirect:" + uriBuilder.path("/hunters").build().toUriString();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editHunter(@PathVariable long id, Model model, HttpServletRequest request, UriComponentsBuilder uriBuilder, RedirectAttributes redirectAttributes) {

        MushroomHunterDTO logUser = (MushroomHunterDTO) request.getSession().getAttribute("user");

        if(!logUser.isAdmin() && logUser.getId() != id){
            return "redirect:" + uriBuilder.path("/").build().toUriString();
        }

        log.debug("[HUNTER] Edit {}", id);
        MushroomHunterDTO hunterDTO = hunterFacade.findHunterById(id);

        model.addAttribute("hunterEdit", hunterDTO);
        return "/hunters/edit";
    }

    @RequestMapping(value="/edit/{id}", method = RequestMethod.POST)
    public String update(@PathVariable long id,
        @Valid @ModelAttribute("hunterEdit")MushroomHunterUpdateDTO formBean,
        BindingResult bindingResult,
        Model model,
        UriComponentsBuilder uriBuilder,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request) {

        MushroomHunterDTO logUser = (MushroomHunterDTO) request.getSession().getAttribute("user");

        if(!logUser.isAdmin() && logUser.getId() != id){
            return "redirect:" + uriBuilder.path("/").build().toUriString();
        }

        formBean.setId(id);

        if(!logUser.isAdmin() && logUser.getId() == id){
            formBean.setAdmin(false);
        }

        log.debug("Hunter - update");
        if (bindingResult.hasErrors()) {
            log.debug("Hunter - has errors:");

            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }
            model.addAttribute("hunterEdit", formBean);
            return "hunters/edit";
        }

        log.debug("[HUNTER] Update: {}", formBean);
        MushroomHunterDTO result = hunterFacade.updateHunter(formBean);

        // updating self - change redirect
        if (logUser.getId().equals(formBean.getId())){
            request.getSession().setAttribute("user", hunterFacade.findHunterById(result.getId()));
        }

        redirectAttributes.addFlashAttribute("alert_success", "Hunter " + result.getUserNickname() + " was updated");
        return "redirect:" + uriBuilder.path("/hunters/read/{id}").buildAndExpand(id).encode().toUriString();
    }

}