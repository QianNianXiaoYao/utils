package com.example.demo.utils.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author 陈坤
 * @serial 2019/2/28
 */
@Slf4j
public class BeanUtils {

    /**
     * 测试
     *
     * @param args args
     */
    public static void main(String[] args) {
        //**********************copyBean****************************
        Source source = new Source("1", "name", "22");
        Target target = new Target();
        copyBean(source, target);
        System.out.println("target = " + target);
        System.out.println("-------------------------------");

        /*
         * Source 中的age 和Target 中的age类型不一致情况处理
         * par1: 源中的值
         * par2: 源中的值类型
         * par3: 源中字段的 get set 方法名称
         */
        copyBean(source, target, (o, aClass, o1) -> {
            if (o1.equals("setAge")) {
                return Integer.valueOf(o.toString());
            }
            return o;
        });
        System.out.println("target = " + target + "\n");
        //***********************copyList***************************
        List<Source> sourceList = new ArrayList<>();
        // 初始集合
        for (int i = 0; i < 5; i++)
            sourceList.add(new Source(i + "", "name" + i, "11" + i));
        // 字段类型一样
        List<Target> targetList1 = copyList(sourceList, Target.class);
        targetList1.forEach(System.out::println);
        System.out.println("--------------------------------------");
        // 字段类型不一样
        List<Target> targetList = copyList(sourceList, Target.class, (o, aClass, o1) -> {
            if (o1.equals("setAge")) {
                return Integer.valueOf(o.toString());
            }
            return o;
        });
        targetList.forEach(System.out::println);

    }

    /**
     * 集合内bean copy
     *
     * @param sCol 待转换集合
     * @param t    欲转换类型
     * @param <S>  S
     * @param <T>  T
     * @return list
     * @author 陈坤
     */
    public static <S, T> List<T> copyList(Collection<S> sCol, Class<T> t) {
        return copyList(sCol, t, null);
    }

    /**
     * 集合内bean copy
     *
     * @param sCol      待转换集合
     * @param t         欲转换类型
     * @param converter 自定义转换器
     * @param <S>       S
     * @param <T>       T
     * @return .
     * @author 陈坤
     */
    public static <S, T> List<T> copyList(Collection<S> sCol, Class<T> t, Converter converter) {
        List<T> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sCol) && sCol.size() > 0) {
            BeanCopier beanCopier = getBeanCopier(sCol.iterator().next().getClass(), t, converter);
            for (S s : sCol) {
                T t1 = null;
                try {
                    t1 = t.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建实体对象失败", e);
                }
                beanCopier.copy(s, t1, converter);
                list.add(t1);
            }
        }
        return list;
    }

    /**
     * bean copy
     *
     * @param s   原类型
     * @param t   欲转换类型
     * @param <S> S
     * @param <T> T
     * @author 陈坤
     */
    public static <S, T> void copyBean(S s, T t) {
        copyBean(s, t, null);
    }


    /**
     * bean copy
     *
     * @param s         原类型
     * @param t         欲转换类型
     * @param converter 自定义类型转换条件
     * @param <S>       S
     * @param <T>       T
     * @author 陈坤
     */
    public static <S, T> void copyBean(S s, T t, Converter converter) {
        if (Objects.isNull(s) && Objects.isNull(t)) {
            log.error("源对象或被转换对象不能为Null");
            return;
        }
        getBeanCopier(s.getClass(), t.getClass(), converter).copy(s, t, converter);
    }

    /**
     * 获取创建的 BeanCopier 转换
     *
     * @param s         原类型
     * @param t         欲转换类型
     * @param converter 自定义类型转换条件
     * @param <S>       S
     * @param <T>       T
     * @return BeanCopier
     * @author 陈坤
     */
    private static <S, T> BeanCopier getBeanCopier(Class<S> s, Class<T> t, Converter converter) {
        return converter == null ? BeanCopier.create(s, t, false) : BeanCopier.create(s, t, true);
    }

    /**
     * 将list中的对象转成指定的对象
     * <p>
     * set方式
     *
     * @param c   list
     * @param f   Func
     * @param <T> 欲转换对象
     * @param <R> 转换后对象
     * @return R
     * @author 陈坤
     */
    public static <T, R> Collection<R> copyList(Collection<T> c, Function<T, R> f) {
        return c.stream().map(f).collect(Collectors.toList());
    }

    /**
     * 将 传入实体对象转成指定对象
     * <p>
     * set方式
     *
     * @param bean bean
     * @param f    Func
     * @param <T>  欲转换对象
     * @param <R>  转换后对象
     * @return R
     * @author 陈坤
     */
    public static <T, R> R copyBean(T bean, Function<T, R> f) {
        return f.apply(bean);
    }
}
