import me.thorny.twoColoredRange.RedGreenColor
import me.thorny.twoColoredRange.RedGreenIntArrayRange
import me.thorny.twoColoredRange.RedGreenLongArrayRange
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ExtraRedGreenArrayRangeTests {
  @Test
  fun testDefaultColors() {
    var range = RedGreenIntArrayRange(1..1)
    assertEquals(RedGreenColor.RED, range.defaultColor)
    assertEquals(RedGreenColor.GREEN, range.otherColor)
    range = RedGreenIntArrayRange(1..1, RedGreenColor.RED)
    assertEquals(RedGreenColor.RED, range.defaultColor)
    assertEquals(RedGreenColor.GREEN, range.otherColor)
    range = RedGreenIntArrayRange(1..1, RedGreenColor.GREEN)
    assertEquals(RedGreenColor.GREEN, range.defaultColor)
    assertEquals(RedGreenColor.RED, range.otherColor)
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
}
