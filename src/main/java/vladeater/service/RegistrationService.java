package vladeater.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import vladeater.domain.User;
import vladeater.domain.dto.CaptchaResponseDto;
import vladeater.util.ControllerUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author XE on 13.09.2019
 * @project twitterApp
 */

@Service
public class RegistrationService {

    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Value("${recaptcha.secret}")
    private String secret;

    private final RestTemplate restTemplate;
    private final UserService userService;

    public RegistrationService(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    public String addNewUser(
            String passwordConfirm,
            String captchaResponse,
            User user,
            BindingResult bindingResult,
            Model model
    ) {
        return addRegistrationParams(passwordConfirm, captchaResponse, user, bindingResult, model);
    }

    private String addRegistrationParams(String passwordConfirm, String captchaResponse, User user, BindingResult bindingResult, Model model) {
        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
        if (response != null && !response.isSuccess()) {
            model.addAttribute("captchaError", "Ты робот?");
        }
        if (isConfirmEmpty){
            model.addAttribute("password2Error","Пароль не может быть пустым");
        }
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Пароли разные!");
            return "registration";
        }
        if (isConfirmEmpty || bindingResult.hasErrors() || (response != null && !response.isSuccess())) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }
        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "Данный пользователь существует!");
        }
        return "redirect:/login";
    }


    public String addAttributes(Model model, String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated){
            model.addAttribute("messageType","success");
            model.addAttribute("message","Пользователь успешно активирован!");
        } else {
            model.addAttribute("messageType","danger");
            model.addAttribute("message","Код активации не найден(((");
        }
        return "login";
    }
}
