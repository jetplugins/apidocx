package io.yapix.action;

import com.intellij.openapi.actionSystem.AnAction;
import io.yapix.process.apifox.ApifoxUploadAction;
import io.yapix.process.eolink.EolinkUploadAction;
import io.yapix.process.rap2.Rap2UploadAction;
import io.yapix.process.showdoc.ShowdocUploadAction;
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

    Eolink(EolinkUploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new EolinkUploadAction();
        }
    },
    ShowDoc(ShowdocUploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new ShowdocUploadAction();
        }
    },
    Apifox(ApifoxUploadAction.ACTION_TEXT) {
        @Override
        public AnAction getAction() {
            return new ApifoxUploadAction();
        }
    },

    ;

    private String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract AnAction getAction();
}
