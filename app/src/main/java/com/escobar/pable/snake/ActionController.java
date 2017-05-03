package com.escobar.pable.snake;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Professor on 4/29/2017.
 */
public class ActionController {
    private ArrayList<Action> actions;
    private HashMap<Integer,Action> actionHM;
    public ActionController(){
        actions = new ArrayList<Action>();
        actionHM = new HashMap<Integer,Action>();
    }
    public void add_action(Action action){
        actionHM.put(action.getId(),action);
        actions.add(action);
        action.setActionController(this);
    }
    public Action getAction(int id){
        return actionHM.get(id);
    }
}
