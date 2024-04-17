package foo

import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.stage.ChiselStage
import firrtl.options.TargetDirAnnotation
// import project_foo._

object elaborate extends App {
  val targetDirectory = "Verilog"
  val dir 	= TargetDirAnnotation("Verilog")
  args(0) match{
    case "test_csr" => (new ChiselStage()).emitSystemVerilog(
        new test(),
        Array("--target-dir", "Verilog", "--full-stacktrace", "--output-annotation-file", "Foo.sv")
      )
		case "AlveoDynamicTop" =>
      (new ChiselStage()).emitSystemVerilog(
        new Foo(),
        Array("--target-dir", "Verilog", "--full-stacktrace", "--output-annotation-file", "Foo.sv")
      )


  }
}
