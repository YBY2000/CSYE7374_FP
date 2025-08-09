package com.example.entity;

import java.math.BigDecimal;

public enum Decorator {
    SPICY("spicy", "Spicy", new BigDecimal("1.0")) {
        @Override
        public FoodItem apply(FoodItem base) { return new SpicyDecorator(base); }
    },
    GARLIC("garlic", "Garlic", new BigDecimal("0.5")) {
        @Override
        public FoodItem apply(FoodItem base) { return new GarlicDecorator(base); }
    };

    private final String code;
    private final String label;
    private final BigDecimal surcharge;

    Decorator(String code, String label, BigDecimal surcharge) {
        this.code = code;
        this.label = label;
        this.surcharge = surcharge;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
    public BigDecimal getSurcharge() { return surcharge; }

    public abstract FoodItem apply(FoodItem base);

    public static Decorator fromCode(String code) {
        if (code == null) return null;
        for (Decorator d : values()) {
            if (d.code.equalsIgnoreCase(code)) return d;
        }
        return null;
    }
}

