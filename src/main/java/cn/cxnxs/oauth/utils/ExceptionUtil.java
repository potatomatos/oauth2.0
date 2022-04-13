package cn.cxnxs.oauth.utils;

/**
 * <p>Title: ExceptionUtil</p>
 *
 * <p>Description:异常工具类 </p>
 *
 * @author mengjinyuan
 * @date 2019年4月8日
 */
public class ExceptionUtil {

    //是否是debug模式
    public static boolean debug = true;

    /**
     * 获取异常堆栈信息
     *
     * @param exception
     * @return
     */
    public static String getTrack(Exception exception) {

        if (null == exception) {
            return "";
        }
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        if (debug) {
            return exception.getMessage() + "<br/>" + getTrack(stackTraceElements);
        } else {
            return exception.getMessage();
        }
    }

    /**
     * 获取异常堆栈信息
     *
     * @param stackTraceElements
     * @return
     */
    public static String getTrack(StackTraceElement[] stackTraceElements) {
        StringBuffer sbf = new StringBuffer();
        for (StackTraceElement e : stackTraceElements) {
            if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append("<br/>");
            }
            sbf.append(java.text.MessageFormat.format("    at {0}.{1}({2}) {3}", e.getClassName(), e.getMethodName(), e.getFileName(),
                    e.getLineNumber()));
        }
        return sbf.toString();
    }
}
