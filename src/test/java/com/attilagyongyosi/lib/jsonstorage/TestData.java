package com.attilagyongyosi.lib.jsonstorage;

import com.attilagyongyosi.lib.jsonstorage.domain.TestModel;

public final class TestData {
    public static final TestModel MODEL1 = TestModel.builder()
            .id(1)
            .active(false)
            .property("nice")
            .property("cosy")
            .property("model")
            .relative(TestModel.builder()
                    .id(2)
                    .active(true)
                    .property("funk")
                    .build())
            .relative(TestModel.builder()
                    .id(3)
                    .active(true)
                    .build())
            .build();

    public static final TestModel MODEL2 = TestModel.builder()
            .id(4)
            .active(true)
            .property("small")
            .property("big")
            .relative(TestModel.builder()
                    .id(5)
                    .active(false)
                    .property("warm")
                    .build())
            .build();

    private TestData() {}
}
