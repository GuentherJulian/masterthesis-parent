object A {
  def main(args: Array[String]) = {
    #foreach (field in fields)
    var ${field} = "";
    #end
  }
}