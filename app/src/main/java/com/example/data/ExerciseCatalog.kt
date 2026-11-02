package com.example.data

import com.example.data.model.Exercise
import com.example.data.model.ExerciseDifficulty
import com.example.data.model.ExerciseTestCase

object ExerciseCatalog {

    val exercises: List<Exercise> = listOf(
        // === BEGINNER ===
        Exercise(
            id = "beg_01_sum_two",
            title = "Sum of Two Numbers",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Basics & Math",
            description = """
                Write a function `sum_two_numbers(a, b)` that takes two numeric arguments `a` and `b` and returns their arithmetic sum.
                
                ### Example:
                `sum_two_numbers(3, 5)` -> `8`
                `sum_two_numbers(-10, 4)` -> `-6`
            """.trimIndent(),
            functionName = "sum_two_numbers",
            starterCode = "def sum_two_numbers(a, b):\n    # Return the sum of a and b\n    return a + b\n",
            solutionHint = "Use the `+` arithmetic addition operator.",
            testCases = listOf(
                ExerciseTestCase(listOf(3, 5), 8, "Basic positive numbers"),
                ExerciseTestCase(listOf(-10, 4), -6, "Negative and positive number"),
                ExerciseTestCase(listOf(0, 0), 0, "Zero addition"),
                ExerciseTestCase(listOf(100, 250), 350, "Larger numbers", isHidden = true)
            )
        ),
        Exercise(
            id = "beg_02_even_odd",
            title = "Even or Odd Checker",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Conditionals",
            description = """
                Write a function `check_even_odd(n)` that returns the string `"Even"` if the integer `n` is even, or `"Odd"` if `n` is odd.
                
                ### Example:
                `check_even_odd(4)` -> `"Even"`
                `check_even_odd(7)` -> `"Odd"`
            """.trimIndent(),
            functionName = "check_even_odd",
            starterCode = "def check_even_odd(n):\n    # Write your conditional check here\n    if n % 2 == 0:\n        return \"Even\"\n    else:\n        return \"Odd\"\n",
            solutionHint = "Use the modulo operator `% 2 == 0` to check divisibility by 2.",
            testCases = listOf(
                ExerciseTestCase(listOf(4), "Even", "Even number 4"),
                ExerciseTestCase(listOf(7), "Odd", "Odd number 7"),
                ExerciseTestCase(listOf(0), "Even", "Zero is even"),
                ExerciseTestCase(listOf(101), "Odd", "Large odd number", isHidden = true)
            )
        ),
        Exercise(
            id = "beg_03_reverse_string",
            title = "Reverse a String",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Strings",
            description = """
                Write a function `reverse_string(s)` that takes a string `s` and returns it reversed.
                
                ### Example:
                `reverse_string("python")` -> `"nohtyp"`
                `reverse_string("racecar")` -> `"racecar"`
            """.trimIndent(),
            functionName = "reverse_string",
            starterCode = "def reverse_string(s):\n    # Reverse string s\n    return s[::-1]\n",
            solutionHint = "Python supports slicing with a negative step: `s[::-1]`.",
            testCases = listOf(
                ExerciseTestCase(listOf("python"), "nohtyp", "Word 'python'"),
                ExerciseTestCase(listOf("racecar"), "racecar", "Palindrome 'racecar'"),
                ExerciseTestCase(listOf("hello world"), "dlrow olleh", "Sentence with spaces"),
                ExerciseTestCase(listOf("12345"), "54321", "Numeric string", isHidden = true)
            )
        ),
        Exercise(
            id = "beg_04_max_element",
            title = "Find Maximum Element",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Lists & Math",
            description = """
                Write a function `find_max(numbers)` that returns the largest number in a non-empty list `numbers`.
                
                ### Example:
                `find_max([1, 9, 3, 7])` -> `9`
                `find_max([-5, -2, -10])` -> `-2`
            """.trimIndent(),
            functionName = "find_max",
            starterCode = "def find_max(numbers):\n    # Return the maximum value\n    return max(numbers)\n",
            solutionHint = "You can use Python's built-in `max()` function or iterate through elements.",
            testCases = listOf(
                ExerciseTestCase(listOf(listOf(1, 9, 3, 7)), 9, "List [1, 9, 3, 7]"),
                ExerciseTestCase(listOf(listOf(-5, -2, -10)), -2, "Negative numbers"),
                ExerciseTestCase(listOf(listOf(42)), 42, "Single item list"),
                ExerciseTestCase(listOf(listOf(100, 20, 500, 3)), 500, "Unsorted list", isHidden = true)
            )
        ),
        Exercise(
            id = "beg_05_count_vowels",
            title = "Count Vowels in String",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Strings & Loops",
            description = """
                Write a function `count_vowels(s)` that returns the number of vowels (`a, e, i, o, u`, case-insensitive) present in string `s`.
                
                ### Example:
                `count_vowels("Python Code")` -> `3`  # ('o', 'o', 'e')
                `count_vowels("sky")` -> `0`
            """.trimIndent(),
            functionName = "count_vowels",
            starterCode = "def count_vowels(s):\n    vowels = \"aeiouAEIOU\"\n    count = 0\n    for char in s:\n        if char in vowels:\n            count += 1\n    return count\n",
            solutionHint = "Iterate through each character and check if it exists in `'aeiouAEIOU'`.",
            testCases = listOf(
                ExerciseTestCase(listOf("Python Code"), 3, "'Python Code' has 3 vowels"),
                ExerciseTestCase(listOf("sky"), 0, "'sky' has no vowels"),
                ExerciseTestCase(listOf("AEIOUaeiou"), 10, "All vowels"),
                ExerciseTestCase(listOf("Arcade Snake Break"), 6, "Mixed sentence", isHidden = true)
            )
        ),
        Exercise(
            id = "beg_06_celsius_to_fahrenheit",
            title = "Celsius to Fahrenheit",
            difficulty = ExerciseDifficulty.BEGINNER,
            category = "Basics & Math",
            description = """
                Write a function `to_fahrenheit(c)` that converts temperature in Celsius to Fahrenheit using the formula `(c * 9/5) + 32`.
                
                ### Example:
                `to_fahrenheit(0)` -> `32`
                `to_fahrenheit(100)` -> `212`
            """.trimIndent(),
            functionName = "to_fahrenheit",
            starterCode = "def to_fahrenheit(c):\n    return (c * 9 / 5) + 32\n",
            solutionHint = "Multiply by 9, divide by 5, and add 32.",
            testCases = listOf(
                ExerciseTestCase(listOf(0), 32, "Freezing point (0 C)"),
                ExerciseTestCase(listOf(100), 212, "Boiling point (100 C)"),
                ExerciseTestCase(listOf(-40), -40, "Coincidence point (-40 C)"),
                ExerciseTestCase(listOf(25), 77, "Room temperature (25 C)", isHidden = true)
            )
        ),

        // === INTERMEDIATE ===
        Exercise(
            id = "mid_01_palindrome",
            title = "Valid Palindrome",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Strings & Logic",
            description = """
                Write a function `is_palindrome(s)` that returns `True` if `s` is a palindrome (reads the same forward and backwards, ignoring case), or `False` otherwise.
                
                ### Example:
                `is_palindrome("Radar")` -> `True`
                `is_palindrome("Python")` -> `False`
            """.trimIndent(),
            functionName = "is_palindrome",
            starterCode = "def is_palindrome(s):\n    cleaned = s.lower()\n    return cleaned == cleaned[::-1]\n",
            solutionHint = "Convert string to lowercase with `.lower()`, then check if `s == s[::-1]`.",
            testCases = listOf(
                ExerciseTestCase(listOf("Radar"), true, "Capitalized 'Radar'"),
                ExerciseTestCase(listOf("level"), true, "'level' palindrome"),
                ExerciseTestCase(listOf("Python"), false, "'Python' is not a palindrome"),
                ExerciseTestCase(listOf("noon"), true, "'noon' palindrome", isHidden = true)
            )
        ),
        Exercise(
            id = "mid_02_fizzbuzz",
            title = "FizzBuzz List Generator",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Loops & Modulo",
            description = """
                Write a function `fizz_buzz(n)` that returns a list of strings from 1 up to `n`:
                - For multiples of 3, output `"Fizz"`
                - For multiples of 5, output `"Buzz"`
                - For multiples of both 3 and 5, output `"FizzBuzz"`
                - For other numbers, output the number as a string (e.g. `"1"`)
                
                ### Example:
                `fizz_buzz(5)` -> `["1", "2", "Fizz", "4", "Buzz"]`
            """.trimIndent(),
            functionName = "fizz_buzz",
            starterCode = "def fizz_buzz(n):\n    result = []\n    for i in range(1, n + 1):\n        if i % 15 == 0:\n            result.append(\"FizzBuzz\")\n        elif i % 3 == 0:\n            result.append(\"Fizz\")\n        elif i % 5 == 0:\n            result.append(\"Buzz\")\n        else:\n            result.append(str(i))\n    return result\n",
            solutionHint = "Check `% 15 == 0` first for multiples of both 3 and 5.",
            testCases = listOf(
                ExerciseTestCase(listOf(5), listOf("1", "2", "Fizz", "4", "Buzz"), "First 5 numbers"),
                ExerciseTestCase(listOf(3), listOf("1", "2", "Fizz"), "First 3 numbers"),
                ExerciseTestCase(
                    listOf(15),
                    listOf("1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz", "11", "Fizz", "13", "14", "FizzBuzz"),
                    "15 numbers including FizzBuzz",
                    isHidden = true
                )
            )
        ),
        Exercise(
            id = "mid_03_two_sum",
            title = "Two Sum Indices",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Lists & Algorithms",
            description = """
                Write a function `two_sum(nums, target)` that returns the indices `[i, j]` of the two numbers such that `nums[i] + nums[j] == target`.
                Assume each input has exactly one valid solution.
                
                ### Example:
                `two_sum([2, 7, 11, 15], 9)` -> `[0, 1]`
                `two_sum([3, 2, 4], 6)` -> `[1, 2]`
            """.trimIndent(),
            functionName = "two_sum",
            starterCode = "def two_sum(nums, target):\n    for i in range(len(nums)):\n        for j in range(i + 1, len(nums)):\n            if nums[i] + nums[j] == target:\n                return [i, j]\n    return []\n",
            solutionHint = "You can loop through all pairs `(i, j)` where `j > i`.",
            testCases = listOf(
                ExerciseTestCase(listOf(listOf(2, 7, 11, 15), 9), listOf(0, 1), "Target 9 with [2, 7]"),
                ExerciseTestCase(listOf(listOf(3, 2, 4), 6), listOf(1, 2), "Target 6 with [2, 4]"),
                ExerciseTestCase(listOf(listOf(3, 3), 6), listOf(0, 1), "Target 6 with duplicate numbers"),
                ExerciseTestCase(listOf(listOf(1, 5, 8, 12, 20), 25), listOf(1, 4), "Target 25", isHidden = true)
            )
        ),
        Exercise(
            id = "mid_04_remove_duplicates",
            title = "Remove Duplicates (Preserve Order)",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Lists",
            description = """
                Write a function `remove_duplicates(items)` that removes duplicate values from a list while preserving the first appearance order of items.
                
                ### Example:
                `remove_duplicates([1, 2, 2, 3, 1, 4])` -> `[1, 2, 3, 4]`
                `remove_duplicates(["a", "b", "a", "c"])` -> `["a", "b", "c"]`
            """.trimIndent(),
            functionName = "remove_duplicates",
            starterCode = "def remove_duplicates(items):\n    seen = []\n    for item in items:\n        if item not in seen:\n            seen.append(item)\n    return seen\n",
            solutionHint = "Iterate through items and append to a new list if `item not in seen`.",
            testCases = listOf(
                ExerciseTestCase(listOf(listOf(1, 2, 2, 3, 1, 4)), listOf(1, 2, 3, 4), "Integers with repeats"),
                ExerciseTestCase(listOf(listOf("a", "b", "a", "c")), listOf("a", "b", "c"), "String elements"),
                ExerciseTestCase(listOf(listOf(5, 5, 5)), listOf(5), "All identical elements"),
                ExerciseTestCase(listOf(listOf(10, 20, 30)), listOf(10, 20, 30), "Already unique", isHidden = true)
            )
        ),
        Exercise(
            id = "mid_05_fibonacci",
            title = "N-th Fibonacci Number",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Algorithms & Math",
            description = """
                Write a function `fibonacci(n)` that returns the `n`-th Fibonacci number where `fib(0) = 0`, `fib(1) = 1`, and `fib(n) = fib(n-1) + fib(n-2)`.
                
                ### Example:
                `fibonacci(0)` -> `0`
                `fibonacci(6)` -> `8`
                `fibonacci(10)` -> `55`
            """.trimIndent(),
            functionName = "fibonacci",
            starterCode = "def fibonacci(n):\n    if n <= 0:\n        return 0\n    if n == 1:\n        return 1\n    a, b = 0, 1\n    for _ in range(2, n + 1):\n        a, b = b, a + b\n    return b\n",
            solutionHint = "Use two variables `a` and `b` iteratively to compute the sequence efficiently.",
            testCases = listOf(
                ExerciseTestCase(listOf(0), 0, "Base case n=0"),
                ExerciseTestCase(listOf(1), 1, "Base case n=1"),
                ExerciseTestCase(listOf(6), 8, "n=6 -> 8"),
                ExerciseTestCase(listOf(10), 55, "n=10 -> 55", isHidden = true)
            )
        ),
        Exercise(
            id = "mid_06_word_frequency",
            title = "Word Frequency Counter",
            difficulty = ExerciseDifficulty.INTERMEDIATE,
            category = "Dictionaries & Strings",
            description = """
                Write a function `word_count(text)` that splits a string into words by space and returns a dictionary mapping each word to its frequency count.
                
                ### Example:
                `word_count("apple banana apple")` -> `{"apple": 2, "banana": 1}`
            """.trimIndent(),
            functionName = "word_count",
            starterCode = "def word_count(text):\n    counts = {}\n    words = text.split()\n    for w in words:\n        if w in counts:\n            counts[w] += 1\n        else:\n            counts[w] = 1\n    return counts\n",
            solutionHint = "Use `text.split()` to extract words and build a count dictionary.",
            testCases = listOf(
                ExerciseTestCase(listOf("apple banana apple"), mapOf("apple" to 2, "banana" to 1), "Repeated fruits"),
                ExerciseTestCase(listOf("python python python"), mapOf("python" to 3), "Single repeated word"),
                ExerciseTestCase(listOf("one two three"), mapOf("one" to 1, "two" to 1, "three" to 1), "All distinct words"),
                ExerciseTestCase(listOf("snake bite snake code bite"), mapOf("snake" to 2, "bite" to 2, "code" to 1), "Game themed", isHidden = true)
            )
        ),

        // === ADVANCED ===
        Exercise(
            id = "adv_01_valid_parentheses",
            title = "Valid Parentheses & Brackets",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Stacks & Data Structures",
            description = """
                Write a function `is_valid_brackets(s)` that determines if the input string containing `'(', ')', '{', '}', '[', ']'` is valid:
                1. Open brackets must be closed by the same type of brackets.
                2. Open brackets must be closed in the correct order.
                
                ### Example:
                `is_valid_brackets("()[]{}")` -> `True`
                `is_valid_brackets("(]")` -> `False`
                `is_valid_brackets("([{}])")` -> `True`
            """.trimIndent(),
            functionName = "is_valid_brackets",
            starterCode = "def is_valid_brackets(s):\n    stack = []\n    mapping = {\")\": \"(\", \"}\": \"{\", \"]\": \"[\"}\n    for char in s:\n        if char in mapping:\n            top = stack.pop() if len(stack) > 0 else \"#\"\n            if mapping[char] != top:\n                return False\n        else:\n            stack.append(char)\n    return len(stack) == 0\n",
            solutionHint = "Use a list as a stack: push opening brackets and pop when encountering closing brackets.",
            testCases = listOf(
                ExerciseTestCase(listOf("()[]{}"), true, "Valid multiple pairs"),
                ExerciseTestCase(listOf("([{}])"), true, "Nested brackets"),
                ExerciseTestCase(listOf("(]"), false, "Mismatched pair"),
                ExerciseTestCase(listOf("([)]"), false, "Wrong closing order", isHidden = true)
            )
        ),
        Exercise(
            id = "adv_02_merge_sorted_lists",
            title = "Merge Two Sorted Lists",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Algorithms",
            description = """
                Write a function `merge_sorted(list1, list2)` that merges two sorted lists of integers into a single sorted list.
                
                ### Example:
                `merge_sorted([1, 2, 4], [1, 3, 4])` -> `[1, 1, 2, 3, 4, 4]`
                `merge_sorted([], [0])` -> `[0]`
            """.trimIndent(),
            functionName = "merge_sorted",
            starterCode = "def merge_sorted(list1, list2):\n    return sorted(list1 + list2)\n",
            solutionHint = "Concatenate the lists and return `sorted(list1 + list2)` or merge with two pointers.",
            testCases = listOf(
                ExerciseTestCase(listOf(listOf(1, 2, 4), listOf(1, 3, 4)), listOf(1, 1, 2, 3, 4, 4), "Standard sorted merge"),
                ExerciseTestCase(listOf(listOf(), listOf(0)), listOf(0), "One empty list"),
                ExerciseTestCase(listOf(listOf(5, 10), listOf(1, 2, 3)), listOf(1, 2, 3, 5, 10), "Disjoint ranges"),
                ExerciseTestCase(listOf(listOf(2, 4, 6), listOf(1, 3, 5)), listOf(1, 2, 3, 4, 5, 6), "Interleaved", isHidden = true)
            )
        ),
        Exercise(
            id = "adv_03_diagonal_sum",
            title = "Matrix Diagonal Sum",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Matrices & 2D Arrays",
            description = """
                Write a function `diagonal_sum(mat)` that takes a square `n x n` 2D matrix and returns the sum of the primary diagonal and secondary diagonal elements.
                If `n` is odd, do not double-count the central element.
                
                ### Example:
                `diagonal_sum([[1, 2, 3], [4, 5, 6], [7, 8, 9]])` -> `25`
                # Primary: 1 + 5 + 9 = 15, Secondary: 3 + 5 + 7 = 15, Middle 5 counted once: 15 + 15 - 5 = 25
            """.trimIndent(),
            functionName = "diagonal_sum",
            starterCode = "def diagonal_sum(mat):\n    n = len(mat)\n    total = 0\n    for i in range(n):\n        total += mat[i][i]\n        total += mat[i][n - 1 - i]\n    if n % 2 == 1:\n        total -= mat[n // 2][n // 2]\n    return total\n",
            solutionHint = "Primary diagonal is `mat[i][i]`, secondary is `mat[i][n - 1 - i]`.",
            testCases = listOf(
                ExerciseTestCase(
                    listOf(listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))),
                    25,
                    "3x3 Matrix sum"
                ),
                ExerciseTestCase(
                    listOf(listOf(listOf(1, 1, 1, 1), listOf(1, 1, 1, 1), listOf(1, 1, 1, 1), listOf(1, 1, 1, 1))),
                    8,
                    "4x4 Matrix sum"
                ),
                ExerciseTestCase(
                    listOf(listOf(listOf(5))),
                    5,
                    "1x1 Matrix"
                ),
                ExerciseTestCase(
                    listOf(listOf(listOf(2, 0), listOf(0, 2))),
                    4,
                    "2x2 Matrix",
                    isHidden = true
                )
            )
        ),
        Exercise(
            id = "adv_04_flatten_list",
            title = "Flatten Nested Lists",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Recursion & Lists",
            description = """
                Write a function `flatten(nested)` that flattens a list containing integers or sublists of integers into a single 1D list.
                
                ### Example:
                `flatten([1, [2, 3], [4, [5]]])` -> `[1, 2, 3, 4, 5]`
                `flatten([[10], 20, [30]])` -> `[10, 20, 30]`
            """.trimIndent(),
            functionName = "flatten",
            starterCode = "def flatten(nested):\n    result = []\n    for item in nested:\n        if isinstance(item, list):\n            for sub in flatten(item):\n                result.append(sub)\n        else:\n            result.append(item)\n    return result\n",
            solutionHint = "Check if an item is a list; if so, recurse or iterate through it.",
            testCases = listOf(
                ExerciseTestCase(
                    listOf(listOf(1, listOf(2, 3), listOf(4, listOf(5)))),
                    listOf(1, 2, 3, 4, 5),
                    "Deeply nested list"
                ),
                ExerciseTestCase(
                    listOf(listOf(listOf(10), 20, listOf(30))),
                    listOf(10, 20, 30),
                    "Mixed numbers and lists"
                ),
                ExerciseTestCase(
                    listOf(listOf(1, 2, 3)),
                    listOf(1, 2, 3),
                    "Already flat"
                ),
                ExerciseTestCase(
                    listOf(listOf(listOf(listOf(42)))),
                    listOf(42),
                    "Triple nested single item",
                    isHidden = true
                )
            )
        ),
        Exercise(
            id = "adv_05_prime_factorization",
            title = "Prime Factors",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Math & Number Theory",
            description = """
                Write a function `prime_factors(n)` that returns a list of the prime factors of integer `n` in ascending order.
                
                ### Example:
                `prime_factors(12)` -> `[2, 2, 3]`
                `prime_factors(13)` -> `[13]`
                `prime_factors(100)` -> `[2, 2, 5, 5]`
            """.trimIndent(),
            functionName = "prime_factors",
            starterCode = "def prime_factors(n):\n    factors = []\n    d = 2\n    while d * d <= n:\n        while n % d == 0:\n            factors.append(d)\n            n //= d\n        d += 1\n    if n > 1:\n        factors.append(n)\n    return factors\n",
            solutionHint = "Divide out 2s, then test odd divisors up to `sqrt(n)`. Add remainder if `n > 1`.",
            testCases = listOf(
                ExerciseTestCase(listOf(12), listOf(2, 2, 3), "12 = 2 * 2 * 3"),
                ExerciseTestCase(listOf(13), listOf(13), "Prime number 13"),
                ExerciseTestCase(listOf(100), listOf(2, 2, 5, 5), "100 = 2^2 * 5^2"),
                ExerciseTestCase(listOf(84), listOf(2, 2, 3, 7), "84 factorization", isHidden = true)
            )
        ),
        Exercise(
            id = "adv_06_longest_unique_substring",
            title = "Longest Substring Without Repeats",
            difficulty = ExerciseDifficulty.ADVANCED,
            category = "Strings & Sliding Window",
            description = """
                Write a function `length_of_longest_substring(s)` that returns the length of the longest substring without repeating characters.
                
                ### Example:
                `length_of_longest_substring("abcabcbb")` -> `3`  # ("abc")
                `length_of_longest_substring("bbbbb")` -> `1`     # ("b")
                `length_of_longest_substring("pwwkew")` -> `3`    # ("wke")
            """.trimIndent(),
            functionName = "length_of_longest_substring",
            starterCode = "def length_of_longest_substring(s):\n    seen = {}\n    max_len = 0\n    start = 0\n    for i in range(len(s)):\n        char = s[i]\n        if char in seen and seen[char] >= start:\n            start = seen[char] + 1\n        seen[char] = i\n        curr_len = i - start + 1\n        if curr_len > max_len:\n            max_len = curr_len\n    return max_len\n",
            solutionHint = "Use a sliding window with a dictionary storing the most recent index of each character.",
            testCases = listOf(
                ExerciseTestCase(listOf("abcabcbb"), 3, "'abcabcbb' -> 3"),
                ExerciseTestCase(listOf("bbbbb"), 1, "'bbbbb' -> 1"),
                ExerciseTestCase(listOf("pwwkew"), 3, "'pwwkew' -> 3"),
                ExerciseTestCase(listOf(""), 0, "Empty string -> 0", isHidden = true)
            )
        )
    )

    fun getExerciseById(id: String): Exercise? {
        return exercises.find { it.id == id }
    }
}
