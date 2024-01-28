package com.java2e.martin.common.data.dynamic.datasource;

import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.concurrent.Callable;

/**
 * @author: liangcan
 * @version: 1.0
 * @date: 2022/12/3 12:33
 * @describtion: SqlHelperDsContextHolder
 */
@Slf4j
public class SqlHelperDsContextHolder {
    private static final ThreadLocal<Stack<String>> CONTEXTHOLDER = new ThreadLocal<>();

    /**
     * 切换数据源
     *
     * @param logicName 数据源名称，null代表默认数据源
     */
    public static void switchTo(String logicName) {
        Stack<String> switchStack = CONTEXTHOLDER.get();
        if (switchStack == null) {
            switchStack = new Stack<>();
            CONTEXTHOLDER.set(switchStack);
        }
        switchStack.push(logicName);
    }

    /**
     * 退出当前数据源，会返回到上一次设置的值
     */
    public static void backToLast() {
        Stack<String> switchStack = CONTEXTHOLDER.get();
        if (switchStack != null && !switchStack.empty()) {
            switchStack.pop();
        }
    }

    /**
     * Clear.
     */
    public static void clear() {
        log.debug("移除线程数据源");
        CONTEXTHOLDER.remove();
    }


    /**
     * Get string.
     *
     * @return the string
     */
    public static String get() {
        Stack<String> switchStack = CONTEXTHOLDER.get();
        if (switchStack != null && !switchStack.empty()) {
            return switchStack.peek();
        }
        return null;
    }

    public static <R> R executeOn(String datasourceName, Callable<R> callable) {
        switchTo(datasourceName);
        try {
            R re = callable.call();
            return re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            backToLast();
        }
    }


}