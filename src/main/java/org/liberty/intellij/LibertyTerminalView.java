package org.liberty.intellij;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.terminal.*;

import java.util.ArrayList;

public class LibertyTerminalView extends TerminalView {
    private final LocalTerminalDirectRunner myTerminalRunner;
    private final Project myProject;
    public ArrayList<ShellTerminalWidget> widgets;

    public LibertyTerminalView(@NotNull Project project, LocalTerminalDirectRunner myTerminalRunner, Project myProject) {
        super(project);
        this.myTerminalRunner = myTerminalRunner;
        this.myProject = myProject;

    }
    @Override
    public void createNewSession(@NotNull AbstractTerminalRunner terminalRunner, @Nullable TerminalTabState tabState) {
        super.createNewSession(terminalRunner, tabState);
    }

    /** Create console view code
     // check for existing console view based on display name
     ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(event.getProject()).getConsole();

     Content content = toolWindow.getContentManager().getFactory().createContent(consoleView.getComponent(), "MyPlugin Output", false);
     toolWindow.getContentManager().addContent(content);

     ArrayList<String> cmds = new ArrayList<>();
     cmds.add("./gradlew");
     GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmds);
     generalCommandLine.setCharset(Charset.forName("UTF-8"));
     generalCommandLine.setWorkDirectory(currentProject.getBasePath());

     ProcessHandler processHandler = null;
     try {
     processHandler = new OSProcessHandler(generalCommandLine);
     } catch (ExecutionException e) {
     e.printStackTrace();
     }
     processHandler.startNotify();

     consoleView.attachToProcess(processHandler);
     **/


    /** PopUp Example
     StringBuffer dlgMsg = new StringBuffer(event.getPresentation().getText() + " Selected!");
     String dlgTitle = event.getPresentation().getDescription();
     // If an element is selected in the editor, add info about it.
     Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
     if (nav != null) {
     dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()));
     }
     Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
     **/



}
