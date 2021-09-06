package de.otto.dash.customer;

import co.elastic.apm.api.CaptureTransaction;
import de.otto.dash.customer.CustomerModel.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AddressInitializer implements ApplicationListener<ApplicationStartedEvent> {

    public static final String MY_ECUUID = "44330310-e02c-42bd-9394-227208f9a106";
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressInitializer.class);

    private final AddressRepository addressRepository;

    public AddressInitializer(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    @CaptureTransaction(value = "initialize address", type = "external")
    public void onApplicationEvent(ApplicationStartedEvent event) {
        addressRepository.deleteAll();
        addressRepository.findById(MY_ECUUID).ifPresentOrElse(
                address -> LOGGER.info("Already found existing {}", address),
                () -> {
                    var address = new Address(
                            MY_ECUUID,
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
                    LOGGER.info("Initialized new {}", addressRepository.insert(address));
                });
    }
}
