package de.otto.dash.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;

public interface CustomerModel {

    record Address(
            @Id @JsonIgnore String ecuuid,
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
