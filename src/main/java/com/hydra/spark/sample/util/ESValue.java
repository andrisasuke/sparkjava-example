package com.hydra.spark.sample.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ESValue {

    String PERSON_INDEX = "person";
    String POFILE_DOCTYPE = "profile";
}
