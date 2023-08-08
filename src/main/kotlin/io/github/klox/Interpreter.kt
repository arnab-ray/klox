package io.github.klox

import Klox
import io.github.klox.errors.RunTimeError

class Interpreter : Expr.Visitor<Any?> {
    fun interpret(expression: Expr?) {
        expression?.let {
            try {
                val value = evaluate(expression)
                println(stringify(value))
            } catch (error: RunTimeError) {
                Klox.runtimeError(error)
            }
        }
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
        TODO("Not yet implemented")
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
                }
                if (left is String && right is String) {
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        TODO("Not yet implemented")
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }
}