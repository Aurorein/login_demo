package com.haoweilai.demo1.filter;

import com.alibaba.fastjson.JSON;
import com.haoweilai.demo1.common.constants.RedisConstants;
import com.haoweilai.demo1.exceptions.LoginException;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.service.IStudentService;
import com.haoweilai.demo1.util.*;
import com.haoweilai.demo1.vo.UserContextUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginFilter", urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Autowired
    UserContextUtils userContextUtils;

    @Autowired
    RedisRepository redisRepository;

    @Autowired
    IStudentService studentService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String accessToken = TokenUtil.getAccessToken(request);
        if(StringUtils.isBlank(accessToken)) {
            // 拦截，尝试登录
            throw new LoginException();
        }

        boolean access_exists = redisRepository.exists(RedisConstants.RedisKey.ACCESS_TOKEN_PREFIX + MD5Util.md5(accessToken));
        if(access_exists) {
            // 直接放行
            filterChain.doFilter(servletRequest, servletResponse);
        }
        // accessToken过期，使用refreshToken
        String refreshToken = TokenUtil.getRefreshToken(request);
        if(StringUtils.isBlank(refreshToken)) {
            // 重新登录
            throw new LoginException();
        }
        String origin  = request.getHeader("Origin");
        boolean exists = redisRepository.exists(RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshToken));
        if(exists) {
            String userId = parseToken(refreshToken);
            String accessToken1 = JwtUtils.generateAccessToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES);
            String refreshToken1 = JwtUtils.generateRefreshToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);

            // refreshToken前缀 + md5加密的token作为key，value是student对象
            String redisRefreshKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshToken);
            redisRepository.setExpire(redisRefreshKey, refreshToken, 60 * 60 * 24 * 2);

            // accessToken前缀 + md5加密的token作为key，value是student对象
            String redisAccessKey = RedisConstants.RedisKey.ACCESS_TOKEN_PREFIX + MD5Util.md5(accessToken);
            redisRepository.setExpire(redisAccessKey, JSON.toJSONString(userContextUtils.getStudent()), 60 * 10);
        }

        // accessToken没过期，正常放行
        filterChain.doFilter(request, response);
    }

    private String parseToken(String token) {
        //对token进行解析
        String userId = JwtUtils.getUsernameFromToken(token, RsaUtils.publicKey);

        userContextUtils.setUserId(userId);
        Student student = studentService.getById(userId);
        userContextUtils.setStudent(student);

        return userId;
    }
}
