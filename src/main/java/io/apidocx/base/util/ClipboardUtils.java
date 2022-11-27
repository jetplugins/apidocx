package io.apidocx.base.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClipboardUtils {

    /**
     * 设置剪贴版内容
     */
    public static void setClipboard(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(content);
        clipboard.setContents(selection, null);
    }

}
