package io.github.klox


sealed class Stmt {
    class Block(val statements: List<Stmt>) : Stmt ()
    class Class(val name: Token, val superclass: Expr.Variable, val methods: List<Stmt.Function>) : Stmt ()
    class Expression(val expression: Expr) : Stmt ()
    class Function(val name: Token, val params: List<Token>, val body: List<Stmt>) : Stmt ()
    class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt) : Stmt ()
    class Break(val keyword: Token) : Stmt ()
    class Return(val keyword: Token, val value: Expr) : Stmt ()
    class Var(val name: Token, val initializer: Expr) : Stmt ()
    class While(val condition: Expr, val body: Stmt) : Stmt ()
}
