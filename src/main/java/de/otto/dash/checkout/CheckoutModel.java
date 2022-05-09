package de.otto.dash.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.dash.customer.CustomerModel.Address;

import java.util.List;
import java.util.Map;

public interface CheckoutModel {

    record Checkout(
            String id,
            String orderId,
            Pricing pricing,
            @JsonProperty("_embedded") CheckoutEmbedded embedded
    ) {
    }

    record CheckoutItem(
            String variationId,
            int quantity,
            String id,
            String name
    ) {
        CheckoutItem(
                String variationId,
                int quantity
        ) {
            this(variationId, quantity, null, null);
        }
    }

    record CheckoutEmbedded(
            @JsonProperty("ch:checkout-items") List<CheckoutItem> checkoutItems
    ) {
    }

    record Pricing(
            int total
    ) {
    }

    record PaymentMethod(
            String paymentMethod
    ) {
    }

    record DeliveryAddress(
            String type,
            Address addressData
    ) {
    }

    record CheckoutOrder(
            String clientIp,
            String customerEmail,
            String trackingOrderId,
            int installmentTotalInterest
    ) {
        CheckoutOrder(
                String clientIp,
                String customerEmail
        ) {
            this(clientIp, customerEmail, null, 0);
        }
    }

    record OrderedCheckout(
            @JsonProperty("_embedded") OrderedCheckoutEmbedded embedded
    ) {
    }

    record OrderedCheckoutEmbedded(
            @JsonProperty("ch:ordered-articles") OrderedArticles orderedArticles
    ) {
    }

    record OrderedArticles(
            String id,
            String transactionId,
            Map<String, String> payment,
            List<Map<String, Object>> partners
    ) {
    }
}
