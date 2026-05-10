package com.likelionknu.applyserver.application.data.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class ProfileIncompleteException : GlobalException(ErrorCode.FORBIDDEN)