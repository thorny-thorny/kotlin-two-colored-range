package me.thorny.twoColoredRange

interface BoundMath<BoundType: Comparable<BoundType>, LengthType> {
  fun add(bound: BoundType, length: LengthType): BoundType
  fun subtract(first: BoundType, second: BoundType): LengthType
}

fun <BoundType: Comparable<BoundType>, LengthType> BoundMath<BoundType, LengthType>.rangeTouchesRange(
  first: ClosedRange<BoundType>,
  second: ClosedRange<BoundType>,
  step: LengthType,
): Boolean {
  return this.add(first.endInclusive, step) == second.start || this.add(second.endInclusive, step) == first.start
}

object IntBoundMath: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound + length
  override fun subtract(first: Int, second: Int) = first - second
}
