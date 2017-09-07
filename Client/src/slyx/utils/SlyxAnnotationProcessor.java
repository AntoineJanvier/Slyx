package slyx.utils;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Antoine Janvier
 * on 07/09/17.
 */

public class SlyxAnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init (ProcessingEnvironment processingEnv) {
        super.init( processingEnv );
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(SlyxAnnotation.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SlyxAnnotation.class)) {
            if (element instanceof SlyxAnnotation) {
                SlyxAnnotation slyxAnnotation = (SlyxAnnotation) element;
                System.out.println(slyxAnnotation.todo());
            }
        }
        // Claiming that annotations have been processed by this processor
        return true;
    }

}
