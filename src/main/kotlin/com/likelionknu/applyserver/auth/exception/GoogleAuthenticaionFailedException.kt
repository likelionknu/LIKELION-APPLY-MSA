package com.likelionknu.applyserver.auth.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class GoogleAuthenticaionFailedException : GlobalException(ErrorCode.GOOGLE_AUTHENTICATION_FAILED)