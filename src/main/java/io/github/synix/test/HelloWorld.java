package io.github.synix.test;

/**
 * java命令的2种形式: 选项, 主类名(或者JAR文件名),main方法参数
 * java [-options] class [args]
 * java [-options] -jar jarfile [args]
 *
 * 选项以-开头,分为标准选项和非标准选项(以-X开头,其中高级选项以-XX开头)
 * 常见选项: -cp(类路径),-Dproperty=value(system property),-Xms(初始堆大小),-Xmx(最大堆大小),-Xss(线程栈大小)
 */
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, world!");
    }
}
