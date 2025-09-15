package dev.magadiflo.redis.app.city.dto;

public record City(String zip,
                   String city,
                   String stateName,
                   int temperature) {
}
