package ru.inno.bank;

import org.apache.kafka.common.protocol.types.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PredicateDemo {

    public static void main(String[] args) {

        List<String> brands = List.of(
                "Toyota",
                "Mazda",
                "Nissan",
                "Moskvich",
                "Lexus",
                "BMW",
                "Honda"
        );

        List<String> brandsM = filter(brands, brand -> brand.startsWith("M"));
        List<String> brands6L = filter(brands, brand -> brand.length()==6);
        List<String> brandsA = filter(brands, brand -> brand.endsWith("a"));

        System.out.println("brandsM = " + brandsM);
        System.out.println("brands6L = " + brands6L);
        System.out.println("brandsA = " + brandsA);
    }

    public static List<String> filter(List<String> list, Predicate<String> filterFunction) {
        List<String> filtered = new ArrayList<>();

        for (String brand : list) {
            System.out.println(brand + " => " + filterFunction.test(brand));
            if (filterFunction.test(brand)) {
                filtered.add(brand);
            }
        }

        return filtered;
    }
}
