package io.github.klox

sealed class Expr {

    data class Binary(val left: Expr, val op: Token, val right: Expr) : Expr()

}