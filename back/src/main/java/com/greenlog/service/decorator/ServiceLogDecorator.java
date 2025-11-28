/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.greenlog.service.decorator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 *
 * @author Kayqu
 */
public class ServiceLogDecorator implements InvocationHandler {

    private final Object target;
    private final String serviceName;

    private ServiceLogDecorator(Object target) {
        this.target = target;
        this.serviceName = target.getClass().getSimpleName();
    }

    // Factory Method para criar o proxy (o objeto decorado)
    public static <T> T decorate(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new ServiceLogDecorator(target)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        System.out.printf("LOG DECORATOR >> [%s] Chamada: %s(%s)\n", 
            serviceName, 
            method.getName(), 
            args == null ? "Sem argumentos" : Arrays.toString(args));

        try {
            // Chama o método original do Service
            Object result = method.invoke(target, args);
            long endTime = System.currentTimeMillis();
            
            System.out.printf("LOG DECORATOR << [%s] Sucesso. Tempo: %dms\n", 
                serviceName, 
                (endTime - startTime));
            
            return result;
        } catch (InvocationTargetException e) {
            long endTime = System.currentTimeMillis();
            
            System.err.printf("LOG DECORATOR << [%s] Falha em %dms. Exceção: %s\n", 
                serviceName, 
                (endTime - startTime), 
                e.getTargetException().getMessage());
                
            throw e.getTargetException();
        }
    }
}