package com.escobar.pable.snake;

/**
 * Created by Professor on 4/21/2017.
 */
public class ApiResponse {
    private String id;
    private String timestamp;
    private Result  result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
