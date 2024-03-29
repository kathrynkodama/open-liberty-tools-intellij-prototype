package org.liberty.intellij.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.liberty.intellij.util.Constants;
import org.liberty.intellij.util.LibertyGradleUtil;
import org.liberty.intellij.util.LibertyProjectUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewTestReport extends AnAction {

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

        // get path to project folder
        final VirtualFile parentFile = file.getParent();

        // parse the build.gradle for test.reports.html.destination
        File testReportFile = null;
        String testReportDest = null;
        try {
            testReportDest = getTestReportDestination(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (testReportDest != null) {
            testReportFile = new File(testReportDest);
            if (!testReportFile.exists()) {
                try {
                    testReportFile = findCustomTestReport(parentFile);
                    System.out.println("CUSTOM TEST REPORT: " + testReportFile.getCanonicalPath());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        if (testReportFile == null || !testReportFile.exists()) {
            // if does not exist look in default location: "build", "reports", "tests", "test", "index.html"
            testReportFile = Paths.get(parentFile.getCanonicalPath(), "build", "reports", "tests", "test", "index.html").normalize().toAbsolutePath().toFile();
        }

        VirtualFile testReportVirtualFile = LocalFileSystem.getInstance().findFileByIoFile(testReportFile);
        if (testReportVirtualFile == null || !testReportVirtualFile.exists()) {
            Messages.showErrorDialog(project, "Test report (" + testReportFile.getAbsolutePath() + ") does not exist.  " +
                            "Run tests to generate a test report.  Ensure your test report is generating at the correct location.",
                    "Gradle Test Report Does Not Exist");
            return;
        }

        // open test report in browser
        BrowserUtil.browse(testReportVirtualFile.getUrl());


    }

    private String getTestReportDestination(VirtualFile file) throws IOException {
        String buildFile = LibertyGradleUtil.fileToString(file.getCanonicalPath());
        String testReportRegex = "(?<=reports.html.destination[\\s\\=|\\=]).*([\"|'])(.*)([\"|'])";

        Pattern pattern = Pattern.compile(testReportRegex);
        Matcher matcher = pattern.matcher(buildFile);
        if (matcher.find( )) {
            if (!matcher.group(2).isEmpty()) {
                // group 2 is the string enclosed in quotation marks
                return matcher.group(2);
            }
        }
        return null;
    }


    private File findCustomTestReport(VirtualFile parentFile) throws IOException {
        // look for the most recently modified index.html files in the workspace
        ArrayList<File> customTestReports = new ArrayList<File>();
        try (Stream<Path> walk = Files.walk(Paths.get(parentFile.getCanonicalPath()))
                .filter(Files::isRegularFile)) {
            List<String> result = walk.map(x -> x.toString())
                    // exclude files from {bin, classes, target} dirs
                    .filter(f-> !f.contains("bin") || !f.contains("classes") || !f.contains("target"))
                    .filter(f -> f.endsWith("index.html")).collect(Collectors.toList());

            // check to see if the index.html contains the TEST_REPORT_STRING
            for (String s : result) {
                String file = LibertyGradleUtil.fileToString(s);
                if (file.contains(Constants.TEST_REPORT_STRING)) {
                    File newFile = new File(s);
                    if (newFile.exists()) {
                        customTestReports.add(newFile);
                    }
                }
            }

            if (customTestReports.size() > 1) {
                File mostRecentlyModified = customTestReports.get(0);
                for (File f : customTestReports) {
                    if (f.lastModified() > mostRecentlyModified.lastModified()) {
                        mostRecentlyModified = f;
                    }
                }
                // return the most recently modified file
                return mostRecentlyModified;
            } else if (customTestReports.size() == 1) {
                return customTestReports.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}