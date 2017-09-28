package com.example.aac088.tasklistoffline;

/**
 * Created by aac088 on 9/27/2017.
 */

public class Model {
    private boolean isSelected;
    private String task;

    public Model(){

    }

    public Model(String task, boolean isSelected){
        this.task = task;
        this.isSelected=isSelected;
    }

    public String getTask(){
        return task;
    }

    public void setTask(String task){
        this.task=task;
    }

    public boolean getSelected(){
        return isSelected;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
    }
}
