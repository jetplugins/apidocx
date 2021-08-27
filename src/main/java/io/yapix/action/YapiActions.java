package io.yapix.action;

public enum YapiActions {

    YApi("Upload To YApi"),
    Rap2("Upload To Rap2"),
    Eolinker("Upload To Eolinker"),
    Curl("Copy as cRUL"),
    ;

    private String name;

    YapiActions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
