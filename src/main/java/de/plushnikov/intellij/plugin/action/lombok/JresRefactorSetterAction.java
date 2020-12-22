package de.plushnikov.intellij.plugin.action.lombok;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import de.plushnikov.intellij.plugin.action.BaseRefactorAction;

public class JresRefactorSetterAction extends BaseRefactorAction {

  @Override
  protected JresRefactorSetterHandler initHandler(Project project, DataContext dataContext) {
    return new JresRefactorSetterHandler(project, dataContext);
  }
}
