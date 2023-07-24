class Scanner(private val source: String) {

    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    private val keywords = mutableMapOf<String, TokenType>()

    init {
        keywords["and"] = TokenType.AND
        keywords["class"] = TokenType.CLASS
        keywords["else"] = TokenType.ELSE
        keywords["false"] = TokenType.FALSE
        keywords["for"] = TokenType.FOR
        keywords["fun"] = TokenType.FUN
        keywords["if"] = TokenType.IF
        keywords["nil"] = TokenType.NIL
        keywords["or"] = TokenType.OR
        keywords["print"] = TokenType.PRINT
        keywords["return"] = TokenType.RETURN
        keywords["super"] = TokenType.SUPER
        keywords["this"] = TokenType.THIS
        keywords["true"] = TokenType.TRUE
        keywords["var"] = TokenType.VAR
        keywords["while"] = TokenType.WHILE
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            ' ', '\r', '\t' -> {} // Ignore whitespaces
            '\n' -> line++
            '"' -> string()
            'o' -> {
                if (match('r')) addToken(TokenType.OR)
            }
            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    Klox.error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        return if (isAtEnd()) return '\u0000' else source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            Klox.error(line, "Unterminated string.")
            return
        }

        // closing ".
        advance()

        // trim surrounding quotes
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun number() {
        while (isDigit(peek())) advance()

        // Look for fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume '.'
            advance()
            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') || (c in 'A'..'Z') || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isDigit(c) || isAlpha(c)
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        val type = keywords.getOrDefault(text, TokenType.IDENTIFIER)
        addToken(type)
    }
}