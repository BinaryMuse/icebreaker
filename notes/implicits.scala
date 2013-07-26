def multiplyByTen(implicit x: Int) = x * 10

println(multiplyByTen(5))

implicit val num = 50

println(multiplyByTen)
