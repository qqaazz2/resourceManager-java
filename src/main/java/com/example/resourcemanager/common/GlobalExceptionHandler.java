package com.example.resourcemanager.common;

import com.example.resourcemanager.enums.ExceptionEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultResponse bizExceptionHandler(HttpServletRequest servletRequest,BizException bizException){
        return ResultResponse.error(bizException.getErrorCode(),bizException.getErrorMsg());
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest servletRequest,NullPointerException exception){
        exception.printStackTrace();
        return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, Exception e){
        return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, MissingServletRequestParameterException e){
        return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, AuthenticationException e){
        return ResultResponse.error(ExceptionEnum.NOT_AUTHORITY);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, BadCredentialsException e){
        return ResultResponse.error("4000","账号或密码错误");
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    @ResponseBody
    public ResultResponse exceptionHandler(HttpServletRequest req, InternalAuthenticationServiceException e){
        return ResultResponse.error("4000",e.getMessage());
    }
}
