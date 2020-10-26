package de.plushnikov.intellij.plugin.processor.clazz;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import de.plushnikov.intellij.plugin.LombokClassNames;
import de.plushnikov.intellij.plugin.problem.ProblemBuilder;
import de.plushnikov.intellij.plugin.problem.ProblemEmptyBuilder;
import de.plushnikov.intellij.plugin.processor.LombokPsiElementUsage;
import de.plushnikov.intellij.plugin.processor.clazz.constructor.NoArgsConstructorProcessor;
import de.plushnikov.intellij.plugin.processor.clazz.constructor.RequiredArgsConstructorProcessor;
import de.plushnikov.intellij.plugin.util.PsiAnnotationSearchUtil;
import de.plushnikov.intellij.plugin.util.PsiAnnotationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * @author Plushnikov Michail
 */
public class JresDataProcessor extends AbstractClassProcessor {

  public JresDataProcessor() {
    super(PsiMethod.class, LombokClassNames.JRES_DATA);
  }

  private JresToStringProcessor getToStringProcessor() {
    return ApplicationManager.getApplication().getService(JresToStringProcessor.class);
  }

  private NoArgsConstructorProcessor getNoArgsConstructorProcessor() {
    return ApplicationManager.getApplication().getService(NoArgsConstructorProcessor.class);
  }

  private JresGetterProcessor getGetterProcessor() {
    return ApplicationManager.getApplication().getService(JresGetterProcessor.class);
  }

  private JresSetterProcessor getSetterProcessor() {
    return ApplicationManager.getApplication().getService(JresSetterProcessor.class);
  }

  private JresEqualsAndHashCodeProcessor getEqualsAndHashCodeProcessor() {
    return ApplicationManager.getApplication().getService(JresEqualsAndHashCodeProcessor.class);
  }

  private RequiredArgsConstructorProcessor getRequiredArgsConstructorProcessor() {
    return ApplicationManager.getApplication().getService(RequiredArgsConstructorProcessor.class);
  }

  @Override
  protected boolean validate(@NotNull PsiAnnotation psiAnnotation, @NotNull PsiClass psiClass, @NotNull ProblemBuilder builder) {
    final PsiAnnotation equalsAndHashCodeAnnotation = PsiAnnotationSearchUtil.findAnnotation(psiClass, LombokClassNames.JRES_EQUALS_AND_HASHCODE);
    if (null == equalsAndHashCodeAnnotation) {
      getEqualsAndHashCodeProcessor().validateCallSuperParamExtern(psiAnnotation, psiClass, builder);
    }

    final String staticName = PsiAnnotationUtil.getStringAnnotationValue(psiAnnotation, "staticConstructor");
    if (shouldGenerateRequiredArgsConstructor(psiClass, staticName)) {
      getRequiredArgsConstructorProcessor().validateBaseClassConstructor(psiClass, builder);
    }

    return validateAnnotationOnRightType(psiClass, builder);
  }

  private boolean validateAnnotationOnRightType(@NotNull PsiClass psiClass, @NotNull ProblemBuilder builder) {
    boolean result = true;
    if (psiClass.isAnnotationType() || psiClass.isInterface() || psiClass.isEnum()) {
      builder.addError("'@Data' is only supported on a class type");
      result = false;
    }
    return result;
  }

  protected void generatePsiElements(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<? super PsiElement> target) {
    if (PsiAnnotationSearchUtil.isNotAnnotatedWith(psiClass, LombokClassNames.JRES_GETTER)) {
      target.addAll(getGetterProcessor().createFieldGetters(psiClass, PsiModifier.PUBLIC));
    }
    if (PsiAnnotationSearchUtil.isNotAnnotatedWith(psiClass, LombokClassNames.JRES_SETTER)) {
      target.addAll(getSetterProcessor().createFieldSetters(psiClass, PsiModifier.PUBLIC));
    }
    if (PsiAnnotationSearchUtil.isNotAnnotatedWith(psiClass, LombokClassNames.JRES_EQUALS_AND_HASHCODE)) {
      target.addAll(getEqualsAndHashCodeProcessor().createEqualAndHashCode(psiClass, psiAnnotation));
    }
    if (PsiAnnotationSearchUtil.isNotAnnotatedWith(psiClass, LombokClassNames.JRES_TO_STRING)) {
      target.addAll(getToStringProcessor().createToStringMethod(psiClass, psiAnnotation));
    }

    final boolean hasConstructorWithoutParamaters;
    final String staticName = PsiAnnotationUtil.getStringAnnotationValue(psiAnnotation, "staticConstructor");
    if (shouldGenerateRequiredArgsConstructor(psiClass, staticName)) {
      target.addAll(getRequiredArgsConstructorProcessor().createRequiredArgsConstructor(psiClass, PsiModifier.PUBLIC, psiAnnotation, staticName, true));
      // if there are no required field, it will already have a default constructor without parameters
      hasConstructorWithoutParamaters = getRequiredArgsConstructorProcessor().getRequiredFields(psiClass).isEmpty();
    } else {
      hasConstructorWithoutParamaters = false;
    }

    if (!hasConstructorWithoutParamaters && shouldGenerateExtraNoArgsConstructor(psiClass)) {
      target.addAll(getNoArgsConstructorProcessor().createNoArgsConstructor(psiClass, PsiModifier.PRIVATE, psiAnnotation, true));
    }
  }

  private boolean shouldGenerateRequiredArgsConstructor(@NotNull PsiClass psiClass, @Nullable String staticName) {
    boolean result = false;
    // create required constructor only if there are no other constructor annotations
    @SuppressWarnings("unchecked") final boolean notAnnotatedWith = PsiAnnotationSearchUtil.isNotAnnotatedWith(psiClass,
      LombokClassNames.NO_ARGS_CONSTRUCTOR,
      LombokClassNames.REQUIRED_ARGS_CONSTRUCTOR,
      LombokClassNames.ALL_ARGS_CONSTRUCTOR,
      LombokClassNames.BUILDER,
      LombokClassNames.SUPER_BUILDER);
    if (notAnnotatedWith) {
      final RequiredArgsConstructorProcessor requiredArgsConstructorProcessor = getRequiredArgsConstructorProcessor();
      final Collection<PsiField> requiredFields = requiredArgsConstructorProcessor.getRequiredFields(psiClass);

      result = requiredArgsConstructorProcessor.validateIsConstructorNotDefined(
        psiClass, staticName, requiredFields, ProblemEmptyBuilder.getInstance());
    }
    return result;
  }

  @Override
  public LombokPsiElementUsage checkFieldUsage(@NotNull PsiField psiField, @NotNull PsiAnnotation psiAnnotation) {
    return LombokPsiElementUsage.READ_WRITE;
  }
}
