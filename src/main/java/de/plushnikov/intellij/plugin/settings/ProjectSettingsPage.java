package de.plushnikov.intellij.plugin.settings;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;

import de.plushnikov.intellij.plugin.Version;
import de.plushnikov.intellij.plugin.provider.LombokAugmentProvider;
import de.plushnikov.intellij.plugin.LombokBundle;
import de.plushnikov.intellij.plugin.provider.LombokProcessorProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ProjectSettingsPage implements SearchableConfigurable, Configurable.NoScroll {

  private JPanel myGeneralPanel;

  private JPanel myLombokPanel;
  private JCheckBox myEnableExtensionMethodSupport;

  private JPanel mySettingsPanel;
  private JCheckBox myEnableLombokVersionWarning;
  private JCheckBox myAnnotationProcessingWarning;

  private final Project myProject;

  public ProjectSettingsPage(Project project) {
    myProject = project;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return LombokBundle.message("plugin.settings.title");
  }

  @Override
  public JComponent createComponent() {
    initFromSettings();
    return myGeneralPanel;
  }

  private void initFromSettings() {
    myEnableExtensionMethodSupport.setSelected(ProjectSettings.isEnabled(myProject, ProjectSettings.IS_EXTENSION_METHOD_ENABLED, false));

    myEnableLombokVersionWarning.setSelected(ProjectSettings.isEnabled(myProject, ProjectSettings.IS_LOMBOK_VERSION_CHECK_ENABLED, false));
    myAnnotationProcessingWarning
      .setSelected(ProjectSettings.isEnabled(myProject, ProjectSettings.IS_ANNOTATION_PROCESSING_CHECK_ENABLED, true));
  }

  @Override
  public boolean isModified() {
    return
      myEnableExtensionMethodSupport.isSelected() != ProjectSettings.isEnabled(myProject, ProjectSettings.IS_EXTENSION_METHOD_ENABLED) ||
      myEnableLombokVersionWarning.isSelected() !=
      ProjectSettings.isEnabled(myProject, ProjectSettings.IS_LOMBOK_VERSION_CHECK_ENABLED, false) ||
      myAnnotationProcessingWarning.isSelected() !=
      ProjectSettings.isEnabled(myProject, ProjectSettings.IS_ANNOTATION_PROCESSING_CHECK_ENABLED, true);
  }

  @Override
  public void apply() {
    ProjectSettings.setEnabled(myProject, ProjectSettings.IS_EXTENSION_METHOD_ENABLED, myEnableExtensionMethodSupport.isSelected());

    ProjectSettings.setEnabled(myProject, ProjectSettings.IS_LOMBOK_VERSION_CHECK_ENABLED, myEnableLombokVersionWarning.isSelected());
    ProjectSettings
      .setEnabled(myProject, ProjectSettings.IS_ANNOTATION_PROCESSING_CHECK_ENABLED, myAnnotationProcessingWarning.isSelected());

    LombokProcessorProvider lombokProcessorProvider = myProject.getService(LombokProcessorProvider.class);
    lombokProcessorProvider.initProcessors();
    // Redo code checking and highlighting.
    LombokAugmentProvider.onConfigChange();
    PsiManager.getInstance(myProject).dropPsiCaches();
    DaemonCodeAnalyzer.getInstance(myProject).restart();
  }

  @Override
  public void reset() {
    initFromSettings();
  }

  @NotNull
  @Override
  public String getId() {
    return getDisplayName();
  }
}
