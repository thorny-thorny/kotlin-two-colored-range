package me.thorny.twoColoredRange

enum class RedBlackColor {
  RED,
  BLACK,
}

open class RedBlackRange<BoundType: Comparable<BoundType>, LengthType>(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
): TwoColoredRange<BoundType, LengthType, RedBlackColor>(
  range,
  step,
  math,
  RedBlackColor.RED,
  RedBlackColor.BLACK,
) {
  fun getRedSubranges() = getSubrangesOfColor(RedBlackColor.RED)

  fun getBlackSubranges() = getSubrangesOfColor(RedBlackColor.BLACK)

  fun setSubrangeBlack(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.BLACK)
}

open class IntRedBlackRange(range: ClosedRange<Int>): RedBlackRange<Int, Int>(
  range,
  1,
  IntBoundMath,
)
