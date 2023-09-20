package expression

class NoBrackExprParser(private val opsNodes: TokenParser.OpsNodes) {
    val opStack = opsNodes.ops.toMutableList()
    val valueAndNodes = opsNodes.valueAndNodes.toMutableList()
    val nodeSpace = opsNodes.nodeSpace


    private fun peekOp(): String? {
        return opStack.lastOrNull()
    }

    private fun removeLastNode(): MathNode {
        val lastValue = valueAndNodes.removeLast()
        if (lastValue in nodeSpace) {
            return nodeSpace[lastValue]
        }
        return ConstantNode(value = lastValue.toDouble())
    }


    private fun tryConsumeNodeWithUnaries(): MathNode {
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

    private fun getOrCreateNodeAtValueIndex(valueIndex: Int): String {
        val lastNode = valueAndNodes[valueIndex]

        if (lastNode in nodeSpace) {
            return lastNode
        }

        return if (variableMatcher.matches(lastNode)) {
            val newNode = VariableNode(name = lastNode)
            nodeSpace.addNodeAndGetId(newNode)
        } else {
            val newNode = ConstantNode(value = lastNode.toDouble())
            nodeSpace.addNodeAndGetId(newNode)
        }
    }

    private fun tryConsumeTwoSideNode(): MathNode {
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

    fun consumeOpAtIndex(opIndex: Int) {
        opStack.removeAt(opIndex)
    }

    fun fullTransform() {
        transformOpsWithPriority(100)
        transformOpsWithPriority(10)
        transformOpsWithPriority(1)
    }

    @Suppress("UNCHECKED_CAST")
    class NodeBuilder {
        var op: String? = null
        var nodes: List<MathNode>? = null

        fun build(): MathNode {
            val op = op!!
            val nodes = nodes!!

            return if (op in unaryOp) {
                UniOperatorNode(
                    childNode = nodes.first(),
                    opKey = op,
                    operation = opToFun[op]!! as (Double) -> Double
                )
            } else {
                TwoSideOperatorNode(
                    children = nodes.toList(),
                    opKey = op,
                    foldingFunction = opToFun[op]!! as TwoSideFun
                )
            }
        }
    }

    fun createNodesBasedOnValues(values: Values): List<MathNode> {
        return buildList {
            if (values.singleValue != null) {
                add(nodeSpace[getOrCreateNodeAtValueIndex(values.singleValue)])
            } else {
                add(nodeSpace[getOrCreateNodeAtValueIndex(values.left!!)])
                add(nodeSpace[getOrCreateNodeAtValueIndex(values.right!!)])
            }
        }
    }

    private fun transformOpsWithPriority(priority: Int) {
        val nodeBuilder = NodeBuilder()
        val allowedOps = opPriority[priority]!!
        var opIndex = 0
        while (opIndex < opStack.size) {
            val op = opStack[opIndex]

            if (op !in allowedOps) {
                opIndex++
                continue
            }

            val valueIndex = getValueIndexForOp(opIndex)!!
            val values = createNodesBasedOnValues(valueIndex)
            val newNode = nodeBuilder.apply {
                this.op = op
                this.nodes = values
            }.build()

            val newNodeId = nodeSpace.addNodeAndGetId(newNode)

            consumeOpAtIndex(opIndex)
            valueAndNodes[valueIndex.existingIndex] = newNodeId

            if (values.size > 1) {
                valueAndNodes.removeAt(valueIndex.right!!)
            }
        }
    }

    fun getValueIndexForOp(opIndex: Int): Values? {
        var valueIndex = 0
        var countedOpIndex = 0

        while (valueIndex < valueAndNodes.size) {
            val op = opStack[countedOpIndex]
            val targetOp = countedOpIndex == opIndex

            if (targetOp) {
                return if (op in unaryOp) {
                    Values.single(valueIndex)
                } else {
                    Values.values(valueIndex, valueIndex + 1)
                }
            }

            if (op in twoSideOp) {
                valueIndex++
                countedOpIndex++
            }
            if (op in unaryOp) {
                countedOpIndex++
            }
        }

        return null
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

    data class Values(
        val left: Int? = null,
        val right: Int? = null,
        val singleValue: Int? = null,
    ) {
        val existingIndex : Int
            get() = left ?: singleValue!!
        val requireRange: IntRange
            get() = left!!..right!!
        val requireSingle: Int
            get() = singleValue!!

        override fun toString(): String {
            return if (left == null) {
                singleValue.toString()
            } else {
                "$left..$right"
            }
        }

        companion object {
            fun single(single: Int): Values {
                return Values(singleValue = single)
            }

            fun values(left: Int, right: Int): Values {
                return Values(left = left, right = right)
            }
        }
    }
}

// -3 + -4 * 5


// op stack: unmin + unmin *
// node stack: 3 4 5

