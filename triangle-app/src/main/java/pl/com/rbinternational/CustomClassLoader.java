package pl.com.rbinternational;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class CustomClassLoader extends ClassLoader {

    private final Map<String, byte[]> classMap = new ConcurrentHashMap<>();
    private final String CLASS_EXTENSION = ".class";
    private final String JAR_EXTENSION = ".jar";
    private final String classPath;

    /**
     * @param classPath Path to the selected class
     */
    public CustomClassLoader(String classPath) {
        if (classPath.endsWith(File.separator)) {
            this.classPath = classPath;
        } else {
            this.classPath = classPath + File.separator;
        }
    }

    /**
     *
     * It looks for internal jar files and loads the classes found in the main jar file
     *
     * @param classPath The path to the main jar file
     */
    public void findJarInJar(String classPath) throws IOException {
        JarFile jf = new JarFile(classPath);
        Iterator<JarEntry> it = jf.stream().iterator();

        while(it.hasNext()) {
            JarEntry je = it.next();
            InputStream in = jf.getInputStream(je);

            if (je.getName().endsWith(JAR_EXTENSION)) {
                loadJarFile(je.getName());
            } else if (je.getName().endsWith(CLASS_EXTENSION)) {
                prepareClass(je.getName(), in);
            }
        }
    }

    /**
     *
     *  Loads the class with the given annotation
     *
     * @param annotationClass The class we are looking for
     * @return Returns the class from the map if it matches the one we are looking for
     */
    public Class<?> loadByAnnotation(Class<?> annotationClass) throws ClassNotFoundException {
        Class<?> foundedClass;
        for (String s : classMap.keySet()) {
            foundedClass = loadClass(s);
            if (annotationCheck(foundedClass, annotationClass)) {
                return foundedClass;
            }
        }
        return null;
    }

    @Override
    protected Class<?> findClass(String name) {
        try {
            byte[] result = getClass(name);
            return defineClass(name, result, 0, result.length);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     *
     * Checks if the found class is the one I am looking for
     *
     * @param foundedClass The class that was found
     * @param annotationClass The class we are looking for
     * @return whether the annotation exists
     */
    private boolean annotationCheck(Class<?> foundedClass, Class<?> annotationClass) {
        boolean isExist = false;
        for (Annotation anon  : foundedClass.getAnnotations()) {
            isExist = anon.toString().contains(annotationClass.getName());
        }
        return isExist;
    }

    /**
     *
     * Returns the class with the given name from the map, or null if not found
     *
     * @param className The name of the class we are looking for
     */
    private byte[] getClass(String className) {
        return classMap.getOrDefault(className, null);
    }

    /**
     *
     * Scans the passed InputStream looking for .class files
     *
     * @param jar Converted jar file to JarInputStream
     */
    private void readJAR(JarInputStream jar) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        while (true) {
            JarEntry je = jar.getNextJarEntry();
            if (je == null) {
                break;
            }
            String name = je.getName();
            if (name.endsWith(CLASS_EXTENSION)) {
                prepareClass(name, jar);
            }
        }
    }

    /**
     *
     * Adds the given class to the map
     *
     * @param className The name of the class we want to add to the class map
     * @param byteCode byte code of the class
     */
    private void addClass(String className, byte[] byteCode) {
        if (!classMap.containsKey(className)) {
            classMap.put(className, byteCode);
        }
    }

    /**
     *
     * Converts the class's byte array and passes it to the next method which will add the converted data to the map
     *
     * @param className class name
     * @param input InputStream of the class
     * @param baos ByteArrayOutputStream of the class
     */
    private void buffer(String className, InputStream input, ByteArrayOutputStream baos) throws IOException {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int bytesNumRead;
        while ((bytesNumRead = input.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesNumRead);
        }
        addClass(className, baos.toByteArray());
    }

    /**
     *
     * Converting jar file to input stream and passes to the method "readJar"
     *
     * @param jarPath path to jar file
     */
    private void loadJarFile(String jarPath) throws IOException {
        try (JarInputStream jar = new JarInputStream(Objects.requireNonNull(CustomClassLoader.class.getClassLoader().getResourceAsStream(jarPath)))) {
            readJAR(jar);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * Converts the class path to a name readable by ClassLoader
     *
     * @param name class name
     * @param in class InputStream
     */
    private void prepareClass(String name, InputStream in) throws IOException {
        String className = name.replace("\\", ".")
                .replace("/", ".")
                .replace(CLASS_EXTENSION, "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        buffer(className, in, baos);
    }
}