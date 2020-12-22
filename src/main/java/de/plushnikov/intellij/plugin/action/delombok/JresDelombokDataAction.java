package de.plushnikov.intellij.plugin.action.delombok;

import com.intellij.openapi.application.ApplicationManager;
import de.plushnikov.intellij.plugin.processor.clazz.JresDataProcessor;
import org.jetbrains.annotations.NotNull;

public class JresDelombokDataAction extends AbstractDelombokAction {

  @Override
  @NotNull
  protected DelombokHandler createHandler() {
    return new DelombokHandler(ApplicationManager.getApplication().getService(JresDataProcessor.class));
  }
}
