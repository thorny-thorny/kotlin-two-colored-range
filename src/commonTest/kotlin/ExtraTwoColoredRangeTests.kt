import me.thorny.twoColoredRange.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

enum class RedBlackYellowColor {
  RED,
  BLACK,
  YELLOW,
}

object BrokenIntBoundMath1: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound
  override fun subtract(bound: Int, length: Int) = IntBoundMath.subtract(bound, length)
  override fun getLength(greater: Int, lesser: Int) = IntBoundMath.getLength(greater, lesser)
}

object BrokenIntBoundMath2: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = IntBoundMath.add(bound, length)
  override fun subtract(bound: Int, length: Int) = bound
  override fun getLength(greater: Int, lesser: Int) = IntBoundMath.getLength(greater, lesser)
}

object BrokenIntBoundMath3: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = IntBoundMath.add(bound, length)
  override fun subtract(bound: Int, length: Int) = IntBoundMath.subtract(bound, length)
  override fun getLength(greater: Int, lesser: Int) = 0
}

class WeirdBound(val value: Int): Comparable<WeirdBound> {
  override fun compareTo(other: WeirdBound): Int {
    return value.compareTo(other.value)
  }

  override fun equals(other: Any?): Boolean {
    if (other is WeirdBound) {
      return value == other.value
    }

    return false
  }

  override fun hashCode(): Int {
    return value
  }
}

object WeirdMath: BoundMath<WeirdBound, Int> {
  override fun add(bound: WeirdBound, length: Int) = WeirdBound(bound.value + length)
  override fun subtract(bound: WeirdBound, length: Int) = WeirdBound(bound.value - length)
  override fun getLength(greater: WeirdBound, lesser: WeirdBound) = greater.value - lesser.value
}

class ExtraTwoColoredRangeTests {
  private fun rangeWithMath(math: BoundMath<Int, Int>): TwoColoredRange<Int, Int, RedBlackYellowColor> {
    return TwoColoredRange(1..2, 1, math, RedBlackYellowColor.RED, RedBlackYellowColor.BLACK)
  }

  @Test
  fun testExtraExceptions() {
    val range = rangeWithMath(IntBoundMath)
    assertFailsWith<Exception> { range.getSubrangesOfColor(RedBlackYellowColor.YELLOW) }
    assertFailsWith<Exception> { range.setSubrangeColor(1..2, RedBlackYellowColor.YELLOW) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath1) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath2) }
    assertFailsWith<Exception> { rangeWithMath(BrokenIntBoundMath3) }
    assertFailsWith<Exception> {
      TwoColoredRange(1..2, 1, IntBoundMath, RedBlackYellowColor.RED, RedBlackYellowColor.RED)
    }
  }

  @Test
  fun testDerivatives() {
    val intRange = TwoColoredIntRange(1..2, RedBlackYellowColor.RED, RedBlackYellowColor.BLACK)
    intRange.setSubrangeOtherColor(2..2)
    assertContentEquals(intRange.getSubrangesOfDefaultColor(), listOf(1..1))
    assertContentEquals(intRange.getSubrangesOfOtherColor(), listOf(2..2))

    val longRange = TwoColoredLongRange(1L..2L, RedBlackYellowColor.RED, RedBlackYellowColor.BLACK)
    longRange.setSubrangeOtherColor(2L..2L)
    assertContentEquals(longRange.getSubrangesOfDefaultColor(), listOf(1L..1L))
    assertContentEquals(longRange.getSubrangesOfOtherColor(), listOf(2L..2L))
  }

  @Test
  fun testWeirdMath() {
    val range = TwoColoredRange(
      WeirdBound(1)..WeirdBound(2),
      1,
      WeirdMath,
      RedBlackYellowColor.RED,
      RedBlackYellowColor.BLACK,
    )

    // Just to cover non-standard ranges related code
    range.setSubrangeOtherColor(WeirdBound(1)..WeirdBound(1))
    range.setSubrangeOtherColor(WeirdBound(2)..WeirdBound(2))
    assertContentEquals(range.getSubrangesOfOtherColor(), listOf(WeirdBound(1)..WeirdBound(2)))
  }
}
