package com.attilagyongyosi.lib.jsonstorage.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.Collection;

@Data
@Builder
@EqualsAndHashCode
@JsonDeserialize(builder = TestModel.TestModelBuilder.class)
public class TestModel {
    private int id;
    private boolean active;
    private @Singular Collection<String> properties;
    private @Singular Collection<TestModel> relatives;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestModelBuilder {}
}
