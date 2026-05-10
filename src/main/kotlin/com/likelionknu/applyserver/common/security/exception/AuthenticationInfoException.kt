package com.likelionknu.applyserver.common.security.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class AuthenticationInfoException : GlobalException(ErrorCode.UNAUTHORIZED)