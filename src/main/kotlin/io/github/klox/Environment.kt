package io.github.klox

import io.github.klox.errors.RunTimeError

class Environment(private val enclosing: Environment? = null) {

    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun get(name: Token): Any {
        return if (values.containsKey(name.lexeme)) {
            values[name.lexeme] ?: throw RunTimeError(name, "Uninitialized variable ${name.lexeme}.")
        } else enclosing?.get(name) ?: throw RunTimeError(name, "Undefined variable ${name.lexeme}.")
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
        } else if (enclosing != null) {
            enclosing.assign(name, value)
        } else {
            throw RunTimeError(name, "Undefined variable ${name.lexeme}.")
        }
    }
}