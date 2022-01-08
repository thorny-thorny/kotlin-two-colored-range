package me.thorny.twoColoredRange

interface BoundMath<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> {
  fun add(bound: BoundType, length: LengthType): BoundType
  fun subtract(bound: BoundType, length: LengthType): BoundType
  fun getLength(greater: BoundType, lesser: BoundType): LengthType
}

object IntBoundMath: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound + length
  override fun subtract(bound: Int, length: Int) = bound - length
  override fun getLength(greater: Int, lesser: Int) = greater - lesser
}

object LongBoundMath: BoundMath<Long, Long> {
  override fun add(bound: Long, length: Long) = bound + length
  override fun subtract(bound: Long, length: Long) = bound - length
  override fun getLength(greater: Long, lesser: Long) = greater - lesser
}
