package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import io.yapix.process.eolinker.EolinkerUploadAction;
import io.yapix.process.rap2.Rap2UploadAction;
import io.yapix.process.yapi.YapiUploadAction;

public enum ActionType {

    YApi(YapiUploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new YapiUploadAction();
        }
    },

    Rap2(Rap2UploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new Rap2UploadAction();
        }
    },

    Eolinker(EolinkerUploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new EolinkerUploadAction();
        }
    },

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

    public abstract AnAction getAction();
}
