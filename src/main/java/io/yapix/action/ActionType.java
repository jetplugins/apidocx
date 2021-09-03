package io.yapix.action;

public enum ActionType {

    YApi("Upload To YApi"),
    Rap2("Upload To Rap2"),
    Eolinker("Upload To Eolinker"),
    Curl("Copy as cRUL", false),
    ;

    private String name;
    private boolean requiredConfigFile = true;

    ActionType(String name) {
        this.name = name;
    }

    ActionType(String name, boolean requiredConfigFile) {
        this.name = name;
        this.requiredConfigFile = requiredConfigFile;
    }

    public String getName() {
        return name;
    }

    public boolean isRequiredConfigFile() {
        return requiredConfigFile;
    }
}
