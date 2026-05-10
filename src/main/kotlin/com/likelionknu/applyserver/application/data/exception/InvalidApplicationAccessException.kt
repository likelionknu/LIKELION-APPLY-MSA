package com.likelionknu.applyserver.application.data.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class InvalidApplicationAccessException : GlobalException(ErrorCode.FORBIDDEN)