package com.likelionknu.applyserver.discord.data.exception

import com.likelionknu.applyserver.common.response.ErrorCode
import com.likelionknu.applyserver.common.response.GlobalException

class DiscordNotificationException : GlobalException(ErrorCode.INTERNAL_SERVER_ERROR)