package de.otto.dash.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.text.NumberFormat;
import java.util.*;

public interface ProductModel {

    record Retailer(
            String id,
            String name
    ) {
    }

    record Products(
            @JsonProperty("_embedded") ProductsEmbedded embedded
    ) {
    }

    record ProductsEmbedded(
            @JsonProperty("o:product") List<Product> products
    ) {
    }

    record Product(
            String id,
            OnlineRelevance onlineRelevance,
            @JsonProperty("_embedded") ProductEmbedded embedded
    ) {
    }

    record OnlineRelevance(
            String value,
            Date valueChangeDate
    ) {
    }

    record ProductEmbedded(
            @JsonProperty("o:variation") List<Variation> variations
    ) {
        public List<Variation> availableVariations() {
            return Optional.ofNullable(variations())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(Variation::isAvailable)
                    .toList();
        }

        public Optional<Variation> getFirstAvailableVariation() {
            return availableVariations().stream().findFirst();
        }
    }

    record Variation(
            String id,
            String sku,
            String name,
            String gtin,
            String productId,
            String articleNumber,
            String moin,
            Date lastModified,
            Date onlineDate,
            Availability availability,
            List<String> sellingPoints,
            Pricing pricing,
            Discount discount,
            Map<String, String> dimensions,
            @JsonProperty("_embedded") VariationEmbedded embedded
    ) {
        public boolean isAvailable() {
            return Optional.ofNullable(availability())
                    .filter(Availability::isAvailable)
                    .map(Availability::value)
                    .isPresent();
        }
    }

    record Availability(
            String value, // AVAILABLE
            String detail,
            int stock,
            boolean delivery24H,
            boolean ordersplit,
            List<String> warehouses
    ) {
        public boolean isAvailable() {
            return "AVAILABLE".equals(value());
        }
    }

    record Pricing(
            boolean sale,
            int retailPrice,
            Date retailPriceFrom,
            int oldPrice,
            int suggestedRetailPrice,
            double taxRate
    ) {
        public String getFormattedPrice() {
            return NumberFormat
                    .getCurrencyInstance(Locale.GERMANY)
                    .format(retailPrice() / 100);
        }
    }

    record Discount(
            double percentage,
            String displayValue,
            int value
    ) {
    }

    record VariationEmbedded(
            @JsonProperty("o:images") Images images
    ) {
    }

    record Images(
            List<Image> images,
            @JsonProperty("_links") Map<String, Link> links
    ) {

        public List<URI> getImageUris(String role) {
            return Optional.ofNullable(images())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(image -> role.equals(image.role()))
                    .map(Image::links)
                    .map(links -> links.get("external"))
                    .map(Link::href)
                    .toList();
        }

        public URI getFirstImageUri(String role) {
            return getImageUris(role)
                    .stream()
                    .findFirst()
                    .orElse(URI.create("https://i.otto.de/i/otto/lh_platzhalter_ohne_abbildung"));
        }

        public URI getMainImageUri() {
            return getFirstImageUri("HAUPT");
        }
    }

    record Image(
            String id,
            String role, // HAUPT, ALTERNATIVE
            String type, // PRODUKTBILD
            int position,
            int height,
            int width,
            String orientation, // SQUARE, PORTRAIT
            int contentLength,
            @JsonProperty("_links") Map<String, Link> links // https://i.otto.de/i/otto/{id}
    ) {
    }

    record Link(
            URI href,
            String type,
            String title
    ) {
    }
}
