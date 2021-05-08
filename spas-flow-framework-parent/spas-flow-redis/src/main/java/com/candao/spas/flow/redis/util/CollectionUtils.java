package com.candao.spas.flow.redis.util;

import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CollectionUtils {
    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 等分List
     *
     * @param source 源数据
     * @param size   等分数
     * @param <T>    泛型
     * @return 等分好的List集合
     */
    public static <T> List<List<T>> partition(List<T> source, int size) {
        return ListUtils.partition(source, size);
    }

    /**
     * 切割 List 可以平均的切割 List 集合
     *
     * @param source 源数据
     * @param n      切割数
     * @return 切割好的List集合
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(source) || n < 1) {
            return result;
        }
        // (先计算出余数)
        int remaider = source.size() % n;
        // 然后是商
        int number = source.size() / n;
        // 偏移量
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result;
    }

    public static <T> void averageAssignConsumer(List<T> source, int n, Consumer<List<T>> consumer) {
        averageAssign(source, n).forEach(consumer);
    }

    public static <T> void partitionConsumer(List<T> source, int size, Consumer<List<T>> consumer) {
        partition(source, size).forEach(consumer);
    }
}