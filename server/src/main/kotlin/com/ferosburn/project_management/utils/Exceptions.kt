package com.ferosburn.project_management.utils

class UnauthorizedException(message: String) : Exception(message)
class ForbiddenException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
class BadRequestException(message: String) : Exception(message)
class InternalServerException(message: String) : Exception(message)