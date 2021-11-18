package de.otto.dash.checkout;

import co.elastic.apm.api.CaptureSpan;
import co.elastic.apm.api.ElasticApm;
import de.otto.dash.checkout.CheckoutModel.*;
import de.otto.dash.rest.RestConfigurationProperties;
import de.otto.dash.rest.TokenHandler;
import de.otto.dash.customer.CustomerModel.Address;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

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

    private final Tracer tracer;

    public CheckoutRestClient(
            RestConfigurationProperties properties,
            RestTemplateBuilder restTemplateBuilder,
            TokenHandler tokenHandler,
            Tracer tracer
    ) {
        this.restTemplate = restTemplateBuilder
                .rootUri(properties.endpoint().url() + "/order-checkout")
                .additionalRequestCustomizers(request -> request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenHandler.getBearerToken()))
                .defaultHeader(HttpHeaders.USER_AGENT, tokenHandler.userAgent())
                .build();
        this.tracer = tracer;
    }

    @CaptureSpan(value = "Checkout API: create checkout", type = "external", subtype = "http")
    public ResponseEntity<Checkout> create(String ecuuid) {
        LOGGER.debug("Creating new checkout");

        var request = RequestEntity
                .post("/checkouts")
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.CHECKOUT_V2)
                .build();

        return restTemplate.exchange(request, Checkout.class);
    }

    @CaptureSpan(value = "Checkout API: add item to checkout", type = "external", subtype = "http")
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

    @CaptureSpan(value = "Checkout API: set payment method of checkout", type = "external", subtype = "http")
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

    @CaptureSpan(value = "Checkout API: set invoice address of checkout", type = "external", subtype = "http")
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

    @CaptureSpan(value = "Checkout API: set delivery address of checkout", type = "external", subtype = "http")
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

    @CaptureSpan(value = "Checkout API: place order", type = "external", subtype = "http")
    public ResponseEntity<OrderedCheckout> order(String ecuuid, String checkoutId, CheckoutOrder checkoutOrder) {
        LOGGER.debug("Placing order from checkout {}", checkoutId);

        var request = RequestEntity
                .post("/checkouts/{checkoutId}/order", checkoutId)
                .header(Headers.ECUUID, ecuuid)
                .accept(MediaTypes.ORDERED_CHECKOUT_V1)
                .contentType(MediaTypes.CHECKOUT_ORDER_V1)
                .body(checkoutOrder);

        io.opentracing.Span openSpan = tracer.activeSpan();
        openSpan.setTag("dash.open.span.key", UUID.randomUUID().toString());

        co.elastic.apm.api.Span vendorSpan = ElasticApm.currentSpan();
        vendorSpan.setLabel("dash.vendor.span.key", UUID.randomUUID().toString());

        co.elastic.apm.api.Transaction vendorTransaction = ElasticApm.currentTransaction();
        vendorTransaction.setLabel("dash.vendor.transaction.key", UUID.randomUUID().toString());

        return restTemplate.exchange(request, OrderedCheckout.class);
    }
}
