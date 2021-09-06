package de.otto.dash.product;

import de.otto.dash.product.ProductModel.Retailer;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class RetailerRepository {

    interface RetailerIds {
        String APART = "1000031";
    }

    public List<Retailer> findAll() {
        // {isDeepSea: true, active: true, name: {'$regex' : '^((?!test|example).)*$', '$options' : 'i'}}
        return io.vavr.collection.List.of(
                        new Retailer("1005528", "Marc Aurel Textil"),
                        new Retailer("1005537", "DeinDesign"),
                        new Retailer("1005539", "deltatecc GmbH"),
                        new Retailer("1000304", "Kasper Wohndesign"),
                        new Retailer("1000292", "JULIE & GRACE GmbH"),
                        new Retailer("1000129", "D&S trading"),
                        new Retailer("1000550", "Taschenkaufhaus"),
                        new Retailer("1000248", "neuetischkultur"),
                        new Retailer("1000132", "DELIFE"),
                        new Retailer("1000146", "Doorout"),
                        new Retailer("1000551", "WE LOVE BAGS"),
                        new Retailer("1000078", "KUNSTLOFT"),
                        new Retailer("1000437", "OTTO Office"),
                        new Retailer("1000347", "Livario"),
                        new Retailer("1000259", "in-trading Handelsgesellschaft mbH"),
                        new Retailer("1000589", "VCM"),
                        new Retailer("1000630", "CLP"),
                        new Retailer("1000191", "Frankonia"),
                        new Retailer("1073712", "Lashuma"),
                        new Retailer("1000403", "mirapodo / myToys"),
                        new Retailer("1000462", "Sofa Dreams"),
                        new Retailer("1000332", "Lautsprecher Teufel"),
                        new Retailer(RetailerIds.APART, "APART")
                )
                .sorted(Comparator.comparing(Retailer::name, String.CASE_INSENSITIVE_ORDER))
                .asJava();
    }

    public Optional<Retailer> findById(String id) {
        return findAll().stream()
                .filter(retailer -> retailer.id().equals(id))
                .findFirst();
    }
}
