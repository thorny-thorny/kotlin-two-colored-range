package me.thorny.twoColoredRange

enum class RedBlackColor {
  RED,
  BLACK,
}

open class RedBlackRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>>(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): TwoColoredRange<BoundType, LengthType, RedBlackColor>(
  range,
  step,
  math,
  defaultColor ?: RedBlackColor.RED,
  if (defaultColor == RedBlackColor.BLACK) RedBlackColor.RED else RedBlackColor.BLACK,
) {
  fun getRedSubranges() = getSubrangesOfColor(RedBlackColor.RED)

  fun getBlackSubranges() = getSubrangesOfColor(RedBlackColor.BLACK)

  fun setSubrangeBlack(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.BLACK)
}

open class RedBlackIntRange(
  range: ClosedRange<Int>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackRange<Int, Int>(
  range,
  1,
  IntBoundMath,
  defaultColor,
)

open class RedBlackLongRange(
  range: ClosedRange<Long>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackRange<Long, Long>(
  range,
  1,
  LongBoundMath,
  defaultColor,
)
