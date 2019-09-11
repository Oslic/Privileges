package ru.fleetcor.util.retry;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import ru.fleetcor.util.retry.Retry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by Ivan.Zhirnov on 08.11.2018.
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation iTestAnnotation, Class aClass, Constructor constructor, Method method) {
        iTestAnnotation.setRetryAnalyzer(Retry.class);
    }
}
