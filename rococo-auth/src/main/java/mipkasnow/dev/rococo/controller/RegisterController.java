package mipkasnow.dev.rococo.controller;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import mipkasnow.dev.rococo.model.RegistrationForm;
import mipkasnow.dev.rococo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private static final String REGISTRATION_VIEW_NAME = "register";
    private static final String MODEL_USERNAME_ATTR = "username";
    private static final String MODEL_REG_FORM_ATTR = "registrationForm";
    private static final String MODEL_FRONT_URI_ATTR = "frontUri";
    private static final String REG_MODEL_ERROR_BEAN_NAME = "org.springframework.validation.BindingResult.registrationForm";

    private final UserService userService;
    private final String frontUri;

    @Autowired
    public RegisterController(UserService userService,
                              @Value("${rococo-front.base-uri}") String frontUri) {
        this.userService = userService;
        this.frontUri = frontUri;
    }

    @GetMapping("/register")
    public String getRegisterPage(@Nonnull Model model) {
        model.addAttribute(MODEL_REG_FORM_ATTR, new RegistrationForm(null, null, null));
        model.addAttribute(MODEL_FRONT_URI_ATTR, frontUri);
        return REGISTRATION_VIEW_NAME;
    }

    @PostMapping(value = "/register")
    public String registerUser(@Valid @ModelAttribute RegistrationForm registrationForm,
                               Errors errors,
                               Model model,
                               HttpServletResponse response) {
        if (!errors.hasErrors()) {
            final String registeredUserName;
            try {
                registeredUserName = userService.registerUser(
                        registrationForm.username(),
                        registrationForm.password()
                );
                response.setStatus(HttpServletResponse.SC_CREATED);
                model.addAttribute(MODEL_USERNAME_ATTR, registeredUserName);
            } catch (DataIntegrityViolationException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                addErrorToRegistrationModel(
                        registrationForm,
                        model,
                        "username", "Username `" + registrationForm.username() + "` already exists"
                );
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        model.addAttribute(MODEL_FRONT_URI_ATTR, frontUri);
        return REGISTRATION_VIEW_NAME;
    }

    private void addErrorToRegistrationModel(@Nonnull RegistrationForm registrationForm,
                                             @Nonnull Model model,
                                             @Nonnull String fieldName,
                                             @Nonnull String error) {
        BeanPropertyBindingResult errorResult = (BeanPropertyBindingResult) model.getAttribute(REG_MODEL_ERROR_BEAN_NAME);
        if (errorResult == null) {
            errorResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
        }
        errorResult.addError(new FieldError("registrationForm", fieldName, error));
    }
}
