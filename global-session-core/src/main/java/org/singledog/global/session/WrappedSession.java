package org.singledog.global.session;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("deprecation")
public class WrappedSession extends WrappedSessionMeta implements HttpSession {
    @JsonIgnore
    private static final long serialVersionUID = -718312180392347970L;
    @JsonIgnore
    private ServletContext servletContext;
    @JsonIgnore
    private HttpSessionContext sessionContext;

    public WrappedSession() {
    }

    public WrappedSession(WrappedSessionMeta meta) {
        BeanUtils.copyProperties(meta, this, "serialVersionUID");
    }

    public WrappedSessionMeta extractMeta() {
        WrappedSessionMeta meta = new WrappedSessionMeta();
        BeanUtils.copyProperties(this, meta, "servletContext", "sessionContext", "serialVersionUID");
        return meta;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    private HashOperations<String, String, Serializable> getHashOperations() {
        return SpringContextUtil.getBean("redisTemplate", RedisTemplate.class).opsForHash();
    }

    protected String getCacheKey() {
        return CacheKeyConstants.H_SESSION + super.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return super.getLastAccessTime();
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return this.sessionContext;
    }

    public void setSessionContext(HttpSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public Object getAttribute(String name) {
        return this.getHashOperations().get(this.getCacheKey(), name);
    }

    @Override
    public Object getValue(String name) {
        return this.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> names = getHashOperations().keys(getCacheKey());
        final Iterator<String> iterator = names.iterator();
        return new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }

    @Override
    public String[] getValueNames() {
        return getHashOperations().keys(getCacheKey()).toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value instanceof Serializable)
            this.getHashOperations().put(this.getCacheKey(), name, (Serializable) value);
        else throw new SerializationException("set sesstion attribute [" + name + "] error ! value not serializable !");
    }

    @Override
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.getHashOperations().delete(getCacheKey(), name);
    }

    @Override
    public void removeValue(String name) {
        this.removeAttribute(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate() {
        SpringContextUtil.getBeanByType(RedisTemplate.class).delete(this.getCacheKey());
    }
}

