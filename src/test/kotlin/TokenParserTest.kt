import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.misterpotz.expression.facade.m
import ru.misterpotz.expression.paramspace.VariableParameterSpace

class TokenParserTest {

    @Test
    fun stringExpressionIsParsedAndEvaluated() {
        val expression = "-10*4+250*(-1)-(-2*20+4)".m
        val expected = -254.0

        Assertions.assertEquals(expected, expression.evaluate())
    }

    @Test
    fun expressionWithVariableIsEvaluated() {
        val expression = "5+(-  kek ) *34  - 3".m
        Assertions.assertEquals(
            -100.0,
            expression.evaluate(
                parameterSpace = VariableParameterSpace(
                    "kek" to 3.0
                )
            )
        )
    }

    @Test
    fun throwsIfNoValueForVariablePassed() {
        val expression = "5+(-  kek ) *34  - 3".m

        Assertions.assertThrows(NullPointerException::class.java) {
            expression.evaluate(VariableParameterSpace())
        }
    }
}