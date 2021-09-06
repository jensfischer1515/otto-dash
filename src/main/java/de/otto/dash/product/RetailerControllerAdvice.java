package de.otto.dash.product;

import de.otto.dash.config.ModelAttributes;
import de.otto.dash.config.RequestParams;
import de.otto.dash.product.ProductModel.Retailer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class RetailerControllerAdvice {

    private final RetailerRepository retailerRepository;

    public RetailerControllerAdvice(RetailerRepository retailerRepository) {
        this.retailerRepository = retailerRepository;
    }

    @ModelAttribute(ModelAttributes.AVAILABLE_RETAILERS)
    public List<Retailer> availableRetailers() {
        return retailerRepository.findAll();
    }

    @ModelAttribute(ModelAttributes.SELECTED_RETAILER)
    public Retailer selectedRetailer(@RequestParam(RequestParams.RETAILER_ID) Optional<String> retailerId) {
        return retailerId.flatMap(retailerRepository::findById)
                .orElse(new Retailer(null, "None"));
    }
}
