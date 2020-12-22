package de.plushnikov.intellij.plugin.action.delombok;

import com.intellij.openapi.application.ApplicationManager;
import de.plushnikov.intellij.plugin.processor.clazz.JresSetterProcessor;
import de.plushnikov.intellij.plugin.processor.field.JresSetterFieldProcessor;
import org.jetbrains.annotations.NotNull;

public class JresDelombokSetterAction extends AbstractDelombokAction {
  @Override
  @NotNull
  protected DelombokHandler createHandler() {
    return new DelombokHandler(
      ApplicationManager.getApplication().getService(JresSetterProcessor.class),
      ApplicationManager.getApplication().getService(JresSetterFieldProcessor.class));
  }
}
