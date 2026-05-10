package com.likelionknu.applyserver.admin.data.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException


class InvalidRequestException :
    GlobalException(ErrorCode.INVALID_REQUEST)