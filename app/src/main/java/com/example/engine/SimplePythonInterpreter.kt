package com.example.engine

/**
 * An embedded, lightweight Python interpreter written in pure Kotlin.
 * Evaluates function definitions, control flow, loops, collections, math,
 * and built-in functions without requiring external native dependencies.
 */
class SimplePythonInterpreter(private val stdout: StringBuilder) {

    private val definedFunctions = mutableMapOf<String, PythonFunction>()
    private val globalScope = mutableMapOf<String, Any?>()

    data class PythonFunction(
        val name: String,
        val params: List<String>,
        val bodyLines: List<Pair<Int, String>> // indentation level, raw code
    )

    class ReturnException(val value: Any?) : Exception()

    fun loadCode(code: String) {
        val rawLines = code.lines()
        var i = 0
        while (i < rawLines.size) {
            val line = rawLines[i]
            val trimmed = line.trim()

            // Skip empty lines or pure comments
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                i++
                continue
            }

            if (trimmed.startsWith("def ")) {
                val funcDef = parseFunctionDef(rawLines, i)
                definedFunctions[funcDef.first.name] = funcDef.first
                i = funcDef.second
            } else {
                // Top-level variable assignment or statement
                try {
                    val indent = getIndentation(line)
                    if (indent == 0) {
                        executeStatement(trimmed, globalScope)
                    }
                } catch (e: ReturnException) {
                    // Ignore top level return
                } catch (e: Exception) {
                    // Ignore top level parse error for comments/imports
                }
                i++
            }
        }
    }

    private fun parseFunctionDef(lines: List<String>, startIdx: Int): Pair<PythonFunction, Int> {
        val defLine = lines[startIdx].trim()
        val header = defLine.removePrefix("def ").substringBefore(":").trim()
        val name = header.substringBefore("(").trim()
        val paramString = header.substringAfter("(").substringBeforeLast(")").trim()
        val params = if (paramString.isEmpty()) emptyList() else paramString.split(",").map { it.trim() }

        val body = mutableListOf<Pair<Int, String>>()
        var idx = startIdx + 1
        var baseIndent = -1

        while (idx < lines.size) {
            val curLine = lines[idx]
            val trimmed = curLine.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                idx++
                continue
            }
            val indent = getIndentation(curLine)
            if (baseIndent == -1) {
                if (indent <= 0) break // not indented body
                baseIndent = indent
            }
            if (indent < baseIndent) {
                break
            }
            body.add(Pair(indent, trimmed))
            idx++
        }

        return Pair(PythonFunction(name, params, body), idx)
    }

    private fun getIndentation(line: String): Int {
        var count = 0
        for (ch in line) {
            if (ch == ' ') count++
            else if (ch == '\t') count += 4
            else break
        }
        return count
    }

    fun callFunction(name: String, args: List<Any?>): Any? {
        val func = definedFunctions[name]
            ?: throw IllegalArgumentException("Function '$name' is not defined in the code.")

        val localScope = HashMap(globalScope)
        for (i in func.params.indices) {
            if (i < args.size) {
                localScope[func.params[i]] = args[i]
            } else {
                localScope[func.params[i]] = null
            }
        }

        try {
            executeBlock(func.bodyLines, 0, func.bodyLines.size, localScope)
        } catch (r: ReturnException) {
            return r.value
        }
        return null
    }

    private fun executeBlock(
        lines: List<Pair<Int, String>>,
        start: Int,
        end: Int,
        scope: MutableMap<String, Any?>
    ) {
        var i = start
        while (i < end) {
            val (indent, line) = lines[i]

            when {
                line.startsWith("return") -> {
                    val expr = line.removePrefix("return").trim()
                    val result = if (expr.isEmpty()) null else evalExpression(expr, scope)
                    throw ReturnException(result)
                }

                line.startsWith("print(") && line.endsWith(")") -> {
                    val inner = line.removeSurrounding("print(", ")")
                    val items = splitByTopLevelComma(inner)
                    val output = items.joinToString(" ") { evalExpression(it.trim(), scope).toString() }
                    stdout.appendLine(output)
                    i++
                }

                line.startsWith("if ") -> {
                    i = handleIfStatement(lines, i, end, scope)
                }

                line.startsWith("for ") -> {
                    i = handleForLoop(lines, i, end, scope)
                }

                line.startsWith("while ") -> {
                    i = handleWhileLoop(lines, i, end, scope)
                }

                line == "pass" -> {
                    i++
                }

                else -> {
                    executeStatement(line, scope)
                    i++
                }
            }
        }
    }

    private fun executeStatement(line: String, scope: MutableMap<String, Any?>) {
        if (line.startsWith("print(") && line.endsWith(")") && !line.contains("=")) {
            val inner = line.removeSurrounding("print(", ")")
            val items = splitByTopLevelComma(inner)
            val output = items.joinToString(" ") { evalExpression(it.trim(), scope).toString() }
            stdout.appendLine(output)
            return
        }

        // Method calls on objects: e.g. nums.append(val) or result.append(x)
        if (line.contains(".append(") && line.endsWith(")")) {
            val objName = line.substringBefore(".append(").trim()
            val argExpr = line.substringAfter(".append(").substringBeforeLast(")").trim()
            val listObj = scope[objName] as? MutableList<Any?>
            if (listObj != null) {
                listObj.add(evalExpression(argExpr, scope))
                return
            }
        }

        if (line.contains(".pop(") && line.endsWith(")")) {
            val objName = line.substringBefore(".pop(").trim()
            val listObj = scope[objName] as? MutableList<Any?>
            if (listObj != null && listObj.isNotEmpty()) {
                listObj.removeAt(listObj.size - 1)
                return
            }
        }

        // Variable updates (+=, -=, *=, /=)
        val updateOp = listOf("+=", "-=", "*=", "/=").find { line.contains(" $it ") || line.contains(it) }
        if (updateOp != null) {
            val varName = line.substringBefore(updateOp).trim()
            val expr = line.substringAfter(updateOp).trim()
            val curVal = scope[varName]
            val delta = evalExpression(expr, scope)
            scope[varName] = when (updateOp) {
                "+=" -> applyAdd(curVal, delta)
                "-=" -> applySubtract(curVal, delta)
                "*=" -> applyMultiply(curVal, delta)
                else -> applyDivide(curVal, delta)
            }
            return
        }

        // Standard assignment: a = expr or a, b = x, y
        if (line.contains("=") && !line.contains("==") && !line.contains("<=") && !line.contains(">=")) {
            val varPart = line.substringBefore("=").trim()
            val exprPart = line.substringAfter("=").trim()

            if (varPart.contains(",")) {
                // Multi assignment: a, b = x, y
                val vars = varPart.split(",").map { it.trim() }
                val evalRes = evalExpression(exprPart, scope)
                if (evalRes is List<*>) {
                    for (k in vars.indices) {
                        if (k < evalRes.size) scope[vars[k]] = evalRes[k]
                    }
                }
            } else if (varPart.contains("[") && varPart.endsWith("]")) {
                // Subscript assignment: list[i] = val or dict[k] = val
                val objName = varPart.substringBefore("[").trim()
                val indexExpr = varPart.substringAfter("[").substringBeforeLast("]").trim()
                val targetObj = scope[objName]
                val keyOrIndex = evalExpression(indexExpr, scope)
                val assignedVal = evalExpression(exprPart, scope)
                if (targetObj is MutableList<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val mList = targetObj as MutableList<Any?>
                    val idx = (keyOrIndex as? Number)?.toInt() ?: 0
                    if (idx in mList.indices) mList[idx] = assignedVal
                } else if (targetObj is MutableMap<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    val mMap = targetObj as MutableMap<Any?, Any?>
                    mMap[keyOrIndex] = assignedVal
                }
            } else {
                scope[varPart] = evalExpression(exprPart, scope)
            }
        }
    }

    private fun handleIfStatement(
        lines: List<Pair<Int, String>>,
        start: Int,
        end: Int,
        scope: MutableMap<String, Any?>
    ): Int {
        val baseIndent = lines[start].first
        // Collect branches: condition -> list of lines
        val branches = mutableListOf<Pair<String?, List<Pair<Int, String>>>>()

        var curIdx = start
        while (curIdx < end) {
            val (indent, line) = lines[curIdx]
            if (indent == baseIndent && (line.startsWith("if ") || line.startsWith("elif ") || line.startsWith("else:") || line == "else")) {
                val conditionStr = when {
                    line.startsWith("if ") -> line.removePrefix("if ").substringBeforeLast(":").trim()
                    line.startsWith("elif ") -> line.removePrefix("elif ").substringBeforeLast(":").trim()
                    else -> null // else branch
                }

                // collect body for this branch
                val branchBody = mutableListOf<Pair<Int, String>>()
                curIdx++
                while (curIdx < end && lines[curIdx].first > baseIndent) {
                    branchBody.add(lines[curIdx])
                    curIdx++
                }
                branches.add(Pair(conditionStr, branchBody))
            } else {
                break
            }
        }

        // Evaluate branches
        for ((cond, body) in branches) {
            val isTrue = if (cond == null) true else isTruthy(evalExpression(cond, scope))
            if (isTrue) {
                executeBlock(body, 0, body.size, scope)
                break
            }
        }

        return curIdx
    }

    private fun handleForLoop(
        lines: List<Pair<Int, String>>,
        start: Int,
        end: Int,
        scope: MutableMap<String, Any?>
    ): Int {
        val baseIndent = lines[start].first
        val header = lines[start].second.removePrefix("for ").substringBeforeLast(":").trim()
        val varName = header.substringBefore(" in ").trim()
        val iterableExpr = header.substringAfter(" in ").trim()

        val loopBody = mutableListOf<Pair<Int, String>>()
        var curIdx = start + 1
        while (curIdx < end && lines[curIdx].first > baseIndent) {
            loopBody.add(lines[curIdx])
            curIdx++
        }

        val iterableVal = evalExpression(iterableExpr, scope)
        val items: Iterable<Any?> = when (iterableVal) {
            is Iterable<*> -> iterableVal
            is CharSequence -> iterableVal.map { it.toString() }
            is Map<*, *> -> iterableVal.keys
            else -> emptyList()
        }

        for (item in items) {
            if (varName.contains(",")) {
                // e.g. for i, x in enumerate(nums)
                val vars = varName.split(",").map { it.trim() }
                if (item is List<*>) {
                    for (k in vars.indices) {
                        if (k < item.size) scope[vars[k]] = item[k]
                    }
                }
            } else {
                scope[varName] = item
            }
            executeBlock(loopBody, 0, loopBody.size, scope)
        }

        return curIdx
    }

    private fun handleWhileLoop(
        lines: List<Pair<Int, String>>,
        start: Int,
        end: Int,
        scope: MutableMap<String, Any?>
    ): Int {
        val baseIndent = lines[start].first
        val conditionStr = lines[start].second.removePrefix("while ").substringBeforeLast(":").trim()

        val loopBody = mutableListOf<Pair<Int, String>>()
        var curIdx = start + 1
        while (curIdx < end && lines[curIdx].first > baseIndent) {
            loopBody.add(lines[curIdx])
            curIdx++
        }

        var loopLimit = 10000 // Prevent infinite loops
        while (isTruthy(evalExpression(conditionStr, scope))) {
            if (--loopLimit <= 0) {
                throw RuntimeException("TimeLimitExceeded: Infinite loop detected in while block.")
            }
            executeBlock(loopBody, 0, loopBody.size, scope)
        }

        return curIdx
    }

    // --- Expression Evaluator ---

    fun evalExpression(expr: String, scope: Map<String, Any?>): Any? {
        val s = expr.trim()
        if (s.isEmpty()) return null

        // Parentheses
        if (s.startsWith("(") && s.endsWith(")") && hasBalancedParentheses(s.substring(1, s.length - 1))) {
            return evalExpression(s.substring(1, s.length - 1), scope)
        }

        // Literals
        if (s == "None") return null
        if (s == "True") return true
        if (s == "False") return false
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length - 1)
        }
        val intVal = s.toLongOrNull()
        if (intVal != null) return if (intVal in Int.MIN_VALUE..Int.MAX_VALUE) intVal.toInt() else intVal
        val doubleVal = s.toDoubleOrNull()
        if (doubleVal != null) return doubleVal

        // List literal: [a, b, c]
        if (s.startsWith("[") && s.endsWith("]")) {
            val inner = s.substring(1, s.length - 1).trim()
            if (inner.isEmpty()) return mutableListOf<Any?>()
            val parts = splitByTopLevelComma(inner)
            return parts.map { evalExpression(it.trim(), scope) }.toMutableList()
        }

        // Dict literal: {k: v}
        if (s.startsWith("{") && s.endsWith("}")) {
            val inner = s.substring(1, s.length - 1).trim()
            if (inner.isEmpty()) return mutableMapOf<Any?, Any?>()
            val parts = splitByTopLevelComma(inner)
            val map = mutableMapOf<Any?, Any?>()
            for (p in parts) {
                val k = p.substringBefore(":").trim()
                val v = p.substringAfter(":").trim()
                map[evalExpression(k, scope)] = evalExpression(v, scope)
            }
            return map
        }

        // Boolean 'or'
        val orParts = splitByTopLevelToken(s, " or ")
        if (orParts.size > 1) {
            for (part in orParts) {
                val res = evalExpression(part.trim(), scope)
                if (isTruthy(res)) return res
            }
            return evalExpression(orParts.last().trim(), scope)
        }

        // Boolean 'and'
        val andParts = splitByTopLevelToken(s, " and ")
        if (andParts.size > 1) {
            var lastRes: Any? = null
            for (part in andParts) {
                lastRes = evalExpression(part.trim(), scope)
                if (!isTruthy(lastRes)) return lastRes
            }
            return lastRes
        }

        // Boolean 'not '
        if (s.startsWith("not ")) {
            return !isTruthy(evalExpression(s.removePrefix("not ").trim(), scope))
        }

        // Comparisons: ==, !=, <=, >=, <, >, in, not in
        val compOps = listOf("==", "!=", "<=", ">=", "<", ">", " not in ", " in ")
        for (op in compOps) {
            val parts = splitByTopLevelToken(s, op)
            if (parts.size == 2) {
                val left = evalExpression(parts[0].trim(), scope)
                val right = evalExpression(parts[1].trim(), scope)
                return evaluateComparison(left, op.trim(), right)
            }
        }

        // Addition and Subtraction (+, -)
        val addSub = findTopLevelAddSub(s)
        if (addSub != null) {
            val left = evalExpression(s.substring(0, addSub.first).trim(), scope)
            val right = evalExpression(s.substring(addSub.first + 1).trim(), scope)
            return if (addSub.second == '+') applyAdd(left, right) else applySubtract(left, right)
        }

        // Multiplication, Division, Modulo, Floor Division (*, /, %, //)
        val mulDiv = findTopLevelMulDiv(s)
        if (mulDiv != null) {
            val left = evalExpression(s.substring(0, mulDiv.first).trim(), scope)
            val op = mulDiv.second
            val right = evalExpression(s.substring(mulDiv.first + op.length).trim(), scope)
            return when (op) {
                "*" -> applyMultiply(left, right)
                "/" -> applyDivide(left, right)
                "//" -> applyFloorDivide(left, right)
                "%" -> applyModulo(left, right)
                "**" -> applyPower(left, right)
                else -> null
            }
        }

        // Slicing or Indexing: e.g. s[::-1] or arr[0] or s[1:4]
        if (s.endsWith("]") && s.contains("[")) {
            val openIdx = findMatchingBracketOpen(s)
            if (openIdx > 0) {
                val targetExpr = s.substring(0, openIdx).trim()
                val sliceExpr = s.substring(openIdx + 1, s.length - 1).trim()
                val targetVal = evalExpression(targetExpr, scope)
                return evaluateSliceOrIndex(targetVal, sliceExpr, scope)
            }
        }

        // Built-in functions: len, sum, max, min, range, str, int, abs, sorted, list, enumerate
        val builtinCall = tryEvaluateBuiltin(s, scope)
        if (builtinCall != null) {
            return builtinCall
        }

        // Method call: e.g. s.lower(), arr.count(x), "".join(arr)
        if (s.contains(".") && s.endsWith(")")) {
            val methodRes = tryEvaluateMethod(s, scope)
            if (methodRes != null) return methodRes
        }

        // Variable lookup
        if (scope.containsKey(s)) {
            return scope[s]
        }

        return s
    }

    private fun evaluateComparison(left: Any?, op: String, right: Any?): Boolean {
        return when (op) {
            "==" -> when {
                left is Number && right is Number -> left.toDouble() == right.toDouble()
                else -> left == right
            }
            "!=" -> when {
                left is Number && right is Number -> left.toDouble() != right.toDouble()
                else -> left != right
            }
            "<" -> (toNumber(left) ?: 0.0) < (toNumber(right) ?: 0.0)
            ">" -> (toNumber(left) ?: 0.0) > (toNumber(right) ?: 0.0)
            "<=" -> (toNumber(left) ?: 0.0) <= (toNumber(right) ?: 0.0)
            ">=" -> (toNumber(left) ?: 0.0) >= (toNumber(right) ?: 0.0)
            "in" -> when (right) {
                is String -> right.contains(left.toString())
                is Iterable<*> -> right.contains(left)
                is Map<*, *> -> right.containsKey(left)
                else -> false
            }
            "not in" -> !evaluateComparison(left, "in", right)
            else -> false
        }
    }

    private fun evaluateSliceOrIndex(target: Any?, sliceExpr: String, scope: Map<String, Any?>): Any? {
        if (target == null) return null

        if (sliceExpr.contains(":")) {
            // Slicing: [start:end:step]
            val parts = sliceExpr.split(":")
            val startStr = parts.getOrNull(0)?.trim()
            val endStr = parts.getOrNull(1)?.trim()
            val stepStr = parts.getOrNull(2)?.trim()

            val step = if (!stepStr.isNullOrEmpty()) (evalExpression(stepStr, scope) as? Number)?.toInt() ?: 1 else 1

            if (target is String) {
                if (step == -1 && (startStr.isNullOrEmpty()) && (endStr.isNullOrEmpty())) {
                    return target.reversed()
                }
                val len = target.length
                val start = if (!startStr.isNullOrEmpty()) normalizeIndex((evalExpression(startStr, scope) as? Number)?.toInt() ?: 0, len) else 0
                val end = if (!endStr.isNullOrEmpty()) normalizeIndex((evalExpression(endStr, scope) as? Number)?.toInt() ?: len, len) else len
                return target.substring(start.coerceIn(0, len), end.coerceIn(start, len))
            } else if (target is List<*>) {
                if (step == -1 && (startStr.isNullOrEmpty()) && (endStr.isNullOrEmpty())) {
                    return target.reversed().toMutableList()
                }
                val len = target.size
                val start = if (!startStr.isNullOrEmpty()) normalizeIndex((evalExpression(startStr, scope) as? Number)?.toInt() ?: 0, len) else 0
                val end = if (!endStr.isNullOrEmpty()) normalizeIndex((evalExpression(endStr, scope) as? Number)?.toInt() ?: len, len) else len
                return target.subList(start.coerceIn(0, len), end.coerceIn(start, len)).toMutableList()
            }
        } else {
            // Indexing: [index]
            val indexKey = evalExpression(sliceExpr, scope)
            if (target is Map<*, *>) {
                return target[indexKey]
            }
            val idx = (indexKey as? Number)?.toInt()
            if (idx != null) {
                if (target is String) {
                    val normalized = if (idx < 0) target.length + idx else idx
                    return if (normalized in target.indices) target[normalized].toString() else ""
                } else if (target is List<*>) {
                    val normalized = if (idx < 0) target.size + idx else idx
                    return if (normalized in target.indices) target[normalized] else null
                }
            }
        }
        return null
    }

    private fun normalizeIndex(idx: Int, length: Int): Int = if (idx < 0) (length + idx).coerceAtLeast(0) else idx

    private fun tryEvaluateBuiltin(s: String, scope: Map<String, Any?>): Any? {
        val builtins = listOf("len", "sum", "max", "min", "abs", "range", "str", "int", "float", "list", "sorted", "round")
        for (b in builtins) {
            if (s.startsWith("$b(") && s.endsWith(")")) {
                val inner = s.removePrefix("$b(").substringBeforeLast(")").trim()
                val args = if (inner.isEmpty()) emptyList() else splitByTopLevelComma(inner).map { evalExpression(it.trim(), scope) }
                return when (b) {
                    "len" -> when (val a = args.getOrNull(0)) {
                        is String -> a.length
                        is Collection<*> -> a.size
                        is Map<*, *> -> a.size
                        else -> 0
                    }
                    "sum" -> when (val a = args.getOrNull(0)) {
                        is Iterable<*> -> a.filterIsInstance<Number>().sumOf { it.toDouble() }.let { if (it % 1.0 == 0.0) it.toInt() else it }
                        else -> 0
                    }
                    "max" -> when {
                        args.size == 1 && args[0] is Iterable<*> -> (args[0] as Iterable<*>).filterIsInstance<Number>().maxByOrNull { it.toDouble() }
                        args.size > 1 -> args.filterIsInstance<Number>().maxByOrNull { it.toDouble() }
                        else -> null
                    }
                    "min" -> when {
                        args.size == 1 && args[0] is Iterable<*> -> (args[0] as Iterable<*>).filterIsInstance<Number>().minByOrNull { it.toDouble() }
                        args.size > 1 -> args.filterIsInstance<Number>().minByOrNull { it.toDouble() }
                        else -> null
                    }
                    "abs" -> (toNumber(args.getOrNull(0)) ?: 0.0).let { kotlin.math.abs(it) }
                    "range" -> when (args.size) {
                        1 -> (0 until ((toNumber(args[0]) ?: 0.0).toInt())).toList()
                        2 -> (((toNumber(args[0]) ?: 0.0).toInt()) until ((toNumber(args[1]) ?: 0.0).toInt())).toList()
                        3 -> (((toNumber(args[0]) ?: 0.0).toInt()) until ((toNumber(args[1]) ?: 0.0).toInt()) step ((toNumber(args[2]) ?: 1.0).toInt())).toList()
                        else -> emptyList<Int>()
                    }
                    "str" -> args.getOrNull(0)?.toString() ?: "None"
                    "int" -> (toNumber(args.getOrNull(0)) ?: 0.0).toInt()
                    "float" -> (toNumber(args.getOrNull(0)) ?: 0.0).toDouble()
                    "list" -> when (val a = args.getOrNull(0)) {
                        is Iterable<*> -> a.toMutableList()
                        is String -> a.map { it.toString() }.toMutableList()
                        else -> mutableListOf<Any?>()
                    }
                    "sorted" -> when (val a = args.getOrNull(0)) {
                        is Iterable<*> -> a.sortedWith { o1, o2 ->
                            when {
                                o1 is Number && o2 is Number -> o1.toDouble().compareTo(o2.toDouble())
                                else -> o1.toString().compareTo(o2.toString())
                            }
                        }.toMutableList()
                        else -> emptyList<Any?>()
                    }
                    "round" -> (toNumber(args.getOrNull(0)) ?: 0.0).let { kotlin.math.round(it).toLong() }
                    else -> null
                }
            }
        }
        return null
    }

    private fun tryEvaluateMethod(s: String, scope: Map<String, Any?>): Any? {
        val dotIdx = s.lastIndexOf('.')
        if (dotIdx <= 0) return null
        val targetExpr = s.substring(0, dotIdx).trim()
        val call = s.substring(dotIdx + 1).trim()
        val methodName = call.substringBefore("(").trim()
        val inner = call.substringAfter("(").substringBeforeLast(")").trim()
        val args = if (inner.isEmpty()) emptyList() else splitByTopLevelComma(inner).map { evalExpression(it.trim(), scope) }
        val target = evalExpression(targetExpr, scope)

        return when (methodName) {
            "lower" -> (target as? String)?.lowercase()
            "upper" -> (target as? String)?.uppercase()
            "strip" -> (target as? String)?.trim()
            "replace" -> if (target is String && args.size >= 2) target.replace(args[0].toString(), args[1].toString()) else target
            "split" -> if (target is String) {
                val sep = args.getOrNull(0)?.toString() ?: " "
                target.split(sep).toMutableList()
            } else null
            "join" -> if (target is String && args.isNotEmpty()) {
                val list = args[0] as? Iterable<*>
                list?.joinToString(target) { it.toString() }
            } else null
            "count" -> if (target is Iterable<*> && args.isNotEmpty()) target.count { it == args[0] } else if (target is String && args.isNotEmpty()) target.windowed(args[0].toString().length).count { it == args[0].toString() } else 0
            "get" -> if (target is Map<*, *> && args.isNotEmpty()) target[args[0]] ?: args.getOrNull(1) else null
            else -> null
        }
    }

    // Helper math operations
    private fun applyAdd(a: Any?, b: Any?): Any? {
        if (a is String || b is String) return "${a ?: ""}${b ?: ""}"
        if (a is List<*> && b is List<*>) return (a + b).toMutableList()
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 0.0
        val sum = na + nb
        return if (sum % 1.0 == 0.0) sum.toInt() else sum
    }

    private fun applySubtract(a: Any?, b: Any?): Any? {
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 0.0
        val diff = na - nb
        return if (diff % 1.0 == 0.0) diff.toInt() else diff
    }

    private fun applyMultiply(a: Any?, b: Any?): Any? {
        if (a is String && b is Number) return a.repeat(b.toInt().coerceAtLeast(0))
        if (a is Number && b is String) return b.repeat(a.toInt().coerceAtLeast(0))
        if (a is List<*> && b is Number) {
            val res = mutableListOf<Any?>()
            repeat(b.toInt().coerceAtLeast(0)) { res.addAll(a) }
            return res
        }
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 0.0
        val prod = na * nb
        return if (prod % 1.0 == 0.0) prod.toInt() else prod
    }

    private fun applyDivide(a: Any?, b: Any?): Any? {
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 1.0
        if (nb == 0.0) throw ArithmeticException("ZeroDivisionError: division by zero")
        return na / nb
    }

    private fun applyFloorDivide(a: Any?, b: Any?): Any? {
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 1.0
        if (nb == 0.0) throw ArithmeticException("ZeroDivisionError: integer division by zero")
        return (na / nb).toInt()
    }

    private fun applyModulo(a: Any?, b: Any?): Any? {
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 1.0
        if (nb == 0.0) throw ArithmeticException("ZeroDivisionError: integer modulo by zero")
        return (na.toLong() % nb.toLong()).toInt()
    }

    private fun applyPower(a: Any?, b: Any?): Any? {
        val na = toNumber(a) ?: 0.0
        val nb = toNumber(b) ?: 0.0
        val res = Math.pow(na, nb)
        return if (res % 1.0 == 0.0) res.toInt() else res
    }

    private fun toNumber(v: Any?): Double? {
        return when (v) {
            is Number -> v.toDouble()
            is String -> v.toDoubleOrNull()
            is Boolean -> if (v) 1.0 else 0.0
            else -> null
        }
    }

    private fun isTruthy(v: Any?): Boolean {
        return when (v) {
            null -> false
            is Boolean -> v
            is Number -> v.toDouble() != 0.0
            is String -> v.isNotEmpty()
            is Collection<*> -> v.isNotEmpty()
            is Map<*, *> -> v.isNotEmpty()
            else -> true
        }
    }

    private fun hasBalancedParentheses(s: String): Boolean {
        var count = 0
        for (ch in s) {
            if (ch == '(') count++
            else if (ch == ')') {
                count--
                if (count < 0) return false
            }
        }
        return count == 0
    }

    private fun findMatchingBracketOpen(s: String): Int {
        var depth = 0
        for (i in s.length - 1 downTo 0) {
            if (s[i] == ']') depth++
            else if (s[i] == '[') {
                depth--
                if (depth == 0) return i
            }
        }
        return -1
    }

    private fun splitByTopLevelComma(s: String): List<String> {
        val results = mutableListOf<String>()
        var start = 0
        var parenDepth = 0
        var bracketDepth = 0
        var braceDepth = 0
        var inQuote = false
        var quoteChar = ' '

        for (i in s.indices) {
            val ch = s[i]
            if (inQuote) {
                if (ch == quoteChar && (i == 0 || s[i - 1] != '\\')) {
                    inQuote = false
                }
            } else {
                when (ch) {
                    '"', '\'' -> { inQuote = true; quoteChar = ch }
                    '(' -> parenDepth++
                    ')' -> parenDepth--
                    '[' -> bracketDepth++
                    ']' -> bracketDepth--
                    '{' -> braceDepth++
                    '}' -> braceDepth--
                    ',' -> {
                        if (parenDepth == 0 && bracketDepth == 0 && braceDepth == 0) {
                            results.add(s.substring(start, i))
                            start = i + 1
                        }
                    }
                }
            }
        }
        if (start < s.length) {
            results.add(s.substring(start))
        }
        return results
    }

    private fun splitByTopLevelToken(s: String, token: String): List<String> {
        val results = mutableListOf<String>()
        var start = 0
        var parenDepth = 0
        var bracketDepth = 0
        var braceDepth = 0
        var inQuote = false
        var quoteChar = ' '
        var i = 0

        while (i < s.length) {
            val ch = s[i]
            if (inQuote) {
                if (ch == quoteChar && (i == 0 || s[i - 1] != '\\')) {
                    inQuote = false
                }
                i++
            } else {
                when (ch) {
                    '"', '\'' -> { inQuote = true; quoteChar = ch; i++ }
                    '(' -> { parenDepth++; i++ }
                    ')' -> { parenDepth--; i++ }
                    '[' -> { bracketDepth++; i++ }
                    ']' -> { bracketDepth--; i++ }
                    '{' -> { braceDepth++; i++ }
                    '}' -> { braceDepth--; i++ }
                    else -> {
                        if (parenDepth == 0 && bracketDepth == 0 && braceDepth == 0 && s.startsWith(token, i)) {
                            results.add(s.substring(start, i))
                            i += token.length
                            start = i
                        } else {
                            i++
                        }
                    }
                }
            }
        }
        if (start < s.length) {
            results.add(s.substring(start))
        }
        return results
    }

    private fun findTopLevelAddSub(s: String): Pair<Int, Char>? {
        var parenDepth = 0
        var bracketDepth = 0
        var braceDepth = 0
        var inQuote = false
        var quoteChar = ' '

        // Search right to left for lowest precedence (+, -)
        for (i in s.length - 1 downTo 0) {
            val ch = s[i]
            if (inQuote) {
                if (ch == quoteChar && (i == 0 || s[i - 1] != '\\')) inQuote = false
            } else {
                when (ch) {
                    '"', '\'' -> { inQuote = true; quoteChar = ch }
                    ')' -> parenDepth++
                    '(' -> parenDepth--
                    ']' -> bracketDepth++
                    '[' -> bracketDepth--
                    '}' -> braceDepth++
                    '{' -> braceDepth--
                    '+', '-' -> {
                        if (parenDepth == 0 && bracketDepth == 0 && braceDepth == 0 && i > 0 && s[i - 1] != 'e' && s[i - 1] != 'E') {
                            return Pair(i, ch)
                        }
                    }
                }
            }
        }
        return null
    }

    private fun findTopLevelMulDiv(s: String): Pair<Int, String>? {
        var parenDepth = 0
        var bracketDepth = 0
        var braceDepth = 0
        var inQuote = false
        var quoteChar = ' '

        for (i in s.length - 1 downTo 0) {
            val ch = s[i]
            if (inQuote) {
                if (ch == quoteChar && (i == 0 || s[i - 1] != '\\')) inQuote = false
            } else {
                when (ch) {
                    '"', '\'' -> { inQuote = true; quoteChar = ch }
                    ')' -> parenDepth++
                    '(' -> parenDepth--
                    ']' -> bracketDepth++
                    '[' -> bracketDepth--
                    '}' -> braceDepth++
                    '{' -> braceDepth--
                    '*', '/', '%' -> {
                        if (parenDepth == 0 && bracketDepth == 0 && braceDepth == 0) {
                            if (i > 0 && s[i - 1] == '/') return Pair(i - 1, "//")
                            if (i > 0 && s[i - 1] == '*') return Pair(i - 1, "**")
                            return Pair(i, ch.toString())
                        }
                    }
                }
            }
        }
        return null
    }
}
