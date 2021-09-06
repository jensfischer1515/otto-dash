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
import de.otto.dash.customer.AddressRepository;
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
    private final AddressRepository addressRepository;

    public PlaceOrderController(CheckoutRestClient checkoutRestClient, AddressRepository addressRepository) {
        this.checkoutRestClient = checkoutRestClient;
        this.addressRepository = addressRepository;
    }

    @PostMapping("/placeOrder")
    public String placeOrder(
            UriComponentsBuilder uriComponentsBuilder,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            @RequestParam(RequestParams.ECUUID) String ecuuid,
            @RequestParam(RequestParams.RETAILER_ID) String retailerId,
            @RequestParam(RequestParams.VARIATION_ID) String variationId
    ) {
        try (var mdc = MDC.putCloseable(MdcKeys.ECUUID, ecuuid)) {
            // prepare checkout data
            var checkoutItem = new CheckoutItem(variationId, 1);
            var paymentMethod = new PaymentMethod("CREDIT_CARD_ONLINE");
            var invoiceAddress = addressRepository.findById(ecuuid).orElseThrow();
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
            redirectAttributes.addFlashAttribute(ModelAttributes.SELECTED_RETAILER, retailerId);
            redirectAttributes.addFlashAttribute(ModelAttributes.ORDER_ID, checkout.orderId());
            redirectAttributes.addFlashAttribute(ModelAttributes.TRANSACTION_ID, orderedCheckout.embedded().orderedArticles().transactionId());

            return "redirect:" + uriComponentsBuilder
                    .replacePath("/")
                    .replaceQueryParam(RequestParams.ECUUID, ecuuid)
                    .replaceQueryParam(RequestParams.RETAILER_ID, retailerId)
                    .fragment(variationId)
                    .toUriString();
        }
    }
}
