package com.googlecode.aviator.runtime.function.seq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.googlecode.aviator.exception.ExpressionRuntimeException;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;


/**
 * filter(seq,predicate) to filter seq by predicate
 * 
 * @author dennis
 * 
 */
public class SeqFilterFunction implements AviatorFunction {

    @SuppressWarnings("unchecked")
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException(getName() + " has only two arguments");
        }
        Object first = args[0].getValue(env);
        AviatorFunction fun = FunctionUtils.getFunction(1, args, env, 1);
        if (fun == null) {
            throw new ExpressionRuntimeException("There is no function named " + ((AviatorJavaType) args[1]).getName());
        }
        if (first == null) {
            throw new NullPointerException("null seq");
        }
        Class<?> clazz = first.getClass();

        if (Collection.class.isAssignableFrom(clazz)) {
            Collection result = null;
            try {
                result = (Collection) clazz.newInstance();
            }
            catch (Throwable t) {
                // ignore
                result = new ArrayList();
            }
            for (Object obj : (Collection<?>) first) {
                if (fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
                    result.add(obj);
                }
            }
            return new AviatorRuntimeJavaType(result);
        }
        else if (clazz.isArray()) {
            Object[] seq = (Object[]) first;
            List result = new ArrayList();
            for (Object obj : seq) {
                if (fun.call(env, new AviatorRuntimeJavaType(obj)).booleanValue(env)) {
                    result.add(obj);
                }
            }
            return new AviatorRuntimeJavaType(result.toArray());
        }
        else {
            throw new IllegalArgumentException(args[0].desc(env) + " is not a seq");
        }

    }


    public String getName() {
        return "filter";
    }

}
