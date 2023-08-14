package io.github.klox

interface KloxCallable {

    fun arity(): Int

    fun call(interpreter: Interpreter,  arguments: List<Any>): Any?
}

class Kloxfunction(private val declaration: Stmt.Function) : KloxCallable {
    override fun arity(): Int {
        return declaration.params.size
    }

    override fun call(interpreter: Interpreter, arguments: List<Any>): Any? {
        val environment = Environment(interpreter.globals)
        val numberOfParams = declaration.params.size - 1
        for (i in 0..numberOfParams) {
            environment.define(declaration.params[i].lexeme, arguments[i])
        }

        interpreter.executeBlock(declaration.body, environment)
        return null
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}