package com.amarisTest.funds.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
public abstract class BaseModel {
    protected String id;

    public void ensureId() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}