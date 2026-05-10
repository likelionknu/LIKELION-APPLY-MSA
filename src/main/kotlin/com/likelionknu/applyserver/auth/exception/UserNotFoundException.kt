package com.likelionknu.applyserver.auth.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class UserNotFoundException : GlobalException(ErrorCode.UNAUTHORIZED)