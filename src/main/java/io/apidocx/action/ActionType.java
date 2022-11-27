package io.apidocx.action;

import com.intellij.openapi.actionSystem.AnAction;
import io.apidocx.handle.apifox.ApifoxUploadAction;
import io.apidocx.handle.eolink.EolinkUploadAction;
import io.apidocx.handle.rap2.Rap2UploadAction;
import io.apidocx.handle.showdoc.ShowdocUploadAction;
import io.apidocx.handle.yapi.YapiUploadAction;

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

    private final String name;

    ActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract AnAction getAction();
}
