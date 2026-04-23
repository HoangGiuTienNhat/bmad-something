package com.something.domain.rule;

public interface BusinessRule {
    boolean isSatisfied();

    String message();
}
