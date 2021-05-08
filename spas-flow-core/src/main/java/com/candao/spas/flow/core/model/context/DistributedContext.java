package com.candao.spas.flow.core.model.context;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.candao.spas.flow.jackson.EasyJsonUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 分布式请求上下文
 */
public class DistributedContext extends ConcurrentHashMap<String, String> {

    /**
     *
     */
    private static final long serialVersionUID = -7451835910493834100L;

    private final static String NA = "_N/A_";
    private static final ThreadLocal<DistributedContext> threadLocal = InheritableThreadLocal.withInitial(() -> {
        try {
            DistributedContext distributedContext = new DistributedContext();
            distributedContext.setLogId(UUID.randomUUID().toString());
            return distributedContext;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    });

    public DistributedContext() {
        this.setLogId(UUID.randomUUID().toString());
    }

    public DistributedContext(String clientIp) {
        this.setLogId(UUID.randomUUID().toString());
        this.setClientIp(clientIp);
    }

    public static DistributedContext newInstance() {
        threadLocal.set(new DistributedContext());
        return getContext();
    }

    public static DistributedContext newInstance(String clientIp) {
        threadLocal.set(new DistributedContext(clientIp));
        return getContext();
    }

    public static DistributedContext newInstance(DistributedContext context) {
        threadLocal.set(context);
        return getContext();
    }

    public static DistributedContext getContext() {
        DistributedContext distributedContext = threadLocal.get();
        if (distributedContext == null) {
            distributedContext = new DistributedContext();
            threadLocal.set(distributedContext);
        }
        return distributedContext;
    }

    public static DistributedContext getContext(String clientIp) {
        DistributedContext distributedContext = threadLocal.get();
        if (distributedContext == null) {
            distributedContext = new DistributedContext();
            distributedContext.setClientIp(clientIp);
            threadLocal.set(distributedContext);
        }
        return distributedContext;
    }

    public static void setContext(DistributedContext context) {
        threadLocal.set(context);
    }

    public static void removeContext() {
        threadLocal.remove();
    }

    public Long getTimestamp() {
        return getValue("timestamp", Long.class);
    }

    public void setTimestamp(Long timestamp) {
        setValue("timestamp", timestamp);
    }

    public String getLogId() {
        return getValue("logId");
    }

    public void setLogId(String logId) {
        setValue("logId", logId);
    }

    public Integer getStep() {
        return getValue("step", Integer.class);
    }

    public void setStep(Integer step) {
        setValue("step", step);
    }

    public String getClientIp() {
        return getValue("clientIp");
    }

    public void setClientIp(String clientIp) {
        setValue("clientIp", clientIp);
    }

    public String getToken() {
        return getValue("token");
    }

    public void setToken(String token) {
        setValue("token", token);
    }

    public String getSessionId() {
        return getValue("sessionid");
    }

    public void setSessionId(String sessionId) {
        setValue("sessionid", sessionId);
    }

    public String getOs() {
        return getValue("os");
    }

    public void setOs(String os) {
        setValue("os", os);
    }

    public String getBody() {
        return getValue("body");
    }

    public void setBody(String body) {
        setValue("body", body);
    }

    public String getBrowser() {
        return getValue("browser");
    }

    public void setBrowser(String browser) {
        setValue("browser", browser);
    }

    public String getRequestData() {
        return get("requestData");
    }


    public int incrementStep() {
        synchronized (this) {
            Integer step = getStep();
            if (step == null) {
                step = 0;
            }
            setStep(++step);
            return step;
        }
    }

    public void setValue(String key, Object value) {
        if (value != null) {
            if (value instanceof String) {
                put(key, (String) value);
            } else {
                put(key, EasyJsonUtils.toJsonString(value));
            }
        } else {
            remove(key);
        }
    }

    public String getValue(String key) {
        String value = get(key);
        if (StringUtils.isNotBlank(value)) {
            if (NA.equals(value)) {
                return null;
            }
        }
        return value;
    }

    public <T> T getValue(String key, Class<T> clz) {
        String value = getValue(key);
        if (value != null) {
            return EasyJsonUtils.toJavaObject(value, clz);
        }
        return null;
    }

    public String getRequestURI() {
        return getValue("requestURI");
    }

    public void setRequestURI(String requestURI) {
        setValue("requestURI", requestURI);
    }

    public boolean isXxlJobLog() {
        String isXxlJobLog = getValue("isXxlJobLog");
        return StringUtils.isNotBlank(isXxlJobLog) && Boolean.parseBoolean(isXxlJobLog);
    }

    public void setXxlJobLog(boolean isXxlJobLog) {
        setValue("isXxlJobLog", isXxlJobLog);
    }

    public String getUserId() {
        return getValue("userId");
    }
    public void setUserId(String userId) {
        setValue("userId", userId);
    }

    public String getRoleId() {
        return getValue("roleId");
    }
    public void setRoleId(String roleId) {
        setValue("roleId", roleId);
    }

    public String getOperatorId(){return getValue("operatorId",String.class);}
    public void setOperatorId(String operatorId){ setValue("operatorId",operatorId);}

    public String getOperatorName() {
        return getValue("operatorName");
    }
    public void setOperatorName(String operatorName){ setValue("operatorName",operatorName);}
}