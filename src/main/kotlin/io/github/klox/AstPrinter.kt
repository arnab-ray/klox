package io.github.klox

class AstPrinter {

    fun print(expr: Expr?): String {
        val builder = StringBuilder()
        expr?.let { traverseAst(builder, expr) }

        return builder.toString()
    }

    private fun traverseAst(builder: StringBuilder, expr: Expr) {
        when (expr) {
            is Expr.Literal -> builder.append(expr.value.toString())
            is Expr.Binary -> parenthesize(builder, expr.operator.lexeme, expr.left, expr.right)
            is Expr.Unary -> parenthesize(builder, expr.operator.lexeme, expr.right)
            is Expr.Grouping -> parenthesize(builder, "grouping", expr.expression)
            else -> error("No implementation found")
        }
    }

    private fun parenthesize(builder: StringBuilder, name: String, vararg exprs: Expr) {
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            traverseAst(builder, expr)
        }
        builder.append(")")
    }
}