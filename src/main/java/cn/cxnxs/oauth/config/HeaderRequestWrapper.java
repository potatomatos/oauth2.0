package cn.cxnxs.oauth.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-08 22:40
 **/
public class HeaderRequestWrapper extends HttpServletRequestWrapper {

    public HeaderRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private final Map<String, String> headerMap = new HashMap<>();

    public void addHeader(String name, String value) {
        this.headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (this.headerMap.containsKey(name)) {
            headerValue = this.headerMap.get(name);
        }
        return headerValue;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(this.headerMap.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (this.headerMap.containsKey(name)) {
            values = Arrays.asList(this.headerMap.get(name));
        }
        return Collections.enumeration(values);
    }

}
