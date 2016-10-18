package com.attilagyongyosi.lib.jsonstorage.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.Collection;

@Data
@Builder
@EqualsAndHashCode
public class TestModel {
    private int id;
    private boolean active;
    private @Singular Collection<String> properties;
    private @Singular Collection<TestModel> relatives;
}
