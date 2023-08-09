package net.pixaurora.janerator.graphing.variable;

public abstract class Variable {
    protected String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
