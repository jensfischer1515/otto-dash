package de.otto.dash.customer;

public interface CustomerModel {

    String ECUUID = "44330310-e02c-42bd-9394-227208f9a106";

    record Address(
            String ecuuid,
            String salutation,
            String title,
            String lastName,
            String firstName,
            String street,
            String houseNumber,
            String city,
            String zipCode,
            String additional,
            String phoneNumber
    ) {
    }
}
