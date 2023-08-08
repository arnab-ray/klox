package io.github.klox.errors

import io.github.klox.Token

class ParseError : RuntimeException()

class RunTimeError(val token: Token, override val message: String) : RuntimeException(message)