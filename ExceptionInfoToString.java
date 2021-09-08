package com.zx.workflow.comon;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author a
 */
public class ExceptionInfoToString {
    /**
     * 将异常详细信息输出到字符串，便于打印到控制台
     * @param e 异常
     * @return 异常详细信息
     */
    public static String exceptionToString(Exception e){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        e.printStackTrace(printStream);
        return new String(out.toByteArray());
    }
}
