package com.github.wiigen.auth.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class AdUser {
    String username;
    String firstName;
    String lastName;

    @Singular
    List<String> roles;
}
