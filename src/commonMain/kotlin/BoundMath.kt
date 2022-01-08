package me.thorny.twoColoredRange

interface BoundMath<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> {
  fun add(bound: BoundType, length: LengthType): BoundType
  fun subtract(bound: BoundType, length: LengthType): BoundType
  fun getLength(start: BoundType, endExclusive: BoundType): LengthType
}

object IntBoundMath: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound + length
  override fun subtract(bound: Int, length: Int) = bound - length
  override fun getLength(start: Int, endExclusive: Int) = endExclusive - start
}

object LongBoundMath: BoundMath<Long, Long> {
  override fun add(bound: Long, length: Long) = bound + length
  override fun subtract(bound: Long, length: Long) = bound - length
  override fun getLength(start: Long, endExclusive: Long) = endExclusive - start
}
