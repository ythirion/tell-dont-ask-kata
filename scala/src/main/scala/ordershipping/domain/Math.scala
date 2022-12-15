package ordershipping.domain

object Math {
  def roundAt(p: Int)(n: Double): Double = {
    val s = math pow(10, p)
    (math round n * s) / s
  }
}
