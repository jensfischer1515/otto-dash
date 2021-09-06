package de.otto.dash.checkout;

import de.otto.dash.checkout.CheckoutModel.*;
import de.otto.dash.customer.CustomerModel.Address;
import de.otto.dash.rest.RestConfigurationProperties;
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

@Component
public class CheckoutRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutRestClient.class);

    interface MediaTypes {
        MediaType CHECKOUT_V2 = MediaType.valueOf("application/hal+json;charset=utf-8;profile=\"https://api.otto.de/api-docs/profiles/checkout\";version=2");

        MediaType CHECKOUT_ITEM_V1 = MediaType.valueOf("application/hal+json;charset=utf-8;profile=\"https://api.otto.de/api-docs/profiles/checkout-item\";version=1");

        MediaType CHECKOUT_PAYMENT_V1 = MediaType.valueOf("application/hal+json;profile=\"https://api.otto.de/profiles/checkout-payment+v1\"");

        MediaType CHECKOUT_INVOICE_ADDRESS_V1 = MediaType.valueOf("application/hal+json;profile=\"https://api.otto.de/profiles/checkout-invoice-address+v1\"");

        MediaType CHECKOUT_DELIVERY_ADDRESS_V1 = MediaType.valueOf("application/hal+json;profile=\"https://api.otto.de/profiles/checkout-delivery-address+v1\"");

        MediaType CHECKOUT_ORDER_V1 = MediaType.valueOf("application/hal+json;charset=utf-8;profile=\"https://api.otto.de/api-docs/profiles/checkout-order\";version=1");

        MediaType ORDERED_CHECKOUT_V1 = MediaType.valueOf("application/hal+json;charset=utf-8;profile=\"https://api.otto.de/api-docs/profiles/ordered-checkout\";version=1");
    }

    interface Headers {
        String ECUUID = "otto-checkout-ecuuid";
    }

    private final RestTemplate restTemplate;

    public CheckoutRestClient(
            RestConfigurationProperties properties,
            RestTemplateBuilder restTemplateBuilder,
            TokenHandler tokenHandler
    ) {
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.endpoint().url() + "/checkout-core")
                .additionalRequestCustomizers(request -> request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHandler.getBearerToken()))
                .defaultHeader(HttpHeaders.USER_AGENT, tokenHandler.userAgent())
                .build();
    }

    public ResponseEntity<Checkout> create(String ecuuid) {
        LOGGER.debug("Creating new checkout");

        var request = RequestEntity
                .post("/checkouts")
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_V2)
                .build();

        return restTemplate.exchange(request, Checkout.class);
    }

    public ResponseEntity<CheckoutItem> addItem(String ecuuid, String checkoutId, CheckoutItem checkoutItem) {
        LOGGER.debug("Adding {} to checkout {}", checkoutItem, checkoutId);

        var request = RequestEntity
                .post("/checkouts/{checkoutId}/items", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_ITEM_V1)
                .contentType(MediaTypes.CHECKOUT_ITEM_V1)
                .body(checkoutItem);

        return restTemplate.exchange(request, CheckoutItem.class);
    }

    public ResponseEntity<PaymentMethod> setPaymentMethod(String ecuuid, String checkoutId, PaymentMethod paymentMethod) {
        LOGGER.debug("Setting {} to checkout {}", paymentMethod, checkoutId);

        var request = RequestEntity
                .put("/checkouts/{checkoutId}/payment", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_PAYMENT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentMethod);

        return restTemplate.exchange(request, PaymentMethod.class);
    }

    public ResponseEntity<Address> setInvoiceAddress(String ecuuid, String checkoutId, Address invoiceAddress) {
        LOGGER.debug("Setting {} as invoice address to checkout {}", invoiceAddress, checkoutId);

        var request = RequestEntity
                .put("/checkouts/{checkoutId}/invoice-address", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_INVOICE_ADDRESS_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(invoiceAddress);

        return restTemplate.exchange(request, Address.class);
    }

    public ResponseEntity<DeliveryAddress> setDeliveryAddress(String ecuuid, String checkoutId, DeliveryAddress deliveryAddress) {
        LOGGER.debug("Setting {} as delivery address to checkout {}", deliveryAddress, checkoutId);

        var request = RequestEntity
                .put("/checkouts/{checkoutId}/delivery-address", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_DELIVERY_ADDRESS_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(deliveryAddress);

        return restTemplate.exchange(request, DeliveryAddress.class);
    }

    public ResponseEntity<OrderedCheckout> order(String ecuuid, String checkoutId, CheckoutOrder checkoutOrder) {
        LOGGER.debug("Placing order from checkout {}", checkoutId);

        var request = RequestEntity
                .post("/checkouts/{checkoutId}/order", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.ORDERED_CHECKOUT_V1)
                .contentType(MediaTypes.CHECKOUT_ORDER_V1)
                .body(checkoutOrder);

        return restTemplate.exchange(request, OrderedCheckout.class);
    }
}
