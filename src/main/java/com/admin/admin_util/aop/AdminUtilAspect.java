package com.admin.admin_util.aop;

import com.admin.admin_util.annotation.AdminUtilAnnotation;
import com.admin.admin_util.exception.NoAuthorityException;
import com.admin.admin_util.exception.NotLoginException;
import com.admin.admin_util.util.JwtTokenUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 陈群矜
 */
@Aspect
public class AdminUtilAspect {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String ip;

    private int port;

    public AdminUtilAspect(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Pointcut("@annotation(com.admin.admin_util.annotation.AdminUtilAnnotation)")
    public void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();
        AdminUtilAnnotation annotation = method.getAnnotation(AdminUtilAnnotation.class);
        String resourceId = annotation.value();
        String userNo = getUserNo();
        Object val = null;
        if (StringUtils.isBlank(userNo)) {
            throw new NotLoginException();
        }
        try {
            if (hasAuthority(userNo, resourceId)) {
                val = pjp.proceed();
            } else {
                throw new NoAuthorityException();
            }
        } catch (NoAuthorityException exception) {
            throw exception;
        } catch (Throwable exception) {
            throw new RuntimeException();
        }
        return val;
    }

    private boolean hasAuthority(String userNo, String resourceId) throws URISyntaxException {
        String url = "http://" + this.ip + ":" + this.port + "/api/authority/checkAuthorityWithUserNoAndResourceId" +
                "?" + "resourceId" + "=" + resourceId +
                "&" + "userNo" + "=" + userNo;
        RequestEntity<byte[]> entity = new RequestEntity<>(null, null, HttpMethod.GET, new URI(url));
        ResponseEntity<byte[]> exchange = this.restTemplate.exchange(entity, byte[].class);
        byte[] bytes = exchange.getBody();
        if (bytes == null) {
            return false;
        }
        JSONObject result = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8));
        Object data = result.get("data");
        if (data instanceof Boolean) {
            return (boolean) data;
        }
        return false;
    }

    private String getUserNo() {
        HttpServletRequest request = ((ServletRequestAttributes) (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (!jwtTokenUtil.validateToken(token)) {
            return null;
        }
        return jwtTokenUtil.getUserNoFromToken(token);
    }

}
