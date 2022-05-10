package de.otto.dash.checkout;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import de.otto.dash.checkout.CheckoutModel.CheckoutItem;
import de.otto.dash.checkout.CheckoutModel.CheckoutOrder;
import de.otto.dash.checkout.CheckoutModel.DeliveryAddress;
import de.otto.dash.checkout.CheckoutModel.PaymentMethod;
import de.otto.dash.config.MdcKeys;
import de.otto.dash.config.ModelAttributes;
import de.otto.dash.config.RequestParams;
import de.otto.dash.customer.CustomerModel.Address;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PlaceOrderController {

    private final CheckoutRestClient checkoutRestClient;

    public PlaceOrderController(CheckoutRestClient checkoutRestClient) {
        this.checkoutRestClient = checkoutRestClient;
    }

    @PostMapping("/placeOrder")
    public String placeOrder(
            UriComponentsBuilder uriComponentsBuilder,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            @RequestParam(RequestParams.ECUUID) String ecuuid,
            @RequestParam(RequestParams.VARIATION_ID) String variationId
    ) {
        try (var mdc = MDC.putCloseable(MdcKeys.ECUUID, ecuuid)) {
            // prepare checkout data
            var checkoutItem = new CheckoutItem(variationId, 1);
            var paymentMethod = new PaymentMethod("CREDIT_CARD_ONLINE");
            var invoiceAddress = new Address(
                    ecuuid,
                    "Mr.",
                    null,
                    "Fischer",
                    "Jens",
                    "Werner-Otto-Straße",
                    "1-7",
                    "Hamburg",
                    "22179",
                    "MA-EC-ST-CE-T1",
                    null);
            var deliveryAddress = new DeliveryAddress("INVOICE_ADDRESS", invoiceAddress);
            var checkoutOrder = new CheckoutOrder(request.getRemoteAddr(), "provided@customer.email");

            // use Checkout API
            var checkout = checkoutRestClient.create(ecuuid).getBody();

            Transaction transaction = ElasticApm.currentTransaction();
            transaction.setLabel("orderId", checkout.orderId());

            checkoutRestClient.addItem(ecuuid, checkout.id(), checkoutItem);
            checkoutRestClient.setPaymentMethod(ecuuid, checkout.id(), paymentMethod);
            checkoutRestClient.setInvoiceAddress(ecuuid, checkout.id(), invoiceAddress);
            checkoutRestClient.setDeliveryAddress(ecuuid, checkout.id(), deliveryAddress);

            var orderedCheckout = checkoutRestClient.order(ecuuid, checkout.id(), checkoutOrder).getBody();

            // Redirect-after-POST
            redirectAttributes.addFlashAttribute(ModelAttributes.ECUUID, ecuuid);
            redirectAttributes.addFlashAttribute(ModelAttributes.ORDER_ID, checkout.orderId());
            redirectAttributes.addFlashAttribute(ModelAttributes.TRANSACTION_ID, orderedCheckout.embedded().orderedArticles().transactionId());

            return "redirect:" + uriComponentsBuilder
                    .replacePath("/")
                    .replaceQueryParam(RequestParams.ECUUID, ecuuid)
                    .fragment(variationId)
                    .toUriString();
        }
    }
}
