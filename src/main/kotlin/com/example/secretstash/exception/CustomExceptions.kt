package com.example.secretstash.exception

open class BaseException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : BaseException(message)
class BadRequestException(message: String) : BaseException(message)
class ResourceNotFoundException(message: String) : BaseException(message)
class TooManyRequestsException(message: String) : BaseException(message)