package de.plushnikov.intellij.plugin.action.delombok;

import com.intellij.openapi.application.ApplicationManager;
import de.plushnikov.intellij.plugin.processor.clazz.JresGetterProcessor;
import de.plushnikov.intellij.plugin.processor.field.JresGetterFieldProcessor;
import org.jetbrains.annotations.NotNull;

public class JresDelombokGetterAction extends AbstractDelombokAction {

  @Override
  @NotNull
  protected DelombokHandler createHandler() {
    return new DelombokHandler(
      ApplicationManager.getApplication().getService(JresGetterProcessor.class),
      ApplicationManager.getApplication().getService(JresGetterFieldProcessor.class));
  }
}
