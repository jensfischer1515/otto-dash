package de.otto.dash.product;

import de.otto.dash.config.ModelAttributes;
import de.otto.dash.config.RequestParams;
import de.otto.dash.product.ProductModel.Product;
import de.otto.dash.product.ProductModel.ProductEmbedded;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static de.otto.dash.customer.AddressInitializer.MY_ECUUID;

@Controller
public class ProductController {

    private final ProductRestClient productRestClient;

    public ProductController(ProductRestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @GetMapping("/")
    public String dash(
            ModelMap model,
            UriComponentsBuilder uriComponentsBuilder,
            @RequestParam(RequestParams.ECUUID) Optional<String> ecuuid,
            @RequestParam(RequestParams.PAGE) Optional<Integer> page,
            @RequestParam(RequestParams.PAGE_SIZE) Optional<Integer> pageSize
    ) {
        if (ecuuid.isEmpty() || page.isEmpty() || pageSize.isEmpty()) {
            return "redirect:" + uriComponentsBuilder
                    .replacePath("/")
                    .replaceQueryParam(RequestParams.ECUUID, ecuuid.orElse(MY_ECUUID))
                    .replaceQueryParam(RequestParams.PAGE, page.orElse(0))
                    .replaceQueryParam(RequestParams.PAGE_SIZE, pageSize.orElse(20))
                    .toUriString();
        } else {
            var variations = productRestClient.findByBrandId("S000002Xft1", page.get(), pageSize.get())
                    .getBody()
                    .embedded()
                    .products()
                    .stream()
                    .map(Product::embedded)
                    .map(ProductEmbedded::getFirstAvailableVariation)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            model.addAttribute(ModelAttributes.VARIATIONS, variations);

            return "dash";
        }
    }
}
