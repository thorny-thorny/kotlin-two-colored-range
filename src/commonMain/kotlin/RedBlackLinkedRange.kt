package me.thorny.twoColoredRange

enum class RedBlackColor {
  RED,
  BLACK,
}

open class RedBlackLinkedRange<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>>(
  range: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): TwoColoredLinkedRange<BoundType, LengthType, RedBlackColor>(
  range,
  step,
  math,
  defaultColor ?: RedBlackColor.RED,
  if (defaultColor == RedBlackColor.BLACK) RedBlackColor.RED else RedBlackColor.BLACK,
) {
  fun getRedSubranges() = getSubrangesOfColor(RedBlackColor.RED)

  fun getBlackSubranges() = getSubrangesOfColor(RedBlackColor.BLACK)

  fun setSubrangeRed(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.RED)

  fun setSubrangeBlack(subrange: ClosedRange<BoundType>) = setSubrangeColor(subrange, RedBlackColor.BLACK)

  fun getRedSubrange(maxLength: LengthType = step, segmentRange: ClosedRange<BoundType> = range) = getSubrangeOfColor(RedBlackColor.RED, maxLength, segmentRange)

  fun getBlackSubrange(maxLength: LengthType = step, segmentRange: ClosedRange<BoundType> = range) = getSubrangeOfColor(RedBlackColor.BLACK, maxLength, segmentRange)
}

open class RedBlackIntLinkedRange(
  range: ClosedRange<Int>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackLinkedRange<Int, Int>(
  range,
  1,
  IntBoundMath,
  defaultColor,
)

open class RedBlackLongLinkedRange(
  range: ClosedRange<Long>,
  defaultColor: RedBlackColor? = RedBlackColor.RED,
): RedBlackLinkedRange<Long, Long>(
  range,
  1,
  LongBoundMath,
  defaultColor,
)
