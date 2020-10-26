package de.plushnikov.intellij.plugin.action.lombok;

import com.intellij.psi.PsiClass;
import de.plushnikov.intellij.plugin.LombokClassNames;
import org.jetbrains.annotations.NotNull;

public class JresLombokDataHandler extends BaseLombokHandler {

  private final BaseLombokHandler[] handlers;

  public JresLombokDataHandler() {
    handlers = new BaseLombokHandler[]{
      new LombokGetterHandler(), new LombokSetterHandler(),
      new LombokToStringHandler(), new LombokEqualsAndHashcodeHandler()};
  }

  protected void processClass(@NotNull PsiClass psiClass) {
    for (BaseLombokHandler handler : handlers) {
      handler.processClass(psiClass);
    }

    removeDefaultAnnotation(psiClass, LombokClassNames.JRES_GETTER);
    removeDefaultAnnotation(psiClass, LombokClassNames.JRES_SETTER);
    removeDefaultAnnotation(psiClass, LombokClassNames.JRES_TO_STRING);
    removeDefaultAnnotation(psiClass, LombokClassNames.JRES_EQUALS_AND_HASHCODE);
    addAnnotation(psiClass, LombokClassNames.JRES_DATA);
  }

}
