package org.liberty.intellij.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.liberty.intellij.util.Constants;
import org.liberty.intellij.util.LibertyActionUtil;
import org.liberty.intellij.util.LibertyProjectUtil;

import java.io.IOException;

public class LibertyDevCustomStartAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = LibertyProjectUtil.getProject(e.getDataContext());
        if (project == null) return;

        final VirtualFile file = (VirtualFile) e.getDataContext().getData(Constants.LIBERTY_BUILD_FILE);
        if (file == null) return;

        final String projectName = (String) e.getDataContext().getData(Constants.LIBERTY_PROJECT_NAME);
        final String projectType = (String) e.getDataContext().getData(Constants.LIBERTY_PROJECT_TYPE);

        String customParams = Messages.showInputDialog("Specify custom parameters for the liberty dev command (e.g. -DhotTests=true)", "Liberty Dev Custom Parameters",  Messages.getQuestionIcon());

        String startCmd = null;
        if (projectType.equals(Constants.LIBERTY_MAVEN_PROJECT)) {
            startCmd = "mvn io.openliberty.tools:liberty-maven-plugin:dev " + customParams + " -f \"" + file.getCanonicalPath() + "\"";
        }

        ShellTerminalWidget widget = LibertyProjectUtil.getTerminalWidget(project, projectName, true);
        if (widget == null) return;

        LibertyActionUtil.executeCommand(widget, startCmd);
    }
}
