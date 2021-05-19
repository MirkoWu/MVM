package com.mirkowu.lib_base.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class InstanceFactory {

    /**
     * 实例化Mediator
     *
     * @param owner
     * @param superClass
     * @return
     */
    public static <T> T newMediator(ViewModelStoreOwner owner, Class superClass) {
        T t = newInstance(superClass, 0);
        if (t == null) throw new RuntimeException(superClass + " Mediator newInstance failed !");
        return t;
    }

    /**
     * 获取ViewModel
     *
     * @param superClass
     * @param <T>
     * @return
     */
    public static <T> T newViewModel(ViewModelStoreOwner owner, Class superClass) {
        Class cls = getGenericClass(superClass, 0);
        return (T) new ViewModelProvider(owner).get(cls);//
    }

    /**
     * 实例化Model
     *
     * @param superClass
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> T newModel(@NonNull Class superClass) {
        T t = newInstance(superClass, 1);
        if (t == null) throw new RuntimeException(superClass + " Model newInstance failed !");
        return t;
    }

    /**
     * 实例化
     *
     * @param superClass
     * @return
     */
    public static <T> T newInstance(@NonNull Class superClass, int index) {
        return newInstance(superClass, index, null);
    }

    public static <T> T newInstance(@NonNull Class superClass, int index, @Nullable Class defClass) {
        Class cls = getGenericClass(superClass, index, defClass);
        if (cls != null) {
            try {
                return (T) cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取泛型类
     *
     * @param superClass 超类
     * @param index      第几个泛型
     * @param defClass   默认实例
     * @return
     */
    public static Class getGenericClass(@NonNull Class superClass, int index, @Nullable Class defClass) {
        Class cls = defClass; //如果没有指定泛型参数，则默认
        Type type = superClass.getGenericSuperclass();
        if (type instanceof ParameterizedType && ((ParameterizedType) type).getActualTypeArguments().length > 0) {
            cls = (Class) ((ParameterizedType) type).getActualTypeArguments()[index];
        }
        return cls;
    }

    public static Class getGenericClass(@NonNull Class superClass, int index) {
        return getGenericClass(superClass, index, null);
    }
}
