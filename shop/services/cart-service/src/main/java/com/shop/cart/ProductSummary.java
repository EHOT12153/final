package com.shop.cart;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductSummary {

    private Long id;
    private String sku;
    private String name;

    @JsonProperty("price_cents")
    private Integer priceCents;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
}
