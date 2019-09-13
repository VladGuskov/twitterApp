package vladeater.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vladeater.domain.User;
import vladeater.service.RegistrationService;

import javax.validation.Valid;

/**
 * @author Vlados Guskov
 */

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@RequestParam("password2") String passwordConfirm,
                          @RequestParam("g-recaptcha-response") String captchaResponse,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model
    ) {
        return registrationService.addNewUser(passwordConfirm, captchaResponse, user, bindingResult, model);
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        return registrationService.addAttributes(model,code);
    }
}
