package com.escobar.pable.snake;

/**
 * Created by Professor on 4/21/2017.
 */
public class Result {


    private String action;
    private String resolvedQuery;
    private Parameter parameters;
    public String getResolvedQuery() {
        return resolvedQuery;
    }

    public void setResolvedQuery(String resolvedQuery) {
        this.resolvedQuery = resolvedQuery;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public Parameter getParameters() {
        return parameters;
    }

    public void setParameters(Parameter parameters) {
        this.parameters = parameters;
    }
    public String getTimeXAmount(){
        return this.getParameters().getTimex().getAmount();
    }
}
