//package org.intellij.sdk.action;
//
//import com.intellij.openapi.actionSystem.ActionGroup;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import icons.ActionBasicsIcons;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public class DynamicActionGroup extends ActionGroup {
//
//    @NotNull
//    @Override
//    public AnAction[] getChildren(@Nullable AnActionEvent e) {
//        return new AnAction[]{ new PopupDialogAction("Action Added at Runtime",
//                "Dynamic Action Demo", ActionBasicsIcons.Sdk_default_icon) };
//    }
//}
