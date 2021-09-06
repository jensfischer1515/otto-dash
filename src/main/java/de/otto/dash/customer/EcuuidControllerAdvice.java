package de.otto.dash.customer;

import de.otto.dash.config.ModelAttributes;
import de.otto.dash.config.RequestParams;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@ControllerAdvice
public class EcuuidControllerAdvice {

    @ModelAttribute(ModelAttributes.ECUUID)
    public String ecuuid(@RequestParam(RequestParams.ECUUID) Optional<String> ecuuid) {
        return ecuuid.orElse(null);
    }
}
