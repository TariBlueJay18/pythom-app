package com.example.engine

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.SyntaxBuiltin
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxOperator
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.TextPrimary

object SyntaxHighlighter {

    private val KEYWORDS = setOf(
        "def", "return", "if", "elif", "else", "for", "while", "in", "import", "from",
        "class", "pass", "True", "False", "None", "and", "or", "not", "break",
        "continue", "lambda", "try", "except", "finally", "raise", "with", "as", "yield", "assert"
    )

    private val BUILTINS = setOf(
        "print", "len", "sum", "max", "min", "abs", "range", "str", "int", "float",
        "list", "dict", "set", "sorted", "round", "enumerate", "zip", "type", "reversed"
    )

    fun highlight(code: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            var i = 0
            val len = code.length

            while (i < len) {
                val c = code[i]

                // Comments
                if (c == '#') {
                    val lineEnd = code.indexOf('\n', i).let { if (it == -1) len else it }
                    addStyle(SpanStyle(color = SyntaxComment), i, lineEnd)
                    i = lineEnd
                    continue
                }

                // Strings: '...' or "..."
                if (c == '\'' || c == '"') {
                    val quote = c
                    val start = i
                    i++
                    while (i < len && code[i] != quote) {
                        if (code[i] == '\\' && i + 1 < len) {
                            i++
                        }
                        i++
                    }
                    if (i < len) i++ // include closing quote
                    addStyle(SpanStyle(color = SyntaxString), start, i)
                    continue
                }

                // Numbers
                if (c.isDigit()) {
                    val start = i
                    while (i < len && (code[i].isDigit() || code[i] == '.' || code[i] == 'e' || code[i] == 'E')) {
                        i++
                    }
                    addStyle(SpanStyle(color = SyntaxNumber), start, i)
                    continue
                }

                // Words (Keywords, Builtins, Function names, Identifiers)
                if (c.isLetter() || c == '_') {
                    val start = i
                    while (i < len && (code[i].isLetterOrDigit() || code[i] == '_')) {
                        i++
                    }
                    val word = code.substring(start, i)

                    when {
                        word in KEYWORDS -> {
                            addStyle(
                                SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.SemiBold),
                                start,
                                i
                            )
                        }
                        word in BUILTINS -> {
                            addStyle(SpanStyle(color = SyntaxBuiltin), start, i)
                        }
                        // Function name definition: def my_func(
                        start > 4 && code.substring(0, start).trimEnd().endsWith("def") -> {
                            addStyle(
                                SpanStyle(color = SyntaxFunction, fontWeight = FontWeight.Bold),
                                start,
                                i
                            )
                        }
                        // Function call: func(...)
                        i < len && code[i] == '(' -> {
                            addStyle(SpanStyle(color = SyntaxFunction), start, i)
                        }
                        else -> {
                            addStyle(SpanStyle(color = TextPrimary), start, i)
                        }
                    }
                    continue
                }

                // Operators
                if (c in "+-*/%=<>!&|^~:") {
                    addStyle(SpanStyle(color = SyntaxOperator), i, i + 1)
                }

                i++
            }
        }
    }
}
