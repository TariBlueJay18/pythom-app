package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SyntaxHighlighter
import com.example.ui.theme.CodeTextStyle
import com.example.ui.theme.EditorGutter
import com.example.ui.theme.EditorLineNumber
import com.example.ui.theme.PythonBlue
import com.example.ui.theme.SnakeGreen
import com.example.ui.theme.SurfaceBorderDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TerminalBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun PythonEditorField(
    code: String,
    onCodeChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onRunClick: () -> Unit,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val quickSymbols = listOf(
        "    ", ":", "(", ")", "[", "]", "{", "}", "\"", "'",
        "=", "==", "!=", "<", ">", "+", "-", "*", "/", "%",
        "def ", "return ", "if ", "elif ", "else:", "for ", "in ", "range(", "print("
    )

    val lineCount = code.lines().size.coerceAtLeast(1)
    val lineNumbersText = (1..lineCount).joinToString("\n") { it.toString() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(TerminalBlack)
            .border(1.dp, SurfaceBorderDark, RoundedCornerShape(12.dp))
    ) {
        // Editor Header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(PythonBlue.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "solution.py",
                        color = PythonBlue,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$lineCount lines",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onResetClick,
                    modifier = Modifier.testTag("reset_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Code",
                        tint = TextMuted
                    )
                }

                Button(
                    onClick = onRunClick,
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SnakeGreen,
                        disabledContainerColor = SnakeGreen.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("run_code_button")
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            color = SurfaceDark,
                            strokeWidth = 2.dp,
                            modifier = Modifier.padding(2.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            tint = SurfaceDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Run Code",
                            color = SurfaceDark,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Quick Symbol Toolbar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceVariantDark.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickSymbols) { sym ->
                Surface(
                    onClick = {
                        onCodeChange(code + sym)
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderDark),
                    modifier = Modifier.testTag("symbol_${sym.trim()}")
                ) {
                    Text(
                        text = if (sym == "    ") "Tab (4)" else sym.trim(),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Code Area: Gutter with Line Numbers + Scrollable Editor
        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(verticalScrollState)
        ) {
            // Line numbers gutter
            Box(
                modifier = Modifier
                    .background(EditorGutter)
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Text(
                    text = lineNumbersText,
                    color = EditorLineNumber,
                    fontSize = CodeTextStyle.fontSize,
                    lineHeight = CodeTextStyle.lineHeight,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Editable Code with instant syntax highlighting
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                BasicTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    textStyle = CodeTextStyle,
                    cursorBrush = SolidColor(SnakeGreen),
                    visualTransformation = { text ->
                        TransformedText(
                            text = SyntaxHighlighter.highlight(text.text),
                            offsetMapping = OffsetMapping.Identity
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("python_code_input")
                )
            }
        }
    }
}
