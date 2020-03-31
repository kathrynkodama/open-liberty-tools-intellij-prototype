package org.liberty.intellij.util;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.ui.content.Content;
import com.sun.istack.Nullable;
import org.jetbrains.plugins.terminal.AbstractTerminalRunner;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;
import org.jetbrains.plugins.terminal.TerminalTabState;
import org.jetbrains.plugins.terminal.TerminalView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LibertyProjectUtil {

    @Nullable
    public static Project getProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context);
    }


    // returns build.gradle and pom.xml files for the current project
    public  static ArrayList<PsiFile> getBuildFiles(Project project) {
        ArrayList<PsiFile> buildFiles = new ArrayList<PsiFile>();

//        // Gradle support
//        PsiFile[] gradleFiles = FilenameIndex.getFilesByName(project, "build.gradle", GlobalSearchScope.projectScope(project));
//        for (int i = 0; i < gradleFiles.length; i++) {
//            System.out.println(gradleFiles[i].getVirtualFile().getPath());
//            buildFiles.add(gradleFiles[i]);
//        }

        PsiFile[] mavenFiles = FilenameIndex.getFilesByName(project, "pom.xml", GlobalSearchScope.projectScope(project));
        for (int i = 0; i < mavenFiles.length; i++) {
            System.out.println(mavenFiles[i].getVirtualFile().getPath());
            try {
                if (validPom(mavenFiles[i])) {
                    buildFiles.add(mavenFiles[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error parsing pom.xml");
            }
        }
        return buildFiles;
    }

    public static String getProjectNameFromPom(VirtualFile file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        File inputFile = new File(file.getCanonicalPath());
        Document doc = builder.parse(inputFile);

        doc.getDocumentElement().normalize();
        Node root = doc.getDocumentElement();

        NodeList nList = root.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeName().equals("artifactId")) {
                if (nNode.getTextContent() != null) {
                    return nNode.getTextContent();
                }
            }
        }
        return null;
    }

    public static ShellTerminalWidget getTerminalWidget(Project project, String projectName, boolean createWidget) {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow terminalWindow = toolWindowManager.getToolWindow("Terminal");

        // look for existing terminal tab
        ShellTerminalWidget widget = getTerminalWidget(terminalWindow, projectName);
        if (widget != null) {
            return widget;
        } else if (createWidget) {
            // create a new terminal tab
            TerminalView terminalView = TerminalView.getInstance(project);
            AbstractTerminalRunner terminalRunner = terminalView.getTerminalRunner();
            TerminalTabState tabState = new TerminalTabState();
            tabState.myTabName = projectName;
            tabState.myWorkingDirectory = project.getBasePath();
            terminalView.createNewSession(terminalRunner, tabState);
            return getTerminalWidget(terminalWindow, projectName);
        }
        return null;
    }

    private static ShellTerminalWidget getTerminalWidget(ToolWindow terminalWindow, String projectName) {
        Content[] terminalContents = terminalWindow.getContentManager().getContents();
        for (int i = 0; i < terminalContents.length; i++) {
            if (terminalContents[i].getTabName().equals(projectName)) {
                JBTerminalWidget widget = TerminalView.getWidgetByContent(terminalContents[i]);
                ShellTerminalWidget shellWidget = (ShellTerminalWidget) Objects.requireNonNull(widget);
                return shellWidget;
            }
        }
        return null;
    }

    private static boolean validPom(PsiFile file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        File inputFile = new File(file.getVirtualFile().getCanonicalPath());
        Document doc = builder.parse(inputFile);

        doc.getDocumentElement().normalize();
        Node root = doc.getDocumentElement();

        NodeList nList = root.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            // check for liberty maven plugin in profiles
            if (nNode.getNodeName().equals("profiles")) {
                NodeList profiles = nNode.getChildNodes();
                for (int i = 0; i < profiles.getLength(); i++) {
                    Node profile = profiles.item(i);
                    if (profile.getNodeName().equals("profile")) {
                        NodeList profileList = profile.getChildNodes();
                        for (int j = 0; j < profileList.getLength(); j++) {
                            if (profileList.item(j).getNodeName().equals("build")) {
                                NodeList buildNodeList = profileList.item(j).getChildNodes();
                                if (mavenPluginDetected(buildNodeList)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            // check for liberty maven plugin in plugins
            if (nNode.getNodeName().equals("build")) {
                NodeList buildNodeList = nNode.getChildNodes();
                if (mavenPluginDetected(buildNodeList)) {
                    return true;
                }
            }

        }
        return false;
    }

    private static boolean mavenPluginDetected (NodeList buildNodeList) {
        for (int i = 0; i < buildNodeList.getLength(); i++) {
            Node buildNode = buildNodeList.item(i);
            if (buildNode.getNodeName().equals("plugins")) {
                NodeList plugins = buildNode.getChildNodes();
                for (int j = 0; j < plugins.getLength(); j++) {
                    NodeList plugin = plugins.item(j).getChildNodes();
                    boolean groupId = false;
                    boolean artifactId = false;
                    for (int k = 0; k < plugin.getLength(); k++) {
                        Node node = plugin.item(k);
                        if (node.getNodeName().equals("groupId") && node.getTextContent().equals("io.openliberty.tools")) {
                            groupId = true;
                        } else if (node.getNodeName().equals("artifactId") && node.getTextContent().equals("liberty-maven-plugin")) {
                            artifactId = true;
                        }
                        if (groupId && artifactId) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
