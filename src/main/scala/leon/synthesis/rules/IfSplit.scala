/* Copyright 2009-2014 EPFL, Lausanne */

package leon
package synthesis
package rules

import purescala.Trees._
import purescala.TreeOps._
import purescala.Extractors._
import purescala.Constructors._

case object IfSplit extends Rule("If-Split") {
  def instantiateOn(sctx: SynthesisContext, p: Problem): Traversable[RuleInstantiation] = {
    val ifs = collect{
      case i: IfExpr => Set(i)
      case _ => Set[IfExpr]()
    }(p.phi)

    val xsSet = p.xs.toSet

    ifs.flatMap { 
      case i @ IfExpr(cond, _, _) =>
        if ((variablesOf(cond) & xsSet).isEmpty) {
          List(split(i, p, "Split If("+cond+")"))
        } else {
          Nil
        }
    }
  }

  def split(i: IfExpr, p: Problem, description: String): RuleInstantiation = {
    val subs = List(
      Problem(p.as, p.ws, and(p.pc, i.cond), replace(Map(i -> i.thenn), p.phi), p.xs),
      Problem(p.as, p.ws, and(p.pc, not(i.cond)), replace(Map(i -> i.elze), p.phi), p.xs)
    )

    val onSuccess: List[Solution] => Option[Solution] = {
      case sols if sols.size == 2 =>
        val List(ts, es) = sols

        val pre = or(and(i.cond, ts.pre), and(not(i.cond), es.pre))
        val defs = ts.defs ++ es.defs
        val term = IfExpr(i.cond, ts.term, es.term)

        Some(Solution(pre, defs, term, sols.forall(_.isTrusted)))

      case _ =>
        None
    }

    RuleInstantiation.immediateDecomp(p, this, subs, onSuccess, description)
  }
}

