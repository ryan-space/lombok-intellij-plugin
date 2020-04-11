package de.plushnikov.intellij.plugin.action.lombok;

import com.hundsun.jres.studio.annotation.*;
import com.intellij.psi.PsiClass;
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

    removeDefaultAnnotation(psiClass, JRESGetter.class);
    removeDefaultAnnotation(psiClass, JRESSetter.class);
    removeDefaultAnnotation(psiClass, JRESToString.class);
    removeDefaultAnnotation(psiClass, JRESEqualsAndHashCode.class);

    addAnnotation(psiClass, JRESData.class);
  }

}
