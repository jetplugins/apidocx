package io.yapix.action;

public enum YapiActions {

    YApi("Upload To YApi"),
    Rap2("Upload To Rap2"),
    Eolinker("Upload To Eolinker"),
    Curl("Copy as cRUL", false),
    ;

    private String name;
    private boolean requiredConfigFile = true;

    YapiActions(String name) {
        this.name = name;
    }

    YapiActions(String name, boolean requiredConfigFile) {
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
