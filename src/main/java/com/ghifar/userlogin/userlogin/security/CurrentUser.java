package com.ghifar.userlogin.userlogin.security;
/*
* This annotation meta class is using @AuthenticationPrincipal annotation from spring security
* .. only for ACCESSING the currently AUTHENTICATED USER in the Controllers class
* this class purpose so we're not depend on @AuthenticationPrincipal annotation from spring security. we can just always change it in this class.
* */


import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal
public @interface CurrentUser {
}
