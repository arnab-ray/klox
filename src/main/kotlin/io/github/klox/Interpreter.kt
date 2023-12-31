package io.github.klox

import Klox
import io.github.klox.errors.RunTimeError

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Any?> {
    val globals = Environment()
    private var environment = globals

    init {
        globals.define(
            "clock",
            object : KloxCallable {
                override fun arity(): Int {
                    return 0
                }

                override fun call(interpreter: Interpreter, arguments: List<Any>): Any {
                    return System.currentTimeMillis().toDouble() / 1000.0
                }

                override fun toString(): String {
                    return "<native fn>"
                }
            }
        )
    }

    fun interpret(statements: List<Stmt?>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (error: RunTimeError) {
            Klox.runtimeError(error)
        }
    }

    private fun execute(statement: Stmt?) {
        statement?.accept(this)
    }

    private fun stringify(obj: Any?): String {
        return when (obj) {
            null -> "nil"
            is Double -> {
                val text = obj.toString()
                if (text.endsWith(".0")) text.substring(0, text.length - 2) else text
            }
            else -> obj.toString()
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> {
                if (left is Double && right is Double) {
                    left - right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.STAR -> {
                if (left is Double && right is Double) {
                    left * right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.SLASH -> {
                if (left is Double && right is Double) {
                    left / right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be two numbers or two strings")
                }
            }
            TokenType.GREATER -> {
                if (left is Double && right is Double) {
                    left > right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.GREATER_EQUAL -> {
                if (left is Double && right is Double) {
                    left >= right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.LESS -> {
                if (left is Double && right is Double) {
                    left < right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.LESS_EQUAL -> {
                if (left is Double && right is Double) {
                    left <= right
                } else {
                    throw RunTimeError(expr.operator, "Operands must be a number")
                }
            }
            TokenType.BANG_EQUAL -> !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> isEqual(left, right)
            else -> null
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        return when {
            a == null && b == null -> true
            a == null -> false
            else -> a == b
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = expr.arguments.mapNotNull { evaluate(it) }

        if (callee !is KloxCallable) {
            throw RunTimeError(expr.paren, "Can only call functions and classes.")
        }
        if (arguments.size != callee.arity()) {
            throw RunTimeError(expr.paren, "Expected ${callee.arity()} arguments but got ${arguments.size}.")
        }
        return callee.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            TokenType.MINUS -> {
                if (right is Double) {
                    -right
                } else {
                    RunTimeError(expr.operator, "Operand must be a number")
                }
            }
            TokenType.BANG -> !isTruthy(right)
            else -> null
        }
    }

    private fun isTruthy(right: Any?): Boolean {
        return when (right) {
            null -> false
            is Boolean -> right
            else -> true
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any {
        return environment.get(expr.name)
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Any? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    fun executeBlock(statements: List<Stmt?>, environment: Environment) {
        val previous = this.environment

        try {
            this.environment = environment

            for (statement in statements)
                execute(statement)
        } finally {
            this.environment = previous
        }
    }

    override fun visitClassStmt(stmt: Stmt.Class): Any? {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Any? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Any? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Any? {
        val function = Kloxfunction(stmt)
        environment.define(stmt.name.lexeme, function)
        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Any? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }

        return null
    }

    override fun visitBreakStmt(stmt: Stmt.Break): Any? {
        TODO("Not yet implemented")
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Any? {
        TODO("Not yet implemented")
    }

    override fun visitVarStmt(stmt: Stmt.Var): Any? {
        val value = if (stmt.initializer != null) evaluate(stmt.initializer) else null

        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Any? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }

        return null
    }
}