package com.escobar.pable.snake;

import java.util.StringTokenizer;

/**
 * Created by Professor on 4/29/2017.
 */
public class Action implements MessageGen{


    private String name;
    private int id;
    private boolean hasDirectResponse;
    private boolean hasArduinoResponse;
    private boolean hasNextState;
    private int nextState;
    private ActionController actionController;
    private int parameterToSend;
    private boolean requireParam = false;



    private String[] messages;

    public ActionController getActionController() {
        return actionController;
    }

    public void setActionController(ActionController actionController) {
        this.actionController = actionController;
    }

    public int getNextState() {
        return nextState;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }

    public boolean isHasNextState() {
        return hasNextState;
    }

    public void setHasNextState(boolean hasNextState) {
        this.hasNextState = hasNextState;
    }

    public boolean isHasArduinoResponse() {
        return hasArduinoResponse;
    }

    public void setHasArduinoResponse(boolean hasArduinoResponse) {
        this.hasArduinoResponse = hasArduinoResponse;
    }

    public boolean isHasDirectResponse() {
        return hasDirectResponse;
    }

    public void setHasDirectResponse(boolean hasDirectResponse) {
        this.hasDirectResponse = hasDirectResponse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    public int getParameterToSend() {
        return parameterToSend;
    }

    public void setParameterToSend(int parameterToSend) {
        this.parameterToSend = parameterToSend;
    }
    public boolean isRequireParam() {
        return requireParam;
    }

    public void setRequireParam(boolean requireParam) {
        this.requireParam = requireParam;
    }

    public Action(){
        name = "";
        id = 0;
    }
    public Action(int id , String name, ActionController actionController){
        this.id = id;
        this.name = name;
        this.actionController = actionController;
    }

    public String renderReceivedMessage(String message){
        StringTokenizer st = new StringTokenizer(message,"$");
        String resultMessage = messages[Integer.parseInt(st.nextToken())];
        int i = 0;
        while(st.hasMoreTokens()){
            resultMessage = resultMessage.replaceAll("%"+i+"%",st.nextToken());
        }
        return resultMessage;
    }
    public String toArduinoMessage(){
        return (new Integer(this.id)).toString();
    }
}
