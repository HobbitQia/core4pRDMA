package foo

import chisel3._
import chisel3.util._
import qdma._
import hbm._
import common._
import common.storage._
import common.axi._
import qdma.examples._
import common.partialReconfig.AlveoStaticIO
import foo._

class Connect(HBM_PORT: UInt) extends Module {
    val io = IO(new Bundle{
        val userClk     = Input(Clock())
        val userRstn    = Input(Bool())
        val hbm_clk     = Input(Clock())
        val hbm_rstn    = Input(Bool())
        val h2c_aw      = Flipped(Decoupled(new AXI_ADDR(33, 256, 6, 0, 4)))
        val h2c_w       = Flipped(Decoupled(new AXI_DATA_W(33, 256, 6, 0)))
        val h2c_b       = Decoupled(new AXI_BACK(33, 256, 6, 0))
        val c2h_ar      = Flipped(Decoupled(new AXI_ADDR(33, 256, 6, 0, 4)))
        val c2h_r       = Decoupled(new AXI_DATA_R(33, 256, 6, 0))
        val axi_hbm     = new AXI(33, 256, 6, 0, 4)
	})

    val in_axi = Wire(new AXI(33, 256, 6, 0, 4))
	ToZero(in_axi)
	val in_axi_regslice = withClockAndReset(io.userClk, !io.userRstn) { AXIRegSlice(in_axi) }
	in_axi.aw <> io.h2c_aw
	in_axi.w <> io.h2c_w
	in_axi.ar <> io.c2h_ar
	io.c2h_r <> in_axi.r
	io.h2c_b <> in_axi.b

	val out_axi = XAXIConverter(in_axi_regslice, io.userClk, io.userRstn, io.hbm_clk, io.hbm_rstn)
	val out_axi_regslice = withClockAndReset(io.hbm_clk, !io.hbm_rstn) { AXIRegSlice(out_axi) }

	val hbm_h2c_cmd = out_axi_regslice.aw

	val hbm_h2c_data = out_axi_regslice.w

	val hbm_c2h_cmd = out_axi_regslice.ar

	val hbm_c2h_data = Wire(Decoupled(new AXI_DATA_R(33, 256, 6, 0)))
	ToZero(hbm_c2h_data)
	val hbm_h2c_back = Wire(Decoupled(new AXI_BACK(33, 256, 6, 0)))

	out_axi_regslice.r.bits := hbm_c2h_data.bits
	out_axi_regslice.r.valid := hbm_c2h_data.valid
	hbm_c2h_data.ready := out_axi_regslice.r.ready

	out_axi_regslice.b.bits := hbm_h2c_back.bits
	out_axi_regslice.b.valid := hbm_h2c_back.valid
	hbm_h2c_back.ready := out_axi_regslice.b.ready
	
	// H2C 往 hbm 里写数据（应该是 aw 和 w
	val axi_port_0 = io.axi_hbm
	// io.axi_hbm.hbm_init() ?
	hbm_h2c_cmd.ready := axi_port_0.aw.ready
	axi_port_0.aw.valid := hbm_h2c_cmd.valid
	axi_port_0.aw.bits.addr := hbm_h2c_cmd.bits.addr
	axi_port_0.aw.bits.len := hbm_h2c_cmd.bits.len
	// size?
	hbm_h2c_data.ready := axi_port_0.w.ready
	axi_port_0.w.valid := hbm_h2c_data.valid
	axi_port_0.w.bits.data := hbm_h2c_data.bits.data
	axi_port_0.w.bits.last := hbm_h2c_data.bits.last
	//back
	axi_port_0.b.ready := hbm_h2c_back.ready
	hbm_h2c_back.valid := axi_port_0.b.valid
	hbm_h2c_back.bits <> axi_port_0.b.bits

	// C2H 从 hbm 里读数据（应该是 ar
	hbm_c2h_cmd.ready := axi_port_0.ar.ready
	axi_port_0.ar.valid := hbm_c2h_cmd.valid
	axi_port_0.ar.bits.addr := hbm_c2h_cmd.bits.addr
	axi_port_0.ar.bits.len := hbm_c2h_cmd.bits.len


	axi_port_0.r.ready := hbm_c2h_data.ready
	hbm_c2h_data.valid := axi_port_0.r.valid
	hbm_c2h_data.bits.data := axi_port_0.r.bits.data
	hbm_c2h_data.bits.last := axi_port_0.r.bits.last
}