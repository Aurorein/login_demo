package com.haoweilai.demo1.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoweilai.demo1.common.constants.AccountType;
import com.haoweilai.demo1.common.constants.LoginConstants;
import com.haoweilai.demo1.common.constants.OriginType;
import com.haoweilai.demo1.common.constants.RedisConstants;
import com.haoweilai.demo1.exceptions.*;
import com.haoweilai.demo1.model.Student;
import com.haoweilai.demo1.dao.StudentMapper;
import com.haoweilai.demo1.service.IStudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoweilai.demo1.service.ITokenRedisService;
import com.haoweilai.demo1.util.*;
import com.haoweilai.demo1.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author cuixuening
 * @since 2024-12-18
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ITokenRedisService tokenRedisService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Override
    public LoginResp login(LoginReq loginReq) {
        String username = loginReq.getUsername();

        String ip = IpUtil.getIpAddr(request);
        Student stu;
        if (AccountType.PASSWORD.equals(loginReq.getAccountType())) {
            // 1. 账号密码登录验证
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("username", username);
            stu = studentMapper.selectOne(studentQueryWrapper);

            String password = MD5Util.md5(loginReq.getPassword()).toLowerCase();
            if(stu == null) {
                // 还未注册
                throw new LoginException();
            }
//            if(tokenRedisService.isLocked(String.valueOf(stu.getId()), ip)) {
//                // 返回已被锁定
//
//            }
//            if(password != stu.getPassword()) {
//                // 密码错误
//                // 增加错误次数
//
//                boolean isLocked = tokenRedisService.increLocked(String.valueOf(stu.getId()), ip);
//                if(isLocked) {
//                    // 添加到黑名单了，返回已被锁定
//
//                }
//            }
            boolean isValided = validateLogin(String.valueOf(stu.getId()), password.equals(stu.getPassword()));
            if(!isValided) {
                // 登录失败
                throw new LockedException();
            }

        } else {
            // 2. 邮箱登录
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("email", username);
            stu = studentMapper.selectOne(studentQueryWrapper);

            // 从redis中获取code
            String code = tokenRedisService.getCode(loginReq.getUsername());
            if(StringUtils.isBlank(code)) {
                // 验证码失效，重新获取验证码
                throw new EmailCodeExpireException();
            } else {
                if(!code.equals(loginReq.getPassword())) {
                    // 登录失败
                    // 返回验证码不对
                    throw new EmailCodeException();
                }
            }
        };

        String userId = String.valueOf(stu.getId());
        String origin = String.valueOf(request.getHeader("origin"));
        // 登录认证成功，生成accessToken和refreshToken，其中refreshToken存到redis中
        String accessToken = JwtUtils.generateAccessToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES);
        String refreshToken = JwtUtils.generateRefreshToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);

        if(redisRepository.exists(refreshToken)) {
            // 同一端登录
            // 需要踢人下线
            kickOut(userId, origin);
        }
        // refreshToken前缀 + md5加密的token作为key，value是student对象
        String redisRefreshKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshToken);
        redisRepository.setExpire(redisRefreshKey, refreshToken, 60 * 60 * 24 * 2);

        // accessToken前缀 + md5加密的token作为key，value是student对象
        String redisAccessKey = RedisConstants.RedisKey.ACCESS_TOKEN_PREFIX + MD5Util.md5(accessToken);
        redisRepository.setExpire(redisAccessKey, JSON.toJSONString(stu), 60 * 10);

//        String sessionIp = tokenRedisService.getSessionIp(token, loginReq.getDeviceType());
//        if(StringUtils.isNotBlank(sessionIp)) {
//            // 踢人下线
//        }
//        tokenRedisService.addSession(loginReq.getDeviceType(), token, ip);
        LoginResp loginResp = new LoginResp();
        loginResp.setAccessToken(accessToken);
        loginResp.setRefreshToken(refreshToken);
        loginResp.setUserId(Long.parseLong(userId));
        return loginResp;
    }

    private boolean validateLogin(String userId, boolean flag) {
        String accountKey = LoginConstants.COUNT + userId;
        String lockedKey = LoginConstants.LOCKED + userId;
        // 先判断有没有被锁定
        if(redisRepository.exists(lockedKey)) {
            long remainTime = redisRepository.getExpire(lockedKey, TimeUnit.SECONDS);
            // 还没有解锁，拒绝登录
            if(remainTime > 0) {
                throw new LockedException();
            } else {
                redisRepository.del(lockedKey);
            }
        }

        // 没有被锁定
        if(flag) {
           // 登录成功，重置计数器
            redisRepository.del(accountKey);
            return true;
        } else {
            // 登录失败，增加登录次数
            long incred = redisRepository.incr(accountKey);
            if(incred > 5) {
                redisRepository.setExpire(lockedKey, lockedKey, 5 * 60);
            }
            return false;
        }
    }

    @Override
    public RegisterResp register(RegisterReq registerReq) {
        String username = registerReq.getUsername();
        String email = registerReq.getEmail();
        String password = registerReq.getPassword();
        boolean exist = checkUsername(username, email);
        Student student = null;
        if(!exist) {
            String passwordStr = MD5Util.md5(registerReq.getPassword()).toLowerCase();
            student = new Student();
            student.setUsername(username);
            student.setPassword(passwordStr);
            if(StringUtils.isNotEmpty(email)) {
                student.setEmail(email);
            }
            student.setFlag(1);
            student.setCreateTime(LocalDateTime.now());
            student.setUpdateTime(LocalDateTime.now());

            int insert = studentMapper.insert(student);

            RegisterResp registerResp = new RegisterResp();
            registerResp.setUserId(student.getId());
            return registerResp;
        } else {
            throw new RuntimeException();
        }

    }

    private boolean checkUsername(String username, String email) {
        // 验证用户名
        if(StringUtils.isNotEmpty(username)) {
            QueryWrapper<Student> wrapper = new QueryWrapper<Student>().eq("username", username);
            boolean exists = studentMapper.selectCount(wrapper) > 0;
            if(exists) {
                throw new UniqueUsernameException();
            }
        }

        // 验证邮箱格式
        if(StringUtils.isNotEmpty(email)) {
            QueryWrapper<Student> wrapper = new QueryWrapper<Student>().eq("email", email);
            boolean exists = studentMapper.selectCount(wrapper) > 0;
            if(exists) {
                throw new UniqueEmailException();
            }
        }
        return false;

    }

    @Override
    public void logout(String userId) {
        String origin = request.getHeader("Origin");
        kickOut(userId, origin);
    }

    private void kickOut(String userId, String origin) {
        // 删除redis中存的refreshToken
        String refreshToken = JwtUtils.generateRefreshToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);;
        String redisRefreshKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshToken);
        redisRepository.del(redisRefreshKey);
        String accessToken = JwtUtils.generateAccessToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);;
        String redisAccessKey = RedisConstants.RedisKey.ACCESS_TOKEN_PREFIX + MD5Util.md5(accessToken);
        redisRepository.del(redisAccessKey);
    }

    @Override
    public String sendMsg(StudentReq studentReq) {
        String email = studentReq.getEmail();

        // 生成4位随机数
        String code = String.valueOf(ValidateCodeUtils.generateValidateCode(4));

        // 发送邮箱
        SendEmailUtils.sendAuthCodeEmail(email,code);

        // 缓存到redis，默认超时时间60s
        tokenRedisService.saveCode(email, code);
        return code;
    }

    @Override
    public StudentResp editStu(StudentReq studentReq) {
        // 先修改数据库
        Student student = new Student();
        BeanUtils.copyProperties(studentReq, student);
        student.setUpdateTime(LocalDateTime.now());
        studentMapper.updateById(student);
        StudentResp studentResp = new StudentResp();

        String userId = String.valueOf(studentReq.getId());

        // 修改缓存
        String accessTokenApp = JwtUtils.generateAccessToken(userId, OriginType.APP, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES);
        String refreshTokenApp = JwtUtils.generateRefreshToken(userId, OriginType.APP, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);
        String accessTokenWeb = JwtUtils.generateAccessToken(userId, OriginType.WEB, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES);
        String refreshTokenWeb = JwtUtils.generateRefreshToken(userId, OriginType.WEB, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES * 6 * 24 * 2);

        // 所有端的缓存都更新
        // 多端并发，加分布式锁
        Lock lock = redisLockRegistry.obtain(userId);

        try {
            if(lock.tryLock(100, TimeUnit.SECONDS)) {
                try {
                    if(redisRepository.exists(accessTokenApp)) {
                        String redisKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(accessTokenApp);
                        long time = redisRepository.getExpire(redisKey, TimeUnit.SECONDS);
                        redisRepository.del(redisKey);
                        redisRepository.setExpire(redisKey, JSON.toJSONString(student), time);
                    }

                    if(redisRepository.exists(accessTokenWeb)) {
                        String redisKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(accessTokenWeb);
                        long time = redisRepository.getExpire(redisKey, TimeUnit.SECONDS);
                        redisRepository.del(redisKey);
                        redisRepository.setExpire(redisKey, JSON.toJSONString(student), time);
                    }

                    if(redisRepository.exists(refreshTokenApp)) {
                        String redisKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshTokenApp);
                        long time = redisRepository.getExpire(redisKey, TimeUnit.SECONDS);
                        redisRepository.del(redisKey);
                        redisRepository.setExpire(redisKey, refreshTokenApp, time);
                    }

                    if(redisRepository.exists(refreshTokenWeb)) {
                        String redisKey = RedisConstants.RedisKey.REFRESH_TOKEN_PREFIX + MD5Util.md5(refreshTokenWeb);
                        long time = redisRepository.getExpire(redisKey, TimeUnit.SECONDS);
                        redisRepository.del(redisKey);
                        redisRepository.setExpire(redisKey, refreshTokenWeb, time);
                    }
                } finally {
                    lock.unlock();
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        BeanUtils.copyProperties(student, studentResp);
        return studentResp;
    }

    @Override
    public Student getByToken() {
        String userId = userContextUtils.getUserId();
        String origin = request.getHeader("Origin");

        String accessToken = JwtUtils.generateAccessToken(userId, origin, RsaUtils.privateKey, JwtUtils.EXPIRE_MINUTES);
        String redisAccessKey = RedisConstants.RedisKey.ACCESS_TOKEN_PREFIX + MD5Util.md5(accessToken);

        if(redisRepository.exists(redisAccessKey)) {
            String jsonString = (String)redisRepository.get(redisAccessKey);
            Student student = (Student) JSON.parse(jsonString);
            return student;
        } else {
            Student student = studentMapper.selectById(userId);
            return student;
        }
    }

}
