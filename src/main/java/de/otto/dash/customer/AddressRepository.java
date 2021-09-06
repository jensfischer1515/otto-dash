package de.otto.dash.customer;

import de.otto.dash.customer.CustomerModel.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<Address, String> {

}
