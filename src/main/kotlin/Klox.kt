import io.github.klox.AstPrinter
import io.github.klox.Expr
import io.github.klox.Expr.Binary
import io.github.klox.Expr.Literal
import io.github.klox.Expr.Unary
import io.github.klox.Scanner
import io.github.klox.Token
import io.github.klox.TokenType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


class Klox {

    companion object {
        private var hadError = false

        @JvmStatic
        fun main(args: Array<String>) {
//            when(args.size) {
//                0 -> runPrompt()
//                1 -> runFile(args[0])
//                else -> {
//                    println("Usage klox [script]")
//                    exitProcess(64)
//                }
//            }

            val expression = Binary(
                Unary(Token(TokenType.MINUS, "-", null, 1), Literal(123)),
                Token(TokenType.STAR, "*", null, 1),
                Expr.Grouping(Literal(45.67))
            )

            println(AstPrinter().print(expression))
        }

        private fun runFile(path: String) {
            val bytes = Files.readAllBytes(Paths.get(path))
            runKlox(String(bytes, Charset.defaultCharset()))

            if (hadError) exitProcess(65)
        }

        private fun runPrompt() {
            val input = InputStreamReader(System.`in`)
            val reader = BufferedReader(input)

            while (true) {
                print("> ")
                val line = reader.readLine() ?: break
                runKlox(line)
                hadError = false
            }
        }

        private fun runKlox(source: String) {
            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()

            for (token in tokens) {
                println(token)
            }
        }
        
        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error$where: $message")
            hadError = true
        }
    }
}