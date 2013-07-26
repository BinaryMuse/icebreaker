// Option[Thing] - a value of type Thing that may or may not exist

// Valid values:
//   * Some(thing)
//   * None

var maybeString: Option[String] = Some("I'm a real boy!")
maybeString = maybeString map { _.toUpperCase }

println(maybeString.getOrElse("nobody"))


var maybeNotString: Option[String] = None
maybeNotString = maybeNotString map { _.toUpperCase }

println(maybeNotString.getOrElse("nobody"))
