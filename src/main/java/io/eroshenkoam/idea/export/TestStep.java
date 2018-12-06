package io.eroshenkoam.idea.export;

import java.util.List;

/**
 * @author eroshenkoam (Artem Eroshenko).
 */
public class TestStep {

    private String name;

    private List<TestStep> steps;

    public String getName() {
        return name;
    }

    public TestStep setName(String name) {
        this.name = name;
        return this;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public TestStep setSteps(List<TestStep> steps) {
        this.steps = steps;
        return this;
    }
}
