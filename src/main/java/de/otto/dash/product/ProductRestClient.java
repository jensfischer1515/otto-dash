package de.otto.dash.product;

import de.otto.dash.rest.RestConfigurationProperties;
import de.otto.dash.product.ProductModel.Products;
import de.otto.dash.rest.TokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProductRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestClient.class);

    interface MediaTypes {
        MediaType APPLICATION_HAL_JSON = MediaType.valueOf("application/hal+json");
    }

    interface QueryParams {
        String INCLUDE_INACTIVE = "includeInactive";
        String EMBEDDED = "embedded";
        String PAGE = "page";
        String PAGE_SIZE = "pageSize";
        String RETAILER_ID = "retailerId";
    }

    private final RestTemplate restTemplate;

    public ProductRestClient(
            RestConfigurationProperties properties,
            RestTemplateBuilder restTemplateBuilder,
            TokenHandler tokenHandler
    ) {
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.endpoint().url() + "/products")
                .additionalRequestCustomizers(request -> request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHandler.getBearerToken()))
                .defaultHeader(HttpHeaders.USER_AGENT, tokenHandler.userAgent())
                .build();
    }

    public ResponseEntity<Products> findByRetailerId(String retailerId, int page, int pageSize) {
        LOGGER.debug("Fetching products for retailer id {}", retailerId);

        var uri = UriComponentsBuilder
                .fromPath("/")
                .queryParam(QueryParams.EMBEDDED, true)
                .queryParam(QueryParams.INCLUDE_INACTIVE, false)
                .queryParam(QueryParams.PAGE, page)
                .queryParam(QueryParams.PAGE_SIZE, pageSize)
                .queryParam(QueryParams.RETAILER_ID, retailerId)
                .build()
                .toUriString();

        var request = RequestEntity
                .get(uri)
                .accept(MediaTypes.APPLICATION_HAL_JSON)
                .build();

        return restTemplate.exchange(request, Products.class);
    }
}
