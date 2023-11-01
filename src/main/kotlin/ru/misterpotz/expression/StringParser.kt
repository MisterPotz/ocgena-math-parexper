package ru.misterpotz.expression

import ru.misterpotz.expression.node.MathNode

class StringParser {
    var tokens = listOf<String>()
    fun acceptTokens(tokens: List<String>) {
        this.tokens = tokens
    }
}


class TokenParser(private val tokens: List<String>) {
    private val opStack: MutableList<String> = mutableListOf()
    private val valueNodeStack: MutableList<String> = mutableListOf()
    private val nodeSet: MutableList<MathNode> = mutableListOf()
    val nodeSpace = NodeSpace()

    fun reduce() {
        val closestOpOpenBrack = opStack.findLast { it == openBrack }
        val closestNodeOpenBrack = valueNodeStack.findLast { it == openBrack }
    }

    fun parseTokens() {
        var lastToken: String? = null

        for (token in tokens) {
            when {
                token in tokenOps -> {
                    val resultingToken = if (lastToken == openBrack || lastToken in tokenOps) {
                        if (token == "-") {
                            unminus
                        } else {
                            null
                        }
                    } else {
                        token
                    }
                    if (resultingToken != null) {
                        opStack.add(resultingToken)
                    }
                }

                token in brack -> {
                    valueNodeStack.add(token)
                    opStack.add(token)
                    if (token == closeBrack) {
                        reduce()
                    }
                }

                else -> {
                    val modified = if (token.toDoubleOrNull() != null) {
                        token.toDoubleOrNull()!!.toString()
                    } else if (variableMatcher.matches(token)) {
                        "var_$token"
                    } else {
                        throw IllegalStateException()
                    }
                    valueNodeStack.add(modified)
                }
            }
            lastToken = token
        }
    }

    fun buildExpression() {
        parseTokens()


    }
    class OpsNodes(
        val ops: List<String>,
        val valueAndNodes: List<String>,
        val nodeSpace: NodeSpace
    )
}


