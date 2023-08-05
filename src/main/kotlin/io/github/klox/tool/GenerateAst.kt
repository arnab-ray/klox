package io.github.klox.tool

import java.io.PrintWriter
import kotlin.system.exitProcess

class GenerateAst {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                System.err.println("Usage: generate_ast <output directory>")
                exitProcess(64)
            }

            val outputDir = args[0]

            defineAst(outputDir, "Expr", listOf(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Call     : Expr callee, Token paren, List<Expr> arguments",
                "Get      : Expr obj, Token name",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Logical  : Expr left, Token operator, Expr right",
                "Set      : Expr obj, Token name, Expr value",
                "Super    : Token keyword, Token method",
                "This     : Token keyword",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
            ))

            defineAst(outputDir, "Stmt", listOf(
                "Block      : List<Stmt> statements",
                "Class      : Token name, Expr.Variable superclass, List<Stmt.Function> methods",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params, List<Stmt> body",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Break      : Token keyword",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
            ))
        }

        private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.kt"
            val writer = PrintWriter(path, "UTF-8")

            writer.println("package io.github.klox")
            writer.println()
            writer.println()
            writer.println("sealed class $baseName {")

            // The AST classes.
            for (type in types) {
                val className = type.split(":")[0].trim()
                val fields = type.split(":")[1].trim().split(",")
                defineType(writer, baseName, className, fields)
            }

            writer.println("}")
            writer.close()
        }

        private fun defineType(writer: PrintWriter, baseName: String, className: String, fields: List<String>) {
            writer.println(
                "    class $className"
                + "(${fields.map { it.trim().split(" ").reversed().joinToString(": ") }
                    .map { "val $it" }.reduce { acc, field -> "$acc, $field" }})"
                + " : " + baseName + " ()"
            )
        }
    }
}