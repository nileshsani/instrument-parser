package com.instrument;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Instrument {
    private String lastTradingDate;
    private String deliveryDate;
    private String market;
    private String label;
    private boolean tradable;

    public Instrument() {
        this.tradable = true;
    }

    @JsonProperty("LAST_TRADING_DATE")
    public String getLastTradingDate() {
        return lastTradingDate;
    }

    @JsonProperty("LAST_TRADING_DATE")
    public void setLastTradingDate(String lastTradingDate) {
        this.lastTradingDate = lastTradingDate;
    }

    @JsonProperty("DELIVERY_DATE")
    public String getDeliveryDate() {
        return deliveryDate;
    }

    @JsonProperty("DELIVERY_DATE")
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @JsonProperty("MARKET")
    public String getMarket() {
        return market;
    }

    @JsonProperty("MARKET")
    public void setMarket(String market) {
        this.market = market;
    }

    @JsonProperty("LABEL")
    public String getLabel() {
        return label;
    }

    @JsonProperty("LABEL")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("TRADABLE")
    public boolean isTradable() {
        return tradable;
    }

    @JsonProperty("TRADABLE")
    public void setTradable(boolean tradable) {
        this.tradable = tradable;
    }
}
