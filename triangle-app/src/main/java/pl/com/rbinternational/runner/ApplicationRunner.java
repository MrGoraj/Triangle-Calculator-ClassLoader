package pl.com.rbinternational.runner;

import pl.com.rbinternational.Calculator;
import pl.com.rbinternational.CustomClassLoader;

public class ApplicationRunner {
    public void run(Class<?> appClass) {
        final String START_METHOD = "start";
        String classPath = appClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        CustomClassLoader classLoader = new CustomClassLoader(classPath);
        try {
            classLoader.findJarInJar(classPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class<?> calcClass = classLoader.loadByAnnotation(appClass);
            Object calc = calcClass.getDeclaredConstructor().newInstance();
            calcClass.getMethod(START_METHOD).invoke(calc);
        } catch (Exception e) {
            e.getCause().printStackTrace();
        }
    }
}