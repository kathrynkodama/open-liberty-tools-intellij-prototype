package org.intellij.sdk.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import icons.ActionBasicsIcons;

public class CustomDefaultActionGroup extends DefaultActionGroup {
    @Override
    public void update(AnActionEvent event) {
        // Enable/disable depending on whether user is editing
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setEnabled(editor != null);
        // Take this opportunity to set an icon for the menu entry.
        event.getPresentation().setIcon(ActionBasicsIcons.Sdk_default_icon);
    }
}
