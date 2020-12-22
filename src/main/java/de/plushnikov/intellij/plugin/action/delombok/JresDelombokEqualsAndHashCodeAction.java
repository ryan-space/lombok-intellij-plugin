package de.plushnikov.intellij.plugin.action.delombok;

import com.intellij.openapi.application.ApplicationManager;
import de.plushnikov.intellij.plugin.processor.clazz.JresEqualsAndHashCodeProcessor;

public class JresDelombokEqualsAndHashCodeAction extends AbstractDelombokAction {

  @Override
  protected DelombokHandler createHandler() {
    return new DelombokHandler(ApplicationManager.getApplication().getService(JresEqualsAndHashCodeProcessor.class));
  }
}
