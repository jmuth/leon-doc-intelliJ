package funcheck.purescala


/** This pretty-printer uses Unicode for some operators, to make sure we
 * distinguish PureScala from "real" Scala (and also because it's cute). */
object PrettyPrinter {
  import Common._
  import Trees._
  import TypeTrees._
  import Definitions._

  import java.lang.StringBuffer

  def apply(tree: Expr): String = {
    val retSB = pp(tree, new StringBuffer)
    retSB.toString
  }

  def apply(tpe: TypeTree): String = {
    val retSB = pp(tpe, new StringBuffer)
    retSB.toString
  }

  def apply(defn: Definition): String = {
    val retSB = pp(defn, new StringBuffer, 0)
    retSB.toString
  }

  // EXPRESSIONS
  private def ppUnary(sb: StringBuffer, expr: Expr, op: String): StringBuffer = {
    var nsb: StringBuffer = sb
    nsb.append(op)
    nsb.append("(")
    nsb = pp(expr, nsb)
    nsb.append(")")
    nsb
  }

  private def ppBinary(sb: StringBuffer, left: Expr, right: Expr, op: String): StringBuffer = {
    var nsb: StringBuffer = sb
    nsb.append("(")
    nsb = pp(left, nsb)
    nsb.append(op)
    nsb = pp(right, nsb)
    nsb.append(")")
    nsb
  }

  private def ppNary(sb: StringBuffer, exprs: Seq[Expr], op: String): StringBuffer = {
    var nsb = sb
    nsb.append("(")
    val sz = exprs.size
    var c = 0

    exprs.foreach(ex => {
      nsb = pp(ex, nsb) ; c += 1 ; if(c < sz) nsb.append(op)
    })

    nsb.append(")")
    nsb
  }

  private def pp(tree: Expr, sb: StringBuffer): StringBuffer = tree match {
    case And(exprs) => ppNary(sb, exprs, " \u2227 ")            // \land
    case Or(exprs) => ppNary(sb, exprs, " \u2228 ")             // \lor
    case Not(Equals(l, r)) => ppBinary(sb, l, r, " \u2260 ")    // \neq
    case Not(expr) => ppUnary(sb, expr, "\u00AC")               // \neg
    case Equals(l,r) => ppBinary(sb, l, r, " = ")
    case IntLiteral(v) => sb.append(v)
    case BooleanLiteral(v) => sb.append(v)
    case IfExpr(c, t, e) => {
      var nsb = sb
      nsb.append("if (")
      nsb = pp(c, nsb)
      nsb.append(") { ")
      nsb.append(t)
      nsb.append(" } else { ")
      nsb.append(e)
      nsb.append(" }")
      nsb
    }

    case _ => sb.append("Expr?")
  }

  // TYPE TREES
  private def pp(tpe: TypeTree, sb: StringBuffer): StringBuffer = tpe match {
    case _ => sb.append("Type?")
  }

  // DEFINITIONS
  private def pp(defn: Definition, sb: StringBuffer, lvl: Int): StringBuffer = defn match {
    case _ => sb.append("Defn?")
  }
}
