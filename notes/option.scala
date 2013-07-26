// Option[Thing] - a value of type Thing that may or may not exist

// Valid values:
//   * Some(thing)
//   * None

val maybeString: Option[String] = Some("I'm a real boy!")
println(maybeString map { _.toUpperCase })

val maybeNotString: Option[String] = None
println(maybeNotString map { _.toUpperCase })
