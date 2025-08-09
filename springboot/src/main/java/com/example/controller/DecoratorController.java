package com.example.controller;

import com.example.common.Result;
import com.example.entity.Decorator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/decorators")
public class DecoratorController {

    public static class DecoratorInfo {
        public String code;
        public String label;
        public BigDecimal surcharge;
        public DecoratorInfo(String code, String label, BigDecimal surcharge) {
            this.code = code;
            this.label = label;
            this.surcharge = surcharge;
        }
    }

    @GetMapping("/list")
    public Result list() {
        List<DecoratorInfo> list = new ArrayList<>();
        for (Decorator d : Decorator.values()) {
            list.add(new DecoratorInfo(d.getCode(), d.getLabel(), d.getSurcharge()));
        }
        return Result.success(list);
    }
}

