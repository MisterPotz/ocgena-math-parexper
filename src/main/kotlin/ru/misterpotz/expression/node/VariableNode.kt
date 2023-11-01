package ru.misterpotz.expression.node

import ru.misterpotz.expression.paramspace.ParameterSpace

class VariableNode(val name : String) : MathNode {
    override fun evaluate(parameterSpace: ParameterSpace): Double {
        return parameterSpace[name]
    }

    override fun toString(): String {
        return printExpr()
    }

    override fun printTree(): String {
        return name
    }

    override fun printExpr(): String {
        return name
    }
}