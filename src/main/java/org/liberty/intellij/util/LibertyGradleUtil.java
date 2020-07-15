package org.liberty.intellij.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class LibertyGradleUtil {

    /**
     * Given the gradle build file get the project name
     * This method looks for a settings.gradle file in the same parent dir
     * If a settings.gradle file exists, use the rootProject.name attribute
     * @param file build.gradle file
     * @return project name if it exists in a settings.gradle, null if not
     */
    public static String getProjectName(VirtualFile file) {
        VirtualFile parentFolder = file.getParent();
        Path settingsPath = Paths.get(parentFolder.getCanonicalPath(), "settings.gradle");
        File settingsFile = settingsPath.toFile();
        if (settingsFile.exists()) {
            System.out.println("settings.gradle exists: " + settingsPath);
            try {
                FileInputStream input = new FileInputStream(settingsFile);
                Properties prop = new Properties();
                prop.load(input);
                String name = prop.getProperty("rootProject.name");
                if (name != null) {
                    // return name without surrounding quotes
                    return name.replaceAll("^[\"']+|[\"']+$", "");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Check if a Gradle build file is using the liberty gradle plugin
     * @param file build.gradle file
     * @return true if the liberty gradle plugin is detected in the build.gradle
     * @throws IOException
     */
    public static boolean validBuildGradle(PsiFile file) throws IOException {
        FileInputStream in = null;

        try {
            in = new FileInputStream(file.getVirtualFile().getCanonicalPath());
            System.out.println(in.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            // check if "apply plugin: 'liberty'" is specified in the build.gradle
            boolean libertyPlugin = false;
            if (sb.indexOf("apply plugin: 'liberty'") > -1) {
                libertyPlugin = true;
            } else {
                // check if liberty is in the plugins block
                //TODO: replace this with a regexp
                int pluginsIndex = sb.indexOf("plugins {");
                if (pluginsIndex > -1) {
                    String pluginBlockStart = sb.substring(pluginsIndex);
                    int end = pluginBlockStart.indexOf("}");
                    if (end > -1) {
                        String pluginBlockFull = pluginBlockStart.substring(0, end);
                        if (pluginBlockFull.contains("id 'io.openliberty.tools.gradle.Liberty'")) {
                            libertyPlugin = true;
                        }
                    }
                }
            }

            System.out.println("liberty plugin detected: " + libertyPlugin);
            if (libertyPlugin) {
                // check if group matches io.openliberty.tools and name matches liberty-gradle-plugin
                //TODO: find a more robust way to search for io.openliberty.tools in the buildscript/dependencies blcok
                int dependenciesIndex = sb.indexOf("dependencies {");
                if (dependenciesIndex > -1) {
                    String dependenciesBlock = sb.substring(dependenciesIndex);
                    System.out.println(dependenciesBlock);
                    if (dependenciesBlock.contains("classpath 'io.openliberty.tools:liberty-gradle-plugin")) {
                        return true;
                    }
                }
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return false;
    }

}
