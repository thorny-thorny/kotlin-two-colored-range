import me.thorny.twoColoredRange.RedBlackColor
import me.thorny.twoColoredRange.RedBlackIntRange
import me.thorny.twoColoredRange.RedBlackLongRange
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ExtraRedBlackRangeTests {
  @Test
  fun testDefaultColors() {
    var range = RedBlackIntRange(1..1)
    assert(range.defaultColor == RedBlackColor.RED && range.otherColor == RedBlackColor.BLACK)
    range = RedBlackIntRange(1..1, RedBlackColor.RED)
    assert(range.defaultColor == RedBlackColor.RED && range.otherColor == RedBlackColor.BLACK)
    range = RedBlackIntRange(1..1, RedBlackColor.BLACK)
    assert(range.defaultColor == RedBlackColor.BLACK && range.otherColor == RedBlackColor.RED)
  }

  @Test
  fun testLongRange() {
    val range = RedBlackLongRange(1L..2L)
    range.setSubrangeBlack(2L..2L)
    assertContentEquals(range.getRedSubranges(), listOf(1L..1L))
    assertContentEquals(range.getBlackSubranges(), listOf(2L..2L))
  }
}
