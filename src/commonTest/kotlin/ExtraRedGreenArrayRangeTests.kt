import me.thorny.twoColoredRange.RedGreenArrayRange
import me.thorny.twoColoredRange.RedGreenColor
import me.thorny.twoColoredRange.RedGreenIntArrayRange
import me.thorny.twoColoredRange.RedGreenLongArrayRange
import me.thorny.twoColoredRange.math.IntBoundMath
import me.thorny.twoColoredRange.rangeUtils.ClosedRangeFactory
import kotlin.test.*

class ExtraRedGreenArrayRangeTests {
  @Test
  fun testDefaultColors() {
    var range = RedGreenIntArrayRange(1..1)
    assertEquals(RedGreenColor.Red, range.defaultColor)
    assertEquals(RedGreenColor.Green, range.otherColor)
    range = RedGreenIntArrayRange(1..1, RedGreenColor.Red)
    assertEquals(RedGreenColor.Red, range.defaultColor)
    assertEquals(RedGreenColor.Green, range.otherColor)
    range = RedGreenIntArrayRange(1..1, RedGreenColor.Green)
    assertEquals(RedGreenColor.Green, range.defaultColor)
    assertEquals(RedGreenColor.Red, range.otherColor)
  }

  @Test
  fun testLongRange() {
    val range = RedGreenLongArrayRange(1L..2L)
    range.setSubrangeGreen(2L..2L)
    assertContentEquals(listOf(1L..1L), range.getRedSubranges())
    assertContentEquals(listOf(2L..2L), range.getGreenSubranges())
  }

  @Test
  fun testExtraMethods() {
    val range = RedGreenIntArrayRange(1..5)
    range.setSubrangeGreen(1..5)
    assertEquals(1..1, range.getGreenSubrange())
    range.setSubrangeRed(1..5)
    assertEquals(1..1, range.getRedSubrange())
  }

  @Test
  fun testDefaultConstructorArguments() {
    val range = RedGreenArrayRange(1..3, 1, IntBoundMath)
    assertEquals(RedGreenColor.Red, range.defaultColor)
    assertIs<ClosedRangeFactory<Int>>(range.rangeFactory)
  }
}
