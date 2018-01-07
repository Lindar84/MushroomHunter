package cz.muni.fi.pa165.mushrooms.mvc.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterAuthenticateDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterCreateDTO;
import cz.muni.fi.pa165.mushrooms.dto.MushroomHunterDTO;
import cz.muni.fi.pa165.mushrooms.facade.MushroomHunterFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final static Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private MushroomHunterFacade hunterFacade;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String loginUser(Model model, HttpServletRequest request) {

        if(request.getSession().getAttribute("user") != null){
            return "home";
        }

        log.debug("[AUTH] Login");

        model.addAttribute("userLogin", new MushroomHunterAuthenticateDTO());
        return "/auth/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutUser(Model model, HttpServletRequest request) {
        log.debug("[AUTH] Logout");
        request.getSession().removeAttribute("user");
        return "home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerUser(Model model, HttpServletRequest request) {
        log.debug("[AUTH] Register");
        model.addAttribute("userRegister", new MushroomHunterCreateDTO());
        return "auth/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("userRegister") MushroomHunterCreateDTO formBean, BindingResult bindingResult,
        Model model, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder, HttpServletRequest request) {
        log.debug("register(userRegister={})", formBean);


        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }

            model.addAttribute("userRegister", formBean);
            return "/auth/register";
        }

        if(hunterFacade.findHunterByNickname(formBean.getUserNickname()) != null){
            redirectAttributes.addFlashAttribute("alert_warning", "Nick " + formBean.getUserNickname() + " already exists!");
            return "redirect:" + uriBuilder.path("/auth/register").build().toUriString();
        }

        MushroomHunterDTO user = hunterFacade.registerHunter(formBean);
        request.getSession().setAttribute("user", user);
        //report success
        redirectAttributes.addFlashAttribute("alert_success", "Register " + formBean.getUserNickname() + " succeeded");

        return "redirect:" + uriBuilder.path("/").build().toUriString(); //TODO: change to "profile" page
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("userLogin") MushroomHunterAuthenticateDTO formBean, BindingResult bindingResult,
        Model model, RedirectAttributes redirectAttributes, UriComponentsBuilder uriBuilder, HttpServletRequest request) {
        log.debug("login(userLogin={})", formBean);

        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.debug("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.debug("FieldError: {}", fe);
            }
            model.addAttribute("userLogin", new MushroomHunterAuthenticateDTO());
            return "/auth/login";
        }

        MushroomHunterDTO matchingUser = hunterFacade.findHunterByNickname(formBean.getNickname());
        if(matchingUser==null) {
            log.warn("no user with nick {}", formBean.getNickname());
            redirectAttributes.addFlashAttribute("alert_warning", "No user with nick: " + formBean.getNickname());
            return "redirect:" + uriBuilder.path("/auth").build().toUriString();
        }

        if (!hunterFacade.authenticate(formBean)) {
            log.warn("wrong credentials: user={} password={}", formBean.getNickname(), formBean.getPassword());
            redirectAttributes.addFlashAttribute("alert_warning", "Login " + formBean.getNickname() + " failed ");
            return "redirect:" + uriBuilder.path("/auth").build().toUriString();
        }
        request.getSession().setAttribute("user", matchingUser);
        //report success
        redirectAttributes.addFlashAttribute("alert_success", "Login " + formBean.getNickname() + " succeeded ");
        return "redirect:" + uriBuilder.path("/hunters/read/" + matchingUser.getId()).build().toUriString();
    }
}