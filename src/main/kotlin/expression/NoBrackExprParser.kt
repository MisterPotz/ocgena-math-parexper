package expression

import java.lang.IllegalStateException

class NoBrackExprParser(private val opsNodes: NodeBuilder.OpsNodes) {
    val opStack = opsNodes.ops.toMutableList()
    val valueAndNodes = opsNodes.valueAndNodes.toMutableList()
    val nodeSpace = opsNodes.nodeSpace


    private fun peekOp() : String? {
        return opStack.lastOrNull()
    }

    private fun removeLastNode(): MathNode {
        val lastValue = valueAndNodes.removeLast()
        if (lastValue in nodeSpace) {
            return nodeSpace[lastValue]
        }
        return ConstantNode(value = lastValue.toDouble())
    }

    private fun tryConsumeNodeWithUnaries() : MathNode {
        val lastNode = removeLastNode()
        val lastOp = peekOp()

        return when (lastOp) {
            unminus -> {
                opStack.removeLast()
                -lastNode
            }
            null -> {
                lastNode
            }
            else -> {
                lastNode
            }
        }
    }

    private fun tryConsumeTwoSideNode() : MathNode {
        val lastOp = peekOp() ?: throw IllegalStateException("had to consume two side but op was not found")

        val foldingFunctions = Maths.foldingFunctions[lastOp]!!
        val opKey = lastOp
        opStack.removeLast()
        val rightHandSide = removeLastNode()
        val leftHandSide = tryConsumeNodeWithUnaries()

        return TwoSideOperatorNode(
            children = listOf(leftHandSide, rightHandSide),
            opKey = opKey,
            foldingFunction = foldingFunctions
        )
    }

    fun parseAndUpdateNodeSpace(): MathNode {
        while (opStack.isNotEmpty()) {
            val lastOp = opStack.last()

            val newNode = when {
                lastOp in unaryOp -> {
                  tryConsumeNodeWithUnaries()

                }
                lastOp in twoSideOp -> {
                    tryConsumeTwoSideNode()
                }
                else -> {
                    throw IllegalStateException("encountered unknown op in op stack: $lastOp")
                }
            }
            val nodeId = nodeSpace.addNodeAndGetId(newNode)
            valueAndNodes.add(nodeId)
        }
        val lastNode = valueAndNodes.first()
        return nodeSpace[lastNode]
    }
}