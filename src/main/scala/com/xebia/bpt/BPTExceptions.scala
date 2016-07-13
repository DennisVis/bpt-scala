package com.xebia.bpt

case class NotFoundException(message: String) extends Exception(message)

case class FailureException(message: String) extends Exception(message)
