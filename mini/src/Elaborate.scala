package mini

import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.stage.ChiselStage
import firrtl.options.TargetDirAnnotation
import mini.core._
import mini.junctions._

object elaborate extends App {
  val targetDirectory = "Verilog"
  val dir 	= TargetDirAnnotation("Verilog")
  val config = MiniConfig()
  args(0) match{
		case "mini" =>
      (new ChiselStage()).emitSystemVerilog(
        new Tile(
          coreParams = config.core,
          nastiParams = config.nasti,
          cacheParams = config.cache
        ),
        Array("--target-dir", "Verilog", "--full-stacktrace"),
        
      )


  }
}
