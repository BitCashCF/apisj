package org.apis.gui.model;

import org.apis.gui.model.base.BaseModel;

import java.util.ArrayList;

public class MyAddressModel extends BaseModel {
    private String address;
    private String alias;
    private int exist;
    private ArrayList<String> groupList;

    public MyAddressModel(String address, String alias, int exist, ArrayList<String> groupList){
        this.address = address;
        this.alias = alias;
        this.exist = exist;
        this.groupList = groupList;

        if(this.groupList == null){
            this.groupList = new ArrayList<>();
        }
    }

    public void addGroup(String group){
        this.groupList.add(group);
    }

    public void removeGroup(String group){
        this.groupList.remove(group);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }
}
