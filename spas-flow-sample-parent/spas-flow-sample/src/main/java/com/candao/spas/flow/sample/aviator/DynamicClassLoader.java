package com.candao.spas.flow.sample.aviator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
 
public class DynamicClassLoader extends ClassLoader{
    
    private static final String SUFFIX = ".class";
    public String[] paths;
 
    public DynamicClassLoader(String[] paths) {
        this.paths = paths;
    }
    
    public DynamicClassLoader(ClassLoader parent,String[] paths){
        super(parent);
        this.paths = paths;
    }
 
    @SuppressWarnings("deprecation")
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException { 
        String classPath = getClassPath(className);
        if(classPath != null){
            byte[] clazz = loadClazz(classPath);
            return defineClass(clazz, 0, clazz.length); 
        }else{
            System.out.println("class is not found !");
            return null;
        }
    }
 
    public byte[] loadClazz(String classPath) { 
        try { 
            FileInputStream in = new FileInputStream(new File(classPath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while((b = in.read()) != -1){
                baos.write(b);
            }
            in.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    public String getClassPath(String className){
        for(String path : paths){
            if(className.contains(".")){
                className = className.replaceAll(".", File.separator);
            }
            String classPath = path + className + SUFFIX; 
            File classFile = new File(classPath);
            if(classFile.exists()){
                return classPath;
            }
        }
        return null;
    }
}